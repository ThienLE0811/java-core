package com.javacore.api.io.java_io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Accessing Resources using Paths
 * <p>
 * 0. Vị trí bài này
 * - Bước 21/41, bài 2/18 trong section The Java I/O API của lộ trình dev.java/learn.
 * - Thuộc nhóm File System Basics của section.
 * - Một câu cho cả bài: File là cách cũ (Java 1.0), Path + Files là cách mới (Java 7+);
 *   chuyển đổi qua lại giữa hai cách bằng file.toPath() và path.toFile().
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/io/java-io/file-system/file-path/">dev.java - Accessing Resources using Paths</a>
 * <p>
 * ======================================================================
 * TRANG GỐC - ACCESSING RESOURCES USING PATHS (mục 1 đến 3)
 * ======================================================================
 * <p>
 * 1. LỚP DI SẢN FILE
 * - (Introducing the Legacy File Class)
 * - Khái niệm: đại diện trừu tượng cho đường dẫn (tập tin, thư mục).
 * - new File(...) chỉ tạo đối tượng trong bộ nhớ, KHÔNG tạo file thật trên ổ cứng.
 * - Hạn chế: xóa hay tạo file thất bại chỉ trả về false chứ không báo lý do, không hỗ trợ tốt tính năng hiện đại.
 * <p>
 * 2. INTERFACE PATH
 * - (Introducing the Path Interface)
 * - Là chuẩn mới từ Java 7, dùng thay thế cho File.
 * - Biểu diễn đường dẫn tập tin hoặc thư mục.
 * - Đối tượng Path cũng chỉ nằm trong bộ nhớ (file có thể chưa tồn tại trên đĩa).
 * - Các thao tác như ghép đường dẫn (resolve), cắt chuỗi chỉ xử lý chữ, không chạm vào ổ đĩa.
 * - Khi nào cần thao tác thật với ổ cứng (tạo, xóa, kiểm tra file), ta dùng các hàm static của lớp Files.
 * <p>
 * 3. REFACTOR CODE TỪ FILE SANG PATH
 * - (Refactoring your Code to Using Path)
 * - Hai hàm cầu nối quan trọng nhất:
 *   + Từ File cũ sang Path mới: gọi file.toPath()
 *   + Từ Path mới về File cũ: gọi path.toFile()
 * - Bảng thay thế các thao tác cơ bản:
 *   + new File(parent, "con.txt")  ->  parentPath.resolve("con.txt")
 *   + file.exists()                ->  Files.exists(path)
 *   + file.length()                ->  Files.size(path)
 *   + file.createNewFile()         ->  Files.createFile(path)
 *   + file.delete()                ->  Files.delete(path) hoặc Files.deleteIfExists(path)
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 4. MẸO DỄ NHỚ CHO NGƯỜI MỚI
 * - Path = "Tấm biển ghi địa chỉ nhà" (chỉ là chữ, nhà có thật hay không chưa biết).
 * - Files = "Người đi tới địa chỉ đó để làm việc" (gõ cửa, kiểm tra nhà có tồn tại không, xây nhà mới, phá nhà).
 * - Khi dùng thư viện cũ bắt truyền File: cứ dùng Path để xử lý, đến dòng cuối cùng thì gọi path.toFile().
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 5. CÂU HỎI TỰ ÔN TẬP
 * - Muốn chuyển từ File sang Path thì gọi hàm gì?
 * - Muốn chuyển từ Path sang File thì gọi hàm gì?
 * - Lệnh Path.of("a.txt") có tạo ra file a.txt trên đĩa không?
 * - Để kiểm tra một Path có tồn tại trên đĩa thật hay không, ta dùng hàm nào của lớp Files?
 */
public class t2_AccessingResourcesUsingPaths {

    void main() {
        // ==============================================================
        // 1. CÁCH TẠO PATH VÀ CHUYỂN ĐỔI QUA LẠI VỚI FILE CŨ
        // ==============================================================
        // Cách tạo Path hiện đại nhất (từ Java 11 trở lên)
        Path duongDan = Path.of("bai-tap.txt");
        IO.println("1. Tạo Path đơn giản: " + duongDan);

        // Chuyển từ File (cũ) sang Path (mới)
        File fileCu = new File("bai-tap.txt");
        Path pathMoi = fileCu.toPath();
        IO.println("   File cũ chuyển sang Path mới: " + pathMoi);

        // Chuyển ngược lại từ Path (mới) sang File (cũ) khi cần truyền vào thư viện cũ
        File chuyenVeCu = pathMoi.toFile();
        IO.println("   Path mới chuyển về File cũ:   " + chuyenVeCu.getName());


        // ==============================================================
        // 2. GHÉP ĐƯỜNG DẪN BẰNG RESOLVE (CHỈ TRÊN BỘ NHỚ, KHÔNG CHẠM ĐĨA)
        // ==============================================================
        Path thuMucCha = Path.of("tailieu");
        // Ghép thêm tên file vào thư mục: tailieu + "loichao.txt" -> tailieu\loichao.txt
        Path fileCon = thuMucCha.resolve("loichao.txt");
        IO.println("\n2. Ghép đường dẫn bằng resolve(): " + fileCon);


        // ==============================================================
        // 3. THAO TÁC THỰC TẾ VỚI Ổ ĐĨA BẰNG LỚP FILES
        // ==============================================================
        IO.println("\n3. Thao tác thật trên ổ đĩa bằng lớp Files:");
        Path fileTam = Path.of("chao-javacore.txt");

        try {
            // Kiểm tra xem file đã có sẵn chưa
            boolean daCo = Files.exists(fileTam);
            IO.println("   File có tồn tại trước khi tạo không? " + daCo);

            // Tạo file mới trên ổ đĩa (thay cho file.createNewFile())
            if (!daCo) {
                Files.createFile(fileTam);
                IO.println("   -> Đã tạo file thành công: " + fileTam);
            }

            // Ghi một dòng chữ đơn giản vào file
            Files.writeString(fileTam, "Xin chao Java Core!");

            // Lấy dung lượng file theo byte (thay cho file.length())
            long kichThuoc = Files.size(fileTam);
            IO.println("   -> Kích thước file: " + kichThuoc + " bytes");

            // Xóa file sau khi dùng xong (thay cho file.delete())
            boolean daXoa = Files.deleteIfExists(fileTam);
            IO.println("   -> Đã xóa file dọn dẹp: " + daXoa);

        } catch (IOException e) {
            IO.println("   Có lỗi I/O xảy ra: " + e.getMessage());
        }


        // ==============================================================
        // BÀI TẬP TỰ LUYỆN ĐƠN GIẢN DÀNH CHO BẠN
        // ==============================================================
        // TODO 1: Dùng Path.of(...) để tạo một đường dẫn trỏ tới file "thong-tin.txt".
        //         In ra màn hình đường dẫn tuyệt đối của nó bằng hàm toAbsolutePath().
        //
        // TODO 2: Dùng Files.exists(...) để kiểm tra xem file "thong-tin.txt" có thật trên máy bạn không.
        //
        // TODO 3: Thử tạo một đối tượng File cũ bằng new File("test.txt"), rồi dùng hàm .toPath()
        //         để lấy đối tượng Path tương ứng và in ra màn hình.
    }
}
