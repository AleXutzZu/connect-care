package me.alexutzzu.teledon.controller;

import jakarta.validation.Valid;
import me.alexutzzu.teledon.controller.dto.CreateDonorRequest;
import me.alexutzzu.teledon.controller.dto.PaginationParams;
import me.alexutzzu.teledon.model.dto.DonorDto;
import me.alexutzzu.teledon.model.dto.DonorWithoutDonations;
import me.alexutzzu.teledon.service.DonorService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donors")
public class DonorController {

    private final DonorService donorService;

    public DonorController(DonorService donorService) {
        this.donorService = donorService;
    }

    @GetMapping
    public ResponseEntity<?> getAllDonors(
            @Valid PaginationParams paginationParams,
            @RequestParam(required = false) String search
    ) {
        if (paginationParams.page() != null && paginationParams.size() != null) {
            Page<DonorWithoutDonations> donors = donorService.getAllDonors(paginationParams.page(), paginationParams.size(), search);
            return ResponseEntity.ok(donors);
        } else {
            List<DonorWithoutDonations> donors = donorService.getAllDonors(search);
            return ResponseEntity.ok(donors);
        }
    }

    @GetMapping("/{donorId}")
    public DonorDto getDonor(@PathVariable Long donorId) {
        return donorService.getDonor(donorId);
    }

    @PostMapping
    public ResponseEntity<DonorDto> createDonor(@RequestBody @Valid CreateDonorRequest body) {
        var entity = donorService.createDonor(body.firstName(), body.lastName(), body.address(), body.phoneNumber());
        return ResponseEntity.status(HttpStatus.CREATED).body(entity);
    }

    @PutMapping("/{donorId}")
    public ResponseEntity<DonorDto> updateDonor(@PathVariable Long donorId, @RequestBody @Valid CreateDonorRequest body) {
        var entity = donorService.updateDonor(donorId, body.firstName(), body.lastName(), body.address(), body.phoneNumber());
        return ResponseEntity.ok(entity);
    }

    @DeleteMapping("/{donorId}")
    public ResponseEntity<?> deleteDonor(@PathVariable Long donorId) {
        donorService.deleteDonor(donorId);
        return ResponseEntity.noContent().build();
    }
}
