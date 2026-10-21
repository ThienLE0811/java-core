package com.javacore.jvm.core_tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.spi.ToolProvider;
import java.util.stream.Stream;

/**
 * Javap - the Disassembler
 * <p>
 * 0. Vị trí bài này
 * - Bài 3/12 của section The Core JDK Tools. Đi liền sau {@code t2_} vì nó soi đúng thứ mà
 * {@code javac} vừa sinh ra.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/jvm/core-tools/javap/">dev.java - Javap - the Disassembler</a>
 * - Trang gốc có 4 mục: Introducing Javap, Description, Options, Examples. Rất ngắn, phần
 * lớn là bảng option.
 * - Một câu cho cả bài: {@code javap} đọc {@code .class} và in ra NHỮNG GÌ THỰC SỰ CÓ TRONG
 * ĐÓ - không phải những gì bạn nghĩ mình đã viết. Nó là công cụ để trả lời "compiler đã
 * dịch cái này thành gì?".
 * - Mọi con số ở mục 6 trở đi là ĐO THẬT trên JDK 25.0.4.1 / Windows.
 * <p>
 * ======================================================================
 * TRANG GỐC - JAVAP (mục 1 đến 4)
 * ======================================================================
 * <p>
 * 1. JAVAP LÀ GÌ
 * - Nhận vào {@code .class} (tên class, không phải tên file - tìm qua {@code -cp}), in ra
 * mô tả của class đó.
 * - MẶC ĐỊNH KHÔNG in bytecode. Không có cờ nào thì nó chỉ in chữ ký các thành viên, tức là
 * một dạng "header file" của class.
 * - Nó KHÔNG dựng lại được mã nguồn. Không phải decompiler (như CFR, Fernflower). Nó chỉ
 * phơi bày bytecode ở dạng người đọc được.
 * <p>
 * 2. CÁC CỜ VỀ PHẠM VI HIỂN THỊ
 * - {@code -public}: chỉ thành viên public.
 * - {@code -protected}: protected và public.
 * - {@code -package}: package, protected, public. ĐÂY LÀ MẶC ĐỊNH.
 * - {@code -private} (hay {@code -p}): tất cả, kể cả private.
 * - Nhớ cái mặc định là {@code -package}, nếu không sẽ tưởng class không có field private.
 * <p>
 * 3. CÁC CỜ VỀ NỘI DUNG
 * - {@code -c}: in bytecode đã dịch ngược. Đây là cờ hay dùng nhất.
 * - {@code -l}: in {@code LineNumberTable} và {@code LocalVariableTable}.
 * - {@code -s}: in chữ ký kiểu nội bộ (dạng {@code (Ljava/lang/String;)I}).
 * - {@code -v} / {@code -verbose}: in tất tần tật, gồm constant pool, phiên bản class file,
 * flags, và mọi attribute.
 * - {@code -constants}: in giá trị của {@code static final}.
 * - {@code -sysinfo}: đường dẫn, kích thước, ngày, mã băm MD5 của file class.
 * - {@code --module} / {@code --module-path} / {@code --system}: soi class nằm trong module.
 * <p>
 * 4. ĐỌC GÌ TRONG KẾT QUẢ {@code -v}
 * - {@code major version}: phiên bản class file. Công thức: {@code major = 44 + bản JDK}.
 * - {@code flags}: {@code ACC_PUBLIC}, {@code ACC_SUPER}, {@code ACC_FINAL}...
 * - Constant pool: bảng hằng số, nơi chứa mọi tên class, tên method, chuỗi ký tự.
 * - {@code SourceFile}: tên file nguồn, còn giữ lại kể cả khi không có {@code -g}.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐO THẬT TRÊN JDK 25.0.4.1 / WINDOWS
 * ======================================================================
 * <p>
 * 5. BẢNG {@code major version} - TRA NGƯỢC RA JDK
 * - Đo thật: class biên dịch bằng JDK 25 cho {@code major version: 69}.
 * - Công thức 44 + N: 52 = Java 8, 55 = Java 11, 61 = Java 17, 65 = Java 21, 69 = Java 25.
 * - VÌ SAO PHẢI NHỚ: đây là cách duy nhất xác định một {@code .jar} của bên thứ ba được
 * build bằng JDK nào. Khi gặp {@code UnsupportedClassVersionError: class file version 65.0,
 * this version of the Java Runtime only recognizes class file versions up to 61.0}, dịch
 * ra là: thư viện build bằng 21, runtime đang là 17.
 * <p>
 * 6. {@code javap -l} MỘT MÌNH THÌ KHÔNG RA GÌ - BẪY THẬT
 * - Chạy {@code javap -l Demo} ra đúng câu này:
 * {@code Warning: bad combination of options: -l without -c, line number and local variable
 * tables will not be printed}
 * - Phải là {@code javap -c -l} thì hai bảng mới hiện. Trang gốc không nói.
 * <p>
 * 7. KIỂM CHỨNG CỜ {@code -g} CỦA BÀI TRƯỚC
 * - Cùng một {@code Demo.java} (một constructor ngầm + một phương thức), đếm bằng
 * {@code javap -c -l}:
 * + {@code javac} mặc định: {@code LineNumberTable} xuất hiện 2 lần,
 * {@code LocalVariableTable} 0 lần.
 * + {@code javac -g}: {@code LineNumberTable} 2 lần, {@code LocalVariableTable} 2 lần.
 * + Con số 2 chính là số phương thức - mỗi phương thức một bảng. {@code main()} bên dưới
 * in lại đúng hai dòng này.
 * - Vậy mặc định ĐÃ CÓ số dòng (đủ để stack trace đọc được), chỉ thiếu tên biến cục bộ.
 * Đây là câu trả lời đo được cho mục 9 của {@code t2_}, không phải suy đoán.
 * <p>
 * 8. NỐI CHUỖI KHÔNG CÒN LÀ {@code StringBuilder} - PHÁT HIỆN CHỈ {@code javap} CHO THẤY
 * - Nguồn: {@code return "hi " + name + local;}
 * - Bytecode thật trên JDK 25:
 * {@code 9: invokedynamic #13, 0 // InvokeDynamic
 * #0:makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;}
 * - Tức là KHÔNG có {@code new StringBuilder()} nào cả. Từ JDK 9 (JEP 280), compiler sinh
 * ra một lệnh {@code invokedynamic}, và việc chọn chiến lược nối chuỗi được hoãn tới lúc
 * chạy, do {@code StringConcatFactory} quyết định.
 * - HỆ QUẢ CHO LỜI KHUYÊN CŨ: câu "luôn dùng StringBuilder thay vì +" là lời khuyên của
 * thời Java 8. Với nối chuỗi trong MỘT biểu thức, {@code +} nay nhanh ngang hoặc hơn.
 * {@code StringBuilder} vẫn đúng khi nối TRONG VÒNG LẶP - mỗi vòng là một
 * {@code invokedynamic} riêng, vẫn sinh rác.
 * - Đây là ví dụ rõ nhất cho câu mở bài: {@code javap} cho thấy cái thực sự chạy, không
 * phải cái mình tưởng.
 * <p>
 * 9. KHI NÀO BACKEND THỰC SỰ MỞ {@code javap}
 * - Nghi ngờ phiên bản class trong một jar lạ (mục 5).
 * - Kiểm tra tên tham số có được giữ lại không: Spring cần tên tham số cho
 * {@code @PathVariable}/{@code @RequestParam} khi không ghi rõ tên. Dấu hiệu là attribute
 * {@code MethodParameters}, sinh ra bởi cờ {@code -parameters} - KHÔNG phải {@code -g}.
 * Thiếu nó, Spring Boot báo {@code Name for argument of type [java.lang.String] not
 * specified}.
 * - Xem một record hay enum thực sự được dịch thành gì.
 * - Xác nhận lambda được dịch thành {@code invokedynamic} chứ không phải inner class.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 10. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - {@code javap} không cờ in ra gì? Phạm vi mặc định là gì?
 * - {@code major version} 61 và 69 ứng với JDK nào? Công thức?
 * - Vì sao {@code -l} một mình lại vô dụng?
 * - Nối chuỗi bằng {@code +} trên JDK 25 sinh ra lệnh gì?
 * - Cờ nào giữ tên THAM SỐ, cờ nào giữ tên BIẾN CỤC BỘ?
 */
