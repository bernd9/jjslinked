package one.xis.profile;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ActiveProfile {

    static final String KEY = "profile";
    public static final String DEFAULT_PROFILE = "default";
    private static volatile String currentProfile;

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

    static void setActiveProfile(String profile) {
        currentProfile = profile;
    }

    public static void unload() {
        currentProfile = null;
    }

    public static void main(String[] args) {
        System.out.println(getCurrentProfile());
    }
}
