package com.coderintuition.CoderIntuition.tasks;

import java.util.Arrays;
import java.util.List;

import com.coderintuition.CoderIntuition.models.User;
import com.coderintuition.CoderIntuition.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EmailTask {

    @Autowired
    private UserRepository userRepository;

    // send email every morning at 9am Eastern
    @Scheduled(cron = "0 * * * * *", zone = "America/New_York")
    // @Scheduled(cron = "0 0 9 * * *", zone = "America/New_York")
    public void sendEmails() {
        List<User> emailableUsers = userRepository.findEmailableUsers();
        log.info("Emailable users: ");
        for (User user : emailableUsers) {
            log.info(user.getEmail());
        }
    }
}
