# Introduction
A basic GRPC endpoint implemented in java, including REST transcoding and SwaggerUI.

Inludes config files and deployment scripts for Kubernetes on Google Cloud Platform. 

- Integrates with Google Cloud Endpoints
- Performs REST-transcoding using Google's Enterprise Service Proxy (part of Cloud Endpoints) 
- Generates swagger.json from the .proto files and deploys SwaggerUI

The scripts are only tested on OSX.

# Prerequisites
- gcloud command line utilities
- protoc
- docker
- java 8
- maven
- a GCP project (remember to set current project to the one with the GKE cluster mentioned on the next line)
- create a container cluster in your GCP project as described here: https://cloud.google.com/endpoints/docs/quickstart-grpc-container-engine
- for SwaggerUI (I found a GO-based protoc plugin that could generate swagger.json from .proto files)
    - install GO
    - install these go libs:
        - go get -u github.com/grpc-ecosystem/grpc-gateway/protoc-gen-grpc-gateway
        - go get -u github.com/grpc-ecosystem/grpc-gateway/protoc-gen-swagger
        - go get -u github.com/golang/protobuf/protoc-gen-go

- create an API key in GCP cloud console (API Manager->Credentials->Create credentials->API Key)
- replace the api key in swaggerui/index.html with your own key
- use this API key as parameter to the deploy and test scripts.


# Deploy and test
- Do a search for 'MYPROJECTID' everywhere and replace with your GCP project ID 
- ./bin/deploy.sh MYPROJECTID MYAPIKEY
- GRPC is available on port 8000
- REST is available on port 8001
- SwaggerUI is available on :8001/swaggerui


# DISCLAIMER
- This endpoint does NOT setup any certificates, so any communication is UNencrypted.
- This endpoint does NOT do any kind of authentication
- The code and scripts in this repo have only been tested with OSX.