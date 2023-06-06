package org.qo.tinymsg;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
public class Logger{
    private static Server server;
    public static void log(String Text){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(server.workingDirectory, true))) {
            writer.write(Text);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
