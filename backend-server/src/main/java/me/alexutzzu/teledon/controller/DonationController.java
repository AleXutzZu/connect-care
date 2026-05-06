package me.alexutzzu.teledon.controller;

import jakarta.validation.Valid;
import me.alexutzzu.teledon.controller.dto.CreateDonationRequest;
import me.alexutzzu.teledon.model.dto.DonationDto;
import me.alexutzzu.teledon.service.DonationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donations")
public class DonationController {
    private final DonationService donationService;

    public DonationController(DonationService donationService) {
        this.donationService = donationService;
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
        return ResponseEntity.status(HttpStatus.CREATED).body(entity);
    }

    @PutMapping("/{donationId}")
    public ResponseEntity<DonationDto> updateDonation(@PathVariable Long donationId, @RequestBody @Valid CreateDonationRequest body) {
        var entity = donationService.updateDonation(donationId, body.charityId(), body.charityId(), body.amount());
        return ResponseEntity.ok(entity);
    }

    @DeleteMapping("/{donationId}")
    public ResponseEntity<?> deleteDonation(@PathVariable Long donationId) {
        donationService.deleteDonation(donationId);
        return ResponseEntity.noContent().build();
    }
}
