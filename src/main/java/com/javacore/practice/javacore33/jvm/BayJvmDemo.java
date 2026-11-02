package com.javacore.practice.javacore33.jvm;

// Bài 37 phần 1, 2, 4, 5 - chạy bằng nút Run trong IDE, không cần VM option.
// Phần 3 (StackOverflowError / OutOfMemoryError) nằm ở TranBoNhoDemo.
public class BayJvmDemo {

    private static final int SO_LAN_NOI_CHUOI = 100_000;

    void main() {
//        phan1IntegerCache();
//        phan2StringPool();
//        phan4NoiChuoi();
        phan5TruyenThamSo();
    }

    // --- Phần 1: Integer cache -------------------------------------------

    private static void phan1IntegerCache() {
        tieuDe("Phần 1 - Integer cache");

        Integer a = 127;
        Integer b = 127;
        Integer c = 128;
        Integer d = 128;

        System.out.printf("Integer 127 == Integer 127 -> %b%n", a == b);
        System.out.printf("Integer 128 == Integer 128 -> %b%n", c == d);

        // Tìm ranh giới: box hai lần cùng một giá trị rồi so sánh tham chiếu.
        System.out.println();
        System.out.println("Ranh giới của cache:");
        for (int i = 125; i <= 130; i++) {
            Integer x = i;
            Integer y = i;
            System.out.printf("  %3d: %-5b  (identityHashCode %s / %s)%n",
                    i, x == y, maBam(x), maBam(y));
        }

        // Integer so với int: int không box được, nên Integer bị UNBOX về int
        // rồi so sánh giá trị số. Không còn dính dáng gì tới cache nữa.
        int soNguyen = 128;
        System.out.printf("%nInteger 128 == int 128     -> %b   (unbox, so giá trị)%n", c == soNguyen);

        System.out.println("""
                
                Vì sao: autoboxing gọi Integer.valueOf(), method này trả object
                dùng lại từ cache cho khoảng [-128, 127]. Ngoài khoảng đó mỗi lần
                box là một object mới. Cận trên chỉnh được bằng -XX:AutoBoxCacheMax,
                cận dưới -128 thì cố định.
                Ở Spring: so sánh @PathVariable Long id bằng == chạy đúng ở dev
                (id nhỏ) và sai ở prod khi id vượt 127. Luôn dùng equals().""");
    }

    // --- Phần 2: String pool ---------------------------------------------

    private static void phan2StringPool() {
        tieuDe("Phần 2 - String pool");

        String s1 = "abc";
        String s2 = "abc";
        String moi = new String("abc");

        System.out.printf("\"abc\" == \"abc\"                     -> %-5b  %s / %s%n",
                s1 == s2, maBam(s1), maBam(s2));
        System.out.printf("new String(\"abc\") == \"abc\"        -> %-5b  %s / %s%n",
                moi == s1, maBam(moi), maBam(s1));
        System.out.printf("new String(\"abc\").intern() == ... -> %-5b  %s / %s%n",
                moi.intern() == s1, maBam(moi.intern()), maBam(s1));
        System.out.printf("moi.equals(\"abc\")                 -> %-5b  (nội dung thì luôn bằng)%n",
                moi.equals(s1));

        // Nối chuỗi: kết quả phụ thuộc compiler có gộp được thành hằng hay không.
        String bien = "ab";
        final String hang = "ab";

        System.out.printf("%nString bien = \"ab\";       (bien + \"c\") == \"abc\" -> %b%n", (bien + "c") == s1);
        System.out.printf("final String hang = \"ab\"; (hang + \"c\") == \"abc\" -> %b%n", (hang + "c") == s1);

        System.out.println("""
                
                Vì sao: literal được nạp vào string pool lúc class load nên mọi
                "abc" trong code là cùng một object. new String() luôn tạo object
                mới trên heap, chỉ intern() mới kéo được về bản trong pool.
                Hai dòng cuối: biến final khởi tạo bằng literal là hằng biên dịch,
                javac gộp sẵn thành "abc" và lấy từ pool. Biến thường thì phép nối
                xảy ra lúc chạy, tạo object mới -> false.""");
    }

    // --- Phần 4: nối chuỗi trong vòng lặp --------------------------------

    private static void phan4NoiChuoi() {
        tieuDe("Phần 4 - nối chuỗi %,d lần".formatted(SO_LAN_NOI_CHUOI));

        long batDau = System.nanoTime();
        String chuoi = "";
        for (int i = 0; i < SO_LAN_NOI_CHUOI; i++) {
            chuoi += "x";
        }
        long msCong = (System.nanoTime() - batDau) / 1_000_000;

        batDau = System.nanoTime();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SO_LAN_NOI_CHUOI; i++) {
            sb.append("x");
        }
        String ketQua = sb.toString();
        long msBuilder = (System.nanoTime() - batDau) / 1_000_000;

