package org.thuvienbook.view;

import org.thuvienbook.util.GoogleBooksAPI;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Map;

public class BookDetailDialog extends JDialog {
    public BookDetailDialog(Frame owner, String isbn) {
        super(owner, "Chi Tiết Sách", true);
        setSize(400, 500);
        setLocationRelativeTo(owner);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        try {
            Map<String, String> bookInfo = GoogleBooksAPI.getInfoByISBN(isbn);
            String title = bookInfo.get("title");
            String author = bookInfo.get("author");
            String thumbnailUrl = bookInfo.get("thumbnail");

            // Panel cho hình ảnh bìa
            JLabel lblThumbnail = new JLabel();
            if (!thumbnailUrl.isEmpty()) {
                URL imageUrl = new URL(thumbnailUrl);
                ImageIcon icon = new ImageIcon(imageUrl);
                Image img = icon.getImage().getScaledInstance(200, 300, Image.SCALE_SMOOTH);
                lblThumbnail.setIcon(new ImageIcon(img));
            } else {
                lblThumbnail.setText("Không có bìa sách");
            }
            lblThumbnail.setHorizontalAlignment(JLabel.CENTER);
            mainPanel.add(lblThumbnail, BorderLayout.NORTH);

            // Panel cho thông tin sách
            JPanel infoPanel = new JPanel(new GridLayout(0, 1, 10, 10));
            infoPanel.setOpaque(false);
            infoPanel.add(new JLabel("Tiêu đề: " + (title.isEmpty() ? "Không có thông tin" : title)));
            infoPanel.add(new JLabel("Tác giả: " + (author.isEmpty() ? "Không có thông tin" : author)));
            infoPanel.add(new JLabel("ISBN: " + isbn));
            mainPanel.add(infoPanel, BorderLayout.CENTER);

            // Nút đóng
            JButton btnClose = new JButton("Đóng");
            btnClose.addActionListener(e -> dispose());
            mainPanel.add(btnClose, BorderLayout.SOUTH);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải thông tin sách: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        add(mainPanel);
    }
}