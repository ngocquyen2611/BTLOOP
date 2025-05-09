package org.thuvienbook.view;

import java.util.Scanner;

public class LibraryView {
    private final Scanner scanner = new Scanner(System.in);

    public void printMenu() {
        System.out.println("\n====== LIBRARY MENU ======");
        System.out.println("[0] Thoát");
        System.out.println("[1] Thêm tài liệu");
        System.out.println("[2] Xóa tài liệu");
        System.out.println("[3] Cập nhật tài liệu");
        System.out.println("[4] Tìm tài liệu");
        System.out.println("[5] Hiển thị tài liệu");
        System.out.println("[6] Mượn tài liệu");
        System.out.println("[7] Trả tài liệu");
        System.out.print("Lựa chọn: ");
    }

    public int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            return -1;
        }
    }

    public String getString(String message) {
        System.out.print(message);
        return scanner.nextLine();
    }

    public int getInt(String message) {
        System.out.print(message);
        return Integer.parseInt(scanner.nextLine());
    }

    public void showMessage(String message) {
        System.out.println(message);
    }
}
