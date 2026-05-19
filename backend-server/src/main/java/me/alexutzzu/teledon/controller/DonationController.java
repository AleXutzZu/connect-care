package me.alexutzzu.teledon.controller;

import jakarta.validation.Valid;
import me.alexutzzu.teledon.controller.dto.CreateDonationRequest;
import me.alexutzzu.teledon.controller.dto.events.DonationEvent;
import me.alexutzzu.teledon.controller.dto.events.SseEvent;
import me.alexutzzu.teledon.model.dto.DonationDto;
import me.alexutzzu.teledon.service.DonationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Sinks;

import java.util.List;

@RestController
@RequestMapping("/api/donations")
public class DonationController {
    private final DonationService donationService;
    private final Sinks.Many<SseEvent<?>> eventSink;


    public DonationController(DonationService donationService, Sinks.Many<SseEvent<?>> eventSink) {
        this.donationService = donationService;
        this.eventSink = eventSink;
    }

    @GetMapping
    public List<DonationDto> getAllDonations() {
        return donationService.getAllDonations();
    }

    @GetMapping("/{donationId}")
    public DonationDto getDonation(@PathVariable Long donationId) {
        return donationService.getDonation(donationId);
    }

    @PostMapping
    public ResponseEntity<DonationDto> createDonation(@RequestBody @Valid CreateDonationRequest body) {
        var entity = donationService.createDonation(body.charityId(), body.donorId(), body.amount());
        eventSink.tryEmitNext(DonationEvent.created(entity));
        return ResponseEntity.status(HttpStatus.CREATED).body(entity);
    }

    @PutMapping("/{donationId}")
    public ResponseEntity<DonationDto> updateDonation(@PathVariable Long donationId, @RequestBody @Valid CreateDonationRequest body) {
        var entity = donationService.updateDonation(donationId, body.charityId(), body.charityId(), body.amount());
        eventSink.tryEmitNext(DonationEvent.updated(entity));
        return ResponseEntity.ok(entity);
    }

    @DeleteMapping("/{donationId}")
    public ResponseEntity<?> deleteDonation(@PathVariable Long donationId) {
        donationService.deleteDonation(donationId);
        eventSink.tryEmitNext(DonationEvent.deleted(donationId));
        return ResponseEntity.noContent().build();
    }
}
