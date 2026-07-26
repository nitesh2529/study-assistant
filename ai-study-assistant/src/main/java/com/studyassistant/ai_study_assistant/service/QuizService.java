package com.studyassistant.ai_study_assistant.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyassistant.ai_study_assistant.dto.QuizResponse;
import com.studyassistant.ai_study_assistant.exception.QuizGenerationException;
import com.studyassistant.ai_study_assistant.exception.ResourceNotFoundException;
import com.studyassistant.ai_study_assistant.model.Note;
import com.studyassistant.ai_study_assistant.model.Quiz;
import com.studyassistant.ai_study_assistant.repository.NoteRepository;
import com.studyassistant.ai_study_assistant.repository.QuizRepository;

/**
 * Service layer handling AI-generated quizzes derived from study notes.
 * <p>
 * Coordinates {@link NoteRepository} to fetch source content,
 * {@link GeminiService} to generate structured quiz JSON, and
 * {@link QuizRepository} to persist and retrieve results.
 */
@Service
public class QuizService {

    private static final String QUIZ_PROMPT_PREFIX = """
            Based on the following study notes, generate a multiple-choice quiz \
            with  5 number of question that cover every topic of note. Each question must have exactly 4 options, \
            and one correct answer that exactly matches one of the options.

            Respond with ONLY valid JSON, no markdown code fences, no preamble, \
            no explanation - just the raw JSON in this exact structure:
            {
              "questions": [
                {
                  "questionText": "string",
                  "options": ["string", "string", "string", "string"],
                  "correctAnswer": "string"
                }
              ]
            }

            Study notes:
            """;

    private final QuizRepository quizRepository;
    private final NoteRepository noteRepository;
    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;

    /**
     * Constructs the service with its required collaborators via
     * constructor injection.
     *
     * @param quizRepository repository for persisting/retrieving quizzes
     * @param noteRepository repository for fetching the source note's content
     * @param geminiService  service that calls the Gemini API for generation
     */
    public QuizService(QuizRepository quizRepository,
                        NoteRepository noteRepository,
                        GeminiService geminiService) {
        this.quizRepository = quizRepository;
        this.noteRepository = noteRepository;
        this.geminiService = geminiService;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Generates a new quiz for the given note: verifies ownership, prompts
     * Gemini for structured JSON quiz content, parses the response into
     * {@link Quiz.QuizQuestion} objects, persists the result, and returns it.
     *
     * @param userId the id of the authenticated user requesting the quiz
     * @param noteId the id of the note to generate a quiz from
     * @return the newly generated and persisted quiz
     * @throws ResourceNotFoundException if no matching note exists for this user
     * @throws QuizGenerationException   if Gemini's response cannot be parsed as valid quiz JSON
     */
    public QuizResponse generateQuiz(String userId, String noteId) {
        Note note = noteRepository.findById(noteId)
                .filter(n -> n.getUserId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Note not found: " + noteId));

        String prompt = QUIZ_PROMPT_PREFIX + note.getContent();
        String rawResponse = geminiService.getAnswer(prompt);

        List<Quiz.QuizQuestion> questions = parseQuizJson(rawResponse);

        Quiz quiz = Quiz.builder()
                .userId(userId)
                .noteId(noteId)
                .questions(questions)
                .build();

        Quiz saved = quizRepository.save(quiz);

        return toResponse(saved);
    }

    /**
     * Retrieves the most recently generated quiz for a note, if one exists.
     *
     * @param userId the id of the authenticated user
     * @param noteId the id of the note whose quiz is requested
     * @return the latest quiz for that note
     * @throws ResourceNotFoundException if the note doesn't belong to the user,
     *                                    or no quiz has been generated yet
     */
    public QuizResponse getLatestQuiz(String userId, String noteId) {
        verifyNoteOwnership(userId, noteId);

        Quiz quiz = quizRepository.findFirstByNoteIdOrderByCreatedAtDesc(noteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No quiz has been generated yet for note: " + noteId));

        return toResponse(quiz);
    }

    /**
     * Retrieves all quizzes ever generated for a note, most recent first.
     *
     * @param userId the id of the authenticated user
     * @param noteId the id of the note whose quiz history is requested
     * @return a list of all quizzes generated for that note
     */
    public List<QuizResponse> getQuizHistory(String userId, String noteId) {
        verifyNoteOwnership(userId, noteId);

        return quizRepository.findByNoteId(noteId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Parses Gemini's raw text response into a list of QuizQuestion objects.
     * Strips markdown code fences defensively, since models sometimes wrap
     * JSON in ```json ... ``` blocks despite instructions not to.
     *
     * @param rawResponse the raw text returned by GeminiService
     * @return the parsed list of quiz questions
     * @throws QuizGenerationException if the response is not valid, well-formed quiz JSON
     */
    private List<Quiz.QuizQuestion> parseQuizJson(String rawResponse) {
        String cleaned = rawResponse
                .replaceAll("(?s)```json", "")
                .replaceAll("(?s)```", "")
                .trim();

        try {
            QuizJsonWrapper wrapper = objectMapper.readValue(cleaned, QuizJsonWrapper.class);

            if (wrapper.questions == null || wrapper.questions.isEmpty()) {
                throw new QuizGenerationException("Gemini returned no quiz questions");
            }

            return wrapper.questions;

        } catch (Exception ex) {
            throw new QuizGenerationException("Failed to parse Gemini's quiz response as valid JSON: " + ex.getMessage());
        }
    }

    /**
     * Confirms the given note exists and belongs to the given user.
     *
     * @param userId the id of the authenticated user
     * @param noteId the id of the note to verify
     * @throws ResourceNotFoundException if no matching note exists for this user
     */
    private void verifyNoteOwnership(String userId, String noteId) {
        noteRepository.findById(noteId)
                .filter(n -> n.getUserId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Note not found: " + noteId));
    }

    /**
     * Maps a persisted Quiz document to its API-facing QuizResponse DTO.
     *
     * @param quiz the persisted quiz
     * @return the corresponding QuizResponse
     */
    private QuizResponse toResponse(Quiz quiz) {
        return QuizResponse.builder()
                .id(quiz.getId())
                .noteId(quiz.getNoteId())
                .questions(quiz.getQuestions())
                .createdAt(quiz.getCreatedAt())
                .build();
    }

    /**
     * Private helper class matching the expected top-level JSON shape
     * Gemini is instructed to return: {@code { "questions": [...] } }.
     * Used only as a Jackson deserialization target - never exposed
     * outside this class.
     */
    private static class QuizJsonWrapper {
        public List<Quiz.QuizQuestion> questions;
    }

}
