package com.ejc.api.profile;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Profile {

    static final String KEY = "profile";
    public static final String DEFAULT_PROFILE = "default";
    private static String currentProfile;

    public static synchronized String getCurrentProfile() {
        if (currentProfile == null) {
            currentProfile = System.getProperty(KEY);
        }
        if (currentProfile == null) {
            currentProfile = System.getenv(KEY);
        }
        if (currentProfile == null) {
            currentProfile = DEFAULT_PROFILE;
        }
        return currentProfile;
    }


    public static void main(String[] args) {
        System.out.println(getCurrentProfile());
    }
}
