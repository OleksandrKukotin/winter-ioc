package org.github.oleksandrkukotin.service.circular;

import org.github.oleksandrkukotin.core.annotation.Snowflake;

/**
 * Depends on PongService, forming a cycle: PingService → PongService → PingService.
 * Used to demonstrate CircularDependencyException in the Winter demo.
 */
@Snowflake
public class PingService {

    private final PongService pongService;

    public PingService(PongService pongService) {
        this.pongService = pongService;
    }

    public String ping() {
        return "ping → " + pongService.pong();
    }
}
