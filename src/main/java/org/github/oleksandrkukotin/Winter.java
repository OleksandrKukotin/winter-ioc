package org.github.oleksandrkukotin;

import org.github.oleksandrkukotin.core.SimpleSnowflakeFactory;
import org.github.oleksandrkukotin.service.FieldInjectedService;
import org.github.oleksandrkukotin.service.SetterInjectedService;
import org.github.oleksandrkukotin.service.UserService;

public class Winter {

    private static final SimpleSnowflakeFactory snowflakeFactory = new SimpleSnowflakeFactory();

    public static void main(String[] args) {
        snowflakeFactory.scan("org.github.oleksandrkukotin");

        // Constructor injection with @Qualifier
        UserService userService = snowflakeFactory.getSnowflake("UserService", UserService.class);
        System.out.println("[constructor] " + userService.processUserMessageService());

        // Field injection with @Melt + @Qualifier
        FieldInjectedService fieldService = snowflakeFactory.getSnowflake("FieldInjectedService", FieldInjectedService.class);
        System.out.println(fieldService.send());

        // Setter injection with @Melt + @Qualifier
        SetterInjectedService setterService = snowflakeFactory.getSnowflake("SetterInjectedService", SetterInjectedService.class);
        System.out.println(setterService.send());
    }
}
