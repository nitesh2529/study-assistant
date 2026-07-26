export default function ErrorBanner({ error, onDismiss }) {
  if (!error) return null

  const fieldErrors = error.fieldErrors ? Object.entries(error.fieldErrors) : []

  return (
    <div
      role="alert"
      className="flex items-start justify-between gap-4 rounded-lg border border-bad/40 bg-bad/10 px-4 py-3 text-sm text-bad"
    >
      <div>
        <p className="font-medium">{error.message}</p>
        {fieldErrors.length > 0 && (
          <ul className="mt-1 list-inside list-disc space-y-0.5">
            {fieldErrors.map(([field, msg]) => (
              <li key={field}>
                <span className="font-mono">{field}</span>: {msg}
              </li>
            ))}
          </ul>
        )}
      </div>
      {onDismiss && (
        <button
          onClick={onDismiss}
          aria-label="Dismiss error"
          className="shrink-0 text-bad/70 hover:text-bad"
        >
          ✕
        </button>
      )}
    </div>
  )
}
