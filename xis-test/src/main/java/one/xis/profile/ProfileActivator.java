package one.xis.profile;

import one.xis.test.ActivateProfile;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProfileActivator {
    void activateProfile(ActivateProfile activateProfileAnnotation) {
        ActiveProfile.setActiveProfile(activateProfileAnnotation.value());
    }
}
