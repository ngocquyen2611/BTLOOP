package org.thuvienbook.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GoogleBooksAPI {
    // Thay thế bằng API Key thực tế từ Google Cloud Console
    private static final String API_KEY = "AIzaSyBpfkedCjTgVZ7ukWTPgGBc7ay2pk0NkuA";

    public static Map<String, String> getInfoByISBN(String isbn) throws Exception {
        if (isbn == null || isbn.trim().isEmpty() || !isbn.matches("\\d{10}|\\d{13}")) {
            throw new IllegalArgumentException("ISBN không hợp lệ. Phải là 10 hoặc 13 chữ số.");
        }

        // Kiểm tra API Key trước khi gọi
        if (API_KEY.equals("YOUR_ACTUAL_API_KEY")) {
            throw new IllegalStateException("API Key chưa được thiết lập. Vui lòng thay thế YOUR_ACTUAL_API_KEY bằng API Key hợp lệ từ Google Cloud Console.");
        }

        HttpURLConnection conn = null;
        try {
            String urlString = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbn + "&key=" + API_KEY;
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("Lỗi API: Mã phản hồi " + responseCode + ". Kiểm tra API Key hoặc kết nối mạng.");
            }

            try (InputStream is = conn.getInputStream(); java.util.Scanner sc = new java.util.Scanner(is)) {
                StringBuilder json = new StringBuilder();
                while (sc.hasNext()) json.append(sc.nextLine());

                JSONObject root = new JSONObject(json.toString());
                if (!root.has("items") || root.getJSONArray("items").length() == 0) {
                    throw new RuntimeException("Không tìm thấy sách với ISBN: " + isbn);
                }

                JSONObject volumeInfo = root.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo");
                Map<String, String> result = new HashMap<>();

                result.put("title", volumeInfo.optString("title", ""));
                if (volumeInfo.has("authors")) {
                    JSONArray authors = volumeInfo.getJSONArray("authors");
                    String authorString = authors.toList().stream()
                            .map(Object::toString)
                            .collect(Collectors.joining(", "));
                    result.put("author", authorString);
                } else {
                    result.put("author", "");
                }

                return result;
            }
        } catch (Exception e) {
            if (e instanceof java.net.UnknownHostException) {
                throw new RuntimeException("Không thể kết nối đến Google Books API. Kiểm tra kết nối mạng.");
            } else if (e instanceof java.net.SocketTimeoutException) {
                throw new RuntimeException("Hết thời gian chờ khi gọi Google Books API. Thử lại sau.");
            }
            throw new RuntimeException("Lỗi khi gọi Google Books API: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}