package me.alexutzzu.teledon.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "public", name = "donation")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "charityid", nullable = false)
    private Charity charity;

    @ManyToOne
    @JoinColumn(name = "donorid", nullable = false)
    private Donor donor;

    @Column(nullable = false)
    private Double amount;


    public static Donation ofDetails(Charity charity, Donor donor, double amount) {
        return Donation.builder().charity(charity).donor(donor).amount(amount).build();
    }
}
