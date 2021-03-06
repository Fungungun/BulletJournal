package com.bulletjournal.controller;

import com.bulletjournal.clients.UserClient;
import com.bulletjournal.controller.models.Myself;
import com.bulletjournal.controller.models.UpdateMyselfParams;
import com.bulletjournal.controller.models.User;
import com.bulletjournal.repository.UserDaoJpa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@RestController
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    public static final String MYSELF_ROUTE = "/api/myself";
    public static final String LOGOUT_MYSELF_ROUTE = "/api/myself/logout";

    @Autowired
    private UserClient userClient;

    @Autowired
    private UserDaoJpa userDaoJpa;

    @GetMapping("/api/users/{username}")
    public User getUser(String username) {
        return userClient.getUser(username);
    }

    @GetMapping(MYSELF_ROUTE)
    public Myself getMyself(@RequestParam(name = "expand", defaultValue = "false") String expand) {
        String username = MDC.get(UserClient.USER_NAME_KEY);
        String timezone = null;
        if (Objects.equals(expand, "true")) {
            com.bulletjournal.repository.models.User user =
                    this.userDaoJpa.getByName(username);
            timezone = user.getTimezone();
        }
        User self = userClient.getUser(username);
        return new Myself(self, timezone);
    }

    @PatchMapping(MYSELF_ROUTE)
    public Myself updateMyself(@NotNull @Valid UpdateMyselfParams updateMyselfParams) {
        String username = MDC.get(UserClient.USER_NAME_KEY);
        com.bulletjournal.repository.models.User user =
                this.userDaoJpa.updateMyself(username, updateMyselfParams);
        User self = userClient.getUser(username);
        return new Myself(self, user.getTimezone());
    }

    @PostMapping(LOGOUT_MYSELF_ROUTE)
    public ResponseEntity<?> logout() {
        String username = MDC.get(UserClient.USER_NAME_KEY);
        LOGGER.info("Logging out " + username);
        this.userClient.logout(username);
        LOGGER.info(username + " is logged out, redirecting");
        return ResponseEntity.ok().build();
    }
}
