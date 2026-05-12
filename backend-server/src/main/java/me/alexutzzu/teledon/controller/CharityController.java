package me.alexutzzu.teledon.controller;

import jakarta.validation.Valid;
import me.alexutzzu.teledon.controller.dto.CreateCharityRequest;
import me.alexutzzu.teledon.controller.dto.PaginationParams;
import me.alexutzzu.teledon.model.dto.CharityDto;
import me.alexutzzu.teledon.model.dto.CharityWithRaisedSum;
import me.alexutzzu.teledon.service.CharityService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/charities")
public class CharityController {
    private final CharityService charityService;

    public CharityController(CharityService charityService) {
        this.charityService = charityService;
    }

    @GetMapping
    public Page<CharityWithRaisedSum> getAllCharities(@Valid PaginationParams paginationParams) {
        return charityService.getAllCharities(paginationParams.page(), paginationParams.size());
    }

    @GetMapping("/{charityId}")
    public CharityDto getCharity(@PathVariable Long charityId) {
        return charityService.getCharity(charityId);
    }

    @PostMapping
    public ResponseEntity<CharityDto> createCharity(@RequestBody @Valid CreateCharityRequest body, Authentication authentication) {
        var entity = charityService.createCharity(body, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(entity);
    }

    @PutMapping("/{charityId}")
    public ResponseEntity<CharityDto> updateCharity(@PathVariable Long charityId, @RequestBody @Valid CreateCharityRequest body) {
        var entity = charityService.updateCharity(charityId, body);
        return ResponseEntity.ok(entity);
    }

    @DeleteMapping("/{charityId}")
    public ResponseEntity<?> deleteCharity(@PathVariable Long charityId) {
        charityService.deleteCharity(charityId);
        return ResponseEntity.noContent().build();
    }
}
