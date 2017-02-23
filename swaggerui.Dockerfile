FROM node:7.4

RUN npm install http-server -g

EXPOSE 9002

ADD ./swaggerui ./docs/swaggerui
ADD ./target/myapi.swagger.json ./docs/swaggerui/swagger.json
ENTRYPOINT http-server ./docs -p 9002