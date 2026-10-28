package com.javacore.practice.javacore33.collections.map;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Demo bài 18 - {@link HashMap}: Cơ chế bên trong, đếm tần suất và các cạm bẫy thực tế.
 */
public class MapDemo {

    public static void main(String[] args) {
        demTanSuatVaTop5();
        coCheBucketVaHashCollision();
        thamHoaMutableKey();
        baLoaiMap();
        nullTrongMap();
    }

    private static void demTanSuatVaTop5() {
        System.out.println("=== 1. Đếm tần suất từ và Top 5 phổ biến ===");

        String doanVan = """
                Java là một ngôn ngữ lập trình phổ biến. Java mạnh mẽ, an toàn và đa nền tảng.
                Học Java cần kiên trì, thực hành Java mỗi ngày và hiểu sâu về Java Collections.
                Lập trình Java giúp bạn xây dựng ứng dụng lớn.
                """;

        System.out.println("Đoạn văn đầu vào:\n" + doanVan.trim());

        // Đếm bằng merge()
        Map<String, Integer> tanSuat = WordCounter.countWithMerge(doanVan);
        System.out.println("\nTổng số từ phân biệt: " + tanSuat.size());
        System.out.println("Tần suất (HashMap): " + tanSuat);

        // Top 5 từ phổ biến nhất
        List<WordCounter.WordCount> top5 = WordCounter.topWords(tanSuat, 5);
        System.out.println("\nTop 5 từ xuất hiện nhiều nhất:");
        for (int i = 0; i < top5.size(); i++) {
            WordCounter.WordCount wc = top5.get(i);
            System.out.printf("  %d. '%s' (%d lần)%n", i + 1, wc.word(), wc.count());
        }

        // Các từ chỉ xuất hiện đúng 1 lần (Hapax legomena)
        List<String> xuatHienMotLan = WordCounter.wordsAppearingOnce(tanSuat);
        System.out.println("\nCác từ chỉ xuất hiện 1 lần (" + xuatHienMotLan.size() + " từ):");
        System.out.println("  " + xuatHienMotLan);
    }

    private static void coCheBucketVaHashCollision() {
        System.out.println();
        System.out.println("=== 2. Bên trong HashMap: Bucket & Hash Collision (Va chạm mã băm) ===");

        // Tạo 2 key khác nhau hoàn toàn (equals = false) nhưng cố tình có CÙNG hashCode
        KhoaVaCham k1 = new KhoaVaCham("KeyA", 42);
        KhoaVaCham k2 = new KhoaVaCham("KeyB", 42);

        System.out.println("k1.hashCode() = " + k1.hashCode());
        System.out.println("k2.hashCode() = " + k2.hashCode());
        System.out.println("k1.equals(k2) = " + k1.equals(k2));

        Map<KhoaVaCham, String> map = new HashMap<>();
        map.put(k1, "Dữ liệu A");
        map.put(k2, "Dữ liệu B");

        // HashMap tính (n - 1) & hash -> cả 2 key rơi vào CÙNG 1 BUCKET (thùng chứa)!
        // Khi đó, HashMap dùng kỹ thuật Chaining (Linked List / Red-Black Tree) để lưu cả 2 node trong cùng bucket.
        System.out.println("Map size sau khi put 2 key va chạm: " + map.size() + " (chứa đủ cả hai)");
        System.out.println("map.get(k1) = " + map.get(k1));
        System.out.println("map.get(k2) = " + map.get(k2));

        System.out.println("-> Hash Collision KHÔNG làm mất dữ liệu: HashMap duyệt danh sách trong bucket"
                + " và dùng key.equals() để phân biệt!");
    }

