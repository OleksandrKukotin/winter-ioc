package org.github.oleksandrkukotin.service;

import org.github.oleksandrkukotin.core.annotation.Qualifier;
import org.github.oleksandrkukotin.core.annotation.Snowflake;

@Snowflake
public class UserService {

    @Qualifier("SmsService")
    private final MessageService messageService;

    public UserService(MessageService messageService) {
        this.messageService = messageService;
    }

    public String processUserMessageService() {
        return messageService.doSomething();
    }
}
