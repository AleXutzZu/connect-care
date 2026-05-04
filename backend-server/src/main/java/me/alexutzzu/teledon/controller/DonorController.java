package me.alexutzzu.teledon.controller;

import me.alexutzzu.teledon.service.DonorService;
import org.springframework.stereotype.Controller;

@Controller("/api/donors")
public class DonorController {

    private final DonorService donorService;

    public DonorController(DonorService donorService) {
        this.donorService = donorService;
    }


}
