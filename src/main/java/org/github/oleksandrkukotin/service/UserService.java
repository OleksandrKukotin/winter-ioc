package org.github.oleksandrkukotin.service;

public class UserService {

    private final EmailService emailService;

    public UserService(EmailService emailService) {
        this.emailService = emailService;
    }

    public String processUserEmailService() {
        return emailService.doSomething();
    }
}
