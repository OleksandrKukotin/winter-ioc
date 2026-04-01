package org.github.oleksandrkukotin.service.circular;

import org.github.oleksandrkukotin.core.annotation.Snowflake;

/**
 * Depends on PingService, forming a cycle: PongService → PingService → PongService.
 * Used to demonstrate CircularDependencyException in the Winter demo.
 */
@Snowflake
public class PongService {

    private final PingService pingService;

    public PongService(PingService pingService) {
        this.pingService = pingService;
    }

    public String pong() {
        return "pong";
    }
}
