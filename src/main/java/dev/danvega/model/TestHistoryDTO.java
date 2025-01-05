package dev.danvega.model;

import java.time.LocalDate;

public class TestHistoryDTO {
    private Long id;
    private String testName;
    private int score; // Format comme entier pour cohérence
    private boolean passed;
    private LocalDate testDate;

    // Constructeur par défaut requis pour la désérialisation JSON
    public TestHistoryDTO() {
    }

    // Constructeur avec tous les champs
    public TestHistoryDTO(Long id, String testName, int score, boolean passed, LocalDate testDate) {
        this.id = id;
        this.testName = testName;
        this.score = score;
        this.passed = passed;
        this.testDate = testDate;
    }

    // Getters et setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public LocalDate getTestDate() {
        return testDate;
    }

    public void setTestDate(LocalDate testDate) {
        this.testDate = testDate;
    }

    @Override
    public String toString() {
        return "TestHistoryDTO{" +
                "id=" + id +
                ", testName='" + testName + '\'' +
                ", score=" + score +
                ", passed=" + passed +
                ", testDate=" + testDate +
                '}';
    }
}
