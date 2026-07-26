import client from './client'

/**
 * Generates a new AI summary for a note. Backend: POST /api/summaries/{noteId}/generate
 * @param {string} noteId
 * @returns {Promise<{id: string, noteId: string, summaryText: string, createdAt: string}>}
 */
export const generateSummary = async (noteId) => {
  const { data } = await client.post(`/api/summaries/${noteId}/generate`)
  return data
}

/**
 * Retrieves the most recently generated summary for a note.
 * Backend: GET /api/summaries/{noteId}
 * @param {string} noteId
 */
export const getLatestSummary = async (noteId) => {
  const { data } = await client.get(`/api/summaries/${noteId}`)
  return data
}

/**
 * Retrieves every summary ever generated for a note, most recent first.
 * Backend: GET /api/summaries/{noteId}/history
 * @param {string} noteId
 */
export const getSummaryHistory = async (noteId) => {
  const { data } = await client.get(`/api/summaries/${noteId}/history`)
  return data
}
