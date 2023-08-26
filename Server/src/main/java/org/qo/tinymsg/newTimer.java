package org.qo.tinymsg;
import java.util.Timer;
import java.util.TimerTask;

public class newTimer {
    private Timer timer;
    public static int counter;

    public newTimer() {
        timer = new Timer();
        counter = 0;
    }

    public void start() {
        TimerTask task = new TimerTask() {
            public void run() {
                counter++;
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1);
    }

    public void stop() {
        timer.cancel();
    }
}
