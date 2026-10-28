package com.javacore.practice.javacore33.apiresult;

// Component nằm trong ngoặc của phần khai báo, KHÔNG phải trong thân { }.
// Record cấm instance field tự khai: state của record đúng bằng danh sách component này,
// vì equals/hashCode/toString đều sinh ra từ nó.
public record User(long id, String name, String email) {

    // Cố ý KHÔNG có compact constructor. User ở đây chỉ là payload để Success<User> bọc;
    // phần validate đã là bài 15 (xem Person). Để User dựng được cả với name/email null
    // còn cho thấy một điều ở bài 16: record pattern bind ra null chứ không lọc null.
}
