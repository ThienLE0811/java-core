package com.javacore.practice.javacore33.collections;

import com.javacore.practice.javacore33.oop.sanpham.Product;
import com.javacore.practice.javacore33.records.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ListDemo {

    public static void main(String[] args) {
//        thaoTacCoBan();
//        bayRemoveOverload();
//        xoaKhiDangDuyet();
        sapXep();
//        listChiDoc();
//        arrayListVsLinkedList();
    }

    private static ArrayList<String> taoDanhSach() {
        return new ArrayList<String>(List.of("Java"));
    }

    private static void thaoTacCoBan() {
        System.out.println("=== 1. Thao tác cơ bản trên ArrayList ===");
        ArrayList<String> danhSach = taoDanhSach();
        danhSach.add("Python");
        danhSach.add(1, "Javascript");
        danhSach.set(2, "C++");

        // Sau 3 thao tác trên: ["Java", "Javascript", "C++"]
        // add(1, x) ĐẨY phần tử cũ sang phải, set(2, x) GHI ĐÈ. Hai việc khác hẳn nhau.
        System.out.println("  danh sách  : " + danhSach);

        System.out.println("  size       : " + danhSach.size());
        System.out.println("  get(1)     : " + danhSach.get(1));
        System.out.println("  isEmpty    : " + danhSach.isEmpty());

        // contains/indexOf so sánh bằng equals, KHÔNG phải ==.
        // Với String điều này quan trọng: "C++" literal và new String("C++") vẫn tìm thấy.
        System.out.println("  contains   : " + danhSach.contains("C++"));
        System.out.println("  indexOf    : " + danhSach.indexOf("C++"));

        // Không tìm thấy thì indexOf trả -1, không ném exception, không trả null.
        // Quên kiểm tra -1 rồi đem đi get() là ăn IndexOutOfBoundsException.
        System.out.println("  indexOf(Go): " + danhSach.indexOf("Go"));

        // Java 21+ (SequencedCollection): đọc được ý đồ hơn get(0) / get(size()-1),
        // và không tự tay tính sai chỉ số cuối.
        System.out.println("  getFirst   : " + danhSach.getFirst());
        System.out.println("  getLast    : " + danhSach.getLast());

        for (String ngonNgu : danhSach) {
            System.out.println("    - " + ngonNgu);
        }

        System.out.println("  -> ArrayList là mảng tự nở: get(i) O(1), còn add/remove giữa list"
                + " phải dịch chuyển phần đuôi");
    }

    private static void bayRemoveOverload() {
        System.out.println();
        System.out.println("=== 2. Bẫy remove: int index hay Object? ===");
        List<Integer> l = new ArrayList<>(List.of(10, 20, 30));
        l.remove(1);
        System.out.println("  danh sách  : " + l);

        List<Integer> l2 = new ArrayList<>(List.of(10, 20, 30));
        l2.remove(Integer.valueOf(20));
        System.out.println("  danh sách 2  : " + l2);

        System.out.println("  -> ");
    }

    private static void xoaKhiDangDuyet() {
        System.out.println();
        System.out.println("=== 3. Xóa khi đang duyệt: ConcurrentModificationException ===");

        // CÁCH SAI: for-each đang chạy mà gọi list.remove() -> ConcurrentModificationException.
        // "Concurrent" ở đây KHÔNG liên quan gì tới đa luồng. Một luồng duy nhất vẫn nổ.
        // Bọc trong thu(...) để thấy exception mà chương trình không chết.
        thu("for-each + remove  ", () -> {
            List<String> ds = new ArrayList<>(List.of("Java", "Javascript", "Python", "Go"));
            for (String s : ds) {
                if (s.startsWith("J")) {
                    ds.remove(s);
                }
            }
        });

        // CÁI BẪY TRONG CÁI BẪY: xóa phần tử ÁP CHÓT thì KHÔNG nổ.
        // for-each dừng khi cursor == size. Xóa một phần tử làm size giảm 1, cursor chạm
        // size sớm một nhịp -> thoát vòng lặp trước khi kịp kiểm tra modCount.
        // Phần tử cuối bị bỏ qua, im lặng, không exception. Loại bug này khó tìm hơn nhiều.
        thu("xóa phần tử áp chót", () -> {
            List<String> ds = new ArrayList<>(List.of("a", "b", "c"));
            for (String s : ds) {
                if (s.equals("b")) {
                    ds.remove(s);
                }
            }
            System.out.print("(còn " + ds + ") ");
        });

        // ĐÚNG 1: Iterator.remove() tự cập nhật expectedModCount nên không nổ.
        // Dùng khi cần làm thêm việc gì đó tại đúng thời điểm xóa.
        List<String> quaIterator = new ArrayList<>(List.of("Java", "Javascript", "Python", "Go"));
        Iterator<String> it = quaIterator.iterator();
        while (it.hasNext()) {
            if (it.next().startsWith("J")) {
                it.remove();
            }
        }
        System.out.println("  qua Iterator        : " + quaIterator);

        // ĐÚNG 2: removeIf - cùng việc đó, một dòng. Mặc định chọn cái này.
        List<String> quaRemoveIf = new ArrayList<>(List.of("Java", "Javascript", "Python", "Go"));
        quaRemoveIf.removeIf(s -> s.startsWith("J"));
        System.out.println("  qua removeIf        : " + quaRemoveIf);

        // Nhưng removeIf vẫn cần list SỬA ĐƯỢC. List.of(...) thì nổ UnsupportedOperation.
        thu("removeIf trên List.of", () -> List.of("Java", "Go").removeIf(s -> true));

        System.out.println("  -> đang duyệt thì đừng đụng vào list; muốn xóa thì dùng removeIf,"
                + " hoặc Iterator.remove() khi cần kiểm soát từng bước");
    }

    private static void sapXep() {
        System.out.println();
        System.out.println("=== 4. Sắp xếp bằng Comparator ===");

        System.out.println("  gốc                 : " + gon(diem()));

        // 1. naturalOrder: dùng compareTo của chính Point.
        // Point.compareTo sắp theo KHOẢNG CÁCH TỚI GỐC, không phải theo x rồi y.
        // Bài học: "thứ tự tự nhiên" là thứ tác giả class quy định, phải đi đọc mới biết.
        List<Point> theoTuNhien = diem();
        theoTuNhien.sort(Comparator.naturalOrder());
        System.out.println("  naturalOrder        : " + gon(theoTuNhien));

        // 2. Comparator tự dựng: x tăng, x bằng nhau thì xét tiếp y tăng.
        List<Point> theoXroiY = diem();
        theoXroiY.sort(Comparator.comparing(Point::x).thenComparing(Point::y));
        System.out.println("  x↑ rồi y↑           : " + gon(theoXroiY));

        // 3. BẪY: .reversed() đặt cuối chuỗi KHÔNG chỉ đảo tiêu chí cuối.
        // comparing(x).thenComparing(y) trước hết được ghép thành MỘT comparator hoàn chỉnh,
        // rồi .reversed() đảo ngược toàn bộ cái đó -> thành x GIẢM, rồi y GIẢM.
        List<Point> daoCaChuoi = diem();
        daoCaChuoi.sort(Comparator.comparing(Point::x).thenComparing(Point::y).reversed());
        System.out.println("  .reversed() cuối     : " + gon(daoCaChuoi));

        // 4. Muốn x TĂNG mà y GIẢM thì phải đảo riêng tiêu chí thứ hai,
        // bằng cách dựng nó thành comparator độc lập rồi mới .reversed().
        List<Point> xTangYGiam = diem();
        xTangYGiam.sort(Comparator.comparing(Point::x)
                .thenComparing(Comparator.comparing(Point::y).reversed()));
        System.out.println("  x↑ y↓               : " + gon(xTangYGiam));

        // 5. list.sort(cmp) và Collections.sort(list, cmp) làm y hệt nhau.
        // Collections.sort là API cũ (Java 1.2); từ Java 8 List có sort() riêng -> dùng cái này.
        List<Point> quaCollections = diem();
        Collections.sort(quaCollections, Comparator.comparing(Point::x).thenComparing(Point::y));
        System.out.println("  Collections.sort    : " + gon(quaCollections)
                + "  (giống hệt dòng x↑ rồi y↑)");

        // 6. sort trả về void và SỬA TẠI CHỖ. Không có chuyện "lấy bản đã sắp xếp" từ nó.
        // Khác TS: arr.sort() của JS cũng in-place nhưng CÓ trả về mảng nên xích được.
        // Muốn bản mới mà giữ nguyên gốc -> stream().sorted().toList().
        List<Point> goc = diem();
        // Point::x là method reference, viết tắt của lambda:
        // v1: (p -> p.x())
        //
        List<Point> banMoi = goc.stream().sorted(Comparator.comparing(Point::x)).toList();
        System.out.println("  gốc sau stream      : " + gon(goc) + "  (không đổi)");
        System.out.println("  bản mới đã sắp      : " + gon(banMoi));

        // 7. List.of(...) là bất biến -> sort nổ, dù sort chỉ sắp lại chứ không thêm bớt.
        thu("sort trên List.of   ", () -> List.of(3, 1, 2).sort(Comparator.naturalOrder()));

        System.out.println("  -> sort sửa tại chỗ và trả void; .reversed() đảo CẢ chuỗi đứng trước nó,"
                + " muốn đảo riêng một tiêu chí thì phải bọc riêng tiêu chí đó");
    }

    private static void listChiDoc() {
        System.out.println();
        System.out.println("=== 5. Bốn mức 'không sửa được' ===");

        // MỨC 1 - List.of: bất biến thật. Mọi thao tác sửa đều nổ, kể cả sort
        // (chỉ xáo chỗ) và removeIf khi không có gì để xóa.
        List<String> batBien = List.of("Java", "Go");
        System.out.println("  [1] List.of");
        thu("    add                ", () -> batBien.add("Rust"));
        thu("    set                ", () -> batBien.set(0, "Rust"));
        thu("    sort               ", () -> batBien.sort(Comparator.naturalOrder()));
        thu("    removeIf (vô hại)  ", () -> batBien.removeIf(s -> false));

        // MỨC 2 - Arrays.asList: VIEW trên một mảng. Độ dài cố định vì mảng cố định,
        // nhưng từng ô thì ghi đè được -> set OK, add/remove nổ.
        String[] mang = {"Java", "Go"};
        List<String> view = Arrays.asList(mang);
        System.out.println("  [2] Arrays.asList");
        thu("    set                ", () -> view.set(0, "Rust"));
        thu("    add                ", () -> view.add("C"));
        // Sửa qua list thì mảng gốc đổi theo, và ngược lại. Hai mặt của cùng một dữ liệu.
        System.out.println("    mảng gốc sau set   : " + Arrays.toString(mang));
        mang[1] = "Kotlin";
        System.out.println("    sửa mảng -> list   : " + view);

        // MỨC 3 - unmodifiableList: VIEW chỉ-đọc trên list khác.
        // Bạn không sửa được qua view, nhưng ai giữ list gốc vẫn sửa được,
        // và view phản ánh thay đổi đó. Đây là UNMODIFIABLE, không phải IMMUTABLE.
        List<String> goc = new ArrayList<>(List.of("Java", "Go"));
        List<String> chiDoc = Collections.unmodifiableList(goc);
        System.out.println("  [3] unmodifiableList");
        thu("    add qua view       ", () -> chiDoc.add("Rust"));
        goc.add("Rust");
        System.out.println("    sửa gốc -> view    : " + chiDoc + "  (view ĐỔI THEO)");

        // MỨC 4 - copy thật sự: cắt đứt mọi liên hệ với nguồn.
        List<String> banSao = new ArrayList<>(goc);
        goc.add("Scala");
        System.out.println("  [4] new ArrayList<>(goc)");
        System.out.println("    gốc                : " + goc);
        System.out.println("    bản sao            : " + banSao + "  (không đổi)");

        // List.copyOf: vừa copy vừa bất biến - thứ nên trả ra từ service layer.
        List<String> copyBatBien = List.copyOf(goc);
        thu("    List.copyOf + add  ", () -> copyBatBien.add("Rust"));

        // null: List.of cấm tiệt, Arrays.asList thì cho.
        System.out.println("  [5] null");
        thu("    List.of(1,null,3)  ", () -> List.of(1, null, 3));
        System.out.println("    Arrays.asList      : " + Arrays.asList(1, null, 3));

        System.out.println("  -> 'không sửa được' có 4 mức khác nhau: bất biến thật (List.of),"
                + " cố định độ dài (Arrays.asList), view chỉ-đọc trên dữ liệu người khác vẫn sửa"
                + " được (unmodifiableList), và bản sao độc lập (new ArrayList / List.copyOf)");
    }

    private static void arrayListVsLinkedList() {
        System.out.println();
        System.out.println("=== 6. ArrayList vs LinkedList: đo thật ===");

        // Đây là phép đo thô, KHÔNG phải benchmark nghiêm túc: JIT, GC, cache state đều
        // nhiễu vào. Đo đúng chuẩn thì dùng JMH. Nhưng chênh lệch dưới đây đủ lớn
        // (hàng trăm lần) để kết luận không bị nhiễu lật ngược.
        final int n = 200_000;

        List<Integer> arrayList = new ArrayList<>();
        List<Integer> linkedList = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            arrayList.add(i);
            linkedList.add(i);
        }

        // A. TRUY CẬP NGẪU NHIÊN theo chỉ số.
        // ArrayList: tính địa chỉ ô mảng, O(1).
        // LinkedList: không có chỉ số, phải đi từ đầu (hoặc cuối) tới nút thứ i, O(n).
        // -> cả vòng lặp thành O(n²). Đây là chỗ chênh lệch khủng khiếp nhất.
        System.out.println("  A. get(i) " + n + " lần (chỉ chạy 20_000 cho LinkedList):");
        System.out.println("     ArrayList   : " + doMs(() -> {
            for (int i = 0; i < n; i++) {
                arrayList.get(i);
            }
        }) + " ms");
        System.out.println("     LinkedList  : " + doMs(() -> {
            for (int i = 0; i < 20_000; i++) {
                linkedList.get(i);
            }
        }) + " ms  (chỉ 1/10 số vòng mà vẫn lâu hơn nhiều)");

        // B. CHÈN ĐẦU DANH SÁCH.
        // ArrayList: phải dịch toàn bộ phần đuôi sang phải, O(n) mỗi lần.
        // LinkedList: nối lại 2 con trỏ, O(1) thật.
        // -> đây là trận LinkedList thắng.
        System.out.println("  B. add(0, x) 50_000 lần:");
        System.out.println("     ArrayList   : " + doMs(() -> {
            List<Integer> l = new ArrayList<>();
            for (int i = 0; i < 50_000; i++) {
                l.add(0, i);
            }
        }) + " ms");
        System.out.println("     LinkedList  : " + doMs(() -> {
            List<Integer> l = new LinkedList<>();
            for (int i = 0; i < 50_000; i++) {
                l.addFirst(i);
            }
        }) + " ms");

        // C. XÓA THEO ĐIỀU KIỆN khi duyệt tuần tự - tình huống thực tế hay gặp.
        // Ở đây LinkedList ĐƯỢC chơi đúng sở trường: đi tuần tự bằng Iterator nên
        // không tốn chi phí tìm nút, và mỗi lần xóa là O(1) thật.
        // ArrayList thì mỗi lần xóa phải dồn mảng... nhưng vẫn thường thắng,
        // vì dữ liệu nằm liền kề trong bộ nhớ (cache locality) còn mỗi nút LinkedList
        // là một object riêng rải rác khắp heap -> mỗi bước nhảy là một lần cache miss.
        System.out.println("  C. removeIf (xóa số chẵn) trên " + n + " phần tử:");
        System.out.println("     ArrayList   : " + doMs(() -> {
            List<Integer> l = new ArrayList<>(arrayList);
            l.removeIf(x -> x % 2 == 0);
        }) + " ms");
        System.out.println("     LinkedList  : " + doMs(() -> {
            List<Integer> l = new LinkedList<>(arrayList);
            l.removeIf(x -> x % 2 == 0);
        }) + " ms");

        System.out.println("  -> (a) ArrayList get(i) O(1), LinkedList O(n);"
                + " (b) muốn hưởng O(1) của LinkedList thì phải ĐANG ĐỨNG ở vị trí đó"
                + " (Iterator, addFirst/addLast) - tìm tới vị trí đã là O(n) rồi;"
                + " (c) ArrayList nằm liền khối trong bộ nhớ nên thân thiện với CPU cache,"
                + " còn LinkedList rải rác khắp heap. Thực tế: mặc định ArrayList,"
                + " chỉ cân nhắc LinkedList khi cần hàng đợi hai đầu - mà khi đó ArrayDeque"
                + " còn tốt hơn.");
    }

    // Mỗi lần gọi trả một list MỚI: các lần sort bên dưới không giẫm lên nhau.
    private static List<Point> diem() {
        return new ArrayList<>(List.of(
                new Point(2, 1),
                new Point(1, 3),
                new Point(2, 0),
                new Point(1, 1)));
    }

    // Point.toString mặc định của record dài dòng; rút gọn cho dễ so sánh bằng mắt.
    private static String gon(List<Point> ds) {
        StringBuilder sb = new StringBuilder("[");
        for (Point p : ds) {
            if (sb.length() > 1) {
                sb.append(", ");
            }
            sb.append("(").append((int) p.x()).append(",").append((int) p.y()).append(")");
        }
        return sb.append("]").toString();
    }

    // Đo thô bằng nanoTime rồi đổi ra ms. Chạy nóng 1 lần trước để JIT kịp biên dịch,
    // nếu không lần đo đầu tiên luôn chậm bất thường và bạn sẽ kết luận sai.
    private static long doMs(Runnable r) {
        r.run();
        long batDau = System.nanoTime();
        r.run();
        return (System.nanoTime() - batDau) / 1_000_000;
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
