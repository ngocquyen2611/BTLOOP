package org.thuvienbook.view;

import org.thuvienbook.controller.GUIController;
import org.thuvienbook.model.Library;
import org.thuvienbook.model.User;
import org.thuvienbook.model.UserRole;
import org.thuvienbook.util.AuthService;
import org.thuvienbook.util.TextStorage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URL;

// Lớp LoginGUI đại diện cho giao diện đăng nhập và đăng ký người dùng
public class LoginGUI extends JFrame {
    private final Library thuVien; // Đối tượng thư viện để truy cập dữ liệu người dùng
    private final JTextField tfUsername; // Trường nhập tên đăng nhập
    private final JPasswordField pfPassword; // Trường nhập mật khẩu
    private final JTextField tfFullName; // Trường nhập họ tên (chỉ khi đăng ký)
    private final JLabel lblError; // Nhãn hiển thị thông báo lỗi
    private final JPanel registerPanel; // Panel chứa trường Họ tên (ẩn/hiện)
    private boolean isRegisterMode; // Biến cờ xác định đang ở chế độ đăng ký hay đăng nhập

    // Constructor nhận đối tượng Library
    public LoginGUI(Library thuVien) {
        this.thuVien = thuVien;
        this.isRegisterMode = false; // Mặc định ở chế độ đăng nhập

        // Thiết lập JFrame (tiêu đề, kích thước, vị trí, hành động đóng, không cho thay đổi kích thước)
        setTitle("📚 Thư Viện Số - Đăng Nhập");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // Panel chính với BorderLayout và khoảng đệm
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 244, 255)); // Màu nền xanh nhạt

        // Thêm hình ảnh nền hoặc logo (nếu tìm thấy)
        JLabel lblBackground = new JLabel();
        // Đường dẫn tới hình ảnh trong thư mục resources
        // Đảm bảo file ảnh của bạn có tên là image.png và nằm trong thư mục resources
        URL imageUrl = getClass().getResource("/image.png"); // <-- Kiểm tra lại đường dẫn này
        if (imageUrl != null) {
            ImageIcon icon = new ImageIcon(imageUrl);
            // Thay đổi kích thước hình ảnh
            Image img = icon.getImage().getScaledInstance(250, 180, Image.SCALE_SMOOTH);
            lblBackground.setIcon(new ImageIcon(img));
            lblBackground.setHorizontalAlignment(JLabel.CENTER);
            mainPanel.add(lblBackground, BorderLayout.NORTH);
        } else {
            // Nếu không tìm thấy hình ảnh, hiển thị tiêu đề text thay thế
            System.err.println("Không tìm thấy hình ảnh tại /image.png. Kiểm tra đường dẫn hoặc file trong resources.");
            JLabel lblTitle = new JLabel("Thư Viện Số", SwingConstants.CENTER);
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
            lblTitle.setForeground(new Color(33, 150, 243)); // Màu xanh
            mainPanel.add(lblTitle, BorderLayout.NORTH);
        }

        // Panel chứa các trường nhập liệu (sử dụng GridBagLayout để căn chỉnh)
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(false); // Nền trong suốt
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10); // Khoảng cách giữa các thành phần
        gbc.fill = GridBagConstraints.HORIZONTAL; // Lấp đầy chiều ngang
        gbc.weightx = 1.0; // Cho phép các trường nhập liệu mở rộng theo chiều ngang

        // --- Bố cục cho Tên đăng nhập ---
        JLabel lblUsername = new JLabel("Tên đăng nhập:");
        lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; // Cột 0
        gbc.gridy = 0; // Hàng 0
        gbc.anchor = GridBagConstraints.EAST; // Căn phải nhãn
        gbc.weightx = 0; // Nhãn không mở rộng theo chiều ngang
        inputPanel.add(lblUsername, gbc);

        tfUsername = new JTextField(15);
        tfUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1; // Cột 1
        gbc.gridy = 0; // Hàng 0
        gbc.anchor = GridBagConstraints.WEST; // Căn trái trường nhập
        gbc.weightx = 1.0; // Trường nhập mở rộng theo chiều ngang
        inputPanel.add(tfUsername, gbc);

        // --- Bố cục cho Mật khẩu ---
        JLabel lblPassword = new JLabel("Mật khẩu:");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; // Cột 0
        gbc.gridy = 1; // Hàng 1
        gbc.anchor = GridBagConstraints.EAST; // Căn phải nhãn
        gbc.weightx = 0; // Nhãn không mở rộng
        inputPanel.add(lblPassword, gbc);

        pfPassword = new JPasswordField(15);
        pfPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1; // Cột 1
        gbc.gridy = 1; // Hàng 1
        gbc.anchor = GridBagConstraints.WEST; // Căn trái trường nhập
        gbc.weightx = 1.0; // Trường nhập mở rộng
        inputPanel.add(pfPassword, gbc);

        // --- Panel chứa trường Họ tên (chỉ hiển thị khi đăng ký) ---
        registerPanel = new JPanel(new GridBagLayout());
        registerPanel.setOpaque(false);
        GridBagConstraints gbcReg = new GridBagConstraints();
        gbcReg.insets = new Insets(5, 10, 5, 10); // Khoảng cách
        gbcReg.fill = GridBagConstraints.HORIZONTAL;
        gbcReg.weightx = 1.0; // Cho phép trường Họ tên mở rộng

        JLabel lblFullName = new JLabel("Họ tên:");
        lblFullName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbcReg.gridx = 0; // Cột 0 trong registerPanel
        gbcReg.gridy = 0; // Hàng 0 trong registerPanel
        gbcReg.anchor = GridBagConstraints.EAST; // Căn phải nhãn
        gbcReg.weightx = 0; // Nhãn không mở rộng
        registerPanel.add(lblFullName, gbcReg);

        tfFullName = new JTextField(15);
        tfFullName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbcReg.gridx = 1; // Cột 1 trong registerPanel
        gbcReg.gridy = 0; // Hàng 0 trong registerPanel
        gbcReg.anchor = GridBagConstraints.WEST; // Căn trái trường nhập
        gbcReg.weightx = 1.0; // Trường nhập mở rộng
        registerPanel.add(tfFullName, gbcReg);

        // --- Thêm registerPanel vào inputPanel ---
        gbc.gridx = 0; // Bắt đầu từ cột 0 của inputPanel
        gbc.gridy = 2; // Đặt dưới hàng Mật khẩu (hàng 1)
        gbc.gridwidth = 2; // Chiếm cả 2 cột của inputPanel
        gbc.weighty = 1.0; // <-- Quan trọng: Cho phép panel đăng ký nhận thêm không gian dọc
        gbc.fill = GridBagConstraints.BOTH; // Cho phép panel lấp đầy cả chiều ngang và dọc
        inputPanel.add(registerPanel, gbc);
        registerPanel.setVisible(false); // Ẩn panel đăng ký ban đầu

        mainPanel.add(inputPanel, BorderLayout.CENTER); // Thêm inputPanel vào panel chính

        // Panel chứa các nút chức năng (Đăng nhập, Đăng ký, Thoát)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(false);

        // Tạo và tùy chỉnh các nút
        JButton btnLogin = createStyledButton("Đăng nhập", new Color(33, 150, 243)); // Màu xanh
        JButton btnRegister = createStyledButton("Đăng ký", new Color(255, 152, 0)); // Màu cam
        JButton btnExit = createStyledButton("Thoát", new Color(158, 158, 158)); // Màu xám

        // Thêm các nút vào buttonPanel
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegister);
        buttonPanel.add(btnExit);

        // Nhãn hiển thị thông báo lỗi
        lblError = new JLabel("", SwingConstants.CENTER);
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblError.setForeground(Color.RED);
        mainPanel.add(lblError, BorderLayout.SOUTH); // Đặt nhãn lỗi ở cuối panel chính

        mainPanel.add(buttonPanel, BorderLayout.PAGE_END); // Đặt buttonPanel ở cuối trang

        add(mainPanel); // Thêm panel chính vào JFrame

        // Xử lý sự kiện cho các nút
        btnLogin.addActionListener(e -> {
            if (isRegisterMode) {
                toggleMode(false); // Nếu đang ở chế độ đăng ký, chuyển về đăng nhập
            } else {
                handleLogin(); // Nếu đang ở chế độ đăng nhập, xử lý đăng nhập
            }
        });

        btnRegister.addActionListener(e -> {
            if (isRegisterMode) {
                handleRegister(); // Nếu đang ở chế độ đăng ký, xử lý đăng ký
            } else {
                toggleMode(true); // Nếu đang ở chế độ đăng nhập, chuyển sang đăng ký
            }
        });

        btnExit.addActionListener(e -> System.exit(0)); // Thoát ứng dụng khi nhấn nút Thoát
    }

    // Phương thức tạo nút với style tùy chỉnh (tương tự trong LibraryGUI)
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker()),
                new EmptyBorder(8, 16, 8, 16)));
        return btn;
    }

    // Chuyển đổi giữa chế độ Đăng nhập và Đăng ký
    private void toggleMode(boolean registerMode) {
        this.isRegisterMode = registerMode;
        if (registerMode) {
            setTitle("📚 Thư Viện Số - Đăng Ký");
            registerPanel.setVisible(true); // Hiển thị panel Họ tên
            lblError.setText(""); // Xóa thông báo lỗi
        } else {
            setTitle("📚 Thư Viện Số - Đăng Nhập");
            registerPanel.setVisible(false); // Ẩn panel Họ tên
            tfFullName.setText(""); // Xóa nội dung trường Họ tên
            lblError.setText(""); // Xóa thông báo lỗi
        }
        // Gọi revalidate() và repaint() để cập nhật lại giao diện sau khi thay đổi hiển thị
        revalidate();
        repaint();
    }

    // Xử lý logic đăng nhập
    private void handleLogin() {
        String username = tfUsername.getText().trim();
        String password = new String(pfPassword.getPassword()).trim();

        // Kiểm tra các trường bắt buộc
        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Vui lòng điền đầy đủ tên đăng nhập và mật khẩu.");
            return;
        }

        // Sử dụng AuthService để kiểm tra thông tin đăng nhập
        AuthService auth = new AuthService(thuVien.getUsers());
        User user = auth.login(username, password);
        if (user != null) {
            dispose(); // Đóng giao diện đăng nhập
            // Khởi tạo giao diện chính LibraryGUI và Controller
            LibraryGUI gui = new LibraryGUI();
            GUIController ctrl = new GUIController(thuVien, gui, user);
            ctrl.showGUI(); // Hiển thị giao diện chính
        } else {
            lblError.setText("Sai tên đăng nhập hoặc mật khẩu."); // Thông báo lỗi
        }
    }

    // Xử lý logic đăng ký
    private void handleRegister() {
        String username = tfUsername.getText().trim();
        String password = new String(pfPassword.getPassword()).trim();
        String fullName = tfFullName.getText().trim();

        // Kiểm tra các trường bắt buộc
        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
            lblError.setText("Vui lòng điền đầy đủ thông tin.");
            return;
        }

        // Kiểm tra tên đăng nhập đã tồn tại chưa
        if (thuVien.findUser(username) != null) {
            lblError.setText("Tài khoản đã tồn tại.");
            return;
        }

        // Tạo người dùng mới với vai trò USER
        User newUser = new User(username, password, fullName, UserRole.USER);
        thuVien.addUser(newUser); // Thêm người dùng vào thư viện
        TextStorage.saveUsers(thuVien.getUsers()); // Lưu dữ liệu người dùng vào file
        lblError.setText("Đăng ký thành công! Vui lòng đăng nhập."); // Thông báo thành công
        toggleMode(false); // Chuyển về chế độ đăng nhập
    }
}
