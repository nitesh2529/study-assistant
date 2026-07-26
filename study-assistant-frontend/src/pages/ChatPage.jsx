import { useEffect, useState } from 'react'
import Layout from '../components/Layout'
import ErrorBanner from '../components/ErrorBanner'
import LoadingSpinner from '../components/LoadingSpinner'
import EmptyState from '../components/EmptyState'
import { askQuestion, getChatHistory, deleteChatMessage } from '../api/chat'
import { formatDate } from '../utils/formatDate'
import ReactMarkdown from 'react-markdown'
import remarkGfm from 'remark-gfm'
export default function ChatPage() {
  const [question, setQuestion] = useState('')
  const [history, setHistory] = useState([])
  const [loadingHistory, setLoadingHistory] = useState(true)
  const [asking, setAsking] = useState(false)
  const [error, setError] = useState(null)

  const loadHistory = async () => {
    setLoadingHistory(true)
    try {
      const data = await getChatHistory()
      setHistory(data)
    } catch (err) {
      setError(err)
    } finally {
      setLoadingHistory(false)
    }
  }

  useEffect(() => {
    loadHistory()
  }, [])

  const handleAsk = async (e) => {
    e.preventDefault()
    if (!question.trim()) return

    setError(null)
    setAsking(true)
    try {
      const newMessage = await askQuestion(question.trim())
      setHistory((prev) => [newMessage, ...prev])
      setQuestion('')
    } catch (err) {
      setError(err)
    } finally {
      setAsking(false)
    }
  }

  const handleDelete = async (id) => {
    const prev = history
    setHistory((h) => h.filter((m) => m.id !== id))
    try {
      await deleteChatMessage(id)
    } catch (err) {
      setHistory(prev)
      setError(err)
    }
  }

  return (
    <Layout>
      <header className="mb-8">
        <h1 className="font-display text-3xl text-ink">
          Ask <span className="highlight-underline px-1">anything</span>
        </h1>
        <p className="mt-1 text-sm text-ink-light">
          Technical questions, answered and saved for later revision.
        </p>
      </header>

      <form onSubmit={handleAsk} className="mb-8 flex gap-3">
        <input
          type="text"
          value={question}
          onChange={(e) => setQuestion(e.target.value)}
          maxLength={2000}
          placeholder="What is polymorphism in Java?"
          className="flex-1 rounded-lg border border-paper-line bg-white px-4 py-3 text-ink placeholder:text-ink-light/60 focus:border-highlighter"
        />
        <button
  type="submit"
  disabled={asking || !question.trim()}
  className="shrink-0 rounded-lg bg-ink px-6 py-3 font-semibold text-paper transition-opacity hover:opacity-90 disabled:cursor-not-allowed disabled:opacity-40"
>
  {asking ? 'Asking...' : 'Ask'}
</button>
      </form>

      {error && (
        <div className="mb-6">
          <ErrorBanner error={error} onDismiss={() => setError(null)} />
        </div>
      )}

      {loadingHistory ? (
        <LoadingSpinner label="Loading history" />
      ) : history.length === 0 ? (
        <EmptyState
          title="No questions yet"
          description="Ask your first question above — it'll be saved here for quick revision later."
        />
      ) : (
        <ul className="space-y-4">
          {history.map((msg) => (
            <li
              key={msg.id}
              className="rounded-xl border border-paper-line bg-white p-5 shadow-sm"
            >
              <div className="flex items-start justify-between gap-4">
                <p className="font-display text-lg text-ink">{msg.question}</p>
                <button
                  onClick={() => handleDelete(msg.id)}
                  aria-label="Delete this question"
                  className="shrink-0 text-sm text-ink-light hover:text-bad"
                >
                  Delete
                </button>
              </div>
             <div className="prose prose-sm max-w-none mt-2 text-ink-light">
  <ReactMarkdown remarkPlugins={[remarkGfm]}>{msg.answer}</ReactMarkdown>
</div>
              <p className="mt-3 font-mono text-xs text-ink-light/70">
                {formatDate(msg.createdAt)}
              </p>
            </li>
          ))}
        </ul>
      )}
    </Layout>
  )
}
