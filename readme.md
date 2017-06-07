# Instagram

#### Filtering speed

**-> 3 users/second** 

**-> 180 users/minute** 

**-> 10 800 users/hour**

#### Filtering params

- **instagram.filter.minus.words** [string] - divided by comma words. 
If any of this words will presented in user description, then user will be skipped.

- **instagram.filter.minFollowers** and **instagram.filter.maxFollowers** [number] - restriction for minimal and maximal 
followers count value.

- **instagram.filter.minFolowing** and **instagram.filter.maxFolowing** [number] - restriction for minimal and maximum following count value.

- **instagram.filter.minPostsAmount** [number] - restriction on minimum posted posts.

- **instagram.filter.maxSpentDaysFromLastPost** [number] - maximal amount of passed days from latest post date.

# Where to find `access_token` ?
You can simple get it from the URL after authorization by link:
- [VK](https://oauth.vk.com/authorize?client_id=5849076&display=page&scope=friends&response_type=token&v=5.65&state=123456)
- [Intagram](https://api.instagram.com/oauth/authorize/?client_id=daf63aa91ff2444d82917f210a802698&redirect_uri=http://localhost.com&response_type=token&scope=public_content)