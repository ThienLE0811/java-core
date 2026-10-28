package com.javacore.practice.javacore33.collections.list_set;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import com.javacore.practice.javacore33.records.Point;

public class SetDemo {

    public static void main(String[] args) {
        locTrung();
        baLoaiSet();
        treeSetDungCompareTo();
        nullTrongSet();
        mutateSauKhiVaoSet();
        phepToanTapHop();
    }

    private static void locTrung() {
        System.out.println("=== 1. Lọc trùng mà vẫn giữ thứ tự ===");

        List<String> coTrung = List.of("Go", "Java", "Python", "Java", "Go", "Rust", "Java");
        System.out.println("  gốc                 : " + coTrung);

        // LinkedHashSet giữ THỨ TỰ CHÈN. Lần xuất hiện đầu tiên quyết định vị trí,
        // các lần sau bị bỏ qua chứ không đẩy phần tử lên.
        List<String> giuThuTu = new ArrayList<>(new LinkedHashSet<>(coTrung));
        System.out.println("  qua LinkedHashSet   : " + giuThuTu);

        // HashSet không hứa hẹn thứ tự nào -> dedupe xong là mất thông tin thứ tự gốc.
        List<String> matThuTu = new ArrayList<>(new HashSet<>(coTrung));
        System.out.println("  qua HashSet         : " + matThuTu);

        // Cách viết hiện đại hơn, cùng kết quả với LinkedHashSet: distinct() của Stream
        // cũng được spec bảo đảm giữ thứ tự xuất hiện đầu tiên.
        System.out.println("  qua stream.distinct : " + coTrung.stream().distinct().toList());

        System.out.println("  -> dedupe mà vẫn giữ thứ tự xuất hiện = LinkedHashSet"
                + " (hoặc stream().distinct())");
    }

    private static void baLoaiSet() {
        System.out.println();
        System.out.println("=== 2. HashSet vs LinkedHashSet vs TreeSet ===");

        // LẦN 1 - Integer nhỏ: HashSet in ra TRÔNG như đã sắp xếp.
        // Lý do: hashCode của Integer chính là giá trị của nó, với số nhỏ thì nó rơi
        // đúng vào ô bảng băm theo thứ tự tăng dần. Đây là TRÙNG HỢP, không phải hợp đồng.
        List<Integer> so = List.of(5, 3, 9, 1, 7);
        System.out.println("  [Integer nhỏ] gốc   : " + so);
        System.out.println("    HashSet           : " + new HashSet<>(so) + "  <- trông như sorted, ĐỪNG TIN");
        System.out.println("    LinkedHashSet     : " + new LinkedHashSet<>(so));
        System.out.println("    TreeSet           : " + new TreeSet<>(so));

        // LẦN 2 - String: bản chất "không đảm bảo thứ tự" lộ ra ngay.
        List<String> chu = List.of("Go", "Java", "Python", "Rust", "C++");
        System.out.println("  [String] gốc        : " + chu);
        System.out.println("    HashSet           : " + new HashSet<>(chu) + "  <- không theo quy luật nào bạn kiểm soát được");
        System.out.println("    LinkedHashSet     : " + new LinkedHashSet<>(chu) + "  <- đúng thứ tự chèn");
        System.out.println("    TreeSet           : " + new TreeSet<>(chu) + "  <- theo compareTo của String (alphabet)");

        // LẦN 3 - Integer LỚN: cùng một HashSet, giờ không còn giống sorted nữa.
        List<Integer> soLon = List.of(500, 3, 90000, 17, 64);
        System.out.println("  [Integer lớn] gốc   : " + soLon);
        System.out.println("    HashSet           : " + new HashSet<>(soLon) + "  <- ảo tưởng ở lần 1 tan biến");

        System.out.println("  -> 'không đảm bảo thứ tự' KHÁC 'ngẫu nhiên': nó ổn định trong một lần chạy"
                + " nhưng bạn không được phép phụ thuộc vào nó. Cần thứ tự chèn -> LinkedHashSet;"
                + " cần sắp xếp -> TreeSet");
    }

