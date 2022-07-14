## Architecture

![](architecture1.jpeg)

## Modules

![](modules.jpeg)

## Manual Steps

## expose OAuth Client Secret as an env variable to LAMBDA

![](client-secret.png)



## A Record for auth subdomain

Create an A Record for `auth.` with an alias to your cognito user pool cloud front distribution

![](cognito-user-pool-cloudfrontdistribution.png)

![](route-53-arecord.png)