package org.qo.tinymsg;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;
import java.awt.event.ActionEvent;


public class ImageShow implements Runnable {
    private String imageUrl;
    Client client = new Client();

    public ImageShow(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public void run() {
        try {
            // Download the image from the URL
            BufferedImage originalImage = ImageIO.read(new URL(imageUrl));

            // Determine the maximum display size based on the screen resolution
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int maxWidth = screenSize.width - 100;
            int maxHeight = screenSize.height - 100;

            // Calculate the scale factor to fit the image within the maximum display size
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();
            double scaleFactor = Math.min(1.0, Math.min((double) maxWidth / originalWidth, (double) maxHeight / originalHeight));

            // Scale the image
            int scaledWidth = (int) (originalWidth * scaleFactor);
            int scaledHeight = (int) (originalHeight * scaleFactor);
            Image scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
            // Create a window
            JFrame frame = new JFrame("View Pic");
            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            // Create a label and set the scaled image as its icon
            JLabel label = new JLabel(new ImageIcon(scaledImage));
            frame.getContentPane().add(label);
            frame.setSize(scaledWidth, scaledHeight);
            // Make the window visible
            frame.setVisible(true);
            JMenuBar menuBar = new JMenuBar();
            frame.setJMenuBar(menuBar);
            JMenu fileMenu = new JMenu("Files");
            menuBar.add(fileMenu);
            JMenuItem saveItem = new JMenuItem("Save Image");
            fileMenu.add(saveItem);
            saveItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // 保存图片的逻辑
                    saveImage(originalImage);
                }
            });
            JPanel topPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(Color.WHITE);  // 设置矩形颜色
                    g.fillRect(5, 5, getWidth() - 50, getHeight() - 50);  // 绘制矩形
                    g.setColor(Color.BLACK);  // 设置文字颜色
                    g.setFont(new Font("Arial", Font.BOLD, 20));  // 设置文字字体
                    g.drawString("image source: " + imageUrl, 10, 20);  // 绘制文字
                }
            };

            // 设置顶部面板的大小和布局
            topPanel.setPreferredSize(new Dimension(frame.getWidth(), 50));
            topPanel.setLayout(new BorderLayout());

            // 将顶部面板添加到 JFrame 的顶部位置
            frame.getContentPane().add(topPanel, BorderLayout.NORTH);


        } catch (IOException e) {
            //
        }
    }

    private void saveImage(BufferedImage image) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Image");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "png"));

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try {
                ImageIO.write(image, "jpg", fileToSave);
                System.out.println("Image saved successfully.");
            } catch (IOException ex) {
                System.out.println("Failed to save file. error: " + ex);
            }
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
