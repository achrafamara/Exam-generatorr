package dev.danvega.controller;

import dev.danvega.model.TestHistory;
import dev.danvega.model.TestHistoryDTO;
import dev.danvega.model.User;
import dev.danvega.service.AnswerVerificationService;
import dev.danvega.service.TestHistoryService;
import dev.danvega.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/exam")
public class AnswerController {

    private final AnswerVerificationService verificationService;
    private final UserService userService;
    private final TestHistoryService testHistoryService;
    private final ObjectMapper objectMapper;

    public AnswerController(
            AnswerVerificationService verificationService,
            UserService userService,
            TestHistoryService testHistoryService,
            ObjectMapper objectMapper) {
        this.verificationService = verificationService;
        this.userService = userService;
        this.testHistoryService = testHistoryService;
        this.objectMapper = objectMapper;
    }

    /**
     * Vérification des réponses d'un utilisateur pour un examen
     */
    @PostMapping("/verify-answers")
    public ResponseEntity<Object> verifyAnswers(@RequestBody List<Map<String, Object>> userAnswers) {
        try {
            System.out.println("DEBUG: Réponses utilisateur reçues : " + userAnswers);

            // Appelle le service pour vérifier les réponses
            List<Map<String, Object>> results = verificationService.verifyUserAnswers(userAnswers);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            System.err.println("ERREUR: Impossible de vérifier les réponses. " + e.getMessage());
            return ResponseEntity.badRequest().body("Failed to verify answers.");
        }
    }
    @GetMapping("/test-debug")
    public ResponseEntity<Object> testDebug() {
        try {
            List<Map<String, Object>> testAnswers = List.of(
                    Map.of("question", "Question 1", "isCorrect", true),
                    Map.of("question", "Question 2", "isCorrect", false),
                    Map.of("question", "Question 3", "isCorrect", true)
            );

            int totalQuestions = testAnswers.size();
            int correctAnswers = (int) testAnswers.stream()
                    .filter(answer -> (boolean) answer.get("isCorrect"))
                    .count();

            int numericScore = (int) ((correctAnswers / (double) totalQuestions) * 100);
            String score = numericScore + "%";
            boolean passed = numericScore >= 50;

            return ResponseEntity.ok(Map.of(
                    "score", score,
                    "passed", passed
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erreur lors du test.");
        }
    }


    /**
     * Préparer le prompt pour le modèle de langage (LLM)
     */
    private String prepareVerificationPrompt(List<Map<String, Object>> userAnswers) {
        // Implémentez la logique de génération du prompt ici
        return "Generated prompt based on user answers";
    }

    /**
     * Nettoyer et traiter la réponse brute reçue du LLM
     */
    private String sanitizeResponse(String response) {
        // Implémentez la logique pour nettoyer la réponse brute ici
        return response.trim();
    }

    /**
     * Récupérer l'historique des tests d'un utilisateur
     */
    @GetMapping("/history/{username}")
    public ResponseEntity<Object> getTestHistory(@PathVariable String username) {
        try {
            // Retrieve the user by username
            User user = userService.getUserByUsername(username);

            // Retrieve the test history and convert it to DTOs
            List<TestHistoryDTO> history = testHistoryService.getTestHistoryDTOByUserId(user.getId());

            return ResponseEntity.ok(history);
        } catch (UsernameNotFoundException e) {
            System.err.println("ERREUR: Utilisateur introuvable : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("ERREUR: Impossible de récupérer l'historique : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving history: " + e.getMessage());
        }
    }

    @PostMapping("/submit-answers")
    public ResponseEntity<Object> submitAnswers(
            @RequestBody List<Map<String, Object>> userAnswers,
            @RequestParam String testFileName) {
        try {
            System.out.println("=== Données reçues ===");
            System.out.println("UserAnswers: " + userAnswers);

            // Récupérer l'utilisateur connecté
            User currentUser = userService.getCurrentUser();

            // Vérifier les réponses
            List<Map<String, Object>> results = verificationService.verifyUserAnswers(userAnswers);
            System.out.println("=== Résultats vérifiés ===");
            System.out.println(results);

            // Calcul du score
            int totalQuestions = results.size();
            int correctAnswers = (int) results.stream()
                    .filter(result -> (boolean) result.get("isCorrect"))
                    .count();
            System.out.println("Total Questions: " + totalQuestions);
            System.out.println("Correct Answers: " + correctAnswers);

            // Calcul du score numérique
            int numericScore = (int) ((correctAnswers / (double) totalQuestions) * 100);
            System.out.println("Numeric Score: " + numericScore);

            // Déterminer si l'utilisateur a réussi
            boolean passed = numericScore >= 50;
            System.out.println("Passed: " + passed);

            // Sauvegarde des résultats
            testHistoryService.saveTestHistory(currentUser, testFileName, numericScore, passed);

            // Retourner les résultats
            return ResponseEntity.ok(Map.of(
                    "score", numericScore,
                    "passed", passed,
                    "results", results
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erreur lors de la soumission des réponses.");
        }
    }

    @GetMapping("/test-save")
    public ResponseEntity<Object> testSave() {
        User user = userService.getCurrentUser();
        testHistoryService.saveTestHistory(user, "Test Dynamique", 85, true);
        return ResponseEntity.ok("Données test sauvegardées !");
    }
    @PostMapping("/save")
    public ResponseEntity<Object> saveTestResults(@RequestBody TestHistoryDTO testHistoryDTO) {
        try {
            System.out.println("=== Données reçues pour enregistrement ===");
            System.out.println("Test Name: " + testHistoryDTO.getTestName());
            System.out.println("Score: " + testHistoryDTO.getScore());
            System.out.println("Passed: " + testHistoryDTO.isPassed());

            User currentUser = userService.getCurrentUser();

            // Utilisez directement le score en tant qu'entier
            int rawScore = testHistoryDTO.getScore();

            // Déterminez si l'utilisateur a réussi
            boolean passed = rawScore >= 50;

            // Sauvegarde dans la base de données
            testHistoryService.saveTestHistory(
                    currentUser,
                    testHistoryDTO.getTestName(),
                    rawScore, // Utilisez l'entier directement
                    passed
            );

            return ResponseEntity.ok("Test enregistré avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'enregistrement des résultats du test.");
        }
    }

    @GetMapping("/user/history")
    public ResponseEntity<List<TestHistoryDTO>> getUserTestHistory() {
        try {
            User currentUser = userService.getCurrentUser();
            // Fetch the list of DTOs from the service
            List<TestHistoryDTO> testHistories = testHistoryService.getTestHistoryDTOByUserId(currentUser.getId());
            return ResponseEntity.ok(testHistories);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

}
