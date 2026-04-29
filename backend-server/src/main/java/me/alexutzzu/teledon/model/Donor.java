package me.alexutzzu.teledon.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(name = "phonenumber", nullable = false)
    private String phoneNumber;

    @OneToMany(mappedBy = "donor")
    private List<Donation> donations;

    public static Donor ofDetails(String firstName, String lastName, String address, String phoneNumber) {
        return Donor.builder()
                .firstName(firstName)
                .lastName(lastName)
                .address(address)
                .phoneNumber(phoneNumber)
                .build();
    }
}
