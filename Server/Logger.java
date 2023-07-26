package org.qo.tinymsg;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    public static long time = System.currentTimeMillis();
    static Server server = new Server();
    public static void log(String message){
        String logFilePath = Server.LOG_FILE;
        try {
            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            Date date = new Date(System.currentTimeMillis());
            FileWriter fileWriter = new FileWriter(logFilePath, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("[LOG " + date + " ]" + message);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (IOException e) {
            server.log("unable to write log. printed error messages below : " + e, 2);
        }
    }
    public static void startup(){
        String logFilePath = Server.LOG_FILE;
        try {
            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            Date date = new Date(System.currentTimeMillis());
                FileWriter fileWriter = new FileWriter(logFilePath, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write("[Startup]" + server.ServerVersion + " loaded. Inner version: " + server.innerVersion +" Current time is " + date);
                bufferedWriter.newLine();
                bufferedWriter.write("[Startup] server bind at port " + server.port );
                bufferedWriter.newLine();
                bufferedWriter.close();
        } catch (IOException e){
            server.log("unable to create log file. please check if you have permission." + e, 2);
        }
    }
}