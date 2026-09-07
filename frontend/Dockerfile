# ---- build stage ----
FROM node:20-alpine AS build
WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci

COPY . .

# Vite inlines VITE_-prefixed vars into the built bundle at build time -- there's no nginx (or
# any other server) in the runtime stage to substitute this in later, so it must be supplied as a
# docker-compose build arg (see docker-compose.yml/.env.example). Changing it means rebuilding
# this image.
ARG VITE_API_BASE_URL
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL
RUN npm run build

# ---- runtime stage ----
# No nginx: `serve` is a tiny static file server, which is all a pre-built Vite bundle needs.
FROM node:20-alpine
WORKDIR /app
RUN npm install -g serve@14

COPY --from=build /app/dist ./dist

# Informational only -- see the note on EXPOSE in backend/Dockerfile. The container listens on
# $PORT (defaulting to 5175 to match vite.config.ts's local-dev fallback); docker-compose.yml sets
# PORT from SUBSCRIPTIONS_FRONTEND_HTTP_PORT so the container's listening port always matches its
# published port.
EXPOSE 5175

CMD ["sh", "-c", "serve -s dist -l ${PORT:-5175}"]
