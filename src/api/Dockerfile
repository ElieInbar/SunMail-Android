# src/api/Dockerfile

# 1. Builder stage
FROM node:18-alpine AS builder

WORKDIR /usr/src/app

# Copy the dependency files and install them
COPY package*.json ./
RUN npm ci --omit=dev

# Copy the source code into the container
COPY . .

# 2. Runtime stage
FROM node:18-alpine

WORKDIR /usr/src/app

# Get the node_modules and compiled code from the builder stage
COPY --from=builder /usr/src/app/node_modules ./node_modules
COPY --from=builder /usr/src/app ./

EXPOSE 8080

CMD ["node", "src/gmail.js"]
