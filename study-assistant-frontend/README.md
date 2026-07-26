# Study Assistant — Frontend

React + Vite + Tailwind CSS frontend for the AI-Assisted Study & Query Assistant backend.

## Setup

```bash
npm install
cp .env.example .env
# edit .env if your backend runs somewhere other than http://localhost:8080
npm run dev
```

Open http://localhost:5173. Make sure your Spring Boot backend is running on the URL set in `.env`, and that CORS is enabled on the backend for `http://localhost:5173` (see note below).

## Backend CORS reminder

The Spring Boot backend's `SecurityConfig` does not have CORS configured yet by default. If you get blocked requests in the browser console (CORS errors), add a CORS configuration on the backend allowing `http://localhost:5173` as an origin, or update `vite.config.js` to proxy `/api` requests to the backend during development.

## Pages

- `/login`, `/register` — auth
- `/chat` — ask questions, view/delete history
- `/notes` — upload notes, browse uploaded notes
- `/notes/:noteId` — view note content, generate/view summary, generate/take quiz

## Build for production

```bash
npm run build
```

Output goes to `dist/`.
