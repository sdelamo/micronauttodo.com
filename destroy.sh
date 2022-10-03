#!/bin/bash
EXIT_STATUS=0
cd infra
cdk destroy --profile mntodo --require-approval never || EXIT_STATUS=$?
if [ $EXIT_STATUS -ne 0 ]; then
  exit $EXIT_STATUS
fi
cd ..
exit $EXIT_STATUS
