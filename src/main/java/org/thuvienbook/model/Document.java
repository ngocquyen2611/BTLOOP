    package org.thuvienbook.model;

    import java.io.Serializable;

    public abstract class Document implements Serializable {
        private static int nextId = 1;

        protected String id;
        protected String title;
        protected String author;
        protected int quantity;

        // Tạo mới -> tự tăng ID
        public Document(String title, String author, int quantity) {
            this.id = "DOC" + nextId++;
            this.title = title;
            this.author = author;
            this.quantity = quantity;
        }

        // Dùng khi LOAD từ file
        public Document(String id, String title, String author, int quantity) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.quantity = quantity;

            try {
                int num = Integer.parseInt(id.replaceAll("\\D", ""));
                if (num >= nextId)
                    nextId = num + 1;
            } catch (Exception ignored) {
            }
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getAuthor() {
            return author;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public void setTitle(String newTitle) {
            this.title = newTitle;
        }

        public void setAuthor(String newAuthor) {
            this.author = newAuthor;
        }

        public abstract void printInfo();
    }