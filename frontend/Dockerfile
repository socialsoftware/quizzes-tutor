FROM node:22-alpine AS build-stage
WORKDIR /app
COPY package.json .npmrc ./
RUN npm install
COPY . .
ARG NODE_ENV
RUN if [ "$NODE_ENV" = "dev" ] ; then npm run build:dev ; else npm run build ; fi


## Production stage

FROM nginx:stable-alpine AS production-stage
RUN mkdir /app
COPY --from=build-stage /app/dist /app
