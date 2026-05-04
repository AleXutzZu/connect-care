package me.alexutzzu.teledon.controller;

import me.alexutzzu.teledon.service.CharityService;
import org.springframework.stereotype.Controller;

@Controller("/api/charities")
public class CharityController {
    private final CharityService charityService;

    public CharityController(CharityService charityService) {
        this.charityService = charityService;
    }


}
