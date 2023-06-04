package org.qo.tinymsg;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageShow implements Runnable {
    private String filePath;

    public ImageShow(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void run() {
        File file = new File(filePath);
        BufferedImage image;
        try {
            image = ImageIO.read(file);

            // 创建一个窗口
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // 创建一个标签，并将图片设置为标签的图像
            JLabel label = new JLabel(new ImageIcon(image));
            frame.getContentPane().add(label);

            // 自适应窗口大小以适应图像
            frame.pack();

            // 设置窗口可见
            frame.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createAndShowGUI() {
        // 创建 ImageShow 对象，并传入图片文件路径
        ImageShow imageShow = new ImageShow(filePath);

        // 创建线程并启动
        Thread thread = new Thread(imageShow);
        thread.start();
    }

    public static void main(String args) {
            String filePath = args;

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    ImageShow program = new ImageShow(filePath);
                    program.createAndShowGUI();
                }
            });
    }
}
