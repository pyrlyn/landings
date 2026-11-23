# syntax=docker/dockerfile:1
# Kotlin landing (pyrlyn/landing): ./gradlew generateSite -> build/site, served by nginx on :80.
# Build context is this directory (landings/pyrlyn/), not the repository root.
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
ARG SITE_URL=https://pyrlyn.dev
ARG SITE_BASE=/
ENV SITE_URL=$SITE_URL SITE_BASE=$SITE_BASE
COPY . .
# Resolving maven.pkg.github.com/pyrlyn/backend (backend:landing-client) needs a token with
# read:packages. It comes in as the BuildKit secret GITHUB_TOKEN, exposed only to this RUN as an
# environment variable, so it never lands in an image layer, the image config or the build cache:
#   docker build --secret id=GITHUB_TOKEN,env=GITHUB_TOKEN landings/pyrlyn
# Coolify passes build variables this way when "Use Docker Build Secrets" is on.
RUN --mount=type=secret,id=GITHUB_TOKEN,env=GITHUB_TOKEN \
    ./gradlew --no-daemon -Dorg.gradle.configuration-cache=false generateSite && test -f build/site/index.html

FROM nginx:1.30-alpine
COPY docker/paas-nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/build/site /usr/share/nginx/html
EXPOSE 80
