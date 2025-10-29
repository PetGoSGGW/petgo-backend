package pl.petgo.backend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "breeds")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Breed {

    @Id
    @Column(length = 20)
    private String breedCode;

    @Column(nullable = false, unique = true)
    private String name;
}