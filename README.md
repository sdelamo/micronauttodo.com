A todo application built in Java and the [Micronaut Framework](https://micronaut.io).

Online at http://serverlesstodo.com

The application shows different runtimes and persistence options. 

## Runtime

### Netty + Microstream

To run the app in development mode run: 

```
./gradlew :app-netty-microstream:run -t
```

### Netty + DynamoDB

To run the app in development mode run:

```
./gradlew :app-netty-dynamodb:run -t
```

#### DynamoDB Local

For a runtime using DynamoDB, you can run [DynamoDB Local via Docker](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.html)

```
docker run -it --rm -p 8000:8000 amazon/dynamodb-local
```
