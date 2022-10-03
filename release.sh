#!/bin/bash
EXIT_STATUS=0
./gradlew test || EXIT_STATUS=$?
if [ $EXIT_STATUS -ne 0 ]; then
 exit $EXIT_STATUS
fi
./gradlew :app-lambda-graalvm:buildNativeLambda -Pmicronaut.runtime=lambda || EXIT_STATUS=$?
if [ $EXIT_STATUS -ne 0 ]; then
 exit $EXIT_STATUS
fi
./gradlew :app-lambda-java:shadowJar || EXIT_STATUS=$?
if [ $EXIT_STATUS -ne 0 ]; then
 exit $EXIT_STATUS
fi
./gradlew :function-cognito-post-confirmation:shadowJar || EXIT_STATUS=$?
if [ $EXIT_STATUS -ne 0 ]; then
 exit $EXIT_STATUS
fi
./gradlew :function-lambda-websockets:shadowJar || EXIT_STATUS=$?
if [ $EXIT_STATUS -ne 0 ]; then
 exit $EXIT_STATUS
fi
cd infra
cdk deploy --profile mntodo --require-approval never || EXIT_STATUS=$?
if [ $EXIT_STATUS -ne 0 ]; then
  exit $EXIT_STATUS
fi
cd ..
exit $EXIT_STATUS
