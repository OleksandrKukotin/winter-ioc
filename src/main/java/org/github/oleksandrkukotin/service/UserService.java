package org.github.oleksandrkukotin.service;

import org.github.oleksandrkukotin.core.annotation.Qualifier;
import org.github.oleksandrkukotin.core.annotation.Snowflake;

@Snowflake
public class UserService {

    private final MessageService messageService;

    public UserService(@Qualifier("EmailService") MessageService messageService) {
        this.messageService = messageService;
    }

    public String processUserMessageService() {
        return messageService.doSomething();
    }
}
