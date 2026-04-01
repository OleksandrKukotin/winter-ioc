package org.github.oleksandrkukotin.service.constructor;

import org.github.oleksandrkukotin.core.annotation.Qualifier;
import org.github.oleksandrkukotin.core.annotation.Snowflake;
import org.github.oleksandrkukotin.service.messaging.MessageService;

@Snowflake
public class UserService {

    private final MessageService messageService;

    public UserService(@Qualifier("SmsService") MessageService messageService) {
        this.messageService = messageService;
    }

    public String processUserMessageService() {
        return messageService.doSomething();
    }
}
