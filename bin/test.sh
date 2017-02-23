# Wait until the load balancer gets assigned a public IP
APIKEY=$1

echo -n 'Waiting for public IP...'
while kubectl get service | grep myapi-grpc-java-loadbalancer | grep -q pending
do
    echo -n '.'
    sleep 1
done
HOST=`kubectl get service | grep myapi-grpc-java-loadbalancer | awk '{print $3}'`
echo $HOST

# Health check
echo -n 'Testing gRPC endpoint...'
unhealthy=1 
tries=0
max_tries=10
while [ $unhealthy -ne 0 ]
do
    echo -n '.'
    java -cp target/myapi-1.0.0-jar-with-dependencies.jar com.slb.grpc.myapi.MyApiClient --host $HOST --port 8000 --key $APIKEY> /dev/null 2>&1
    unhealthy=$?
    
    tries=$((tries + 1))
    if [ $tries -eq $max_tries ]
    then
        break
    fi
    
    sleep 1
done

if [ $unhealthy -ne 0 ]
then
    echo 'FAILED'
else
    echo 'OK'
fi

echo -n 'Testing REST endpoint...'
curl -s -k http://$HOST:8001/v1/health?key=$APIKEY | grep "\"status\":\"ok\"" > /dev/null 2>&1
if [ $? -ne 0 ]
then
    echo 'FAILED'
else
    echo 'OK'
fi

echo -n 'Testing swagger.json...'
# just grep for some random marker inside the swagger.json file
curl -s -k http://$HOST:8001/swaggerui/swagger.json | grep Ping > /dev/null
if [ $? -ne 0 ]
then
    echo 'FAILED'
else
    echo 'OK'
fi

echo -n 'Testing SwaggerUI...'
# just grep for some random marker inside the index.html file
curl -s -k http://$HOST:8001/swaggerui/index.html | grep swagger.json > /dev/null
if [ $? -ne 0 ]
then
    echo 'FAILED'
else
    echo 'OK'
fi

echo -n 'Testing CORS...'
curl -s -k -i -X OPTIONS http://$HOST:8001 | grep "Access-Control-Allow-Origin: *" > /dev/null
if [ $? -ne 0 ]
then
    echo 'FAILED'
else
    echo 'OK'
fi