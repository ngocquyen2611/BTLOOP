package org.thuvienbook;

import com.formdev.flatlaf.FlatLightLaf;
import org.thuvienbook.model.Document;
import org.thuvienbook.model.Library;
import org.thuvienbook.model.User;
import org.thuvienbook.model.UserRole;
import org.thuvienbook.util.TextStorage;
import org.thuvienbook.util.DocumentStorage;
import org.thuvienbook.view.LoginGUI;

import javax.swing.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("❌ Không thể thiết lập FlatLaf");
        }

        SwingUtilities.invokeLater(() -> {
            Library thuVien = new Library();

            // 🔄 Load dữ liệu tài liệu trước
            List<Document> danhSachTaiLieu = DocumentStorage.load();
            danhSachTaiLieu.forEach(thuVien::addDocument);

            // 🔄 Load dữ liệu người dùng
            List<User> danhSachNguoiDung = TextStorage.loadUsers(thuVien);
            if (danhSachNguoiDung.isEmpty()) {
                danhSachNguoiDung.add(new User("admin", "admin123", "Quản trị viên", UserRole.ADMIN));
                danhSachNguoiDung.add(new User("user", "user123", "Người dùng", UserRole.USER));
                TextStorage.saveUsers(danhSachNguoiDung);
            }
            danhSachNguoiDung.forEach(thuVien::addUser);

            // Hiển thị giao diện đăng nhập mới
            LoginGUI loginGUI = new LoginGUI(thuVien);
            loginGUI.setVisible(true);
        });
    }
}