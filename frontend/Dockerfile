## Build stage

FROM node:10.17.0-alpine3.10 as build-stage
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
ARG NODE_ENV
RUN if [ "$NODE_ENV" = "development" ] ; then npm run build:dev ; else npm run build ; fi


## Production stage

FROM nginx:stable-alpine as production-stage
RUN mkdir /app
COPY --from=build-stage /app/dist /app
