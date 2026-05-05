package me.alexutzzu.teledon.controller;

import me.alexutzzu.teledon.controller.dto.CreateCharityRequest;
import me.alexutzzu.teledon.controller.dto.UpdateCharityRequest;
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
    public ResponseEntity<CharityDto> createCharity(@RequestBody CreateCharityRequest createCharityRequest) {
        var entity = charityService.createCharity(createCharityRequest.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(entity);
    }

    @PutMapping("/{charityId}")
    public ResponseEntity<CharityDto> updateCharity(@PathVariable Long charityId, @RequestBody UpdateCharityRequest updateCharityRequest) {
        var entity = charityService.updateCharity(charityId, updateCharityRequest.name());
        return ResponseEntity.status(HttpStatus.OK).body(entity);
    }

    @DeleteMapping("/{charityId}")
    public ResponseEntity<?> deleteCharity(@PathVariable Long charityId) {
        charityService.deleteCharity(charityId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
