
package org.thuvienbook.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private static int idCounter = 1;
    private int userId;
    private String username;
    private String password;
    private String fullName;
    private UserRole role;
    private List<Document> borrowedDocuments;

    public User(String username, String password, String fullName, UserRole role) {
        this.userId = idCounter++;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.borrowedDocuments = new ArrayList<>();
    }

    public boolean checkCredentials(String user, String pass) {
        return this.username.equals(user) && this.password.equals(pass);
    }

    public void borrowDocument(Document doc) {
        borrowedDocuments.add(doc);
    }

    public void returnDocument(Document doc) {
        borrowedDocuments.remove(doc);
    }

    // Getter
    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    public UserRole getRole() {
        return role;
    }

    public List<Document> getBorrowedDocuments() {
        return borrowedDocuments;
    }

    // Setter
    public void setPassword(String password) {
        this.password = password;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public void printUserInfo() {
        System.out.println("ID: " + userId +
                ", Tên: " + fullName +
                ", Username: " + username +
                ", Vai trò: " + role +
                ", Số sách mượn: " + borrowedDocuments.size());
    }
}
