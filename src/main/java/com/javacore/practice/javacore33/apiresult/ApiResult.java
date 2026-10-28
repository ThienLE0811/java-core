package com.javacore.practice.javacore33.apiresult;

// Union đóng: compiler biết ApiResult<T> CHỈ có đúng 3 nhánh này, không hơn.
// Nhờ vậy switch trên nó exhaustive được mà không cần default.
// permits ghi TÊN TRẦN, không có <T>: nó nói về class nào, không nói về kiểu cụ thể nào.
public sealed interface ApiResult<T> permits Success, Failure, Loading {

    // Thân cố ý để trống.
    //
    // Cám dỗ là khai String render(); rồi cho mỗi record @Override. Đó là polymorphism cổ
    // điển, và nó đánh đổi ngược với thứ bài này đang học:
    //   - abstract method : thêm NHÁNH mới dễ, thêm THAO TÁC mới phải sửa cả 3 record
    //   - sealed + switch : thêm THAO TÁC mới dễ, thêm NHÁNH mới thì compiler chỉ thẳng
    //                       vào từng switch còn thiếu
    // Giữ interface trống là chọn vế thứ hai một cách có chủ ý.
}

// Không record nào được public: một file chỉ cho phép MỘT kiểu public, và cái đó là ApiResult.
// Hệ quả: ba record này package-private, chỉ dùng được trong package apiresult.

// T ở đây xuất hiện 3 lần với 3 vai khác nhau:
//   Success<T>   khai báo type parameter của riêng record này
//   (T data)     dùng nó làm kiểu component
//   ApiResult<T> truyền nó lên interface cha
record Success<T>(T data) implements ApiResult<T> {
}

// Lỗi không mang theo dữ liệu thành công, nên T không nằm trong component nào.
// Vẫn phải khai <T>: không có nó thì lấy gì truyền cho ApiResult<T>?
record Failure<T>(String error, int code) implements ApiResult<T> {
}

// Ngoặc rỗng là BẮT BUỘC - record luôn phải có header, kể cả khi không giữ gì.
// Hệ quả: mỗi new Loading<User>() là một object mới, dù chúng equals nhau.
record Loading<T>() implements ApiResult<T> {
}