package org.thuvienbook.controller;

import org.thuvienbook.model.Book;
import org.thuvienbook.model.Document;
import org.thuvienbook.model.Library;
import org.thuvienbook.model.Thesis;
import org.thuvienbook.model.User;
import org.thuvienbook.model.UserRole;
import org.thuvienbook.view.LibraryView;

public class LibraryController {
    private final Library library;
    private final LibraryView view;
    private final User currentUser;

    public LibraryController(Library library, LibraryView view, User user) {
        this.library = library;
        this.view = view;
        this.currentUser = user;
    }

    public void run() {
        int choice;
        do {
            view.printMenu();
            choice = view.getIntInput();

            switch (choice) {
                case 0 -> view.showMessage("Thoát.");
                case 1 -> {
                    if (currentUser.getRole() == UserRole.ADMIN)
                        addDocument();
                    else
                        view.showMessage("Chỉ quản trị viên mới được thêm tài liệu.");
                }
                case 2 -> removeDocument();
                case 3 -> updateDocument();
                case 4 -> findDocument();
                case 5 -> displayDocuments();
                case 6 -> borrowDocument();
                case 7 -> returnDocument();
                default -> view.showMessage("Lựa chọn không hợp lệ.");
            }

        } while (choice != 0);
    }

    private void addDocument() {
        String title = view.getString("Tiêu đề: ");
        String author = view.getString("Tác giả: ");
        int quantity = view.getInt("Số lượng: ");
        String type = view.getString("Loại (book/thesis): ").toLowerCase();

        Document doc = switch (type) {
            case "book" -> new Book(title, author, quantity);
            case "thesis" -> new Thesis(title, author, quantity);
            default -> null;
        };

        if (doc != null) {
            library.addDocument(doc);
            view.showMessage("📘 Đã thêm với mã: " + doc.getId());
        } else {
            view.showMessage("❌ Loại tài liệu không hợp lệ.");
        }
    }

    private void removeDocument() {
        String id = view.getString("ID tài liệu cần xóa: ");
        boolean removed = library.removeDocument(id);
        view.showMessage(removed ? "Đã xóa." : "Không tìm thấy tài liệu.");
    }

    private void updateDocument() {
        String id = view.getString("ID tài liệu cần cập nhật: ");
        Document doc = library.findDocument(id);
        if (doc == null) {
            view.showMessage("Không tìm thấy tài liệu.");
            return;
        }

        String newTitle = view.getString("Tiêu đề mới (" + doc.getTitle() + "): ");
        String newAuthor = view.getString("Tác giả mới (" + doc.getAuthor() + "): ");
        int newQuantity = view.getInt("Số lượng mới (" + doc.getQuantity() + "): ");

        library.updateDocument(id, newTitle, newAuthor, newQuantity);
        view.showMessage("Đã cập nhật.");
    }

    private void findDocument() {
        String id = view.getString("ID tài liệu: ");
        Document doc = library.findDocument(id);
        if (doc != null)
            doc.printInfo();
        else
            view.showMessage("Không tìm thấy.");
    }

    private void displayDocuments() {
        for (Document d : library.getDocuments()) {
            d.printInfo();
        }
    }

    private void borrowDocument() {
        String docId = view.getString("ID tài liệu muốn mượn: ");
        boolean success = library.borrowDocument(currentUser, docId);
        view.showMessage(success ? "Đã mượn." : "Không thể mượn.");
    }

    private void returnDocument() {
        String docId = view.getString("ID tài liệu muốn trả: ");
        boolean success = library.returnDocument(currentUser, docId);
        view.showMessage(success ? "Đã trả." : "Không thể trả.");
    }
}