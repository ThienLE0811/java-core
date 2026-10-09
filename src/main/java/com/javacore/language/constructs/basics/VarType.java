package com.javacore.language.constructs.basics;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

/**
 * Using the Var Type Identifier
 * <p>
 * 1. The Var Keyword
 * - var (Java SE 10+) cho phép khai báo local variable không cần ghi rõ kiểu,
 * compiler tự suy luận kiểu dựa trên giá trị gán ban đầu.
 * - var KHÔNG phải là 1 kiểu dữ liệu, chỉ là type inference - kiểu thật được
 * chốt cứng lúc compile và không đổi được sau đó.
 * <p>
 * 2. Examples with Var - dùng được ở:
 * - local variable trong method/constructor/initializer block.
 * - for loop: for (var i = 0; i < 10; i++).
 * - try-with-resources: try (var in = new FileInputStream(...)).
 * <p>
 * 3. Restrictions on Using Var - KHÔNG dùng được ở:
 * - field (thuộc tính class).
 * - tham số method/constructor.
 * - return type của method.
 * - biến không có initializer (var x; lỗi compile vì null không mang kiểu).
 * <p>
 * Best practice: dùng var khi kiểu dữ liệu dài dòng giúp code dễ đọc hơn,
 * không lạm dụng tới mức người đọc không đoán được kiểu thực sự là gì.
 */
public class VarType {
    void main() throws IOException {
        var message = "Hello world!";
    }

}
