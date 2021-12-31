# Need to setup

1. setup the mongodb url as env variable
2. setup the secret key as env variable
3. add a user as owner {username: 'username', role:'OWNER'} and store in `users`
4. add `secrets` after encrypting the value as {key: 'keyName', value:'encryptedValue'}
   1. GITHUB_CLIENT_ID
   2. GITHUB_CLIENT_SECRET
