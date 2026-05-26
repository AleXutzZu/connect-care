package me.alexutzzu.teledon.config;

import me.alexutzzu.teledon.controller.dto.events.SseEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;

@Configuration
public class EventConfig {

    @Bean
    public Sinks.Many<SseEvent<?>> globalEventSink() {
        return Sinks.many().multicast().directBestEffort();
    }
}
