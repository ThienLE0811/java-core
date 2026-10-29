package com.javacore.practice.javacore33.collections.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * Bài 18 - Xử lý đếm tần suất từ bằng {@link HashMap}.
 *
 * <p>Cung cấp logic nghiệp vụ chuẩn hóa văn bản, đếm số lần xuất hiện của từng từ,
 * trích xuất top N từ phổ biến nhất và danh sách các từ chỉ xuất hiện 1 lần.
 */
public final class WordCounter {

    // Phân tách bởi bất kỳ ký tự nào KHÔNG phải là chữ cái (\p{L}) hoặc chữ số (\p{Nd}).
    // Pattern này hỗ trợ an toàn cho cả ký tự Unicode (tiếng Việt có dấu).
    private static final Pattern WORD_DELIMITER = Pattern.compile("[^\\p{L}\\p{Nd}]+");

    private WordCounter() {
        // Utility class, không cho khởi tạo
    }

    /**
     * Đếm tần suất xuất hiện của các từ bằng {@link Map#merge(Object, Object, java.util.function.BiFunction)}.
     *
     * <p>Đây là cách viết hiện đại (Java 8+):
     * {@code map.merge(word, 1, Integer::sum)}
     * Nếu key chưa có -> gán giá trị 1.
     * Nếu key đã có -> gọi hàm cộng (oldVal, 1) -> lưu giá trị mới.
     *
     * @param text đoạn văn bản đầu vào
     * @return Map chứa [từ: số lần xuất hiện], từ được chuẩn hóa chữ thường
     */
    public static Map<String, Integer> countWithMerge(String text) {
        Map<String, Integer> frequencies = new HashMap<>();
        List<String> words = extractWords(text);

        for (String word : words) {
            frequencies.merge(word, 1, Integer::sum);
        }
        return frequencies;
    }

    /**
     * Đếm tần suất xuất hiện của các từ bằng {@link Map#getOrDefault(Object, Object)}.
     *
     * <p>Cách viết truyền thống:
     * {@code map.put(word, map.getOrDefault(word, 0) + 1)}
     *
     * @param text đoạn văn bản đầu vào
     * @return Map chứa [từ: số lần xuất hiện], từ được chuẩn hóa chữ thường
     */
    public static Map<String, Integer> countWithGetOrDefault(String text) {
        Map<String, Integer> frequencies = new HashMap<>();
        List<String> words = extractWords(text);

        for (String word : words) {
            frequencies.put(word, frequencies.getOrDefault(word, 0) + 1);
        }
        return frequencies;
    }

    /**
     * Lấy danh sách {@code n} từ xuất hiện nhiều nhất.
     *
     * <p>Tiêu chí sắp xếp:
     * <ol>
     *   <li>Số lần xuất hiện giảm dần (từ phổ biến nhất lên đầu).</li>
     *   <li>Nếu số lần bằng nhau: sắp xếp theo từ tăng dần (A-Z) để kết quả luôn xác định.</li>
     * </ol>
     *
     * @param frequencies map tần suất từ
     * @param n           số lượng từ cần lấy (n <= 0 sẽ trả về danh sách rỗng)
     * @return danh sách {@link WordCount} chứa từ và số lần xuất hiện
     */
    public static List<WordCount> topWords(Map<String, Integer> frequencies, int n) {
        if (frequencies == null || frequencies.isEmpty() || n <= 0) {
            return Collections.emptyList();
        }

        Comparator<Map.Entry<String, Integer>> comparator = Map.Entry
                .<String, Integer>comparingByValue().reversed()
                .thenComparing(Map.Entry.comparingByKey());

        return frequencies.entrySet().stream()
                .sorted(comparator)
                .limit(n)
                .map(entry -> new WordCount(entry.getKey(), entry.getValue()))
                .toList();
    }

    /**
     * Tìm các từ chỉ xuất hiện đúng 1 lần duy nhất trong đoạn văn (Hapax legomenon).
     *
     * @param frequencies map tần suất từ
     * @return danh sách các từ xuất hiện 1 lần, sắp xếp A-Z
     */
    public static List<String> wordsAppearingOnce(Map<String, Integer> frequencies) {
        if (frequencies == null || frequencies.isEmpty()) {
            return Collections.emptyList();
        }

        return frequencies.entrySet().stream()
                .filter(entry -> entry.getValue() != null && entry.getValue() == 1)
                .map(Map.Entry::getKey)
                .sorted()
                .toList();
    }

    // Nhóm các từ theo chữ cái đầu bằng computeIfAbsent: nếu key chưa có thì tạo List rỗng
    // rồi trả về chính List đó để .add() ngay - thay cho cụm get + null check + put.
    // Dùng TreeMap để kết quả luôn xác định (sắp theo chữ cái đầu).
    public static Map<Character, List<String>> groupByFirstLetter(List<String> words) {
        if (words == null || words.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Character, List<String>> groups = new TreeMap<>();
        for (String word : words) {
            if (word == null || word.isBlank()) {
                continue;
            }
            groups.computeIfAbsent(word.charAt(0), key -> new ArrayList<>()).add(word);
        }
        return groups;
    }

    /**
     * Tách đoạn văn bản thành danh sách từ sạch, chuẩn hóa chữ thường.
     *
     * @param text văn bản gốc
     * @return danh sách các từ hợp lệ (không rỗng, đã lowercase)
     */
    public static List<String> extractWords(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        String[] tokens = WORD_DELIMITER.split(text);
        return java.util.Arrays.stream(tokens)
                .filter(s -> !s.isBlank())
                .map(s -> s.toLowerCase(Locale.ROOT))
                .toList();
    }

    /**
     * DTO bất biến mô tả cặp [từ, số lần xuất hiện].
     */
    public record WordCount(String word, int count) {
        public WordCount {
            Objects.requireNonNull(word, "word không được null");
            if (count < 0) {
                throw new IllegalArgumentException("count không được âm: " + count);
            }
        }
    }
}
