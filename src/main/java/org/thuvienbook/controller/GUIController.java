package org.thuvienbook.controller;

import org.thuvienbook.Main;
import org.thuvienbook.model.*;
import org.thuvienbook.util.TextStorage;
import org.thuvienbook.util.DocumentStorage;
import org.thuvienbook.view.AddDocumentDialog;
import org.thuvienbook.view.LibraryGUI;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

public class GUIController {
    private final Library thuVien;
    private final LibraryGUI giaoDien;
    private final User nguoiDangNhap;

    public GUIController(Library thuVien, LibraryGUI giaoDien, User nguoiDangNhap) {
        this.thuVien = thuVien;
        this.giaoDien = giaoDien;
        this.nguoiDangNhap = nguoiDangNhap;

        if (nguoiDangNhap.getRole() == UserRole.ADMIN) {
            giaoDien.panelButton.add(giaoDien.btnThem);
            giaoDien.panelButton.add(giaoDien.btnXoa);
            giaoDien.panelButton.add(giaoDien.btnCapNhat);
            giaoDien.panelButton.add(giaoDien.btnHienThi);
            giaoDien.panelButton.add(giaoDien.btnThongKe);
            giaoDien.panelButton.add(giaoDien.btnTaiKhoan);
        } else {
            giaoDien.panelButton.add(giaoDien.btnHienThi);
            giaoDien.panelButton.add(giaoDien.btnMuon);
            giaoDien.panelButton.add(giaoDien.btnTra);
        }
        giaoDien.panelButton.add(giaoDien.btnThoat);

        giaoDien.btnThem.addActionListener(e -> themTaiLieu());
        giaoDien.btnXoa.addActionListener(e -> xoaTaiLieu());
        giaoDien.btnCapNhat.addActionListener(e -> capNhatTaiLieu());
        giaoDien.btnHienThi.addActionListener(e -> hienThiTatCa());
        giaoDien.btnMuon.addActionListener(e -> muonTaiLieu());
        giaoDien.btnTra.addActionListener(e -> traTaiLieu());
        giaoDien.btnThongKe.addActionListener(e -> hienThiThongKeMuon());
        giaoDien.btnTaiKhoan.addActionListener(e -> hienThiDanhSachNguoiDung());
        giaoDien.btnThoat.addActionListener(e -> System.exit(0));

        giaoDien.btnDangXuat.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc muốn đăng xuất?", "Đăng xuất",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                giaoDien.dispose();
                Main.main(null);
            }
        });

        if (giaoDien.btnSearch != null) {
            giaoDien.btnSearch.addActionListener(e -> timKiemTaiLieu());
        } else {
            System.err.println("❌ btnSearch is null");
        }

        hienThiTatCa();
        giaoDien.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String id = giaoDien.layMaTaiLieuDangChon();
                if (id != null) {
                    Document doc = thuVien.findDocument(id);
                    if (doc != null) {
                        String loai = (doc instanceof Book) ? "Sách" : "Luận văn";
                        JOptionPane.showMessageDialog(null,
                                "📘 Mã: " + doc.getId() +
                                        "\n📖 Tiêu đề: " + doc.getTitle() +
                                        "\n✍️ Tác giả: " + doc.getAuthor() +
                                        "\n📦 Số lượng: " + doc.getQuantity() +
                                        "\n📚 Loại: " + loai,
                                "Thông tin tài liệu", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });
    }

    public void showGUI() {
        giaoDien.setVisible(true);
    }

    private void hienThiTatCa() {
        Object[][] data = thuVien.getDocuments().stream()
                .map(d -> new Object[] { d.getId(), d.getTitle(), d.getAuthor(), d.getQuantity() })
                .toArray(Object[][]::new);
        giaoDien.capNhatBang(data);
        giaoDien.capNhatThongTinHeThong(thuVien.getDocuments().size(), thuVien.getUsers().size());
    }

    private void timKiemTaiLieu() {
        String keyword = giaoDien.tfSearch.getText().trim();
        List<Document> results;

        if (keyword.isEmpty()) {
            hienThiTatCa();
            JOptionPane.showMessageDialog(giaoDien, "Hiển thị tất cả tài liệu.");
            return;
        }

        results = thuVien.searchDocuments(keyword);

        Object[][] data = results.stream()
                .map(d -> new Object[] { d.getId(), d.getTitle(), d.getAuthor(), d.getQuantity() })
                .toArray(Object[][]::new);

        giaoDien.capNhatBang(data);
        JOptionPane.showMessageDialog(giaoDien, "✅ Tìm thấy " + results.size() + " tài liệu phù hợp.");
    }

    private void themTaiLieu() {
        AddDocumentDialog dialog = new AddDocumentDialog(giaoDien);
        dialog.setVisible(true);
        if (dialog.newDocument != null) {
            thuVien.addDocument(dialog.newDocument);
            DocumentStorage.save(thuVien.getDocuments());
            hienThiTatCa();
            JOptionPane.showMessageDialog(giaoDien, "✅ Đã thêm.");
        }
    }

    private void xoaTaiLieu() {
        String id = giaoDien.layMaTaiLieuDangChon();
        if (id == null) {
            id = JOptionPane.showInputDialog("Nhập mã tài liệu cần xoá:");
            if (id == null || id.trim().isEmpty()) return;
        }

        int confirm = JOptionPane.showConfirmDialog(giaoDien, "Bạn có chắc muốn xoá tài liệu có mã " + id + "?", "Xác nhận xoá", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (thuVien.removeDocument(id)) {
                DocumentStorage.save(thuVien.getDocuments());
                hienThiTatCa();
                JOptionPane.showMessageDialog(giaoDien, "✅ Đã xoá.");
            } else {
                JOptionPane.showMessageDialog(giaoDien, "❌ Không tìm thấy tài liệu có mã " + id + ".");
            }
        }
    }

    private void capNhatTaiLieu() {
        String id = giaoDien.layMaTaiLieuDangChon();
        if (id == null) {
            id = JOptionPane.showInputDialog("Nhập mã tài liệu cần cập nhật:");
            if (id == null || id.trim().isEmpty()) return;
        }

        Document doc = thuVien.findDocument(id);
        if (doc == null) {
            JOptionPane.showMessageDialog(giaoDien, "❌ Không tìm thấy tài liệu có mã " + id + ".");
            return;
        }

        JTextField tfTitle = new JTextField(doc.getTitle());
        JTextField tfAuthor = new JTextField(doc.getAuthor());
        JTextField tfQuantity = new JTextField(String.valueOf(doc.getQuantity()));

        Object[] form = {
                "Tiêu đề mới:", tfTitle,
                "Tác giả mới:", tfAuthor,
                "Số lượng mới:", tfQuantity
        };

        int option = JOptionPane.showConfirmDialog(giaoDien, form, "Cập nhật tài liệu: " + doc.getId(), JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String newTitle = tfTitle.getText().trim();
            String newAuthor = tfAuthor.getText().trim();
            String quantityText = tfQuantity.getText().trim();

            if (newTitle.isEmpty() || newAuthor.isEmpty() || quantityText.isEmpty()) {
                JOptionPane.showMessageDialog(this.giaoDien, "Vui lòng điền đầy đủ thông tin.");
                return;
            }

            try {
                int newQuantity = Integer.parseInt(quantityText);
                if (newQuantity < 0) throw new NumberFormatException();

                thuVien.updateDocument(id, newTitle, newAuthor, newQuantity);
                DocumentStorage.save(thuVien.getDocuments());
                hienThiTatCa();
                JOptionPane.showMessageDialog(giaoDien, "✅ Đã cập nhật tài liệu.");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(giaoDien, "Số lượng phải là số nguyên không âm.");
            }
        }
    }

    private void muonTaiLieu() {
        String id = JOptionPane.showInputDialog(giaoDien, "Nhập mã tài liệu muốn mượn:");
        if (id == null || id.trim().isEmpty()) return;

        Document doc = thuVien.findDocument(id);
        if (doc == null) {
            JOptionPane.showMessageDialog(giaoDien, "❌ Không tìm thấy tài liệu có mã " + id + ".");
            return;
        }

        if (thuVien.borrowDocument(nguoiDangNhap, id)) {
            System.out.println("Danh sách mượn của " + nguoiDangNhap.getUsername() + ": " + nguoiDangNhap.getBorrowedDocuments());
            TextStorage.saveUsers(thuVien.getUsers());
            DocumentStorage.save(thuVien.getDocuments());
            hienThiTatCa();
            JOptionPane.showMessageDialog(giaoDien, "📥 Mượn thành công.");
        } else {
            JOptionPane.showMessageDialog(giaoDien, "❌ Không thể mượn tài liệu có mã " + id + " (số lượng không đủ).");
        }
    }

    private void traTaiLieu() {
        String id = JOptionPane.showInputDialog(giaoDien, "Nhập mã tài liệu muốn trả:");
        if (id == null || id.trim().isEmpty()) return;

        Document doc = thuVien.findDocument(id);
        if (doc == null) {
            JOptionPane.showMessageDialog(giaoDien, "❌ Không tìm thấy tài liệu có mã " + id + ".");
            return;
        }

        if (thuVien.returnDocument(nguoiDangNhap, id)) {
            System.out.println("Danh sách mượn của " + nguoiDangNhap.getUsername() + " sau khi trả: " + nguoiDangNhap.getBorrowedDocuments());
            TextStorage.saveUsers(thuVien.getUsers());
            DocumentStorage.save(thuVien.getDocuments());
            hienThiTatCa();
            JOptionPane.showMessageDialog(giaoDien, "📤 Trả thành công.");
        } else {
            JOptionPane.showMessageDialog(giaoDien, "❌ Không thể trả tài liệu có mã " + id + " (chưa mượn hoặc không tồn tại).");
        }
    }

    private void hienThiThongKeMuon() {
        List<String> danhSach = thuVien.thongTinMuonTra();
        StringBuilder sb = new StringBuilder("📋 Danh sách mượn:\n");
        for (User u : thuVien.getUsers()) {
            if (!u.getBorrowedDocuments().isEmpty()) {
                System.out.println(u.getUsername() + " mượn: " + u.getBorrowedDocuments());
                sb.append("• ").append(u.getUsername()).append(" đã mượn:\n");
                for (Document doc : u.getBorrowedDocuments()) {
                    String loai = (doc instanceof Book) ? "Sách" : "Luận văn";
                    sb.append("  - ").append(doc.getTitle()).append(" (Loại: ").append(loai).append(", Số lượng: 1)\n");
                }
            }
        }
        if (sb.toString().equals("📋 Danh sách mượn:\n")) {
            sb.append("Không có ai mượn.");
        }

        try (PrintWriter pw = new PrintWriter("muon_tra.txt", "UTF-8")) {
            pw.println("DANH SÁCH MƯỢN:");
            for (User u : thuVien.getUsers()) {
                if (!u.getBorrowedDocuments().isEmpty()) {
                    pw.println("- " + u.getUsername() + " đã mượn:");
                    for (Document doc : u.getBorrowedDocuments()) {
                        String loai = (doc instanceof Book) ? "Sách" : "Luận văn";
                        pw.println("  - " + doc.getTitle() + " (Loại: " + loai + ", Số lượng: 1)");
                    }
                }
            }
            if (pw.checkError()) {
                throw new IOException("Ghi file thất bại.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(giaoDien, "❌ Ghi file thống kê thất bại.");
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(giaoDien, sb.toString(), "Thống kê Mượn Trả", JOptionPane.INFORMATION_MESSAGE);
    }

    private void hienThiDanhSachNguoiDung() {
        List<User> users = thuVien.getUsers();
        String[] columns = { "ID", "Tên đăng nhập", "Mật khẩu", "Họ tên", "Vai trò", "Sách mượn" };
        Object[][] data = new Object[users.size()][6];

        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            data[i][0] = u.getUserId();
            data[i][1] = u.getUsername();
            data[i][2] = u.getPassword();
            data[i][3] = u.getFullName();
            data[i][4] = u.getRole().name();
            data[i][5] = u.getBorrowedDocuments().size();
        }
        JTable table = new JTable(data, columns);
        table.setRowHeight(26);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);

        JButton btnAdd = new JButton("➕ Thêm");
        JButton btnEdit = new JButton("✏️ Sửa");
        JButton btnDelete = new JButton("🗑️ Xoá");
        JButton btnClose = new JButton("❌ Đóng");

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(btnAdd);
        bottomPanel.add(btnEdit);
        bottomPanel.add(btnDelete);
        bottomPanel.add(btnClose);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(new JLabel("📋 Quản lý tài khoản", SwingConstants.CENTER), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        JDialog dialog = new JDialog(giaoDien, "Tài khoản người dùng", true);
        dialog.setSize(700, 400);
        dialog.setLocationRelativeTo(giaoDien);
        dialog.add(panel);

        btnAdd.addActionListener(e -> {
            JTextField tfUser = new JTextField();
            JPasswordField tfPass = new JPasswordField();
            JTextField tfName = new JTextField();
            JComboBox<String> cbRole = new JComboBox<>(new String[] { "USER", "ADMIN" });

            Object[] form = { "Tên đăng nhập:", tfUser, "Mật khẩu:", tfPass, "Họ tên:", tfName, "Vai trò:", cbRole };

            int ok = JOptionPane.showConfirmDialog(dialog, form, "Thêm tài khoản", JOptionPane.OK_CANCEL_OPTION);
            if (ok == JOptionPane.OK_OPTION) {
                String username = tfUser.getText().trim();
                String password = new String(tfPass.getPassword()).trim();
                String fullName = tfName.getText().trim();
                String role = cbRole.getSelectedItem().toString();

                if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng điền đầy đủ thông tin.");
                    return;
                }

                if (thuVien.findUser(username) != null) {
                    JOptionPane.showMessageDialog(dialog, "❌ Tên đăng nhập đã tồn tại.");
                    return;
                }

                User u = new User(username, password, fullName, UserRole.valueOf(role));
                thuVien.addUser(u);
                TextStorage.saveUsers(thuVien.getUsers());
                dialog.dispose();
                hienThiDanhSachNguoiDung();
                JOptionPane.showMessageDialog(giaoDien, "✅ Đã thêm tài khoản.");
            }
        });

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String oldUsername = table.getValueAt(row, 1).toString();
                User user = thuVien.findUser(oldUsername);
                if (user == null || oldUsername.equalsIgnoreCase("admin")) {
                    JOptionPane.showMessageDialog(dialog, "❌ Không thể sửa tài khoản này.");
                    return;
                }
                JTextField tfUsername = new JTextField(user.getUsername());
                JTextField tfFullName = new JTextField(user.getFullName());
                JPasswordField tfPassword = new JPasswordField(user.getPassword());
                JComboBox<String> cbRole = new JComboBox<>(new String[] { "USER", "ADMIN" });
                cbRole.setSelectedItem(user.getRole().name());

                if (nguoiDangNhap.getRole() != UserRole.ADMIN || !nguoiDangNhap.getUsername().equalsIgnoreCase("admin")) {
                    tfUsername.setEditable(false);
                    if (user.getRole() == UserRole.ADMIN) {
                        cbRole.setEnabled(false);
                    }
                }

                Object[] form = { "Tên đăng nhập:", tfUsername, "Mật khẩu:", tfPassword, "Họ tên:", tfFullName, "Vai trò:", cbRole };

                int ok = JOptionPane.showConfirmDialog(dialog, form, "Sửa tài khoản: " + oldUsername, JOptionPane.OK_CANCEL_OPTION);
                if (ok == JOptionPane.OK_OPTION) {
                    String newFullName = tfFullName.getText().trim();
                    String newPassword = new String(tfPassword.getPassword()).trim();
                    String newRole = cbRole.getSelectedItem().toString();

                    if (newFullName.isEmpty() || newPassword.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "Vui lòng điền đầy đủ thông tin Họ tên và Mật khẩu.");
                        return;
                    }

                    user.setPassword(newPassword);
                    user.setFullName(newFullName);
                    user.setRole(UserRole.valueOf(newRole));

                    TextStorage.saveUsers(thuVien.getUsers());
                    dialog.dispose();
                    hienThiDanhSachNguoiDung();
                    JOptionPane.showMessageDialog(giaoDien, "✅ Đã cập nhật tài khoản.");
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "⚠️ Vui lòng chọn tài khoản cần sửa.");
            }
        });

        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String username = table.getValueAt(selectedRow, 1).toString();
                if (username.equalsIgnoreCase("admin")) {
                    JOptionPane.showMessageDialog(dialog, "❌ Không thể xoá tài khoản quản trị viên mặc định.");
                    return;
                }
                if (nguoiDangNhap.getRole() != UserRole.ADMIN && nguoiDangNhap.getUsername().equalsIgnoreCase(username)) {
                    JOptionPane.showMessageDialog(dialog, "❌ Bạn không có quyền tự xoá tài khoản của mình.");
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(dialog, "Bạn chắc chắn muốn xoá tài khoản " + username + "?",
                        "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    User user = thuVien.findUser(username);
                    if (user != null) {
                        thuVien.getUsers().remove(user);
                        TextStorage.saveUsers(thuVien.getUsers());
                        dialog.dispose();
                        hienThiDanhSachNguoiDung();
                        JOptionPane.showMessageDialog(giaoDien, "✅ Đã xoá tài khoản.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "⚠️ Vui lòng chọn tài khoản cần xoá.");
            }
        });

        btnClose.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }
}