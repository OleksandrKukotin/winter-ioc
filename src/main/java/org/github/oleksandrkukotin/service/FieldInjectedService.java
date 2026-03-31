package org.github.oleksandrkukotin.service;

import org.github.oleksandrkukotin.core.annotation.Melt;
import org.github.oleksandrkukotin.core.annotation.Qualifier;
import org.github.oleksandrkukotin.core.annotation.Snowflake;

/**
 * Demonstrates @Melt field injection.
 * No constructor is declared — the container injects EmailService directly into the field.
 */
@Snowflake
public class FieldInjectedService {

    @Melt
    @Qualifier("SmsService")
    private MessageService messageService;

    public String send() {
        return "[field] " + messageService.doSomething();
    }
}
