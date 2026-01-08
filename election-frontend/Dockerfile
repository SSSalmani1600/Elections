# Build stage
FROM node:22-alpine AS build

WORKDIR /app

# Define build-time variable
ARG VITE_API_URL
# Set environment variable for build process
ENV VITE_API_URL=$VITE_API_URL

COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build

# Production stage
FROM nginx:alpine

COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
