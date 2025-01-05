package dev.danvega.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "test_history")
public class TestHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // L'utilisateur ayant effectué le test

    @Column(name = "test_name", nullable = false)
    private String testName;

    // === MODIFICATION : le type de "score" a été changé de String à int ===
    @Column(name = "score", nullable = false)
    private int score; // Conserver comme entier

    @Column(name = "passed", nullable = false)
    private boolean passed;

    @Column(name = "test_date", nullable = false)
    private LocalDate testDate;

    @Override
    public String toString() {
        return "TestHistory{" +
                "id=" + id +
                ", testName='" + testName + '\'' +
                ", score=" + score + // Mise à jour pour afficher l'entier
                ", passed=" + passed +
                ", testDate=" + testDate +
                '}';
    }
}
