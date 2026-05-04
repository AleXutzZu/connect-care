package me.alexutzzu.teledon.service;

import me.alexutzzu.teledon.persistence.DonorRepository;
import org.springframework.stereotype.Service;

@Service
public class DonorService {
    private final DonorRepository donorRepository;

    public DonorService(DonorRepository donorRepository) {
        this.donorRepository = donorRepository;
    }

}