    private static void thamHoaMutableKey() {
        System.out.println();
        System.out.println("=== 3. Cạm bẫy Mutable Key: Đổi trường sau khi đã put vào HashMap ===");

        KeyCoTheSua key = new KeyCoTheSua("user_123");
        Map<KeyCoTheSua, String> map = new HashMap<>();
        map.put(key, "Thông tin User 123");

        System.out.println("Trước khi sửa:");
        System.out.println("  map.containsKey(key): " + map.containsKey(key));
        System.out.println("  map.get(key)        : " + map.get(key));

        // SỬA GIÁ TRỊ CỦA KEY -> LÀM ĐỔI HASHCODE
        key.setId("user_999");
        System.out.println("\nSau khi đổi key.id thành 'user_999':");

        // Java tính hashCode mới -> tìm ở BUCKET MỚI -> bucket đó rỗng -> trả về null!
        System.out.println("  map.containsKey(key): " + map.containsKey(key) + "  <- BỐC HƠI KHỎI TẦM TÌM!");
        System.out.println("  map.get(key)        : " + map.get(key) + "  <- NULL!");
        System.out.println("  map.size()          : " + map.size() + "  <- NHƯNG SIZE VẪN LÀ 1 (rò rỉ bộ nhớ)");

        // Thậm chí không remove được
        map.remove(key);
        System.out.println("  map.size() sau remove: " + map.size() + "  <- XÓA CŨNG BẤT LỰC");

        System.out.println("-> BÀI HỌC SỐNG CÒN: Key của HashMap PHẢI BẤT BIẾN (String, Integer, record)");
    }

    private static void baLoaiMap() {
        System.out.println();
        System.out.println("=== 4. So sánh HashMap vs LinkedHashMap vs TreeMap ===");

        List<String> keys = List.of("Go", "Java", "Python", "Rust", "C++");

        Map<String, Integer> hash = new HashMap<>();
        Map<String, Integer> linked = new LinkedHashMap<>();
        Map<String, Integer> tree = new TreeMap<>();

        for (int i = 0; i < keys.size(); i++) {
            hash.put(keys.get(i), i);
            linked.put(keys.get(i), i);
            tree.put(keys.get(i), i);
        }

        System.out.println("Gốc thêm vào       : " + keys);
        System.out.println("HashMap (không thứ tự): " + hash.keySet());
        System.out.println("LinkedHashMap (thứ tự chèn): " + linked.keySet());
        System.out.println("TreeMap (sắp xếp A-Z): " + tree.keySet());
    }

    private static void nullTrongMap() {
        System.out.println();
        System.out.println("=== 5. Xử lý null trong Map ===");

        // HashMap cho phép 1 null key và nhiều null value
        Map<String, String> hash = new HashMap<>();
        hash.put(null, "Giá trị cho null key");
        hash.put("k1", null);
        hash.put("k2", null);
        System.out.println("HashMap với null key & value: " + hash);

        // TreeMap cấm null key vì cần gọi compareTo()
        thu("TreeMap.put(null, val)", () -> {
            Map<String, String> tree = new TreeMap<>();
            tree.put(null, "loi");
        });

        // Map.of cấm cả null key lẫn null value
        thu("Map.of(null, val)", () -> Map.of(null, "loi"));
        thu("Map.of(key, null)", () -> Map.of("key", null));
    }

    // Class giả lập va chạm mã băm
    private static final class KhoaVaCham {
        private final String ten;
        private final int maBamCoDinh;

        KhoaVaCham(String ten, int maBamCoDinh) {
            this.ten = ten;
            this.maBamCoDinh = maBamCoDinh;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof KhoaVaCham other)) return false;
            return Objects.equals(ten, other.ten);
        }

        @Override
        public int hashCode() {
            return maBamCoDinh; // Cố tình trả cùng 1 giá trị để tạo va chạm
        }

        @Override
        public String toString() {
            return ten;
        }
    }

    // Class mutable có chủ đích để minh họa lỗi
    private static final class KeyCoTheSua {
        private String id;

        KeyCoTheSua(String id) {
            this.id = id;
        }

        void setId(String id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof KeyCoTheSua other)) return false;
            return Objects.equals(id, other.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return "Key[" + id + "]";
        }
    }

    private static void thu(String nhan, Runnable r) {
        try {
            r.run();
            System.out.println("  " + nhan + " -> KHÔNG nổ");
        } catch (RuntimeException e) {
            System.out.println("  " + nhan + " -> " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
