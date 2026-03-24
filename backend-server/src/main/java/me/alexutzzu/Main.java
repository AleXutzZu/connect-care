package me.alexutzzu;

import me.alexutzzu.teledon.model.Volunteer;
import me.alexutzzu.teledon.persistence.CharityRepository;
import me.alexutzzu.teledon.persistence.DonationRepository;
import me.alexutzzu.teledon.persistence.DonorRepository;
import me.alexutzzu.teledon.persistence.VolunteerRepository;
import me.alexutzzu.teledon.persistence.database.DatabaseManager;
import me.alexutzzu.teledon.persistence.impl.JdbcCharityRepositoryImpl;
import me.alexutzzu.teledon.persistence.impl.JdbcDonationRepositoryImpl;
import me.alexutzzu.teledon.persistence.impl.JdbcDonorRepositoryImpl;
import me.alexutzzu.teledon.persistence.impl.JdbcVolunteerRepositoryImpl;

public class Main {
    public static void main(String[] args) {
        try {
            CharityRepository charityRepository = DatabaseManager.getRepositoryInstance(CharityRepository.class, JdbcCharityRepositoryImpl.class);
            DonationRepository donationRepository = DatabaseManager.getRepositoryInstance(DonationRepository.class, JdbcDonationRepositoryImpl.class);
            DonorRepository donorRepository = DatabaseManager.getRepositoryInstance(DonorRepository.class, JdbcDonorRepositoryImpl.class);
            VolunteerRepository volunteerRepository = DatabaseManager.getRepositoryInstance(VolunteerRepository.class, JdbcVolunteerRepositoryImpl.class);

            var result = volunteerRepository.create(new Volunteer(null, "Alex", "password"));
            System.out.println(result);

            result = volunteerRepository.update(new Volunteer(result.id(), "Matei", "password")).orElseThrow();
            System.out.println(result);

//        volunteerRepository.deleteById(result.id());

            System.out.println(volunteerRepository.findById(result.id()));
        } catch (Exception e) {
            System.err.println("Exception occurred whilst running main.");
        }

    }
}