    private static void treeSetDungCompareTo() {
        System.out.println();
        System.out.println("=== 3. 'Trùng' nghĩa là gì: equals hay compareTo? ===");

        Point p1 = new Point(1, 5);
        Point p2 = new Point(1, 99);
        System.out.println("  p1.equals(p2)       : " + p1.equals(p2) + "  (hai điểm KHÁC nhau)");

        // HashSet hỏi equals/hashCode -> hai điểm khác nhau -> giữ cả hai.
        Set<Point> quaHash = new HashSet<>(List.of(p1, p2));
        System.out.println("  HashSet size        : " + quaHash.size());

        // TreeSet KHÔNG dùng equals. Nó hỏi Comparator: comparator này chỉ so x,
        // mà p1.x == p2.x -> compare trả 0 -> TreeSet kết luận "trùng" -> bỏ p2.
        Set<Point> quaTree = new TreeSet<>(Comparator.comparing(Point::x));
        quaTree.addAll(List.of(p1, p2));
        System.out.println("  TreeSet size        : " + quaTree.size() + "  <- MẤT một phần tử");
        System.out.println("  TreeSet chứa        : " + quaTree);

        // Cùng cái bẫy đó xảy ra với naturalOrder, vì Point.compareTo sắp theo khoảng
        // cách tới gốc: (2,0) và (0,2) cùng khoảng cách 2.0 -> TreeSet coi là một.
        // Comment trong Point.java đã cảnh báo: "KHÔNG nhất quán với equals".
        Point a = new Point(2, 0);
        Point b = new Point(0, 2);
        System.out.println("  a.equals(b)         : " + a.equals(b));
        System.out.println("  a.compareTo(b)      : " + a.compareTo(b) + "  (0 nghĩa là 'bằng nhau')");
        Set<Point> tuNhien = new TreeSet<>(List.of(a, b));
        System.out.println("  TreeSet naturalOrder: size=" + tuNhien.size() + " " + tuNhien);

        // Muốn TreeSet hành xử đúng thì comparator phải phân biệt được mọi cặp KHÔNG equals.
        Set<Point> dayDu = new TreeSet<>(Comparator.comparing(Point::x).thenComparing(Point::y));
        dayDu.addAll(List.of(p1, p2, a, b));
        System.out.println("  TreeSet x rồi y     : size=" + dayDu.size() + " " + dayDu);

        System.out.println("  -> HashSet xác định trùng bằng equals/hashCode, TreeSet bằng"
                + " compareTo/Comparator. Comparator thiếu tiêu chí = âm thầm nuốt dữ liệu");
    }

    private static void nullTrongSet() {
        System.out.println();
        System.out.println("=== 4. null trong Set ===");

        // HashSet có một ô riêng cho null -> chứa được, và vẫn là tập hợp nên chỉ một.
        Set<String> hash = new HashSet<>();
        hash.add(null);
        hash.add(null);
        hash.add("Java");
        System.out.println("  HashSet             : " + hash + "  size=" + hash.size());

        Set<String> linked = new LinkedHashSet<>();
        linked.add("Java");
        linked.add(null);
        System.out.println("  LinkedHashSet       : " + linked);

        // TreeSet phải gọi compareTo để biết xếp null vào đâu -> null.compareTo(...) = NPE.
        thu("  TreeSet.add(null)   ", () -> {
            Set<String> tree = new TreeSet<>();
            tree.add("Java");
            tree.add(null);
        });

        // Set.of cấm null ngay từ lúc tạo, giống List.of.
        thu("  Set.of(1, null)     ", () -> Set.of(1, null));

        // Và Set.of còn cấm phần tử TRÙNG - khác hẳn new HashSet<>(List.of(...)) vốn im lặng bỏ qua.
        thu("  Set.of(1, 1)        ", () -> Set.of(1, 1));
        System.out.println("  new HashSet(1,1)    : " + new HashSet<>(List.of(1, 1)) + "  (im lặng gộp)");

        System.out.println("  -> Hash/LinkedHashSet chứa được 1 null; TreeSet thì không"
                + " (phải so sánh được); Set.of cấm cả null lẫn phần tử trùng");
    }

