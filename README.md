## Requirements

- [AWS CLI](https://aws.amazon.com/cli/)
- [AWS CDK](https://aws.amazon.com/cdk/)
- Java 11
- x86 Machine (You will be generating a native image for a custom runtime x86)

## How to Deploy 

Make sure you are using java 11

```
% java -version
openjdk version "11.0.16" 2022-07-19 LTS
OpenJDK Runtime Environment Corretto-11.0.16.8.1 (build 11.0.16+8-LTS)
OpenJDK 64-Bit Server VM Corretto-11.0.16.8.1 (build 11.0.16+8-LTS, mixed mode)
```

Use AWS CLI to authenticate.

```
% aws configure
AWS Access Key ID [****************]: 
AWS Secret Access Key [****************]: 
Default region name [us-east-1]: us-east-2
Default output format [None]:
```

**If you have never run CDK in this AWS account, do `cd infra;cdk bootstrap` first.**

Run the release script

```
% ./release.sh
```

You should see: 

```
✨  Deployment time: 130.72s

Outputs:
mntodoAppStack.GraalVMNativeApi = https://oh2vsk1di4.execute-api.us-east-2.amazonaws.com/prod/
mntodoAppStack.JavaApi = https://x6vd5dxda0.execute-api.us-east-2.amazonaws.com/prod/
mntodoAppStack.JavaExtraApi = https://8p434p2pif.execute-api.us-east-2.amazonaws.com/prod/
```

- Go to the console. 
- Enable feature for extra lambda.
- Publish a version
- Go to `infra/src/main/java/com/micronauttodo/AppStack.java`
- Search for TODO. comment and uncomment so that the API Gateway point to `prod` alias which should point to the version you published for the extra lambda.

- Visit the url of API Gateway connected to the three lambdas. The three use the same DynamoDB database. Thus, if you create an item in the database you will see it in all of them. 
