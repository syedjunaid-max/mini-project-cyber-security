package gui;

import steg.SteganographyUtil;
import crypto.AESUtil;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class SteganographyApp {
    private static JLabel imagePreviewLabel;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Steganography Tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {
        JLabel msgLabel = new JLabel("Message:");
        msgLabel.setBounds(20, 20, 80, 25);
        panel.add(msgLabel);

        JTextField messageText = new JTextField(20);
        messageText.setBounds(100, 20, 450, 25);
        panel.add(messageText);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(20, 60, 80, 25);
        panel.add(passLabel);

        JTextField passwordText = new JTextField(20);
        passwordText.setBounds(100, 60, 450, 25);
        panel.add(passwordText);

        JButton encodeButton = new JButton("Encode Image");
        encodeButton.setBounds(100, 100, 150, 30);
        panel.add(encodeButton);

        JButton decodeButton = new JButton("Decode Image");
        decodeButton.setBounds(300, 100, 150, 30);
        panel.add(decodeButton);

        imagePreviewLabel = new JLabel();
        imagePreviewLabel.setBounds(100, 150, 400, 300);
        imagePreviewLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.add(imagePreviewLabel);

        encodeButton.addActionListener((ActionEvent e) -> {
            try {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "png", "bmp"));
                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    BufferedImage image = ImageIO.read(file);

                    // Show preview
                    ImageIcon icon = new ImageIcon(image.getScaledInstance(400, 300, Image.SCALE_SMOOTH));
                    imagePreviewLabel.setIcon(icon);

                    String message = messageText.getText();
                    String password = passwordText.getText();
                    String encrypted = AESUtil.encrypt(message, password);

                    BufferedImage encodedImage = SteganographyUtil.hideMessage(image, encrypted);

                    JFileChooser saveChooser = new JFileChooser();
                    saveChooser.setDialogTitle("Save As");
                    saveChooser.setFileFilter(new FileNameExtensionFilter("PNG Image", "png"));
                    if (saveChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                        File saveFile = saveChooser.getSelectedFile();
                        ImageIO.write(encodedImage, "png", saveFile);
                        JOptionPane.showMessageDialog(null, "Message hidden successfully!");
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            }
        });

        decodeButton.addActionListener((ActionEvent e) -> {
            try {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "png", "bmp"));
                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    BufferedImage image = ImageIO.read(file);

                    // Show preview
                    ImageIcon icon = new ImageIcon(image.getScaledInstance(400, 300, Image.SCALE_SMOOTH));
                    imagePreviewLabel.setIcon(icon);

                    String password = passwordText.getText();
                    String extracted = SteganographyUtil.extractMessage(image, 128); // 128-byte buffer
                    String decrypted = AESUtil.decrypt(extracted, password);

                    JOptionPane.showMessageDialog(null, "Decrypted message: " + decrypted);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            }
        });
    }
}
