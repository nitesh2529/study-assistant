export default function LoadingSpinner({ label = 'Loading' }) {
  return (
    <div className="flex items-center gap-3 text-paper-line" role="status" aria-live="polite">
      <span className="h-4 w-4 animate-spin rounded-full border-2 border-highlighter border-t-transparent" />
      <span className="font-mono text-sm">{label}...</span>
    </div>
  )
}
