#FROM alpine
FROM node:10.24.1-alpine3.10

#RUN apk add --update nodejs nodejs-npm

WORKDIR /app

COPY package*.json ./

RUN npm ci

COPY . .

#RUN echo fs.inotify.max_user_watches=524288 | tee -a /etc/sysctl.conf && sysctl -p

RUN npm run build

EXPOSE 8080 8081 8889 8888

#CMD ["npm", "run", "start:large"]

CMD ["npm", "run", "serve"] 
