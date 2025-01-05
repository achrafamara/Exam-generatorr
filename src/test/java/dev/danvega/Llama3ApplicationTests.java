package dev.danvega;

import dev.danvega.model.TestHistory;
import dev.danvega.model.TestHistoryDTO;
import dev.danvega.model.User;
import dev.danvega.repository.UserRepository;
import dev.danvega.service.TestHistoryService;
import dev.danvega.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

@SpringBootTest
public class Llama3ApplicationTests {

	@Autowired
	TestHistoryService testHistoryService;
	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	public void setup() {
		// Supprimez tout utilisateur existant
		userRepository.deleteAll();

		// Ajoutez un utilisateur test dans la base de données
		User testUser = new User();
		testUser.setUsername("testuser");
		testUser.setPassword("password"); // Vous pouvez encoder le mot de passe si nécessaire
		userRepository.save(testUser);

		System.out.println("Utilisateur 'testuser' ajouté avec succès !");
	}

	@Test
	@WithMockUser(username = "testuser", roles = "USER")
	public void testSaveTestHistory() {
		// Simuler un utilisateur connecté
		User user = userService.getCurrentUser();

		// Appeler la méthode de sauvegarde
		testHistoryService.saveTestHistory(user, "Test 1", 85, true);

		// Vérifier que l'historique est enregistré
		List<TestHistoryDTO> history = testHistoryService.getTestHistoryByUserId(user.getId());
		Assertions.assertFalse(history.isEmpty());
		Assertions.assertEquals("Test 1", history.get(0).getTestName());
	}
}
