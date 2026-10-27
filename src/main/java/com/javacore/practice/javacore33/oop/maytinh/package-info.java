/**
 * Bài 13 - Exception: máy tính biết tự báo lỗi, và cái giá của việc im lặng.
 *
 * <p>Bài này nằm trong {@code oop} chứ không tách riêng, vì phần khó của nó không phải cú pháp
 * {@code try-catch} mà là <em>thiết kế một cây kế thừa</em>. Khác bài 9 ở chỗ: ở đó
 * {@code Hinh} là gốc do ta tự dựng; ở đây gốc là {@link java.lang.RuntimeException} của JDK,
 * ta chỉ mọc thêm nhánh. Phải tôn trọng những gì {@link java.lang.Throwable} quy định sẵn -
 * cặp constructor {@code (String)} và {@code (String, Throwable)},
 * {@link java.lang.Throwable#getMessage()}, {@link java.lang.Throwable#getCause()}.
 *
 * <p><b>Cái bẫy mà bài này tồn tại để dựng lên.</b> Với kiểu {@code double}, Java
 * <em>không</em> báo lỗi khi tính sai:
 * <pre>
 * 10.0 / 0       →  Infinity
 * 0.0 / 0        →  NaN
 * Math.sqrt(-1)  →  NaN
 * 10 / 0         →  ArithmeticException   (chỉ khi cả hai là số nguyên)
 * </pre>
 * Nghĩa là {@link com.javacore.practice.javacore33.oop.maytinh.DivisionByZeroException} ở đây
 * không phải để bắt lại lỗi của JVM rồi bọc lại - JVM không hề báo gì. Ta phải chủ động kiểm
 * tra rồi chủ động ném. Ai viết bài này mà thấy {@code catch (ArithmeticException e)} thì gần
 * như chắc chắn đang dùng {@code int} và sẽ vỡ ngay khi đổi sang {@code double}.
 *
 * <p><b>Cấu trúc.</b>
 * <pre>
 * RuntimeException
 *   └── CalculatorException          abstract, giữ biểu thức gốc
 *         ├── DivisionByZeroException    / % ^
 *         ├── NegativeSqrtException      sqrt số âm
 *         └── InvalidOperationException  cú pháp sai, toán tử lạ, toán hạng không phải số
 * </pre>
 * Có class cha chung thì người gọi mới viết được {@code catch (CalculatorException e)} để bắt
 * cả cụm, thay vì {@code catch (Exception e)} - thứ sẽ nuốt luôn
 * {@link java.lang.NullPointerException} và biến bug thành lỗi ngầm.
 *
 * <p><b>Ba quyết định thiết kế, mỗi cái đều có lựa chọn thay thế hợp lý:</b>
 * <ol>
 *   <li><em>Unchecked thay vì checked.</em> Lý do đầy đủ nằm trong Javadoc của
 *       {@link com.javacore.practice.javacore33.oop.maytinh.CalculatorException}. Hệ quả nhìn
 *       thấy được: chữ ký method trong
 *       {@link com.javacore.practice.javacore33.oop.maytinh.Calculator} không có mệnh đề
 *       {@code throws}, thông tin đó chuyển hết sang thẻ {@code @throws} của Javadoc.</li>
 *   <li><em>Exception tự dựng message.</em> {@code Calculator} chỉ chuyền dữ liệu thô
 *       ({@code a}, toán tử, {@code b}); message do chính exception {@code String.format} ra.
 *       Nhờ vậy cùng một loại lỗi thì message giống nhau ở mọi chỗ ném, và logic tính toán
 *       không lẫn chuỗi hiển thị.</li>
 *   <li><em>Static factory cho {@code InvalidOperationException}.</em> Class đó gom năm tình
 *       huống khác hẳn nhau nên một constructor không đủ để suy ra message. Hai class còn lại
 *       mỗi cái chỉ một tình huống nên dùng constructor thường.</li>
 * </ol>
 *
 * <p>{@link com.javacore.practice.javacore33.oop.maytinh.MayTinhDemo} có {@code main}, chạy 19
 * biểu thức gồm cả hợp lệ lẫn từng loại lỗi. Nó cũng là nơi <em>duy nhất</em> trong package có
 * {@code catch} - {@code Calculator} chỉ ném chứ không in gì ra màn hình, đúng như một class
 * ở tầng service sau này.
 */
package com.javacore.practice.javacore33.oop.maytinh;
