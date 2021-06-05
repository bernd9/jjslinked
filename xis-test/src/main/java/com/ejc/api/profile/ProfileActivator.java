package com.ejc.api.profile;

import com.ejc.test.ActivateProfile;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProfileActivator {
    void activateProfile(ActivateProfile activateProfileAnnotation) {
        ActiveProfile.setActiveProfile(activateProfileAnnotation.value());
    }
}
