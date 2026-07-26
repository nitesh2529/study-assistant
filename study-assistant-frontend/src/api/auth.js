import client from './client'

/**
 * Registers a new user. Backend: POST /api/auth/register
 * @param {{username: string, email: string, password: string}} payload
 * @returns {Promise<{token: string, tokenType: string, userId: string, username: string, email: string, roles: string[]}>}
 */
export const register = async (payload) => {
  const { data } = await client.post('/api/auth/register', payload)
  return data
}

/**
 * Logs in an existing user. Backend: POST /api/auth/login
 * @param {{usernameOrEmail: string, password: string}} payload
 * @returns {Promise<{token: string, tokenType: string, userId: string, username: string, email: string, roles: string[]}>}
 */
export const login = async (payload) => {
  const { data } = await client.post('/api/auth/login', payload)
  return data
}
