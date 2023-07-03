# TINYMSG BY glowingstone124
## Release 1.4
Download Executable Files in Releases.

Now I use github pages to store Sources, Update Checkers, etc...

For some reason, please download latest source code at ``tmsg.glowingstone.cn/rawcode/latest.zip``

edit your Source to ``http://tmsg.nextage.top/source.json`` plz

If you want to connect to the Official server, please notice that server token is 0

Official Server: ``qoriginal.vip:1234``

Source Code is in the repo. 

If you occured an error in using this app, send me a issue.

## How to use this application?

### Server

At first, you need a Java Runtime Environment which verison over **17**.

Then use java -jar Server.jar to start server on PORT 1234.

then Server will automatically generate a config file called "config_server.json"

here is a sample of server config.

```
{"srvmsg":"CONNECT SUCCESS","port":1234,"workingDirectory":"This is your work dictionary","accessFile":"text.txt","token":"0","NOTOKEN": false, "NOPIC": false, "AllowRegister": true, "noUpdate":  false, "max-trhead": 100}
```

### Client

use java -jar Client.jar to start a client.

Client will automatically generate a config file called "client_cfg.json"

here is a sample of client config.

You can connect to some selected server using Source.
```
{"serverAddress":"localhost","serverPort":1234,"token":"Your custom token here", "Source":"http://qoriginal.vip:9090/instance.json", "fromSource":"NO"}
```
then connect to your server by the prompt.

**YOU SHOULD MAKE SURE THAT SERVERSIDE TOKEN IS SAME TO THE CLIENTSIDE**
