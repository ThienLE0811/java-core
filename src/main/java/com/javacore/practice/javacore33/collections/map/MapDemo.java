package com.javacore.practice.javacore33.collections.map;

import java.util.*;

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
        computeIfAbsentVaMultimap();
        camBayConcurrentModification();
        duyetKeySetVsEntrySet();
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

    private static void computeIfAbsentVaMultimap() {
        System.out.println();
        System.out.println("=== 6. computeIfAbsent: xây Multimap Map<K, List<V>> ===");

        List<String> tuKhoa = WordCounter.extractWords("Java Python Pascal Go Groovy Ruby Rust R Perl");
        System.out.println("Đầu vào: " + tuKhoa);

        // Cách thủ công: get -> kiểm tra null -> tạo List -> put -> add (5 dòng)
        Map<Character, List<String>> thuCong = new TreeMap<>();
        for (String tu : tuKhoa) {
            List<String> nhom = thuCong.get(tu.charAt(0));
            if (nhom == null) {
                nhom = new ArrayList<>();
                thuCong.put(tu.charAt(0), nhom);
            }
            nhom.add(tu);
        }
        System.out.println("Thủ công (get + null check + put): " + thuCong);

        // computeIfAbsent gộp đúng 5 dòng trên thành 1 dòng
        Map<Character, List<String>> gon = WordCounter.groupByFirstLetter(tuKhoa);
        System.out.println("computeIfAbsent                  : " + gon);
        System.out.println("Hai kết quả giống nhau? " + thuCong.equals(gon));

        // Điểm mấu chốt: hàm tạo giá trị là LAZY - chỉ chạy khi key CHƯA tồn tại
        System.out.println("\nTính lazy của computeIfAbsent:");
        Map<String, List<String>> lazy = new HashMap<>();
        lazy.computeIfAbsent("a", k -> taoListCoLog("lần 1 - key 'a' chưa có"));
        lazy.computeIfAbsent("a", k -> taoListCoLog("lần 2 - key 'a' đã có"));
        System.out.println("  -> Chỉ in 1 dòng: lần 2 KHÔNG gọi hàm tạo. Map hiện tại: " + lazy);

        // Ngược lại, putIfAbsent nhận sẵn một VALUE -> new ArrayList<>() luôn bị tạo rồi vứt đi
        System.out.println("\nSo với putIfAbsent (eager):");
        Map<String, List<String>> eager = new HashMap<>();
        eager.putIfAbsent("a", taoListCoLog("lần 1 - key 'a' chưa có"));
        eager.putIfAbsent("a", taoListCoLog("lần 2 - key 'a' đã có"));
        System.out.println("  -> In 2 dòng: lần 2 vẫn cấp phát object thừa rồi bỏ. Map hiện tại: " + eager);

        // Cạm bẫy: hàm tạo trả null -> KHÔNG có entry nào được thêm (không phải put(key, null))
        Map<String, List<String>> traNull = new HashMap<>();
        List<String> ketQua = traNull.computeIfAbsent("x", k -> null);
        System.out.println("\ncomputeIfAbsent trả null -> giá trị nhận được: " + ketQua
                + ", map rỗng? " + traNull.isEmpty() + "  <- KHÔNG tạo entry key='x'");
    }

    private static List<String> taoListCoLog(String nhan) {
        System.out.println("  [tạo ArrayList mới] " + nhan);
        return new ArrayList<>();
    }

    private static void camBayConcurrentModification() {
        System.out.println();
        System.out.println("=== 7. ConcurrentModificationException: vừa duyệt vừa xóa ===");
        System.out.println("Map gốc: " + taoMapDiem() + " (cần xóa các học viên dưới 5 điểm)");

        // SAI: sửa cấu trúc map trong khi iterator của for-each đang chạy
        // -> iterator phát hiện modCount đổi -> fail-fast ném CME
        System.out.println("\nCách SAI:");
        thu("for-each keySet() + map.remove()", () -> {
            Map<String, Integer> map = taoMapDiem();
            for (String key : map.keySet()) {
                if (map.get(key) < 5) {
                    map.remove(key);
                }
            }
        });
        thu("for-each entrySet() + map.put(key mới)", () -> {
            Map<String, Integer> map = taoMapDiem();
            for (Map.Entry<String, Integer> e : map.entrySet()) {
                map.put(e.getKey() + "_backup", e.getValue());
            }
        });

        System.out.println("\nCách ĐÚNG:");

        // 1. Iterator.remove(): iterator tự cập nhật modCount nên không bị fail-fast
        Map<String, Integer> m1 = taoMapDiem();
        Iterator<Map.Entry<String, Integer>> it = m1.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getValue() < 5) {
                it.remove();
            }
        }
        System.out.println("  Iterator.remove()      -> " + m1);

        // 2. removeIf trên entrySet - ngắn gọn nhất, nên dùng mặc định
        Map<String, Integer> m2 = taoMapDiem();
        m2.entrySet().removeIf(entry -> entry.getValue() < 5);
        System.out.println("  entrySet().removeIf()  -> " + m2);

        // 3. Chỉ quan tâm value thì dùng thẳng view values()
        Map<String, Integer> m3 = taoMapDiem();
        m3.values().removeIf(diem -> diem < 5);
        System.out.println("  values().removeIf()    -> " + m3);

        // 4. Chỉ ĐỔI GIÁ TRỊ (không thêm/bớt key) thì for-each vẫn an toàn,
        //    vì setValue()/replaceAll() không làm thay đổi cấu trúc map.
        Map<String, Integer> m4 = taoMapDiem();
        m4.replaceAll((ten, diem) -> Math.min(10, diem + 1));
        System.out.println("  replaceAll() cộng 1 điểm -> " + m4);

        System.out.println("\n-> LƯU Ý: CME là cơ chế fail-fast 'best effort', KHÔNG phải bảo đảm."
                + " Có trường hợp xóa xong vòng lặp thoát êm mà không ném gì cả"
                + " -> càng nguy hiểm vì bug lọt qua test.");
    }

    private static Map<String, Integer> taoMapDiem() {
        Map<String, Integer> diem = new LinkedHashMap<>();
        diem.put("An", 8);
        diem.put("Binh", 3);
        diem.put("Cuong", 9);
        diem.put("Dung", 4);
        diem.put("Em", 7);
        return diem;
    }

    private static void duyetKeySetVsEntrySet() {
        System.out.println();
        System.out.println("=== 8. Duyệt Map: keySet() + get() vs entrySet() ===");

        Map<KhoaDemHash, Integer> map = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            map.put(new KhoaDemHash("key" + i), i);
        }

        // Cách 1: duyệt key rồi gọi get() -> mỗi vòng phải băm lại key để tra bucket
        KhoaDemHash.soLanGoiHashCode = 0;
        int tong1 = 0;
        for (KhoaDemHash key : map.keySet()) {
            tong1 += map.get(key);
        }
        int lanKeySet = KhoaDemHash.soLanGoiHashCode;

        // Cách 2: duyệt entrySet -> key và value lấy thẳng từ node, không băm lại
        KhoaDemHash.soLanGoiHashCode = 0;
        int tong2 = 0;
        for (Map.Entry<KhoaDemHash, Integer> entry : map.entrySet()) {
            tong2 += entry.getValue();
        }
        int lanEntrySet = KhoaDemHash.soLanGoiHashCode;

        // Cách 3: forEach - tương đương entrySet, viết gọn hơn
        KhoaDemHash.soLanGoiHashCode = 0;
        int[] tong3 = {0};
        map.forEach((key, value) -> tong3[0] += value);
        int lanForEach = KhoaDemHash.soLanGoiHashCode;

        System.out.println("Map có " + map.size() + " phần tử, cùng tính tổng value:");
        System.out.printf("  keySet() + get() : tổng=%d, số lần gọi hashCode() = %d%n", tong1, lanKeySet);
        System.out.printf("  entrySet()       : tổng=%d, số lần gọi hashCode() = %d%n", tong2, lanEntrySet);
        System.out.printf("  forEach()        : tổng=%d, số lần gọi hashCode() = %d%n", tong3[0], lanForEach);

        System.out.println("-> keySet() + get() tra bảng băm THÊM 1 lần cho mỗi phần tử (kèm cả equals()).");
        System.out.println("-> Chỉ dùng keySet() khi thật sự KHÔNG cần value. Cần cả hai thì luôn entrySet()/forEach().");

        Map<String, Integer> tanSuat;

        for (int i = 1; i <= 5; i++) {
            map.put(new KhoaDemHash("key" + i), i);
        }

    }

    // test xóa trùng, viết hoa rồi sắp xếp
    public static List<String> tuLapLai(Map<String, Integer> tanSuat) {
        List<String> ketQua = new ArrayList<>();

        for (Map.Entry<String, Integer> e : tanSuat.entrySet()) {
            if (e.getValue() >= 2) {
                ketQua.add(e.getKey().toUpperCase(Locale.ROOT));
            }
        }

        // (4) sắp xếp ketQua A-Z
        ketQua.sort(Comparator.naturalOrder());
        return ketQua;
    }


    // Class đếm số lần hashCode() được gọi, để đo chi phí tra bảng băm
    private static final class KhoaDemHash {
        static int soLanGoiHashCode;

        private final String ten;

        KhoaDemHash(String ten) {
            this.ten = ten;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof KhoaDemHash other)) return false;
            return Objects.equals(ten, other.ten);
        }

        @Override
        public int hashCode() {
            soLanGoiHashCode++;
            return Objects.hash(ten);
        }

        @Override
        public String toString() {
            return ten;
        }
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
