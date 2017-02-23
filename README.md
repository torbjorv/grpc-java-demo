# prerequisites
- gcloud
- protoc
- docker
- java 8
- maven
- a GCP project
- create a container cluster in your GCP project as described here: https://cloud.google.com/endpoints/docs/quickstart-grpc-container-engine

- for SwaggerUI (I found a GO-based protoc plugin that could generate swagger.json from .proto files)
    - install GO
    - install these go libs:
        - go get -u github.com/grpc-ecosystem/grpc-gateway/protoc-gen-grpc-gateway
        - go get -u github.com/grpc-ecosystem/grpc-gateway/protoc-gen-swagger
        - go get -u github.com/golang/protobuf/protoc-gen-go

- create an API key in GCP cloud console (API Manager->Credentials->Create credentials->API Key)

# Deploy and test
- Do a search for 'MYPROJECTID' everywhere and replace with your GCP project ID 
- ./bin/deploy.sh MYPROJECTID MYAPIKEY


# DISCLAIMER
- This endpoint does NOT setup any certificates, so any communication is UNencrypted.
- This endpoint does NOT do any kind of authentication
- The code and scripts in this repo have only been tested with OSX.