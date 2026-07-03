FROM node:22-alpine AS build
WORKDIR /src
COPY . .
RUN npm install && npm run build
FROM nginx:1.27-alpine
COPY --from=build /src/dist /usr/share/nginx/html
