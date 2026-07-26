import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import Layout from '../components/Layout'
import ErrorBanner from '../components/ErrorBanner'
import LoadingSpinner from '../components/LoadingSpinner'
import { getNoteById } from '../api/notes'
import { generateSummary, getLatestSummary } from '../api/summaries'
import { generateQuiz, getLatestQuiz } from '../api/quizzes'
import { formatDate } from '../utils/formatDate'
import ReactMarkdown from 'react-markdown'
import remarkGfm from 'remark-gfm'

const TABS = [
  { key: 'note', label: 'Note' },
  { key: 'summary', label: 'Summary' },
  { key: 'quiz', label: 'Quiz' },
]

export default function NoteDetailPage() {
  const { noteId } = useParams()
  const [note, setNote] = useState(null)
  const [loadingNote, setLoadingNote] = useState(true)
  const [error, setError] = useState(null)
  const [activeTab, setActiveTab] = useState('note')

  useEffect(() => {
    let cancelled = false
    setLoadingNote(true)
    getNoteById(noteId)
      .then((data) => {
        if (!cancelled) setNote(data)
      })
      .catch((err) => {
        if (!cancelled) setError(err)
      })
      .finally(() => {
        if (!cancelled) setLoadingNote(false)
      })
    return () => {
      cancelled = true
    }
  }, [noteId])

  if (loadingNote) {
    return (
      <Layout>
        <LoadingSpinner label="Loading note" />
      </Layout>
    )
  }

  if (error && !note) {
    return (
      <Layout>
        <ErrorBanner error={error} />
        <Link to="/notes" className="mt-4 inline-block text-sm text-study hover:underline">
          ← Back to notes
        </Link>
      </Layout>
    )
  }

  return (
    <Layout>
      <Link to="/notes" className="mb-4 inline-block text-sm text-ink-light hover:text-ink">
        ← Back to notes
      </Link>

      <header className="mb-6">
        <h1 className="font-display text-3xl text-ink">{note.title}</h1>
        <p className="mt-1 font-mono text-xs text-ink-light">
          {note.originalFileName} · {formatDate(note.createdAt)}
        </p>
      </header>

      <div className="mb-6 flex gap-1 border-b border-paper-line">
        {TABS.map((tab) => (
          <button
            key={tab.key}
            onClick={() => setActiveTab(tab.key)}
            className={[
              'px-4 py-2.5 text-sm font-medium transition-colors',
              activeTab === tab.key
                ? 'highlight-underline text-ink'
                : 'text-ink-light hover:text-ink',
            ].join(' ')}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {activeTab === 'note' && <NoteTab note={note} />}
      {activeTab === 'summary' && <SummaryTab noteId={noteId} />}
      {activeTab === 'quiz' && <QuizTab noteId={noteId} />}
    </Layout>
  )
}

function NoteTab({ note }) {
  return (
    <div className="rounded-xl border border-paper-line bg-white bg-ruled-paper p-6">
      <p className="whitespace-pre-wrap text-sm leading-relaxed text-ink">{note.content}</p>
    </div>
  )
}

function SummaryTab({ noteId }) {
  const [summary, setSummary] = useState(null)
  const [loading, setLoading] = useState(true)
  const [generating, setGenerating] = useState(false)
  const [error, setError] = useState(null)
  const [notFoundYet, setNotFoundYet] = useState(false)

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    getLatestSummary(noteId)
      .then((data) => {
        if (!cancelled) setSummary(data)
      })
      .catch((err) => {
        if (cancelled) return
        if (err.status === 404) {
          setNotFoundYet(true)
        } else {
          setError(err)
        }
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })
    return () => {
      cancelled = true
    }
  }, [noteId])

  const handleGenerate = async () => {
    setError(null)
    setGenerating(true)
    try {
      const data = await generateSummary(noteId)
      setSummary(data)
      setNotFoundYet(false)
    } catch (err) {
      setError(err)
    } finally {
      setGenerating(false)
    }
  }

  if (loading) return <LoadingSpinner label="Loading summary" />

  return (
    <div>
      {error && (
        <div className="mb-4">
          <ErrorBanner error={error} onDismiss={() => setError(null)} />
        </div>
      )}

      <button
        onClick={handleGenerate}
        disabled={generating}
        className="mb-5 rounded-lg bg-ink px-4 py-2.5 text-sm font-medium text-paper transition-opacity hover:opacity-90 disabled:opacity-50"
      >
        {generating
          ? 'Generating...'
          : summary
            ? 'Regenerate summary'
            : 'Generate summary'}
      </button>

      {notFoundYet && !summary && (
        <p className="text-sm text-ink-light">
          No summary yet — click "Generate summary" to have Gemini condense this note into key
          points.
        </p>
      )}

    {summary && (
  <div className="rounded-xl border border-paper-line bg-white p-6">
    <div className="prose prose-sm max-w-none">
      <ReactMarkdown remarkPlugins={[remarkGfm]}>{summary.summaryText}</ReactMarkdown>
    </div>
    <p className="mt-4 font-mono text-xs text-ink-light/70">
      {formatDate(summary.createdAt)}
    </p>
  </div>
)}
    </div>
  )
}

function QuizTab({ noteId }) {
  const [quiz, setQuiz] = useState(null)
  const [loading, setLoading] = useState(true)
  const [generating, setGenerating] = useState(false)
  const [error, setError] = useState(null)
  const [notFoundYet, setNotFoundYet] = useState(false)
  const [answers, setAnswers] = useState({})

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    getLatestQuiz(noteId)
      .then((data) => {
        if (!cancelled) setQuiz(data)
      })
      .catch((err) => {
        if (cancelled) return
        if (err.status === 404) {
          setNotFoundYet(true)
        } else {
          setError(err)
        }
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })
    return () => {
      cancelled = true
    }
  }, [noteId])

  const handleGenerate = async () => {
    setError(null)
    setGenerating(true)
    setAnswers({})
    try {
      const data = await generateQuiz(noteId)
      setQuiz(data)
      setNotFoundYet(false)
    } catch (err) {
      setError(err)
    } finally {
      setGenerating(false)
    }
  }

  const selectAnswer = (questionIndex, option) => {
    setAnswers((prev) => ({ ...prev, [questionIndex]: option }))
  }

  const handleRetake = () => {
    setAnswers({})
  }

  if (loading) return <LoadingSpinner label="Loading quiz" />

  const totalQuestions = quiz?.questions?.length || 0
  const answeredCount = Object.keys(answers).length
  const isComplete = totalQuestions > 0 && answeredCount === totalQuestions
  const correctCount = quiz
    ? quiz.questions.filter((q, i) => answers[i] === q.correctAnswer).length
    : 0
  const accuracy = totalQuestions > 0 ? Math.round((correctCount / totalQuestions) * 100) : 0

  return (
    <div>
      {error && (
        <div className="mb-4">
          <ErrorBanner error={error} onDismiss={() => setError(null)} />
        </div>
      )}

      <button
        onClick={handleGenerate}
        disabled={generating}
        className="mb-5 rounded-lg bg-ink px-4 py-2.5 text-sm font-semibold text-paper transition-opacity hover:opacity-90 disabled:cursor-not-allowed disabled:opacity-40"
      >
        {generating ? 'Generating...' : quiz ? 'Regenerate quiz' : 'Generate quiz'}
      </button>

      {notFoundYet && !quiz && (
        <p className="text-sm text-ink-light">
          No quiz yet — click "Generate quiz" for 5 AI-written multiple-choice questions from
          this note.
        </p>
      )}

      {quiz && (
        <div className="space-y-5">
          {isComplete && (
            <div className="rounded-xl border-2 border-ink bg-white p-6">
              <p className="font-mono text-xs uppercase tracking-wider text-ink-light">
                Results
              </p>
              <div className="mt-3 flex items-end justify-between gap-4">
                <div>
                  <p className="font-display text-4xl text-ink">{accuracy}%</p>
                  <p className="mt-1 text-sm text-ink-light">accuracy</p>
                </div>
                <div className="flex gap-4 text-right">
                  <div>
                    <p className="font-display text-2xl text-good">{correctCount}</p>
                    <p className="text-xs text-ink-light">correct</p>
                  </div>
                  <div>
                    <p className="font-display text-2xl text-bad">
                      {totalQuestions - correctCount}
                    </p>
                    <p className="text-xs text-ink-light">wrong</p>
                  </div>
                </div>
              </div>
              <div className="mt-4 h-2 w-full overflow-hidden rounded-full bg-paper-dark">
                <div
                  className="h-full rounded-full bg-good transition-all"
                  style={{ width: `${accuracy}%` }}
                />
              </div>
              <button
                onClick={handleRetake}
                className="mt-4 rounded-lg border border-paper-line px-4 py-2 text-sm font-medium text-ink transition-colors hover:border-ink hover:bg-paper-dark"
              >
                Retake quiz
              </button>
            </div>
          )}

          {!isComplete && totalQuestions > 0 && (
            <p className="font-mono text-xs text-ink-light">
              {answeredCount} of {totalQuestions} answered
            </p>
          )}

          {quiz.questions.map((q, qIndex) => {
            const selected = answers[qIndex]
            return (
              <div key={qIndex} className="rounded-xl border border-paper-line bg-white p-5">
                <p className="font-display text-base text-ink">
                  {qIndex + 1}. {q.questionText}
                </p>
                <div className="mt-3 space-y-2">
                  {q.options.map((option) => {
                    const isSelected = selected === option
                    const isCorrect = option === q.correctAnswer
                    const showResult = Boolean(selected)

                    let optionStyle = 'border-paper-line hover:border-ink-light'
                    if (showResult && isSelected && isCorrect) {
                      optionStyle = 'border-good bg-good/10'
                    } else if (showResult && isSelected && !isCorrect) {
                      optionStyle = 'border-bad bg-bad/10'
                    } else if (showResult && isCorrect) {
                      optionStyle = 'border-good bg-good/10'
                    }

                    return (
                      <button
                        key={option}
                        onClick={() => selectAnswer(qIndex, option)}
                        disabled={showResult}
                        className={`block w-full rounded-lg border px-4 py-2.5 text-left text-sm text-ink transition-colors ${optionStyle}`}
                      >
                        {option}
                      </button>
                    )
                  })}
                </div>
              </div>
            )
          })}
          <p className="font-mono text-xs text-ink-light/70">{formatDate(quiz.createdAt)}</p>
        </div>
      )}
    </div>
  )
}
