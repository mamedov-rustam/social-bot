package com.social.bot.instagram.common;


public final class InstagramSelectors {
    public final static class Css {
        public static final String LOGIN_LINK = "#react-root > section > main > article > div._p8ymb > div:nth-child(2) > p > a";
        public static final String USERNAME_INPUT = "#react-root > section > main > article > div._p8ymb > div:nth-child(1) > div > form > div:nth-child(1) > input";
        public static final String PASSWORD_INPUT = "#react-root > section > main > article > div._p8ymb > div:nth-child(1) > div > form > div:nth-child(2) > input";
        public static final String LOGIN_BUTTON = "#react-root > section > main > article > div._p8ymb > div:nth-child(1) > div > form > span > button";

        public static final String PROFILE_LINK = "#react-root > section > nav > div > div > div > div._nhei4 > div > div:nth-child(3) > a";

        public static final String FIRST_IMAGE = "#react-root > section > main > article > div > div._nljxa > div:nth-child(1) > a:nth-child(1)";
        public static final String SECOND_IMAGE = "#react-root > section > main > article > div > div._nljxa > div:nth-child(2) > a:nth-child(1)";
        public static final String THIRD_IMAGE = "#react-root > section > main > article > div > div._nljxa > div:nth-child(3) > a:nth-child(1)";

        public static final String LIKE_BUTTON = "body > div:nth-child(8) > div > div._g1ax7 > div > article > div._rgrbt > section._ghat4._68sx3 > a._tk4ba._1tv0k > span";
        public static final String FOLLOW_BUTTON = "#react-root > section > main > article > header > div > div > span > span > button";
    }

    public final static class XPath {

    }

    private InstagramSelectors() {}
}
