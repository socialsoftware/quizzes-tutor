#https://stackoverflow.com/questions/31889867/docker-compose-not-overriding-dockerfile-environment-variables

#https://cli.vuejs.org/guide/deployment.html#docker-nginx
FROM node:lts-alpine as build-stage
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# production stage
FROM nginx:stable-alpine as production-stage
RUN mkdir /app
COPY --from=build-stage /app/dist /app