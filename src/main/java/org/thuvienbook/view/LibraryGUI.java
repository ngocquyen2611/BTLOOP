package org.thuvienbook.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class LibraryGUI extends JFrame {
    public JButton btnThem, btnXoa, btnCapNhat, btnHienThi, btnMuon, btnTra, btnThongKe, btnThoat, btnTaiKhoan,
            btnDangXuat, btnSearch;
    public JTextField tfSearch; // Sử dụng JTextField thay vì Label để nhập liệu
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel thongTinHeThong;
    public JPanel panelButton;

    public LibraryGUI() {
        setTitle("📚 Quản Lý Thư Viện");
        setSize(960, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Tạo nút chức năng
        btnThem = createStyledButton("Thêm", new Color(76, 175, 80));
        btnXoa = createStyledButton("Xóa", new Color(244, 67, 54));
        btnCapNhat = createStyledButton("Cập nhật", new Color(255, 152, 0));
        btnHienThi = createStyledButton("Hiển thị", new Color(63, 81, 181));
        btnMuon = createStyledButton("Mượn", new Color(3, 169, 244));
        btnTra = createStyledButton("Trả", new Color(0, 188, 212));
        btnThongKe = createStyledButton("Thống kê", new Color(121, 85, 72));
        btnThoat = createStyledButton("Thoát", new Color(158, 158, 158));
        btnTaiKhoan = createStyledButton("Tài khoản", new Color(100, 181, 246));
        btnDangXuat = createStyledButton("Đăng xuất", new Color(96, 125, 139));
        btnSearch = createStyledButton("🔍 Tìm kiếm", new Color(33, 150, 243)); // Thêm nút tìm kiếm

        // Panel chứa nút chức năng
        panelButton = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        panelButton.setOpaque(false);
        panelButton.setBorder(new EmptyBorder(15, 20, 10, 20));

        // Header với logo, thanh tìm kiếm và nút đăng xuất
        JLabel logo = new JLabel("Thư viện số", SwingConstants.LEFT);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logo.setForeground(new Color(33, 33, 33));

        JPanel headerLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerLeft.setOpaque(false);
        headerLeft.add(logo);

        // Thanh tìm kiếm
        tfSearch = new JTextField(20); // Ô nhập từ khóa
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(tfSearch);
        searchPanel.add(btnSearch);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        headerRight.setOpaque(false);
        headerRight.add(btnDangXuat);

        JPanel headerCenter = new JPanel(new BorderLayout());
        headerCenter.setOpaque(false);
        headerCenter.add(searchPanel, BorderLayout.CENTER);

        JPanel headerBar = new JPanel(new BorderLayout());
        headerBar.setBackground(new Color(240, 244, 255));
        headerBar.setBorder(new EmptyBorder(10, 15, 0, 15));
        headerBar.add(headerLeft, BorderLayout.WEST);
        headerBar.add(headerCenter, BorderLayout.CENTER);
        headerBar.add(headerRight, BorderLayout.EAST);

        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.setBackground(new Color(240, 244, 255));
        panelTop.add(headerBar, BorderLayout.NORTH);
        panelTop.add(panelButton, BorderLayout.SOUTH);

        // Table hiển thị tài liệu
        String[] cols = { "Mã", "Tiêu đề", "Tác giả", "Số lượng" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.getTableHeader().setReorderingAllowed(false);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(26);
        table.setGridColor(new Color(220, 220, 220));
        table.setSelectionBackground(new Color(230, 240, 255));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(245, 245, 245));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < cols.length; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách tài liệu"));

        JPanel panelCenter = new JPanel(new BorderLayout());
        panelCenter.setBackground(Color.WHITE);
        panelCenter.add(scrollPane, BorderLayout.CENTER);

        thongTinHeThong = new JLabel("", SwingConstants.CENTER);
        thongTinHeThong.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        thongTinHeThong.setForeground(new Color(33, 150, 243));
        thongTinHeThong.setBorder(new EmptyBorder(10, 0, 10, 0));

        setLayout(new BorderLayout(0, 0));
        add(panelTop, BorderLayout.NORTH);
        add(panelCenter, BorderLayout.CENTER);
        add(thongTinHeThong, BorderLayout.SOUTH);
    }

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

    public void capNhatBang(Object[][] duLieu) {
        tableModel.setRowCount(0);
        for (Object[] row : duLieu)
            tableModel.addRow(row);
    }

    public void capNhatThongTinHeThong(int tongTL, int tongUser) {
        thongTinHeThong.setText(
                "<html><span style='color:#4CAF50'><b>Tổng tài liệu:</b></span> " + tongTL +
                        "   " +
                        "<span style='color:#2196F3'><b> Người dùng:</b></span> " + tongUser + "</html>"
        );
    }

    public String layMaTaiLieuDangChon() {
        int row = table.getSelectedRow();
        return (row >= 0) ? tableModel.getValueAt(row, 0).toString() : null;
    }

    public JTable getTable() {
        return table;
    }
}