package org.github.oleksandrkukotin;

import org.github.oleksandrkukotin.core.SimpleSnowflakeFactory;
import org.github.oleksandrkukotin.service.UserService;

public class Winter {

    private static final SimpleSnowflakeFactory snowflakeFactory = new SimpleSnowflakeFactory();

    public static void main(String[] args) {
        snowflakeFactory.scan("org.github.oleksandrkukotin");
        UserService service = snowflakeFactory.getSnowflake("UserService", UserService.class);
        System.out.println(service.processUserEmailService());
    }
}
