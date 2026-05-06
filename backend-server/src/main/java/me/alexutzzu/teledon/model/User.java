package me.alexutzzu.teledon.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "public", name = "authuser")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
}
