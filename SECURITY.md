# Security — backend repo

**Never commit these files:**

| File | Use instead |
|------|-------------|
| `.env` | Render → Environment → `MONGODB_URI` |
| `.env.local` | Local only, gitignored |
| `*.pem`, `*.key` | Platform secret stores |

**Safe to commit:** `.env.example` (placeholders only).

If `.env` was ever pushed to GitHub, remove it from history, rotate your Atlas password, and use only Render env vars in production.
