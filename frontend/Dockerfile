## build stage

FROM node:lts-alpine as build-stage
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
ARG NODE_ENV
RUN if [ "$NODE_ENV" = "dev" ] ; then npm run build:dev ; else npm run build ; fi


## production stage

FROM nginx:stable-alpine as production-stage
RUN mkdir /app
COPY --from=build-stage /app/dist /app