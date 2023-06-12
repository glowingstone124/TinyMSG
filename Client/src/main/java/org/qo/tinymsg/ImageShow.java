package org.qo.tinymsg;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

public class ImageShow implements Runnable {
    private String imageUrl;

    public ImageShow(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public void run() {
        try {
            // Download the image from the URL
            BufferedImage image = ImageIO.read(new URL(imageUrl));

            // Create a window
            JFrame frame = new JFrame("View Pic");
            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

            // Create a label and set the image as its icon
            JLabel label = new JLabel(new ImageIcon(image));
            frame.getContentPane().add(label);

            // Adapt window size to fit the image
            frame.pack();

            // Make the window visible
            frame.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createAndShowGUI() {
        // Create ImageShow object and pass the image URL
        ImageShow imageShow = new ImageShow(imageUrl);

        // Create a thread and start it
        Thread thread = new Thread(imageShow);
        thread.start();
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            String imageUrl = args[0];

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    ImageShow program = new ImageShow(imageUrl);
                    program.createAndShowGUI();
                }
            });
        } else {
            System.out.println("Please provide the URL of an image as a command-line argument.");
        }
    }
}
