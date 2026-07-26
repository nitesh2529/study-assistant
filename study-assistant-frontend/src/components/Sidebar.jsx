import { NavLink } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

const links = [
  { to: '/chat', label: 'Ask & Chat', icon: '?' },
  { to: '/notes', label: 'Notes', icon: '#' },
]

export default function Sidebar() {
  const { user, logout } = useAuth()

  return (
    <aside className="flex h-screen w-64 shrink-0 flex-col justify-between bg-charcoal px-5 py-6">
      <div>
        <div className="mb-10 px-1">
          <p className="font-display text-2xl leading-none text-paper">
            Study<span className="text-highlighter">.</span>
          </p>
          <p className="mt-1 font-mono text-[11px] uppercase tracking-wider text-paper/60">
            AI study desk
          </p>
        </div>

        <nav className="space-y-1">
          {links.map((link) => (
            <NavLink
              key={link.to}
              to={link.to}
              className={({ isActive }) =>
                [
                  'group flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-colors',
                  isActive
                    ? 'bg-charcoal-lighter text-paper'
                    : 'text-paper/70 hover:bg-charcoal-light hover:text-paper',
                ].join(' ')
              }
            >
              {({ isActive }) => (
                <>
                  <span className="font-mono text-highlighter">{link.icon}</span>
                  {isActive ? (
                    <span className="highlight-underline px-0.5 text-ink">{link.label}</span>
                  ) : (
                    <span>{link.label}</span>
                  )}
                </>
              )}
            </NavLink>
          ))}
        </nav>
      </div>

      <div className="border-t border-charcoal-lighter pt-4">
        <p className="truncate px-1 font-mono text-xs text-paper/60">
          {user?.username || 'Signed in'}
        </p>
        <button
          onClick={logout}
          className="mt-3 w-full rounded-lg border border-charcoal-lighter px-3 py-2 text-left text-sm font-medium text-paper/80 transition-colors hover:border-bad/40 hover:bg-bad/10 hover:text-bad"
        >
          Sign out
        </button>
      </div>
    </aside>
  )
}