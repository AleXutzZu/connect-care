package me.alexutzzu.teledon.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(schema = "public", name = "donor")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Donor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "firstname", nullable = false)
    private String firstName;

    @Column(name = "lastname", nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String address;

    @Column(name = "phonenumber", nullable = false, unique = true)
    private String phoneNumber;

    @OneToMany(mappedBy = "donor")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Donation> donations = new ArrayList<>();

    public static Donor ofDetails(String firstName, String lastName, String address, String phoneNumber) {
        return Donor.builder()
                .firstName(firstName)
                .lastName(lastName)
                .address(address)
                .phoneNumber(phoneNumber)
                .donations(Collections.emptyList())
                .build();
    }
}
