import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import ErrorBanner from '../components/ErrorBanner'

export default function RegisterPage() {
  const { register } = useAuth()
  const navigate = useNavigate()
  const [username, setUsername] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState(null)
  const [submitting, setSubmitting] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setSubmitting(true)
    try {
      await register(username, email, password)
      navigate('/chat')
    } catch (err) {
      setError(err)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-charcoal px-4">
      <div className="w-full max-w-sm">
        <div className="mb-8 text-center">
          <p className="font-display text-3xl text-paper">
            Study<span className="text-highlighter">.</span>
          </p>
          <p className="mt-1 font-mono text-xs uppercase tracking-wider text-paper-line">
            Create your account
          </p>
        </div>

        <form
          onSubmit={handleSubmit}
          className="space-y-4 rounded-xl border border-charcoal-lighter bg-charcoal-light p-6"
        >
          {error && <ErrorBanner error={error} onDismiss={() => setError(null)} />}

          <div>
            <label htmlFor="username" className="mb-1 block text-sm text-paper-line">
              Username
            </label>
            <input
              id="username"
              type="text"
              required
              minLength={3}
              maxLength={30}
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="w-full rounded-lg border border-charcoal-lighter bg-charcoal px-3 py-2 text-paper placeholder:text-paper-line/50 focus:border-highlighter"
              placeholder="nitesh"
            />
          </div>

          <div>
            <label htmlFor="email" className="mb-1 block text-sm text-paper-line">
              Email
            </label>
            <input
              id="email"
              type="email"
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full rounded-lg border border-charcoal-lighter bg-charcoal px-3 py-2 text-paper placeholder:text-paper-line/50 focus:border-highlighter"
              placeholder="nitesh@example.com"
            />
          </div>

          <div>
            <label htmlFor="password" className="mb-1 block text-sm text-paper-line">
              Password
            </label>
            <input
              id="password"
              type="password"
              required
              minLength={8}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full rounded-lg border border-charcoal-lighter bg-charcoal px-3 py-2 text-paper placeholder:text-paper-line/50 focus:border-highlighter"
              placeholder="At least 8 characters"
            />
          </div>

          <button
            type="submit"
            disabled={submitting}
            className="w-full rounded-lg bg-highlighter py-2.5 font-medium text-ink transition-colors hover:bg-highlighter-soft disabled:opacity-60"
          >
            {submitting ? 'Creating account...' : 'Create account'}
          </button>
        </form>

        <p className="mt-5 text-center text-sm text-paper-line">
          Already have an account?{' '}
          <Link to="/login" className="text-study-soft hover:underline">
            Sign in
          </Link>
        </p>
      </div>
    </div>
  )
}
