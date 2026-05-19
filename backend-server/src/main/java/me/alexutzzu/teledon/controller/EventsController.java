package me.alexutzzu.teledon.controller;

import me.alexutzzu.teledon.controller.dto.events.SseEvent;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;

@RestController
@RequestMapping("/api/event-stream")
public class EventsController {
    private final Sinks.Many<SseEvent<?>> globalEventSink;

    public EventsController(Sinks.Many<SseEvent<?>> globalEventSink) {
        this.globalEventSink = globalEventSink;
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Object>> streamGlobalEvents() {

        Flux<ServerSentEvent<Object>> events = globalEventSink.asFlux()
                .map(event -> {
                    String entityName = event.getClass().getSimpleName();

                    String dynamicEventName = entityName.toLowerCase();

                    return ServerSentEvent.builder()
                            .event(dynamicEventName)
                            .data(event)
                            .build();
                });

        Flux<ServerSentEvent<Object>> initialPing = Flux.just(
                ServerSentEvent.builder()
                        .event("ping")
                        .data("connected")
                        .build()
        );

        Flux<ServerSentEvent<Object>> keepAlive = Flux.interval(Duration.ofSeconds(15))
                .map(tick -> ServerSentEvent.builder()
                        .comment("keep-alive")
                        .build());

        return Flux.concat(initialPing, Flux.merge(events, keepAlive));
    }

}
