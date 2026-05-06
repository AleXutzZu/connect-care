package me.alexutzzu.teledon.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@Entity
@Table(schema = "public", name = "charity")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Charity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "charity")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Donation> donations = new ArrayList<>();

    public static Charity ofName(String name) {
        return Charity.builder().name(name).donations(Collections.emptyList()).build();
    }
}
