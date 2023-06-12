package org.qo.tinymsg;
public class Debugger {
    Server server = new Server();
    Logger logger = new Logger();
    public void log(String message,int lvl){
     server.log(message, lvl);
    }
    public void outputlog(String message) {
        logger.log(message);
    }
}
