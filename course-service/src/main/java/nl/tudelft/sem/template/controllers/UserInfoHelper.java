package nl.tudelft.sem.template.controllers;

import nl.tudelft.sem.template.entities.UserInfo;
import org.springframework.security.core.context.SecurityContextHolder;

final class UserInfoHelper {

    static String getNetID() {
        UserInfo userInfo = (UserInfo) SecurityContextHolder
            .getContext().getAuthentication().getPrincipal();

        return userInfo.getNetID();
    }

    static String getRole() {
        UserInfo userInfo = (UserInfo) SecurityContextHolder
            .getContext().getAuthentication().getPrincipal();

        return userInfo.getRoles();
    }

}
