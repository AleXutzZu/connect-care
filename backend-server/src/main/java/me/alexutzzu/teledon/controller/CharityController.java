package me.alexutzzu.teledon.controller;

import jakarta.validation.Valid;
import me.alexutzzu.teledon.controller.dto.CreateCharityRequest;
import me.alexutzzu.teledon.model.dto.CharityDto;
import me.alexutzzu.teledon.model.dto.CharityWithRaisedSum;
import me.alexutzzu.teledon.service.CharityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/charities")
public class CharityController {
    private final CharityService charityService;

    public CharityController(CharityService charityService) {
        this.charityService = charityService;
    }

    @GetMapping
    public List<CharityWithRaisedSum> getAllCharities() {
        return charityService.getAllCharities();
    }

    @GetMapping("/{charityId}")
    public CharityDto getCharity(@PathVariable Long charityId) {
        return charityService.getCharity(charityId);
    }

    @PostMapping
    public ResponseEntity<CharityDto> createCharity(@RequestBody @Valid CreateCharityRequest body) {
        var entity = charityService.createCharity(body.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(entity);
    }

    @PutMapping("/{charityId}")
    public ResponseEntity<CharityDto> updateCharity(@PathVariable Long charityId, @RequestBody @Valid CreateCharityRequest body) {
        var entity = charityService.updateCharity(charityId, body.name());
        return ResponseEntity.ok(entity);
    }

    @DeleteMapping("/{charityId}")
    public ResponseEntity<?> deleteCharity(@PathVariable Long charityId) {
        charityService.deleteCharity(charityId);
        return ResponseEntity.noContent().build();
    }
}
