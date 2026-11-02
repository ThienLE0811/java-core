package com.javacore.practice.javacore33.lrucache;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Bài 32 - Hợp đồng chung của mọi bản cache trong package này.
 *
 * <p>Có interface thì ba bản cài đặt ({@link LRUCache}, {@link LRUCacheThuCong},
 * {@link LRUCacheDongBo}) hoán đổi được cho nhau: demo và test viết một lần, chạy được
 * với cả ba. Đây cũng chính là hình dáng của {@code interface Repository} trong Spring -
 * tầng Service phụ thuộc vào interface, không phụ thuộc vào bản cài đặt cụ thể.
 *
 * <p><b>Không chấp nhận null</b> ở cả key lẫn value. Key null gần như luôn là bug phía gọi;
 * value null thì tệ hơn, vì {@code get()} sẽ trả {@link Optional#empty()} y như lúc cache
 * miss, khiến code gọi cứ tưởng miss rồi đi query lại database mãi mà không có exception nào.
 *
 * <p>Các bản cài đặt <b>không</b> mặc nhiên thread-safe. Xem Javadoc của từng class.
 */
public interface Cache<K, V> {

    /**
     * Đọc một giá trị. Lần đọc này <b>làm mới</b> độ tươi của key.
     *
     * @return giá trị nếu có, {@link Optional#empty()} nếu miss
     * @throws NullPointerException nếu key null
     */
    Optional<V> get(K key);

    /**
     * Ghi một giá trị. Nếu key đã tồn tại thì ghi đè và làm mới độ tươi.
     * Nếu vượt capacity, phần tử ít được dùng gần đây nhất bị đuổi.
     *
     * @throws NullPointerException nếu key hoặc value null
     */
    void put(K key, V value);

    int size();

    /**
     * Kiểm tra key có trong cache không. Cố ý KHÔNG làm mới độ tươi - một phép
     * "nhìn trộm" không tính là dùng.
     *
     * @throws NullPointerException nếu key null
     */
    boolean chua(K key);

    /**
     * Key theo thứ tự <b>ít tươi nhất trước</b> (phần tử đầu là ứng viên bị đuổi tiếp theo).
     * Chỉ phục vụ demo và test - production không nên lộ thứ tự nội bộ ra ngoài.
     */
    List<K> cacKeyTheoThuTu();

    /**
     * Lấy từ cache, không có thì tính rồi ghi vào cache - thao tác <b>gộp</b>.
     *
     * <p>Vì sao cần method này thay vì để người dùng tự viết:
     * <pre>{@code
     * if (!cache.chua(key)) {      // hai lời gọi riêng biệt
     *     cache.put(key, tinh());  // thread khác có thể chen vào giữa
     * }
     * }</pre>
     * Khóa từng method chỉ đảm bảo <em>mỗi</em> lời gọi là nguyên tử, không đảm bảo
     * <em>cặp</em> lời gọi là nguyên tử. Muốn an toàn thì phép gộp phải nằm trọn bên trong
     * một lần khóa - tức là phải do cache cung cấp, người dùng không tự ghép được.
     *
     * <p>Bản mặc định ở đây KHÔNG nguyên tử (đủ dùng cho cache đơn luồng).
     * {@link LRUCacheDongBo} override lại cho an toàn.
     *
     * @param hamTinh hàm tính giá trị khi cache miss, không được trả null
     */
    default V layHoacTinh(K key, Function<K, V> hamTinh) {
        return get(key).orElseGet(() -> {
            V giaTri = hamTinh.apply(key);
            put(key, giaTri);
            return giaTri;
        });
    }
}
