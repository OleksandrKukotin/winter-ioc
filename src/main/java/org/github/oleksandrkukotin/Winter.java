package org.github.oleksandrkukotin;

import org.github.oleksandrkukotin.core.SimpleSnowflakeFactory;
import org.github.oleksandrkukotin.core.SnowflakeDefinition;
import org.github.oleksandrkukotin.service.EmailService;
import org.github.oleksandrkukotin.service.UserService;

public class Winter {

    private static final SimpleSnowflakeFactory snowflakeFactory = new  SimpleSnowflakeFactory();

    public static void main(String[] args) {
        snowflakeFactory.registerDefinition(new SnowflakeDefinition(UserService.class, "UserService"));
        snowflakeFactory.registerDefinition(new SnowflakeDefinition(EmailService.class, "EmailService"));
        UserService service = snowflakeFactory.getSnowflake("UserService", UserService.class);
        System.out.println(service.processUserEmailService());
    }
}
