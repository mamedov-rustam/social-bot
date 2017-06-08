package com.social.bot.instagram.runner.filter;

import com.social.bot.instagram.client.InstagramHttpClient;
import com.social.bot.instagram.model.InstagramUser;
import com.social.bot.instagram.model.InstagramUserResponseWrapper;
import com.social.bot.instagram.model.Post;
import com.social.bot.instagram.repository.InstagramUserRepository;
import com.social.bot.vk.model.VkUser;
import com.social.bot.vk.service.VkUserRepository;
import com.social.bot.vk.utils.VkUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.stream.Collectors.toList;

@Service
@Order(3)
public class InstagramUserFilterBotRunner implements ApplicationRunner {
    @Value("${instagram.bot.filter.enable}")
    private boolean isEnabled;

    @Value("${instagram.filter.minFollowers}")
    private Long minFolowers;
    @Value("${instagram.filter.maxFollowers}")
    private Long maxFolowers;

    @Value("${instagram.filter.maxFolowing}")
    private Long maxFolowing;
    @Value("${instagram.filter.minFolowing}")
    private Long minFolowing;

    @Value("${instagram.filter.minPostsAmount}")
    private Long minPostsAmount;

    @Value("${instagram.filter.maxSpentDaysFromLastPost}")
    private Integer maxSpentDaysFromLastPost;

    @Value("#{'${instagram.filter.minus.words}'.split(',\\s*')}")
    private List<String> minusWords;

    @Autowired
    private VkUserRepository vkUserRepository;
    @Autowired
    private InstagramUserRepository instagramUserRepository;
    @Autowired
    private InstagramHttpClient instagramHttpClient;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        if (!isEnabled) {
            return;
        }

        long botStartTime = System.currentTimeMillis();
        System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-");
        System.out.println("Start filtering instagram users");
        List<VkUser> vkUsers = vkUserRepository.loadMergedUsers();
        System.out.println("Total users for filtering: " + vkUsers.size());
        System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-");

        List<InstagramUser> goodInstagramUsers = searchForFilteredUsers(vkUsers);
        System.out.println("-----------------------------------------");
        System.out.println("Filtered from " + vkUsers.size() + " to " + goodInstagramUsers.size());

        instagramUserRepository.saveFilteredUsers(goodInstagramUsers);
        System.out.println("Saved successfully.");
        System.out.println("-----------------------------------------");
        double spentTimeInSeconds = (System.currentTimeMillis() - botStartTime) / 1000d;
        System.out.println("Total time spent on instagram filtering " + spentTimeInSeconds + " seconds");
    }

    private List<InstagramUser> searchForFilteredUsers(List<VkUser> vkUsers) {
        long startTime = System.currentTimeMillis();
        AtomicLong requestCounter = new AtomicLong(0);
        AtomicLong badRequestCounter = new AtomicLong(0);
        AtomicLong successfullySaved = new AtomicLong(0);
        AtomicLong badUser = new AtomicLong(0);

        return vkUsers.stream()
                .map(vkUser -> instagramHttpClient.loadUserInfo(vkUser.getInstagram()))
                .peek(respWrap -> {
                    if (requestCounter.get() % 12 == 0) {
                        logCurrentState((long)vkUsers.size(), requestCounter.get(), badRequestCounter.get(),
                                successfullySaved.get(), badUser.get(), startTime);
                    }

                    requestCounter.incrementAndGet();
                    if(respWrap.isHasError()) {
                        badRequestCounter.incrementAndGet();
                    }
                })
                .filter(responseWrapper -> !responseWrapper.isHasError())
                .map(InstagramUserResponseWrapper::getUser)
                .filter(user -> {
                    if (isUserGood(user)) {
                        return true;
                    }

                    badUser.incrementAndGet();
                    return false;
                })
                .peek(user -> {
                    successfullySaved.incrementAndGet();
                })
                .map(user -> {
                    List<Post> posts = sortByDate(user.getMedia().getPosts());
                    List<Post> subPosts = posts.subList(0, 3);
                    user.getMedia().setPosts(subPosts);

                    return user;
                })
                .collect(toList());
    }

    private void logCurrentState(Long total, Long requested, Long withErrors, Long successfullySaved, Long badUsers, Long startTime) {
        long spentTimeInSeconds = (System.currentTimeMillis() - startTime) / 1000;
        long spentMinutes = spentTimeInSeconds / 60;
        long spentSeconds = spentTimeInSeconds % 60;

        System.out.println("----------------------------");
        System.out.println("Sent requests: " + requested);
        System.out.println("Remains: " + (total - requested));
        System.out.println("Good users: " + successfullySaved);
        System.out.println("With errors: " + withErrors);
        System.out.println("Bad users: " + successfullySaved);
        System.out.println("Time spent: " + spentMinutes + " minutes " + spentSeconds + " seconds.");
        System.out.println("----------------------------");
    }

    @SuppressWarnings("all")
    private boolean isUserGood(InstagramUser user) {
        Long followersCount = user.getFollowers().getValue();
        if (followersCount < minFolowers || followersCount > maxFolowers) {
            return false;
        }

        Long followingCount = user.getFollowing().getValue();
        if (followingCount < minFolowing || followingCount > maxFolowing) {
            return false;
        }

        int postsAmount = user.getMedia().getPosts().size();
        if (postsAmount < minPostsAmount) {
            return false;
        }

        String description = user.getDescription();
        if (containsMinusWords(description)) {
            return false;
        }

        List<Post> posts = sortByDate(user.getMedia().getPosts());
        Post latestPost = posts.get(0);

        if (!isValidLatestPostDate(latestPost)) {
            return false;
        }

        return true;
    }

    private List<Post> sortByDate(List<Post> sourcePosts) {
        List<Post> posts = new ArrayList<>(sourcePosts);
        posts.sort((firstPost, secondPost) -> firstPost.getDate().before(secondPost.getDate()) ? 1 : -1);

        return posts;
    }

    private boolean containsMinusWords(String description) {
        for (String minusWord : minusWords) {
            if (StringUtils.containsIgnoreCase(description, minusWord)) {
                return true;
            }
        }

        return false;
    }

    private boolean isValidLatestPostDate(Post post) {
        Date lastValidDate = DateUtils.addDays(new Date(), -maxSpentDaysFromLastPost);
        return post.getDate().after(lastValidDate);
    }
}
