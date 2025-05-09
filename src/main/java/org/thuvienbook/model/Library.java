package org.thuvienbook.model;

import java.util.ArrayList;
import java.util.List;

public class Library {
    private List<Document> documents;
    private List<User> users;

    public Library() {
        documents = new ArrayList<>();
        users = new ArrayList<>();
    }

    // === Tài liệu ===
    public void addDocument(Document doc) {
        documents.add(doc);
    }

    public boolean removeDocument(String id) {
        return documents.removeIf(d -> d.getId().equalsIgnoreCase(id));
    }

    public Document findDocument(String id) {
        for (Document d : documents) {
            if (d.getId().equalsIgnoreCase(id))
                return d;
        }
        return null;
    }

    public boolean updateDocument(String id, String newTitle, String newAuthor, int newQuantity) {
        Document d = findDocument(id);
        if (d != null) {
            d.setTitle(newTitle);
            d.setAuthor(newAuthor);
            d.setQuantity(newQuantity);
            return true;
        }
        return false;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    // === Người dùng ===
    public void addUser(User user) {
        users.add(user);
    }

    public User findUser(String username) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username))
                return u;
        }
        return null;
    }

    public List<User> getUsers() {
        return users;
    }

    // === Mượn / Trả ===
    public boolean borrowDocument(User user, String docId) {
        Document doc = findDocument(docId);
        if (doc != null && doc.getQuantity() > 0) {
            doc.setQuantity(doc.getQuantity() - 1);
            user.borrowDocument(doc);
            return true;
        }
        return false;
    }

    public boolean returnDocument(User user, String docId) {
        Document doc = findDocument(docId);
        if (doc == null) {
            return false; // Tài liệu không tồn tại
        }
        // Kiểm tra xem người dùng có mượn tài liệu này không
        for (Document borrowedDoc : user.getBorrowedDocuments()) {
            if (borrowedDoc.getId().equalsIgnoreCase(docId)) {
                user.returnDocument(doc); // Xóa khỏi danh sách mượn của người dùng
                doc.setQuantity(doc.getQuantity() + 1); // Tăng số lượng
                return true;
            }
        }
        return false; // Người dùng chưa mượn tài liệu này
    }

    // ✅ Thống kê người đang mượn tài liệu
    public List<String> thongTinMuonTra() {
        List<String> danhSach = new ArrayList<>();
        for (User u : users) {
            List<Document> muon = u.getBorrowedDocuments();
            if (!muon.isEmpty()) {
                StringBuilder dong = new StringBuilder(u.getUsername() + " đã mượn:");
                for (Document d : muon) {
                    dong.append(" ").append(d.getTitle());
                }
                danhSach.add(dong.toString());
            }
        }
        return danhSach;
    }

    public List<Document> searchDocuments(String keyword) {
        List<Document> result = new ArrayList<>();
        for (Document doc : documents) {
            if (doc.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                    doc.getAuthor().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(doc);
            }
        }
        return result;
    }

    public void displayUserInfo(String username) {
        User u = findUser(username);
        if (u != null) {
            System.out.println(u);  // toString() bên User phải viết đầy đủ
        } else {
            System.out.println("User not found.");
        }
    }

}
