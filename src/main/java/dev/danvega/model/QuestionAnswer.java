package dev.danvega.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "question_answer")
public class QuestionAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_history_id", nullable = false)
    private TestHistory testHistory; // Associe cette question à un test spécifique

    @Column(name = "question", nullable = false)
    private String question;

    @Column(name = "user_answer", nullable = false)
    private String userAnswer;

    @Column(name = "correct_answer", nullable = false)
    private String correctAnswer;

    @Column(name = "is_correct", nullable = false)
    private boolean isCorrect;

    @Override
    public String toString() {
        return "QuestionAnswer{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", userAnswer='" + userAnswer + '\'' +
                ", correctAnswer='" + correctAnswer + '\'' +
                ", isCorrect=" + isCorrect +
                '}';
    }
}