public class t3_JavapTheDisassembler {

    void main() throws IOException {
        Path work = Files.createTempDirectory("javap-demo");
        try {
            Path source = work.resolve("Demo.java");
            Files.writeString(source, """
                    public class Demo {
                        private int count = 1;
                        public String greet(String name) {
                            int local = count + 1;
                            return "hi " + name + local;
                        }
                    }
                    """);

            ToolProvider javac = ToolProvider.findFirst("javac").orElseThrow();
            ToolProvider javap = ToolProvider.findFirst("javap").orElseThrow();

            Path plain = work.resolve("plain");
            Path debug = work.resolve("debug");
            javac.run(System.out, System.err, "-d", plain.toString(), source.toString());
            javac.run(System.out, System.err, "-g", "-d", debug.toString(), source.toString());

            // 1. Không cờ: chỉ chữ ký, và KHÔNG thấy field private (mục 1, mục 2).
            System.out.println("=== (1) javap Demo - mặc định, phạm vi -package ===");
            javap.run(System.out, System.err, "-cp", plain.toString(), "Demo");

            // 2. -p: private hiện ra.
            System.out.println("\n=== (2) javap -p Demo ===");
            javap.run(System.out, System.err, "-p", "-cp", plain.toString(), "Demo");

            // 3. -c: nối chuỗi hoá ra là invokedynamic (mục 8). Tìm dòng makeConcat.
            System.out.println("\n=== (3) javap -c: nối chuỗi dịch thành gì ===");
            javap.run(System.out, System.err, "-c", "-cp", plain.toString(), "Demo");

            // 4. -l một mình -> cảnh báo, không ra bảng nào (mục 6).
            System.out.println("\n=== (4) javap -l một mình -> cảnh báo ===");
            javap.run(System.out, System.err, "-l", "-cp", plain.toString(), "Demo");

            // 5. So sánh bảng gỡ lỗi giữa javac mặc định và javac -g (mục 7).
            System.out.println("\n=== (5) đếm bảng gỡ lỗi: mặc định vs -g ===");
            System.out.println("  mặc định: LineNumberTable=" + count(javap, plain, "LineNumberTable")
                    + ", LocalVariableTable=" + count(javap, plain, "LocalVariableTable"));
            System.out.println("  với -g  : LineNumberTable=" + count(javap, debug, "LineNumberTable")
                    + ", LocalVariableTable=" + count(javap, debug, "LocalVariableTable"));

            // 6. -v để lấy major version, rồi tra ngược ra bản JDK (mục 5).
            System.out.println("\n=== (6) major version -> JDK nào ===");
            String verbose = capture(javap, "-v", "-cp", plain.toString(), "Demo");
            verbose.lines()
                    .filter(line -> line.contains("major version") || line.contains("minor version"))
                    .map(String::strip)
                    .forEach(System.out::println);
            int major = verbose.lines()
                    .filter(line -> line.contains("major version"))
                    .mapToInt(line -> Integer.parseInt(line.replaceAll("\\D+", "")))
                    .findFirst()
                    .orElse(0);
            System.out.println("  => biên dịch bằng JDK " + (major - 44) + " (công thức 44 + N)");
        } finally {
            deleteRecursively(work);
        }
    }

    private static long count(ToolProvider javap, Path classDir, String attribute) {
        return capture(javap, "-c", "-l", "-cp", classDir.toString(), "Demo")
                .lines()
                .filter(line -> line.contains(attribute))
                .count();
    }

    /** Gom stdout của javap vào chuỗi để đếm/lọc thay vì chỉ in ra màn hình. */
    private static String capture(ToolProvider tool, String... args) {
        var out = new java.io.StringWriter();
        try (var writer = new java.io.PrintWriter(out)) {
            tool.run(writer, writer, args);
        }
        return out.toString();
    }

    private static void deleteRecursively(Path root) throws IOException {
        if (!Files.exists(root)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(root)) {
            for (Path path : walk.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(path);
            }
        }
    }
}
