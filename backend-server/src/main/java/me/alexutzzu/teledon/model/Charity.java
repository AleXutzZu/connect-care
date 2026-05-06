package me.alexutzzu.teledon.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private List<Donation> donations = new ArrayList<>();

    public static Charity ofName(String name) {
        return Charity.builder().name(name).donations(Collections.emptyList()).build();
    }
}
