import Sidebar from './Sidebar'

export default function Layout({ children }) {
  return (
    <div className="flex min-h-screen bg-charcoal">
      <Sidebar />
      <main className="flex-1 overflow-y-auto bg-paper">
        <div className="mx-auto max-w-4xl px-6 py-10 md:px-10">{children}</div>
      </main>
    </div>
  )
}
