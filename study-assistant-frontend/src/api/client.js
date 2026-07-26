import axios from 'axios'

const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'https://study-assistant-fztk.onrender.com'

const client = axios.create({
  baseURL: BASE_URL,
})

// Attach the stored JWT to every outgoing request.
client.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Normalize backend error responses and handle expired/invalid tokens globally.
client.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status
    const backendError = error.response?.data

    if (status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }

    const message = backendError?.message || 'Something went wrong. Please try again.'
    const fieldErrors = backendError?.fieldErrors || null

    return Promise.reject({ status, message, fieldErrors, raw: error })
  }
)

export default client
