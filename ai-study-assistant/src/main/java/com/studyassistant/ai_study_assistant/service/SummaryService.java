package com.studyassistant.ai_study_assistant.service;

import com.studyassistant.ai_study_assistant.dto.SummaryResponse;
import com.studyassistant.ai_study_assistant.exception.ResourceNotFoundException;
import com.studyassistant.ai_study_assistant.model.Note;
import com.studyassistant.ai_study_assistant.model.Summary;
import com.studyassistant.ai_study_assistant.repository.NoteRepository;
import com.studyassistant.ai_study_assistant.repository.SummaryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer handling AI-generated summaries of study notes.
 * <p>
 * Coordinates {@link NoteRepository} to fetch source content,
 * {@link GeminiService} to generate the summary text, and
 * {@link SummaryRepository} to persist and retrieve results.
 */
@Service
public class SummaryService {

    private static final String SUMMARY_PROMPT_PREFIX =
            "Summarize the following study notes into clear, concise key points "
            + "suitable for quick revision. Keep it well-structured and easy to read:\n\n";

    private final SummaryRepository summaryRepository;
    private final NoteRepository noteRepository;
    private final GeminiService geminiService;

    /**
     * Constructs the service with its required collaborators via
     * constructor injection.
     *
     * @param summaryRepository repository for persisting/retrieving summaries
     * @param noteRepository    repository for fetching the source note's content
     * @param geminiService     service that calls the Gemini API for generation
     */
    public SummaryService(SummaryRepository summaryRepository,
                           NoteRepository noteRepository,
                           GeminiService geminiService) {
        this.summaryRepository = summaryRepository;
        this.noteRepository = noteRepository;
        this.geminiService = geminiService;
    }

    /**
     * Generates a new summary for the given note: verifies ownership,
     * sends the note's content to Gemini with a summarization prompt,
     * persists the result, and returns it.
     *
     * @param userId the id of the authenticated user requesting the summary
     * @param noteId the id of the note to summarize
     * @return the newly generated and persisted summary
     * @throws ResourceNotFoundException if no matching note exists for this user
     */
    public SummaryResponse generateSummary(String userId, String noteId) {
        Note note = noteRepository.findById(noteId)
                .filter(n -> n.getUserId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Note not found: " + noteId));

        String prompt = SUMMARY_PROMPT_PREFIX + note.getContent();
        String summaryText = geminiService.getAnswer(prompt);

        Summary summary = Summary.builder()
                .userId(userId)
                .noteId(noteId)
                .summaryText(summaryText)
                .build();

        Summary saved = summaryRepository.save(summary);

        return toResponse(saved);
    }

    /**
     * Retrieves the most recently generated summary for a note, if one exists.
     *
     * @param userId the id of the authenticated user
     * @param noteId the id of the note whose summary is requested
     * @return the latest summary for that note
     * @throws ResourceNotFoundException if the note doesn't belong to the user,
     *                                    or no summary has been generated yet
     */
    public SummaryResponse getLatestSummary(String userId, String noteId) {
        verifyNoteOwnership(userId, noteId);

        Summary summary = summaryRepository.findFirstByNoteIdOrderByCreatedAtDesc(noteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No summary has been generated yet for note: " + noteId));

        return toResponse(summary);
    }

    /**
     * Retrieves all summaries ever generated for a note, most recent first.
     *
     * @param userId the id of the authenticated user
     * @param noteId the id of the note whose summary history is requested
     * @return a list of all summaries generated for that note
     */
    public List<SummaryResponse> getSummaryHistory(String userId, String noteId) {
        verifyNoteOwnership(userId, noteId);

        return summaryRepository.findByNoteId(noteId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Confirms the given note exists and belongs to the given user,
     * without returning it - used by read methods that only need the
     * note's id (already known) to scope the summary lookup.
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
     * Maps a persisted Summary document to its API-facing SummaryResponse DTO.
     *
     * @param summary the persisted summary
     * @return the corresponding SummaryResponse
     */
    private SummaryResponse toResponse(Summary summary) {
        return SummaryResponse.builder()
                .id(summary.getId())
                .noteId(summary.getNoteId())
                .summaryText(summary.getSummaryText())
                .createdAt(summary.getCreatedAt())
                .build();
    }

}
