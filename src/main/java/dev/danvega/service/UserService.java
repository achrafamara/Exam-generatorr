package dev.danvega.service;

import dev.danvega.model.User;
import dev.danvega.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Chargement d'un utilisateur par son nom d'utilisateur pour Spring Security.
     *
     * @param username Nom d'utilisateur
     * @return UserDetails Objet utilisé par Spring Security pour l'authentification
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + username));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    /**
     * Enregistre un nouvel utilisateur dans la base de données.
     *
     * @param username Nom d'utilisateur
     * @param rawPassword Mot de passe en clair (sera encodé avant la sauvegarde)
     * @return true si l'utilisateur est enregistré avec succès, false sinon
     */
    public boolean registerUser(String username, String rawPassword) {
        if (userRepository.findByUsername(username).isPresent()) {
            return false; // L'utilisateur existe déjà
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword)); // Encode le mot de passe avant de le sauvegarder
        userRepository.save(user);
        return true; // Inscription réussie
    }

    /**
     * Récupère un utilisateur par son nom d'utilisateur.
     *
     * @param username Nom d'utilisateur
     * @return L'utilisateur trouvé
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable avec le nom : " + username));
    }

    /**
     * Récupère l'utilisateur actuellement connecté.
     *
     * @return L'utilisateur actuellement connecté
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + username));
        }
        throw new RuntimeException("Aucun utilisateur connecté trouvé.");
    }
}