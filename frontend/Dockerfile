FROM node:24-alpine AS build-stage
WORKDIR /app
# The lockfile pins the exact versions that were tested: without it every build took the newest
# version allowed by package.json, so the image could differ from (and fail where) the code was checked
COPY package.json package-lock.json .npmrc ./
RUN npm ci
COPY . .
ARG NODE_ENV
RUN if [ "$NODE_ENV" = "dev" ] ; then npm run build:dev ; else npm run build ; fi


## Production stage

FROM nginx:stable-alpine AS production-stage
RUN mkdir /app
COPY --from=build-stage /app/dist /app
