import { createContext, useContext, useState, useCallback } from 'react'
import * as authApi from '../api/auth'

const AuthContext = createContext(null)

const readStoredUser = () => {
  try {
    const raw = localStorage.getItem('user')
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(readStoredUser)
  const [token, setToken] = useState(() => localStorage.getItem('token'))

  const persistSession = (authResponse) => {
    const { token: jwt, ...userInfo } = authResponse
    localStorage.setItem('token', jwt)
    localStorage.setItem('user', JSON.stringify(userInfo))
    setToken(jwt)
    setUser(userInfo)
  }

  const login = useCallback(async (usernameOrEmail, password) => {
    const response = await authApi.login({ usernameOrEmail, password })
    persistSession(response)
    return response
  }, [])

  const register = useCallback(async (username, email, password) => {
    const response = await authApi.register({ username, email, password })
    persistSession(response)
    return response
  }, [])

  const logout = useCallback(() => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    setToken(null)
    setUser(null)
  }, [])

  const value = {
    user,
    token,
    isAuthenticated: Boolean(token),
    login,
    register,
    logout,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export const useAuth = () => {
  const ctx = useContext(AuthContext)
  if (!ctx) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return ctx
}
