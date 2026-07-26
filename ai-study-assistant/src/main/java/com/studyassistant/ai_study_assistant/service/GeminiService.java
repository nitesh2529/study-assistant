package com.studyassistant.ai_study_assistant.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.studyassistant.ai_study_assistant.exception.GeminiApiException;

/**
 * Service responsible for communicating with the Google Gemini API
 * to generate AI answers for user-submitted study questions.
 * <p>
 * Uses a reactive {@link WebClient} to call the Gemini
 * {@code generateContent} endpoint and extracts the plain-text
 * answer from the model's structured response.
 */
@Service
public class GeminiService {

    private final WebClient webClient;
    private final String apiKey;

    /**
     * Constructs the service with a dedicated WebClient configured for
     * the Gemini API base URL, and the API key used to authenticate calls.
     *
     * @param apiUrl the full Gemini generateContent endpoint URL
     * @param apiKey the Gemini API key
     */
    public GeminiService(@Value("${gemini.api.url}") String apiUrl,
                          @Value("${gemini.api.key}") String apiKey) {
                            System.out.println("GeminiService initialized with API URL: " + apiUrl + " and API Key: " + apiKey);
        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
        this.apiKey = apiKey;
    }

    /**
     * Sends a question to the Gemini API and returns the generated answer.
     *
     * @param question the user's study question / prompt
     * @return the AI-generated answer as plain text
     * @throws GeminiApiException if the Gemini API call fails or returns
     *                            an unexpected/empty response
     */
    public String getAnswer(String question) {
        System.out.println("Sending question to Gemini API form gemini: " + question);
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", question)
                        ))
                )
        );

        try {
            Map<String, Object> response = webClient.post()
                    .uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return extractAnswerText(response);

        } catch (Exception ex) {
            throw new GeminiApiException("Failed to get a response from Gemini API: " + ex.getMessage(), ex);
        }
    }

    /**
     * Navigates the Gemini response JSON structure to extract the
     * generated answer text.
     * <p>
     * Expected shape: {@code candidates[0].content.parts[0].text}
     *
     * @param response the parsed Gemini API response body
     * @return the extracted answer text
     * @throws GeminiApiException if the response is empty or malformed
     */
    @SuppressWarnings("unchecked")
    private String extractAnswerText(Map<String, Object> response) {
        if (response == null) {
            throw new GeminiApiException("Gemini API returned an empty response");
        }

        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            return (String) parts.get(0).get("text");
        } catch (Exception ex) {
            throw new GeminiApiException("Unexpected response structure from Gemini API", ex);
        }
    }

}
