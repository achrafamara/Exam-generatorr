package dev.danvega.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ExamService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public ExamService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
        this.objectMapper = new ObjectMapper();
    }

    // Étape 1: Extraction propre du texte PDF
    public String extractTextFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);
            System.out.println("INFO: Successfully extracted text from PDF. Content size: " + text.length() + " characters.");
            return cleanExtractedText(text);
        } catch (IOException e) {
            System.out.println("ERROR: Failed to extract text from PDF. " + e.getMessage());
            throw e;
        }
    }

    // Étape 2: Nettoyage intelligent du texte extrait
    private String cleanExtractedText(String text) {
        System.out.println("INFO: Cleaning extracted text...");
        // Suppression des imports, commentaires et lignes non pertinentes
        return text.replaceAll("(?m)^\\s*import .*;", "")  // Supprime les imports
                .replaceAll("(?m)^\\s*//.*", "")       // Supprime les commentaires sur une ligne
                .replaceAll("(?m)^\\s*/\\*.*?\\*/", "") // Supprime les blocs de commentaires
                .replaceAll("(?m)^\\s*@\\w+.*", "")     // Supprime les annotations comme @Service, @Autowired
                .replaceAll("\\{.*?}", "")             // Supprime les blocs de code éventuels
                .replaceAll("\\s+", " ")               // Réduit les espaces multiples
                .trim();
    }

    // Étape 3: Préparation du prompt pour le LLM
    public String preparePrompt(String courseContent) {
        System.out.println("INFO: Preparing prompt for the LLM...");
        return """
            You are tasked to generate a practice exam based on the provided course content. 
            Generate between 15 and 20 multiple-choice questions (QCM) in the following strict JSON format:
            [
                {
                    "question": "Your question text here",
                    "choices": ["Choice 1", "Choice 2", "Choice 3", "Choice 4"]
                }
            ] 
            last element should not end with virgule (,) for a valid json response
            Do not add explanations or any additional text. Here is the course content:
            """ + courseContent;
    }

    // Étape 4: Appel au LLM pour générer les questions
    public String callLLM(String prompt) {
        try {
            System.out.println("INFO: Sending prompt to LLM...");
            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
            System.out.println("INFO: Received response from LLM.");
            return response;
        } catch (Exception e) {
            System.out.println("ERROR: LLM call failed. " + e.getMessage());
            throw new RuntimeException("Failed to generate questions from LLM.");
        }
    }

    // Étape 5: Nettoyage et validation stricte de la réponse JSON
    public JsonNode sanitizeAndValidateResponse(String response) throws IOException {
        System.out.println("DEBUG: Raw response before sanitization: " + response);

        // Extraction de la portion JSON uniquement
        if (response.contains("[")) {
            int startIndex = response.indexOf("[");
            int endIndex = response.lastIndexOf("]");
            if (startIndex != -1 && endIndex != -1) {
                response = response.substring(startIndex, endIndex + 1).trim();
            }
        }

        System.out.println("DEBUG: Sanitized response: " + response);
        try {
            return objectMapper.readTree(response); // Valide et parse la réponse JSON
        } catch (Exception e) {
            System.out.println("ERROR: Invalid JSON response from LLM. " + e.getMessage());
            throw new IOException("Failed to parse LLM response as JSON.");
        }
    }

    // Étape 6: Service principal pour générer l'examen à partir du PDF
    public JsonNode generateExamFromPdf(MultipartFile file) throws IOException {
        // 1. Extraction propre du contenu PDF
        String courseContent = extractTextFromPdf(file);

        // 2. Préparation dynamique du prompt
        String prompt = preparePrompt(courseContent);

        // 3. Appel au LLM
        String rawResponse = callLLM(prompt);

        // 4. Nettoyage et validation de la réponse JSON
        return sanitizeAndValidateResponse(rawResponse);
    }
}
