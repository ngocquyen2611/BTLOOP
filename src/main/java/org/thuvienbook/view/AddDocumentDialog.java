package org.thuvienbook.view;

import org.thuvienbook.model.Book;
import org.thuvienbook.model.Document;
import org.thuvienbook.model.Thesis;
import org.thuvienbook.util.GoogleBooksAPI;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class AddDocumentDialog extends JDialog {
    public Document newDocument;

    public AddDocumentDialog(JFrame parent) {
        super(parent, "➕ Thêm tài liệu", true);
        setSize(400, 350);
        setLocationRelativeTo(parent);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField tfTitle = new JTextField();
        JTextField tfAuthor = new JTextField();
        JTextField tfQuantity = new JTextField();
        JComboBox<String> cbType = new JComboBox<>(new String[]{"Sách", "Luận văn"});
        JTextField tfISBN = new JTextField();
        JButton btnISBN = new JButton("Tự động điền từ ISBN");

        // Kiểm soát nút ISBN dựa trên loại tài liệu
        btnISBN.setEnabled(cbType.getSelectedItem().equals("Sách"));
        tfISBN.setEnabled(cbType.getSelectedItem().equals("Sách"));
        cbType.addActionListener(e -> {
            boolean isBook = cbType.getSelectedItem().equals("Sách");
            btnISBN.setEnabled(isBook);
            tfISBN.setEnabled(isBook);
            if (!isBook) {
                tfISBN.setText(""); // Xóa ISBN khi chuyển sang Luận văn
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Tiêu đề:"), gbc);
        gbc.gridx = 1;
        add(tfTitle, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Tác giả:"), gbc);
        gbc.gridx = 1;
        add(tfAuthor, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Số lượng:"), gbc);
        gbc.gridx = 1;
        add(tfQuantity, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Loại:"), gbc);
        gbc.gridx = 1;
        add(cbType, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1;
        add(tfISBN, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        add(btnISBN, gbc);
        gbc.gridx = 1;
        add(new JLabel(), gbc);

        JButton btnLuu = new JButton("Lưu");
        JButton btnHuy = new JButton("Hủy");
        gbc.gridx = 0;
        gbc.gridy = 6;
        add(btnLuu, gbc);
        gbc.gridx = 1;
        add(btnHuy, gbc);

        btnLuu.addActionListener(e -> {
            String title = tfTitle.getText().trim();
            String author = tfAuthor.getText().trim();
            String quantityText = tfQuantity.getText().trim();

            if (title.isEmpty() || author.isEmpty() || quantityText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn lưu tài liệu này?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            try {
                int quantity = Integer.parseInt(quantityText);
                if (quantity <= 0) throw new NumberFormatException();

                newDocument = cbType.getSelectedItem().equals("Sách")
                        ? new Book(title, author, quantity)
                        : new Thesis(title, author, quantity);
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Số lượng phải là số nguyên dương.");
            }
        });

        btnISBN.addActionListener(e -> {
            String isbn = tfISBN.getText().trim();
            if (isbn.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập ISBN.");
                return;
            }

            btnISBN.setEnabled(false);
            btnISBN.setText("Đang tìm...");
            new SwingWorker<Map<String, String>, Void>() {
                Map<String, String> data;
                String errorMessage;

                @Override
                protected Map<String, String> doInBackground() {
                    try {
                        return GoogleBooksAPI.getInfoByISBN(isbn);
                    } catch (Exception ex) {
                        errorMessage = ex.getMessage();
                        return null;
                    }
                }

                @Override
                protected void done() {
                    try {
                        data = get();
                        if (data != null && !data.isEmpty()) {
                            tfTitle.setText(data.get("title"));
                            tfAuthor.setText(data.get("author"));
                            tfQuantity.setText("1");
                        } else {
                            JOptionPane.showMessageDialog(AddDocumentDialog.this,
                                    errorMessage != null ? errorMessage : "Không tìm thấy thông tin cho ISBN này.");
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(AddDocumentDialog.this, "Lỗi không xác định: " + ex.getMessage());
                    } finally {
                        btnISBN.setEnabled(true);
                        btnISBN.setText("Tự động điền từ ISBN");
                    }
                }
            }.execute();
        });

        btnHuy.addActionListener(e -> dispose());
    }
}