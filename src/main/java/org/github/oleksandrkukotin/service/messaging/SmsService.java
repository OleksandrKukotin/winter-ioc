package org.github.oleksandrkukotin.service.messaging;

import org.github.oleksandrkukotin.core.annotation.Snowflake;

@Snowflake
public class SmsService implements MessageService {

    @Override
    public String doSomething(){
        return "An SMS successfully sent!";
    }
}
