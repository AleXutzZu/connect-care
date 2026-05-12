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

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Double target;

    private String cause;

    @OneToMany(mappedBy = "charity")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Donation> donations = new ArrayList<>();

    public static Charity of(String name, User user, Double target, String cause) {
        return Charity.builder()
                .name(name)
                .user(user)
                .target(target)
                .cause(cause)
                .donations(Collections.emptyList())
                .build();
    }
}
