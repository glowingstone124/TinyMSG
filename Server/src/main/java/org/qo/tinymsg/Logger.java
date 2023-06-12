package org.qo.tinymsg;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;
public class Logger {
    public static long time = System.currentTimeMillis();
    static Server server = new Server();
    public static void log(String message){
        String logFilePath = Server.LOG_FILE;
        try {
            FileWriter fileWriter = new FileWriter(logFilePath, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("[LOG]" + message);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (IOException e) {
            server.log("unable to write log. printed error messages below : " + e, 2);
        }
    }
    public static void startup() {
        String logFilePath = Server.LOG_FILE;
        try {
                FileWriter fileWriter = new FileWriter(logFilePath, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write("[Startup]" + server.ServerVersion + "loaded. Current timestamp is " + time );
                bufferedWriter.newLine();
                bufferedWriter.write("[Startup] server bind at port " + server.port );
                bufferedWriter.newLine();
                bufferedWriter.close();
        } catch (IOException e){
            server.log("unable to create log file. please check if you have permission." + e, 2);
        }
    }
}