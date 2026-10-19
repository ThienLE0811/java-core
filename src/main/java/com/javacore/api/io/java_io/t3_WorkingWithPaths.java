package com.javacore.api.io.java_io;

import java.nio.file.Path;

/**
 * Working with Paths
 * <p>
 * 0. Vị trí bài này
 * - Bước 21/41, bài 3/18 trong section The Java I/O API của lộ trình dev.java/learn.
 * - Thuộc nhóm File System Basics của section.
 * - Một câu cho cả bài: {@code Path} cung cấp các thao tác xử lý cú pháp đường dẫn thuần túy
 * trong bộ nhớ (lấy tên, cắt lát, ghép, chuẩn hóa, so sánh) mà hoàn toàn không cần chạm vào ổ đĩa.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/io/java-io/file-system/path/">dev.java - Working with Paths</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - WORKING WITH PATHS (mục 1 đến 7)
 * ======================================================================
 * <p>
 * 1. TẠO ĐỐI TƯỢNG PATH
 * - (Creating a Path)
 * - Cách hiện đại (Java 11+): dùng {@code Path.of("thu-muc", "file.txt")}.
 * - Cách cũ hơn (Java 7): dùng {@code Paths.get(...)}.
 * - Có thể tạo từ chuỗi phân tách sẵn hoặc danh sách nhiều chuỗi nối tiếp nhau.
 * - Các thành phần tên được đánh chỉ số từ 0 (gần thư mục gốc nhất) đến n-1 (tên file hoặc
 * thư mục con cuối cùng).
 * <p>
 * 2. LẤY THÔNG TIN TỪ ĐƯỜNG DẪN
 * - (Retrieving Information about a Path)
 * - {@code toString()}: trả về chuỗi đường dẫn đầy đủ.
 * - {@code getFileName()}: lấy tên phần tử cuối cùng (tên file hoặc thư mục đích).
 * - {@code getParent()}: lấy đường dẫn thư mục cha (trả về {@code null} nếu chỉ là file tương đối độc lập).
 * - {@code getRoot()}: lấy phần tử gốc (ví dụ C:\ trên Windows, / trên Linux; trả về {@code null}
 * nếu là đường dẫn tương đối).
 * - {@code getNameCount()}: đếm số lượng thành phần tên (không tính phần gốc root).
 * - {@code getName(index)}: lấy thành phần tại vị trí index (0 đến count - 1).
 * - {@code subpath(from, to)}: cắt lấy một đoạn đường dẫn con từ chỉ số from đến to - 1.
 * <p>
 * 3. RÚT GỌN VÀ KHỬ PHẦN THỪA BẰNG NORMALIZE
 * - (Removing Redundancies From a Path)
 * - Dùng để khử dấu chấm . (thư mục hiện tại) và .. (thư mục cha).
 * - Thao tác thuần túy trên chuỗi trong RAM: không kiểm tra đĩa, không giải quyết symbolic link.
 * <p>
 * 4. CHUYỂN ĐỔI ĐƯỜNG DẪN
 * - (Converting a Path)
 * - {@code toAbsolutePath()}: chuyển thành đường dẫn tuyệt đối bắt đầu từ ổ đĩa/gốc (file không
 * cần có thật trên máy).
 * - {@code toUri()}: chuyển đường dẫn thành định dạng URI (ví dụ file:///...).
 * - {@code toRealPath()}: kiểm tra file thật trên ổ cứng, khử thừa và resolve symbolic link;
 * ném {@code IOException} nếu file không tồn tại.
 * <p>
 * 5. GHÉP ĐƯỜNG DẪN BẰNG RESOLVE
 * - (Joining Two Paths)
 * - Dùng {@code resolve("con.txt")} để nối thêm đường dẫn con vào đường dẫn cha.
 * - Bẫy: nếu truyền vào một đường dẫn tuyệt đối, nó sẽ trả về chính đường dẫn tuyệt đối đó
 * (nuốt mất đường dẫn cha).
 * <p>
 * 6. TÌM ĐƯỜNG DẪN TƯƠNG ĐỐI BẰNG RELATIVIZE
 * - (Creating a Path Between Two Paths)
 * - Tìm đường đi từ điểm A sang điểm B (từ thư mục này muốn sang thư mục kia thì phải lùi mấy
 * cấp và đi vào đâu).
 * - Điều kiện bắt buộc: cả hai path phải cùng kiểu (cùng tuyệt đối hoặc cùng tương đối; trên
 * Windows phải cùng ổ đĩa).
 * <p>
 * 7. SO SÁNH VÀ DUYỆT PATH
 * - (Comparing Two Paths)
 * - {@code equals()}: so sánh cú pháp hai đường dẫn (phụ thuộc hệ điều hành, Windows không
 * phân biệt hoa thường).
 * - {@code startsWith()} / {@code endsWith()}: kiểm tra bắt đầu hoặc kết thúc THEO TỪNG THÀNH PHẦN
 * (không phải kiểm tra chuỗi con).
 * - {@code Path} cài đặt {@code Iterable}: có thể dùng vòng lặp for-each để duyệt qua từng
 * thành phần tên của đường dẫn.
 * - Muốn kiểm tra 2 path có thực sự trỏ tới cùng một file trên ổ đĩa hay không: dùng
 * {@code Files.isSameFile(p1, p2)}.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 8. BẪY THỰC TẾ CHO NGƯỜI MỚI
 * - {@code startsWith("tailieu")} kiểm tra thành phần thư mục có tên "tailieu", KHÔNG PHẢI
 * chuỗi con "tai".
 * - {@code relativize} giữa 2 ổ đĩa khác nhau trên Windows (C:\ và D:\) sẽ ném
 * {@code IllegalArgumentException} vì không thể đi tương đối giữa hai ổ.
 * - Toàn bộ method trong bài này đều là thao tác cú pháp (syntactic operations), máy không
 * đọc ghi ổ cứng nên chạy rất nhanh. Ngoại lệ duy nhất là {@code toRealPath()} - nó chạm đĩa.
 * - Đối chiếu TypeScript: cả mục 1 đến 7 ≈ module {@code path} của Node. {@code resolve} ≈
 * {@code path.join} nhưng nuốt đường dẫn tuyệt đối giống {@code path.resolve}; {@code normalize}
 * ≈ {@code path.normalize}; {@code relativize} ≈ {@code path.relative}.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 9. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Method nào dùng để lấy tên file cuối cùng của một {@code Path}?
 * - {@code getRoot()} trả về gì khi gọi trên đường dẫn tương đối {@code Path.of("data/app.txt")}?
 * - Sự khác nhau cơ bản giữa {@code normalize()} và {@code toRealPath()} là gì?
 * - Nếu gọi {@code pathCha.resolve("C:\\Windows")} thì kết quả trả về là gì?
 * - Vì sao {@code Path} có thể dùng được trong vòng lặp for-each?
 * - Trong 7 mục của trang gốc, method nào là method DUY NHẤT chạm vào ổ đĩa?
 */
