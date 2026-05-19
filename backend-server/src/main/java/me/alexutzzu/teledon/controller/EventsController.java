package me.alexutzzu.teledon.controller;

import me.alexutzzu.teledon.controller.dto.events.SseEvent;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.time.Duration;

@RestController
@RequestMapping("/api/event-stream")
public class EventsController {
    private final Sinks.Many<SseEvent<?>> globalEventSink;

    public EventsController(Sinks.Many<SseEvent<?>> globalEventSink) {
        this.globalEventSink = globalEventSink;
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamGlobalEvents() {

        SseEmitter emitter = new SseEmitter(-1L);

        try {
            emitter.send(SseEmitter.event().name("ping").data("connected"));
        } catch (IOException e) {
            emitter.completeWithError(e);
            return emitter;
        }

        Disposable subscription = globalEventSink.asFlux().subscribe(
                event -> {
                    try {
                        String dynamicEventName = event.getClass().getSimpleName().toLowerCase();

                        emitter.send(SseEmitter.event()
                                .name(dynamicEventName)
                                .data(event));
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                    }
                },
                emitter::completeWithError,
                emitter::complete
        );

        emitter.onCompletion(subscription::dispose);
        emitter.onTimeout(subscription::dispose);
        emitter.onError(e -> subscription.dispose());

        return emitter;
    }

}
