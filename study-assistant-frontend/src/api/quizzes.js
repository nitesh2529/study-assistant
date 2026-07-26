import client from './client'

/**
 * Generates a new AI quiz for a note. Backend: POST /api/quizzes/{noteId}/generate
 * @param {string} noteId
 * @returns {Promise<{id: string, noteId: string, questions: Array<{questionText: string, options: string[], correctAnswer: string}>, createdAt: string}>}
 */
export const generateQuiz = async (noteId) => {
  const { data } = await client.post(`/api/quizzes/${noteId}/generate`)
  return data
}

/**
 * Retrieves the most recently generated quiz for a note.
 * Backend: GET /api/quizzes/{noteId}
 * @param {string} noteId
 */
export const getLatestQuiz = async (noteId) => {
  const { data } = await client.get(`/api/quizzes/${noteId}`)
  return data
}

/**
 * Retrieves every quiz ever generated for a note, most recent first.
 * Backend: GET /api/quizzes/{noteId}/history
 * @param {string} noteId
 */
export const getQuizHistory = async (noteId) => {
  const { data } = await client.get(`/api/quizzes/${noteId}/history`)
  return data
}
