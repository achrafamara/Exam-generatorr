package dev.danvega.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_answers")
public class UserAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_history_id", nullable = false)
    private TestHistory testHistory; // Lien avec l'historique du test

    @Column(name = "question", nullable = false)
    private String question;

    @ElementCollection
    @CollectionTable(name = "user_choices", joinColumns = @JoinColumn(name = "user_answer_id"))
    @Column(name = "choice")
    private List<String> userChoices; // Les choix sélectionnés par l'utilisateur
}