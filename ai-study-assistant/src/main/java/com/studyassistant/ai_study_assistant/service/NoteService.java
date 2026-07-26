package com.studyassistant.ai_study_assistant.service;

import java.io.IOException;
import java.util.List;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.studyassistant.ai_study_assistant.dto.NoteResponse;
import com.studyassistant.ai_study_assistant.exception.NoteProcessingException;
import com.studyassistant.ai_study_assistant.exception.ResourceNotFoundException;
import com.studyassistant.ai_study_assistant.model.Note;
import com.studyassistant.ai_study_assistant.repository.NoteRepository;
import com.studyassistant.ai_study_assistant.repository.QuizRepository;
import com.studyassistant.ai_study_assistant.repository.SummaryRepository;

/**
 * Service layer handling the study notes feature: uploading files,
 * extracting their text content, persisting notes, and retrieving
 * or deleting a user's notes.
 */
@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final SummaryRepository summaryRepository;
    private final QuizRepository quizRepository;
    private final Tika tika;

    /**
     * Constructs the service with its required collaborators.
     *
     * @param noteRepository    repository for persisting notes
     * @param summaryRepository repository used to cascade-delete summaries
     *                          when their source note is deleted
     * @param quizRepository    repository used to cascade-delete quizzes
     *                          when their source note is deleted
     */
    public NoteService(NoteRepository noteRepository,
                        SummaryRepository summaryRepository,
                        QuizRepository quizRepository) {
        this.noteRepository = noteRepository;
        this.summaryRepository = summaryRepository;
        this.quizRepository = quizRepository;
        this.tika = new Tika();
    }

    public NoteResponse uploadNote(String userId, MultipartFile file, String title) {
        if (file == null || file.isEmpty()) {
            throw new NoteProcessingException("Uploaded file is empty or missing");
        }

        String extractedText;
        try {
            extractedText = tika.parseToString(file.getInputStream());
        } catch (IOException | org.apache.tika.exception.TikaException ex) {
            throw new NoteProcessingException("Failed to extract text from uploaded file: " + ex.getMessage());
        }

        if (extractedText == null || extractedText.isBlank()) {
            throw new NoteProcessingException("No readable text could be extracted from the uploaded file");
        }

        String resolvedTitle = (title == null || title.isBlank())
                ? file.getOriginalFilename()
                : title;

        Note note = Note.builder()
                .userId(userId)
                .title(resolvedTitle)
                .content(extractedText.trim())
                .originalFileName(file.getOriginalFilename())
                .build();

        Note saved = noteRepository.save(note);

        return toResponse(saved);
    }

    public List<NoteResponse> getNotes(String userId) {
        return noteRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public NoteResponse getNoteById(String userId, String noteId) {
        Note note = findOwnedNote(userId, noteId);
        return toResponse(note);
    }

    /**
     * Deletes a single note, but only if it belongs to the requesting user.
     * Also cascade-deletes any summaries and quizzes generated from this
     * note, to avoid leaving orphaned documents referencing a noteId that
     * no longer exists.
     *
     * @param userId the id of the authenticated user
     * @param noteId the id of the note to delete
     * @throws ResourceNotFoundException if no matching note exists for this user
     */
    public void deleteNote(String userId, String noteId) {
        Note note = findOwnedNote(userId, noteId);
        summaryRepository.deleteByNoteId(note.getId());
        quizRepository.deleteByNoteId(note.getId());
        noteRepository.deleteById(note.getId());
    }

    private Note findOwnedNote(String userId, String noteId) {
        return noteRepository.findById(noteId)
                .filter(n -> n.getUserId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Note not found: " + noteId));
    }

    private NoteResponse toResponse(Note note) {
        return NoteResponse.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .originalFileName(note.getOriginalFileName())
                .createdAt(note.getCreatedAt())
                .build();
    }

}