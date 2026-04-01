package org.github.oleksandrkukotin.service.messaging;

import org.github.oleksandrkukotin.core.annotation.Snowflake;

@Snowflake
public class EmailService implements MessageService {

    @Override
    public String doSomething(){
        return "An email successfully sent!";
    }
}