    private static void mutateSauKhiVaoSet() {
        System.out.println();
        System.out.println("=== 5. Sửa phần tử sau khi đã bỏ vào HashSet ===");

        Hop h = new Hop("Áo");
        Set<Hop> set = new HashSet<>();
        set.add(h);

        System.out.println("  contains trước      : " + set.contains(h));

        // hashCode của h tính từ nhan. Đổi nhan là đổi hashCode -> HashSet đi tìm ở
        // ô bảng băm MỚI, nhưng object vẫn nằm ở ô CŨ -> không bao giờ gặp.
        h.setNhan("Quần");

        System.out.println("  contains sau khi sửa: " + set.contains(h) + "  <- object vẫn trong set!");
        System.out.println("  duyệt for-each      :");
        for (Hop x : set) {
            System.out.println("    - " + x + "  (nó đây, chỉ là không tìm ra)");
        }
        System.out.println("  size                : " + set.size());

        // Tệ hơn: giờ remove cũng không xoá được, và thêm lại thì thành hai phần tử.
        System.out.println("  remove(h)           : " + set.remove(h) + "  <- xoá cũng trượt");
        set.add(new Hop("Quần"));
        System.out.println("  thêm Hop(Quần)      : size=" + set.size() + " " + set);

        System.out.println("  -> phần tử của Set (và key của Map) PHẢI bất biến ở những field"
                + " dùng trong hashCode/equals. Đây chính là lý do record ở bài 15 là lựa chọn đúng");
    }

    private static void phepToanTapHop() {
        System.out.println();
        System.out.println("=== 6. Phép toán tập hợp ===");

        Set<Integer> a = Set.of(1, 2, 3, 4);
        Set<Integer> b = Set.of(3, 4, 5);
        System.out.println("  a                   : " + a);
        System.out.println("  b                   : " + b);

        // Cả ba đều SỬA TẠI CHỖ và trả boolean -> phải copy trước, không thì hỏng dữ liệu gốc.
        Set<Integer> hop = new TreeSet<>(a);
        hop.addAll(b);
        System.out.println("  hợp (addAll)        : " + hop);

        Set<Integer> giao = new TreeSet<>(a);
        giao.retainAll(b);
        System.out.println("  giao (retainAll)    : " + giao);

        Set<Integer> hieu = new TreeSet<>(a);
        hieu.removeAll(b);
        System.out.println("  hiệu a-b (removeAll): " + hieu);

        System.out.println("  a chứa hết b?       : " + a.containsAll(b));
        System.out.println("  a sau tất cả        : " + a + "  (không đổi vì đã copy)");

        // Quên copy thì chính a bị phá. Ở đây a là Set.of nên nó nổ thay vì hỏng âm thầm -
        // đó là một lý do nữa để trả về collection bất biến.
        thu("  retainAll trên Set.of", () -> Set.of(1, 2, 3).retainAll(Set.of(3)));

        System.out.println("  -> addAll/retainAll/removeAll sửa tại chỗ và trả boolean;"
                + " luôn copy trước khi gọi");
    }

    // Mutable có chủ đích, chỉ để minh hoạ mục 5. Đừng viết kiểu này trong code thật.
    private static final class Hop {
        private String nhan;

        Hop(String nhan) {
            this.nhan = nhan;
        }

        void setNhan(String nhan) {
            this.nhan = nhan;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Hop khac)) {
                return false;
            }
            return Objects.equals(nhan, khac.nhan);
        }

        @Override
        public int hashCode() {
            return Objects.hash(nhan);
        }

        @Override
        public String toString() {
            return "Hop[" + nhan + "]";
        }
    }

    private static void thu(String nhan, Runnable r) {
        try {
            r.run();
            System.out.println("  " + nhan + " -> KHÔNG nổ");
        } catch (RuntimeException e) {
            System.out.println("  " + nhan + " -> " + e.getClass().getSimpleName()
                    + ": " + e.getMessage());
        }
    }
}