        // In length() để chắc chắn JIT không loại bỏ hẳn vòng lặp.
        System.out.printf("Toán tử +     %,7d ms   (độ dài %,d)%n", msCong, chuoi.length());
        System.out.printf("StringBuilder %,7d ms   (độ dài %,d)%n", msBuilder, ketQua.length());
        System.out.printf("Chênh         %,7.0f lần%n", (double) msCong / Math.max(msBuilder, 1));

        System.out.println("""
                
                Vì sao: String bất biến, nên mỗi vòng lặp phải cấp phát một String
                mới rồi copy lại toàn bộ ký tự cũ -> tổng chi phí O(n^2).
                StringBuilder giữ một mảng byte và chỉ nhân đôi khi đầy -> O(n).
                Đừng kết luận sai: javac VẪN gộp a + b + c trong MỘT biểu thức
                thành một thao tác nối. Cái chậm là nối qua nhiều vòng lặp.
                Ở Spring: dựng câu SQL, export CSV hay gom log message trong for.""");
    }

    // --- Phần 5: Java truyền theo giá trị --------------------------------

    private static final class NguoiDung {
        private String ten;

        private NguoiDung(String ten) {
            this.ten = ten;
        }

        private void setTen(String ten) {
            this.ten = ten;
        }

        @Override
        public String toString() {
            return "NguoiDung[ten=" + ten + "]";
        }
    }

    private static void ganLai(NguoiDung nguoiDung) {
        // Gán lại tham số: chỉ đổi biến cục bộ bên trong method này.
        nguoiDung = new NguoiDung("Người mới");
        System.out.printf("  trong ganLai sau khi gán    : %s %s%n", nguoiDung, maBam(nguoiDung));
    }

    private static void doiTruong(NguoiDung nguoiDung) {
        // Đi theo tham chiếu rồi sửa object: bên ngoài thấy được.
        nguoiDung.setTen("Người mới");
        System.out.printf("  trong doiTruong sau khi sửa : %s %s%n", nguoiDung, maBam(nguoiDung));
    }

    private static void tangLen(int so) {
        so++;
        System.out.printf("  trong tangLen sau khi tăng  : %d%n", so);
    }

    private static void phan5TruyenThamSo() {
        tieuDe("Phần 5 - Java truyền theo giá trị");

        // Đối chứng primitive: int nằm hẳn trên stack, không có object nào để
        // đi tới. Nên không có cách nào sửa được biến của hàm gọi.
        int so = 10;
        System.out.printf("Trước tangLen   : %d%n", so);
        tangLen(so);
        System.out.printf("Sau   tangLen   : %d   <- KHÔNG đổi (primitive, chỉ có stack)%n%n", so);

        NguoiDung a = new NguoiDung("Thiên");
        System.out.printf("Trước ganLai    : %s %s%n", a, maBam(a));
        ganLai(a);
        System.out.printf("Sau   ganLai    : %s %s   <- KHÔNG đổi%n%n", a, maBam(a));

        NguoiDung b = new NguoiDung("Thiên");
        System.out.printf("Trước doiTruong : %s %s%n", b, maBam(b));
        doiTruong(b);
        System.out.printf("Sau   doiTruong : %s %s   <- CÓ đổi%n", b, maBam(b));

        System.out.println("""
                
                Vì sao: Java luôn truyền theo giá trị. Với object, thứ được copy
                là giá trị của tham chiếu. Gán lại tham số chỉ trỏ bản copy sang
                object khác - nhìn identityHashCode đổi rồi quay về là thấy; đi
                theo tham chiếu để sửa field thì chạm đúng object bên ngoài.
                Nói theo stack/heap: cả 3 biến so, nguoiDung của hàm gọi và của
                hàm được gọi đều nằm trên stack frame RIÊNG. Khác nhau ở chỗ hai
                biến tham chiếu cùng chỉ vào MỘT object trên heap, còn hai biến
                int thì là hai ô nhớ độc lập, không chung gì cả.
                Giống hệt JS/TS - khác C++ ở chỗ Java không có tham chiếu kiểu &.
                Ở Spring: service sửa field của entity được truyền vào, trong
                @Transactional dirty checking sẽ flush xuống DB dù không gọi save().""");
    }

    // --- Tiện ích ---------------------------------------------------------

    private static String maBam(Object o) {
        return "@%08x".formatted(System.identityHashCode(o));
    }

    private static void tieuDe(String ten) {
        System.out.printf("%n=== %s ===%n", ten);
    }
}
