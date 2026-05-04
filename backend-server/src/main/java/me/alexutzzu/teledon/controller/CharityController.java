package me.alexutzzu.teledon.controller;

import me.alexutzzu.teledon.service.CharityService;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/charities")
public class CharityController {
    private final CharityService charityService;

    public CharityController(CharityService charityService) {
        this.charityService = charityService;
    }


}
