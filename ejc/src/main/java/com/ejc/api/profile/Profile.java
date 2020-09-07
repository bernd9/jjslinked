package com.ejc.api.profile;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Profile {

    private static final String KEY = "profile";
    public static final String DEFAULT_PROFILE = "default";

    static {
        init();
    }

    private static void init() {
        if (System.getProperty(KEY) != null) {
            currentProfile = System.getProperty(KEY);
        }
    }

    @Getter
    @NonNull
    private static String currentProfile = DEFAULT_PROFILE;


}
