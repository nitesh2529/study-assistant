import client from './client'

/**
 * Submits a question to the AI assistant. Backend: POST /api/chat/ask
 * @param {string} question
 * @returns {Promise<{id: string, question: string, answer: string, createdAt: string}>}
 */
export const askQuestion = async (question) => {
  const { data } = await client.post('/api/chat/ask', { question })
  return data
}

/**
 * Retrieves the caller's full chat history, most recent first.
 * Backend: GET /api/chat/history
 */
export const getChatHistory = async () => {
  const { data } = await client.get('/api/chat/history')
  return data
}

/**
 * Deletes a single chat message. Backend: DELETE /api/chat/{messageId}
 * @param {string} messageId
 */
export const deleteChatMessage = async (messageId) => {
  await client.delete(`/api/chat/${messageId}`)
}
