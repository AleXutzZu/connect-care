package me.alexutzzu.teledon.model;

public record Donation(Long id, Charity charity, Donor donor, Double amount) {
}
