/**
 * Bài 10 - interface và đa năng lực: {@link com.javacore.practice.javacore33.oop.thietbi.Chargeable}
 * và {@link com.javacore.practice.javacore33.oop.thietbi.Connectable}, với
 * {@code sacTatCa(Chargeable[])} nhận vào một <em>năng lực</em> chứ không phải một họ thiết bị.
 *
 * <p>Tách thành package riêng cùng lý do với {@code oop.hinh} ở bài 9: đây là một cụm năm type
 * đi liền nhau, khác bài 7 và 8 - mỗi bài chỉ một class đứng độc lập trong {@code oop}.
 *
 * <p>Khác bài 9 ở chỗ nào: {@code abstract class Hinh} gom theo quan hệ <em>is-a</em> và mỗi
 * hình chỉ được làm con của đúng một họ. Interface gom theo <em>can-do</em>, nên
 * {@link com.javacore.practice.javacore33.oop.thietbi.DienThoai} mang được hai năng lực cùng
 * lúc - thứ {@code extends} không làm được.
 *
 * <p>Ba lớp cài đặt là ba trường hợp khác nhau, không phải ba biến thể cho đủ số lượng:
 * <ul>
 *   <li>{@code DienThoai} - cả hai năng lực</li>
 *   <li>{@code PinDuPhong} - chỉ sạc được, chứng minh {@code sacTatCa} không đòi hỏi
 *       {@code Connectable}</li>
 *   <li>{@code Router} - chỉ kết nối được, nhét vào {@code sacTatCa} là lỗi compile. File này
 *       tồn tại chỉ để chứng minh điều đó</li>
 * </ul>
 *
 * <p>Quy ước đặt tên trong package: type là <em>năng lực</em> thì dùng tiếng Anh hậu tố
 * {@code -able} theo idiom của chính JDK ({@code Comparable}, {@code Runnable},
 * {@code Closeable}); type là <em>sự vật</em> thì dùng tên tiếng Việt như phần còn lại của
 * {@code javacore33}, hoặc giữ nguyên nếu tiếng Việt không có từ tương ứng ({@code Router}).
 * Tên method thì tiếng Việt hết - đề bài gọi {@code chargeAll}, nhưng đề là mô tả bài toán,
 * không phải API contract.
 */
package com.javacore.practice.javacore33.oop.thietbi;
