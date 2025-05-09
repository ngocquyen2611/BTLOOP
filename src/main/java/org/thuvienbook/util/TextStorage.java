package org.thuvienbook.util;

import org.thuvienbook.model.Document;
import org.thuvienbook.model.User;
import org.thuvienbook.model.UserRole;
import javax.swing.JOptionPane;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.thuvienbook.model.Library;

public class TextStorage {
    private static final String FILE_NAME = "users.txt";
    private static final String FILE_PATH = System.getProperty("user.dir") + File.separator + FILE_NAME;

    public static void saveUsers(List<User> users) {
        try {
            File file = new File(FILE_PATH);
            if (!file.getParentFile().canWrite()) {
                JOptionPane.showMessageDialog(null, "❌ Không có quyền ghi file tại: " + FILE_PATH);
                return;
            }
            try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
                for (User u : users) {
                    StringBuilder line = new StringBuilder();
                    line.append(u.getUsername()).append(",")
                            .append(u.getPassword()).append(",")
                            .append(u.getFullName()).append(",")
                            .append(u.getRole());

                    List<String> borrowedIds = new ArrayList<>();
                    for (Document doc : u.getBorrowedDocuments()) {
                        borrowedIds.add(doc.getId());
                    }
                    if (!borrowedIds.isEmpty()) {
                        line.append(",").append(String.join(",", borrowedIds));
                    }

                    out.println(line.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "❌ Lỗi khi lưu dữ liệu người dùng: " + e.getMessage());
        }
    }

    public static List<User> loadUsers(Library library) {
        List<User> list = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return list;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 4) {
                    String username = parts[0];
                    String password = parts[1];
                    String fullName = parts[2];
                    UserRole role = UserRole.valueOf(parts[3]);

                    User user = new User(username, password, fullName, role);

                    if (parts.length > 4) {
                        for (int i = 4; i < parts.length; i++) {
                            String docId = parts[i].trim();
                            if (!docId.isEmpty()) {
                                Document doc = library.findDocument(docId);
                                if (doc != null) {
                                    user.borrowDocument(doc);
                                }
                            }
                        }
                    }

                    list.add(user);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}