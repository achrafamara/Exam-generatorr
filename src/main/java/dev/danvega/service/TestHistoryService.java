package dev.danvega.service;

import dev.danvega.model.TestHistory;
import dev.danvega.model.TestHistoryDTO;
import dev.danvega.model.User;
import dev.danvega.repository.TestHistoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestHistoryService {

    private final TestHistoryRepository testHistoryRepository;

    public TestHistoryService(TestHistoryRepository testHistoryRepository) {
        this.testHistoryRepository = testHistoryRepository;
    }

    @Transactional
    public void saveTestHistory(User user, String testName, int score, boolean passed) {
        try {
            System.out.println("=== Sauvegarde des résultats ===");
            System.out.println("Utilisateur : " + user.getUsername());
            System.out.println("Test Name : " + testName);
            System.out.println("Score : " + score);
            System.out.println("Passed : " + passed);

            TestHistory testHistory = new TestHistory();
            testHistory.setUser(user);
            testHistory.setTestName(testName);
            testHistory.setScore(score); // Score en entier
            testHistory.setPassed(passed);
            testHistory.setTestDate(LocalDate.now());

            testHistoryRepository.save(testHistory);
            System.out.println("=== Résultats sauvegardés avec succès ===");
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
            throw e;
        }
    }

    public List<TestHistoryDTO> getTestHistoryByUserId(Long userId) {
        List<TestHistory> testHistories = testHistoryRepository.findByUserId(userId);

        // Convert `TestHistory` entities to `TestHistoryDTO` objects
        return testHistories.stream()
                .map(history -> new TestHistoryDTO(
                        history.getId(),
                        history.getTestName(),
                        history.getScore(), // Convert score to String with "%"
                        history.isPassed(),
                        history.getTestDate()
                ))
                .collect(Collectors.toList());
    }

    public List<TestHistoryDTO> getTestHistoryDTOByUserId(Long userId) {
        List<TestHistory> testHistories = testHistoryRepository.findByUserId(userId);

        return testHistories.stream()
                .map(history -> new TestHistoryDTO(
                        history.getId(),
                        history.getTestName(),
                        history.getScore(),
                        history.isPassed(),
                        history.getTestDate()
                ))
                .collect(Collectors.toList());
    }
}
