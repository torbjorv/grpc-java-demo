#!/usr/bin/env bash
if [ $# -lt 2 ]
then
    echo 'Missing arguments (need project ID and API key)'
    exit 1
fi

PROJECTID=$1
APIKEY=$2

# compile java package
mvn clean compile package
if [ $? -ne 0 ]
then
    exit 1
fi

# build binary proto file and upload to Google service management
protoc --include_imports --include_source_info src/main/proto/myapi.proto --descriptor_set_out target/myapi.pb -I./src/main/proto/
gcloud service-management deploy target/myapi.pb api_config.yaml 
APIVERSION=`gcloud service-management describe mygrpcendpoint.endpoints.$PROJECTID.cloud.goog | grep -o 'id:.*' | cut -c5-`

# delete previous deployment
kubectl delete deploy mygrpcendpoint
kubectl delete service mygrpcendpoint-loadbalancer
kubectl delete configmap nginx-config

docker build -t=gcr.io/$PROJECTID/mygrpcendpoint:latest --rm .
gcloud docker push gcr.io/$PROJECTID/mygrpcendpoint:latest

# generate swagger.json and build+push swaggerUI docker image
protoc -I/usr/local/include -Isrc/main/proto -I$GOPATH/src -I$GOPATH/src/github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis --swagger_out=./target/ ./src/main/proto/myapi.proto
docker build -t=gcr.io/$PROJECTID/mygrpcendpoint-swaggerui:latest --rm . -f swaggerui.Dockerfile
gcloud docker push gcr.io/$PROJECTID/mygrpcendpoint-swaggerui:latest

kubectl create configmap nginx-config --from-file=nginx.conf

# have to patch the yaml file with the id and the version of the API configuration
sed "s/apiversion/$APIVERSION/g" gke_config.yaml > tmp.yaml
kubectl create -f tmp.yaml
rm tmp.yaml

./bin/test.sh $APIKEY