package org.github.oleksandrkukotin.service;

import org.github.oleksandrkukotin.core.annotation.Melt;
import org.github.oleksandrkukotin.core.annotation.Qualifier;
import org.github.oleksandrkukotin.core.annotation.Snowflake;

/**
 * Demonstrates @Melt setter injection.
 * The container calls the setter after construction to inject SmsService.
 */
@Snowflake
public class SetterInjectedService {

    private MessageService messageService;

    @Melt
    @Qualifier("SmsService")
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    public String send() {
        return "[setter] " + messageService.doSomething();
    }
}
