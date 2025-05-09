package org.thuvienbook.model;

public class Book extends Document {
    public Book(String title, String author, int quantity) {
        super(title, author, quantity);
    }

    public Book(String id, String title, String author, int quantity) {
        super(id, title, author, quantity);
    }

    @Override
    public void printInfo() {
        System.out.println("[SÁCH] " + id + " | " + title + " | " + author + " | SL: " + quantity);
    }
}