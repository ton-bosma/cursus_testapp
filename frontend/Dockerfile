# ── Stage 1: Build ────────────────────────────────────────────────────────
# Build context is the repo root; frontend sources are under frontend/.
FROM node:22-alpine AS builder

WORKDIR /workspace

# Copy package manifests first for better layer caching
COPY frontend/package.json frontend/package-lock.json ./

# Install dependencies (ci = clean install, reproducible)
RUN npm ci

# Copy the rest of the frontend source
COPY frontend/ ./

# Build the Angular app in production mode.
# Output lands in dist/cursus-plan/ (per angular.json outputPath).
RUN npm run build

# ── Stage 2: Serve with nginx ─────────────────────────────────────────────
FROM nginx:1.27-alpine AS runtime

# Remove the default nginx welcome page
RUN rm -rf /usr/share/nginx/html/*

# Copy the compiled Angular app
COPY --from=builder /workspace/dist/cursus-plan/browser /usr/share/nginx/html

# Minimal nginx config: serve static files and route all paths to index.html
# (required for Angular's client-side routing).
RUN printf 'server {\n\
    listen 80;\n\
    root /usr/share/nginx/html;\n\
    index index.html;\n\
\n\
    location / {\n\
        try_files $uri $uri/ /index.html;\n\
    }\n\
}\n' > /etc/nginx/conf.d/default.conf

EXPOSE 80
