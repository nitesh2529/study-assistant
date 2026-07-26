import { useEffect, useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import Layout from '../components/Layout'
import ErrorBanner from '../components/ErrorBanner'
import LoadingSpinner from '../components/LoadingSpinner'
import EmptyState from '../components/EmptyState'
import { getNotes, uploadNote, deleteNote } from '../api/notes'
import { formatDate } from '../utils/formatDate'

export default function NotesPage() {
  const [notes, setNotes] = useState([])
  const [loading, setLoading] = useState(true)
  const [uploading, setUploading] = useState(false)
  const [error, setError] = useState(null)
  const [dragActive, setDragActive] = useState(false)
  const [title, setTitle] = useState('')
  const fileInputRef = useRef(null)

  const loadNotes = async () => {
    setLoading(true)
    try {
      const data = await getNotes()
      setNotes(data)
    } catch (err) {
      setError(err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadNotes()
  }, [])

  const handleFile = async (file) => {
    if (!file) return
    setError(null)
    setUploading(true)
    try {
      const note = await uploadNote(file, title.trim() || undefined)
      setNotes((prev) => [note, ...prev])
      setTitle('')
    } catch (err) {
      setError(err)
    } finally {
      setUploading(false)
      if (fileInputRef.current) fileInputRef.current.value = ''
    }
  }

  const handleDrop = (e) => {
    e.preventDefault()
    setDragActive(false)
    const file = e.dataTransfer.files?.[0]
    handleFile(file)
  }

  const handleDelete = async (id) => {
    const prev = notes
    setNotes((n) => n.filter((note) => note.id !== id))
    try {
      await deleteNote(id)
    } catch (err) {
      setNotes(prev)
      setError(err)
    }
  }

  return (
    <Layout>
      <header className="mb-8">
        <h1 className="font-display text-3xl text-ink">
          Your <span className="highlight-underline px-1">notes</span>
        </h1>
        <p className="mt-1 text-sm text-ink-light">
          Upload lecture notes or textbook chapters — generate summaries and quizzes from them.
        </p>
      </header>

      <div className="mb-4">
        <label htmlFor="note-title" className="mb-1 block text-sm text-ink-light">
          Title <span className="text-ink-light/60">(optional — defaults to file name)</span>
        </label>
        <input
          id="note-title"
          type="text"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="Chapter 4 - Data Structures"
          className="w-full rounded-lg border border-paper-line bg-white px-4 py-2.5 text-ink placeholder:text-ink-light/60 focus:border-highlighter"
        />
      </div>

      <div
        onDragOver={(e) => {
          e.preventDefault()
          setDragActive(true)
        }}
        onDragLeave={() => setDragActive(false)}
        onDrop={handleDrop}
        onClick={() => fileInputRef.current?.click()}
        className={[
          'mb-8 flex cursor-pointer flex-col items-center justify-center rounded-xl border-2 border-dashed px-6 py-10 text-center transition-colors',
          dragActive ? 'border-highlighter bg-highlighter/10' : 'border-paper-line bg-white',
        ].join(' ')}
      >
        <input
          ref={fileInputRef}
          type="file"
          accept=".pdf,.docx,.txt,.doc"
          className="hidden"
          onChange={(e) => handleFile(e.target.files?.[0])}
        />
        {uploading ? (
          <LoadingSpinner label="Extracting text" />
        ) : (
          <>
            <p className="font-display text-lg text-ink">Drop a file here, or click to browse</p>
            <p className="mt-1 text-sm text-ink-light">PDF, DOCX, or TXT — up to 10MB</p>
          </>
        )}
      </div>

      {error && (
        <div className="mb-6">
          <ErrorBanner error={error} onDismiss={() => setError(null)} />
        </div>
      )}

      {loading ? (
        <LoadingSpinner label="Loading notes" />
      ) : notes.length === 0 ? (
        <EmptyState
          title="No notes yet"
          description="Upload your first file above to start generating summaries and quizzes."
        />
      ) : (
        <ul className="grid gap-4 sm:grid-cols-2">
          {notes.map((note) => (
            <li key={note.id}>
              <Link
                to={`/notes/${note.id}`}
                className="block h-full rounded-xl border border-paper-line bg-white bg-ruled-paper p-5 shadow-sm transition-transform hover:-translate-y-0.5 hover:shadow-md"
              >
                <p className="font-display text-lg text-ink line-clamp-2">{note.title}</p>
                <p className="mt-2 line-clamp-3 text-sm text-ink-light">{note.content}</p>
                <div className="mt-4 flex items-center justify-between">
                  <p className="font-mono text-xs text-ink-light/70">
                    {formatDate(note.createdAt)}
                  </p>
                  <button
                    onClick={(e) => {
                      e.preventDefault()
                      handleDelete(note.id)
                    }}
                    className="text-xs text-ink-light hover:text-bad"
                  >
                    Delete
                  </button>
                </div>
              </Link>
            </li>
          ))}
        </ul>
      )}
    </Layout>
  )
}
