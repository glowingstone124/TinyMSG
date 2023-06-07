# TINYMSG BY glowingstone124
## Release 1.1.1
Download Executable Files in Releases.

Source Code is in the repo. 

If you occured an error in using this app, send me a issue.

## How to use this application?

### Server

At first, you need a Java Runtime Environment which verison over **17**.

Then use java -jar Server.jar to start server on PORT 1234.

then Server will automatically generate a config file called "config_server.json"

here is a sample of server config.

```
{"srvmsg":"CONNECT SUCCESS","port":1234,"workingDirectory":"this/is/your/work/dictionary","accessFile":"text.txt","token":"this will automatically generate"}
```

### Client

use java -jar Client.jar to start a client.

Client will automatically generate a config file called "client_cfg.json"

here is a sample of client config.

```
{"serverAddress":"localhost","serverPort":1234,"token":"Your custom token here"}
```
then connect to your server by the prompt.

**YOU SHOULD MAKE SURE THAT SERVERSIDE TOKEN IS SAME TO THE CLIENTSIDE**
