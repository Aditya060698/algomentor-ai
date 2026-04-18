# AlgoMentor AI Deployment Guide

## Render Architecture

Use two Render services and one managed database:
- Static Site for the React frontend
- Docker Web Service for the Spring Boot backend
- PostgreSQL database for persistent data

This repository includes `render.yaml` so Render can provision the stack from code.

## Free Deploy Setup

This repository is configured for a free Render deployment:
- backend web service on the `free` plan
- frontend static site on Render's free static hosting
- PostgreSQL database on the `free` plan

Important limitations from Render's official docs checked on April 18, 2026:
- free web services spin down after 15 minutes of inactivity
- free Postgres expires 30 days after creation unless upgraded
- free services are not appropriate for real production traffic

## Backend Service

The backend uses `backend/Dockerfile`.

Important environment variables:
- `PORT`
- `SPRING_PROFILES_ACTIVE=prod`
- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USERNAME`
- `DB_PASSWORD`
- `APP_CORS_ALLOWED_ORIGINS`

The backend builds its JDBC URL from those values.

Health check:
- `/api/v1/health`

## Frontend Service

The frontend is deployed as a static site from `frontend/`.

Important environment variable:
- `VITE_API_BASE_URL=https://your-backend.onrender.com/api/v1`

The `render.yaml` rewrite rule sends all routes to `index.html`, which is required for React Router.

## Deployment Flow

1. Push the repository to GitHub.
2. In Render, create a new Blueprint service from the repo.
3. Render reads `render.yaml` and provisions the backend, frontend, and database.
4. Set the unsynced variables:
   - `APP_CORS_ALLOWED_ORIGINS`
   - `VITE_API_BASE_URL`
5. Redeploy both services.

## Local-to-Prod Connection Model

- Frontend calls `VITE_API_BASE_URL`
- Backend accepts requests from `APP_CORS_ALLOWED_ORIGINS`
- Backend connects to PostgreSQL using Render-injected host, port, database, user, and password

## Production Notes

This free setup is good for demos, portfolio review, and interview walkthroughs.

Before calling it production-ready, add:
- Flyway or Liquibase migrations instead of `ddl-auto`
- Redis for caching and rate-limit support
- secrets management for AI provider keys
- request logging and metrics
- separate staging and production environments
- a paid database or external managed database with backups and no expiry
