package org.github.oleksandrkukotin.service;

import org.github.oleksandrkukotin.core.annotation.Snowflake;

@Snowflake
public class UserService {

    private final EmailService emailService;

    public UserService(EmailService emailService) {
        this.emailService = emailService;
    }

    public String processUserEmailService() {
        return emailService.doSomething();
    }
}
