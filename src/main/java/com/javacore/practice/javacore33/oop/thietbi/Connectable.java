package com.javacore.practice.javacore33.oop.thietbi;

/**
 * Bài 10 - năng lực "kết nối mạng được" của một thiết bị.
 *
 * <p>Tách khỏi {@link Chargeable} vì hai năng lực này độc lập với nhau: {@link PinDuPhong}
 * sạc được mà không kết nối, {@link Router} kết nối được mà không sạc, {@link DienThoai} có
 * cả hai. Một class gộp cả bốn method sẽ ép hai trong ba thiết bị phải cài những thứ chúng
 * không làm được.
 *
 * <p>Interface này cố ý <em>không</em> có {@code static} method kiểu
 * {@link Chargeable#sacTatCa(Chargeable[])}. Sạc cả mảng là một việc có nghĩa - cắm sạc xong
 * rồi để đó; còn "kết nối cả mảng" thì không, mỗi thiết bị nối vào mạng nào là chuyện riêng
 * của nó. Đừng thêm method chỉ vì interface kia có.
 */
public interface Connectable {

    /** Bật kết nối. Đang kết nối rồi mà gọi tiếp thì không có gì thay đổi. */
    void ketNoi();

    /** Ngắt kết nối. Đang ngắt rồi mà gọi tiếp thì không có gì thay đổi. */
    void ngatKetNoi();

    /**
     * Thiết bị có đang kết nối hay không.
     *
     * <p>Trả {@code boolean} chứ không {@code Boolean}: trạng thái này luôn xác định, không
     * tồn tại khả năng "chưa biết". Dùng kiểu bọc là mở cửa cho {@code null} vô nghĩa, và mọi
     * chỗ gọi {@code if (thietBi.dangKetNoi())} sẽ phải phòng một NPE không bao giờ nên có.
     *
     * @return {@code true} nếu đang kết nối
     */
    boolean dangKetNoi();
}
