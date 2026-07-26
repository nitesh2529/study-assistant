import client from './client'

/**
 * Uploads a note file and extracts its text. Backend: POST /api/notes/upload
 * @param {File} file
 * @param {string} [title]
 * @returns {Promise<{id: string, title: string, content: string, originalFileName: string, createdAt: string}>}
 */
export const uploadNote = async (file, title) => {
  const formData = new FormData()
  formData.append('file', file)
  if (title) {
    formData.append('title', title)
  }

  const { data } = await client.post('/api/notes/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
  return data
}

/**
 * Retrieves all of the caller's notes, most recent first.
 * Backend: GET /api/notes
 */
export const getNotes = async () => {
  const { data } = await client.get('/api/notes')
  return data
}

/**
 * Retrieves a single note by id. Backend: GET /api/notes/{noteId}
 * @param {string} noteId
 */
export const getNoteById = async (noteId) => {
  const { data } = await client.get(`/api/notes/${noteId}`)
  return data
}

/**
 * Deletes a note (cascades to its summaries and quizzes).
 * Backend: DELETE /api/notes/{noteId}
 * @param {string} noteId
 */
export const deleteNote = async (noteId) => {
  await client.delete(`/api/notes/${noteId}`)
}
