package org.github.oleksandrkukotin;

import org.github.oleksandrkukotin.core.SimpleSnowflakeFactory;
import org.github.oleksandrkukotin.core.exception.CircularDependencyException;
import org.github.oleksandrkukotin.service.constructor.UserService;
import org.github.oleksandrkukotin.service.field.FieldInjectedService;
import org.github.oleksandrkukotin.service.setter.SetterInjectedService;
import org.github.oleksandrkukotin.service.circular.PingService;

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

        // Circular dependency detection
        try {
            snowflakeFactory.getSnowflake("PingService", PingService.class);
        } catch (CircularDependencyException e) {
            System.out.println("[circular] Caught expected exception: " + e.getMessage());
        }
    }
}
