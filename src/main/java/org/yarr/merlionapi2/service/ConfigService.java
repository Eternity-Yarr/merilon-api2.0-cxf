package org.yarr.merlionapi2.service;

public class ConfigService
{
    public String merlionLogin() {
        return System.getProperty("login");
    }

    public String merlionPassword() {
        return System.getProperty("password");
    }

    public static ConfigService i() {
        return Lazy.service;
    }

    private static class Lazy {
        public static final ConfigService service = new ConfigService();
    }
}