public class t3_WorkingWithPaths {

    void main() {
        // ==============================================================
        // 1. TẠO PATH VÀ LẤY CÁC THÀNH PHẦN CƠ BẢN
        // ==============================================================
        // Tạo đường dẫn: gom các thư mục con lại rất tiện bằng dấu phẩy
        Path duongDan = Path.of("duan", "baocao", "2026", "tongket.xlsx");
        IO.println("1. Lấy thông tin từ Path: " + duongDan);

        // Lấy tên file cuối cùng
        IO.println("   Tên file (getFileName):   " + duongDan.getFileName());

        // Lấy thư mục cha chứa nó
        IO.println("   Thư mục cha (getParent):  " + duongDan.getParent());

        // Gốc ổ đĩa (đường dẫn tương đối thì getRoot sẽ trả về null)
        IO.println("   Gốc ổ đĩa (getRoot):      " + duongDan.getRoot());

        // Đếm số lượng thành phần tên trong đường dẫn
        IO.println("   Số thành phần tên:        " + duongDan.getNameCount());
        IO.println("   Thành phần đầu tiên (0):  " + duongDan.getName(0));
        IO.println("   Cắt đoạn con subpath(0,2):" + duongDan.subpath(0, 2));


        // ==============================================================
        // 2. GHÉP ĐƯỜNG DẪN BẰNG RESOLVE
        // ==============================================================
        Path thuMucGoc = Path.of("ung-dung");
        // Nối thêm thư mục con và file
        Path fileCauHinh = thuMucGoc.resolve("cauhinh").resolve("app.properties");
        IO.println("\n2. Ghép nối đường dẫn (resolve): " + fileCauHinh);


        // ==============================================================
        // 3. RÚT GỌN ĐƯỜNG DẪN BẰNG NORMALIZE
        // ==============================================================
        // Dấu . nghĩa là thư mục hiện tại, .. nghĩa là lùi về thư mục cha
        Path duongDanRua = Path.of("duan", "tam", "..", "tailieu", ".", "doc.txt");
        IO.println("\n3. Trước khi rút gọn: " + duongDanRua);
        // normalize sẽ khử "tam/.." và "." chỉ còn "duan/tailieu/doc.txt"
        IO.println("   Sau khi normalize:  " + duongDanRua.normalize());


        // ==============================================================
        // 4. TÌM ĐƯỜNG ĐI GIỮA HAI VỊ TRÍ (RELATIVIZE)
        // ==============================================================
        // Đang đứng ở thư mục A, muốn đi sang thư mục B thì đi thế nào?
        Path thuMucA = Path.of("nhan-vien", "an");
        Path thuMucB = Path.of("nhan-vien", "binh");
        // Từ "an" sang "binh": phải lùi lên 1 cấp (..) rồi đi vào "binh"
        Path duongDiTuASangB = thuMucA.relativize(thuMucB);
        IO.println("\n4. Đường đi từ A sang B (relativize): " + duongDiTuASangB);


        // ==============================================================
        // 5. SO SÁNH VÀ DUYỆT QUA TỪNG PHẦN TỬ BẰNG VÒNG LẶP FOR
        // ==============================================================
        Path kiemTra = Path.of("duan", "backend", "Main.java");
        IO.println("\n5. Kiểm tra tiền tố và duyệt phần tử:");
        // startsWith kiểm tra theo thành phần tên (phải đúng cả từ "duan")
        IO.println("   Có bắt đầu bằng 'duan' không?      " + kiemTra.startsWith("duan"));
        IO.println("   Có kết thúc bằng 'Main.java' không? " + kiemTra.endsWith("Main.java"));

        // Path có hỗ trợ Iterable nên duyệt bằng for-each rất dễ dàng
        IO.println("   Duyệt qua từng cấp thư mục:");
        for (Path tungPhanTu : kiemTra) {
            IO.println("     -> " + tungPhanTu);
        }


        // ==============================================================
        // BÀI TẬP TỰ LUYỆN
        // ==============================================================
        // TODO 1 (mục 2): Tạo một Path trỏ tới "hinh-anh/avatar.png". Dùng getFileName()
        //   và getParent() để in riêng tên ảnh và tên thư mục chứa ảnh.
        //
        // TODO 2 (mục 3): Tạo một Path có đường dẫn thừa: Path.of("a/b/../c/./d.txt").
        //   Gọi normalize() và in kết quả sau khi đã rút gọn.
        //
        // TODO 3 (mục 7): Dùng vòng for-each để in ra từng thành phần trong đường dẫn
        //   Path.of("khoa-hoc/lap-trinh/java-core/bai-1.pdf").
        //
        // TODO 4 (mục 5, 8): Chạy Path.of("a", "b").resolve("/c") rồi dán lại output.
        //   Giải thích vì sao resolve một đường dẫn tuyệt đối lại nuốt mất đường dẫn cha.
        //
        // TODO 5 (mục 6, 8): Gọi relativize giữa Path.of("C:\\x") và Path.of("D:\\y").
        //   Dán nguyên văn exception nhận được vào đây.
        //
        // TODO 6 (mục 4): So sánh normalize() và toRealPath() trên cùng một đường dẫn
        //   KHÔNG tồn tại. Cái nào chạy được, cái nào ném exception, vì sao?
    }
}
