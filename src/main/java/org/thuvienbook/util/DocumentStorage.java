    package org.thuvienbook.util;

    import org.thuvienbook.model.Book;
    import org.thuvienbook.model.Document;
    import org.thuvienbook.model.Thesis;

    import javax.swing.JOptionPane;
    import java.io.*;
    import java.util.ArrayList;
    import java.util.List;

    public class DocumentStorage {
        private static final String FILE_NAME = "documents.txt";

        public static void save(List<Document> documents) {
            try (PrintWriter out = new PrintWriter(new FileWriter(FILE_NAME))) {
                for (Document doc : documents) {
                    String type = (doc instanceof Book) ? "Book" : "Thesis";
                    out.println(type + "," + doc.getId() + "," + doc.getTitle() + "," + doc.getAuthor() + "," + doc.getQuantity());
                }
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "❌ Lỗi khi lưu tài liệu: " + e.getMessage());
            }
        }

        public static List<Document> load() {
            List<Document> list = new ArrayList<>();
            File file = new File(FILE_NAME);
            if (!file.exists()) {
                return list;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",", -1);
                    if (parts.length == 5) {
                        String type = parts[0];
                        String id = parts[1];
                        String title = parts[2];
                        String author = parts[3];
                        int quantity = Integer.parseInt(parts[4]);

                        Document doc;
                        if (type.equals("Book")) {
                            doc = new Book(id, title, author, quantity);
                        } else if (type.equals("Thesis")) {
                            doc = new Thesis(id, title, author, quantity);
                        } else {
                            continue; // Bỏ qua nếu không xác định loại
                        }
                        list.add(doc);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "❌ Lỗi khi tải tài liệu: " + e.getMessage());
            }
            return list;
        }
    }