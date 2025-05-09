package org.thuvienbook.model;

public class Thesis extends Document {
    public Thesis(String title, String author, int quantity) {
        super(title, author, quantity);
    }

    public Thesis(String id, String title, String author, int quantity) {
        super(id, title, author, quantity);
    }

    @Override
    public void printInfo() {
        System.out.println("[LUẬN VĂN] " + id + " | " + title + " | " + author + " | SL: " + quantity);
    }
}
