package com.javacore.practice.javacore33.lrucache;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Bài 32 (mở rộng) - Bản bọc thread-safe cho một {@link Cache} bất kỳ.
 *
 * <h2>Vì sao tách class riêng thay vì nhét {@code synchronized} vào {@link LRUCache}</h2>
 * <ol>
 *   <li><b>Không bắt người dùng đơn luồng trả giá.</b> Khóa không bao giờ miễn phí, kể cả
 *       khi không có tranh chấp. Phần lớn chỗ dùng cache là trong một luồng.</li>
 *   <li><b>Đúng tiền lệ của JDK.</b> {@code ArrayList} không khóa, muốn khóa thì
 *       {@code Collections.synchronizedList()}; {@code HashMap} và {@code ConcurrentHashMap}
 *       là hai class riêng. Java không trộn hai thuộc tính này vào một class.</li>
 *   <li><b>Tên class nói thẳng ra thuộc tính.</b> Cầm {@code LRUCacheDongBo} là biết ngay
 *       mình có gì, không phải mở code ra đọc.</li>
 *   <li><b>Bọc được cả hai bản cài đặt</b> nhờ {@link Cache}, không phải viết hai lần.</li>
 * </ol>
 *
 * <p><b>Điểm yếu, nói cho công bằng:</b> người dùng có thể quên bọc, và khi đó không có gì
 * cảnh báo - chương trình chạy vài tháng rồi mới lộ. Phương án nhét {@code synchronized} vào
 * trong thì không thể dùng sai, đổi lại mọi người đều trả giá. Đây là đánh đổi giữa
 * <em>an toàn mặc định</em> và <em>hiệu năng mặc định</em>, không có đáp án đúng tuyệt đối.
 *
 * <h2>Vì sao dùng {@code synchronized} chứ không phải {@code ReentrantReadWriteLock}</h2>
 * <p>Phản xạ "cache thì nhiều đọc ít ghi, dùng read-write lock" SAI ở đây: vì
 * {@code accessOrder = true}, bản thân {@code get()} cũng sửa cấu trúc danh sách liên kết
 * bên trong. Mọi thao tác đều là ghi, nên read lock - thứ cho phép nhiều thread vào cùng lúc -
 * sẽ phá hỏng cache. Không có thao tác đọc thuần thì read-write lock chỉ tốn thêm chi phí.
 *
 * <p>Cũng không thay được bằng {@code ConcurrentHashMap}: nó không có khái niệm thứ tự
 * truy cập nên không dựng được LRU.
 */
public class LRUCacheDongBo<K, V> implements Cache<K, V> {

    private final Cache<K, V> goc;

    public LRUCacheDongBo(Cache<K, V> goc) {
        this.goc = Objects.requireNonNull(goc, "cache gốc không được null");
    }

    // Mọi method đều khóa, kể cả size() và cacKeyTheoThuTu(). Khóa nửa vời thì vô dụng:
    // đọc không khóa vẫn có thể thấy trạng thái dở dang giữa chừng của một lần put.

    @Override
    public synchronized Optional<V> get(K key) {
        return goc.get(key);
    }

    @Override
    public synchronized void put(K key, V value) {
        goc.put(key, value);
    }

    @Override
    public synchronized int size() {
        return goc.size();
    }

    @Override
    public synchronized boolean chua(K key) {
        return goc.chua(key);
    }

    @Override
    public synchronized List<K> cacKeyTheoThuTu() {
        return goc.cacKeyTheoThuTu();
    }

    /**
     * Phép gộp "không có thì tính rồi ghi" nằm trọn trong MỘT lần khóa.
     *
     * <p>Đây là lý do method này phải tồn tại trên {@link Cache}: người dùng không thể tự
     * ghép {@code get} rồi {@code put} ở ngoài mà vẫn nguyên tử, dù từng lời gọi đều đã khóa.
     *
     * <p>Lưu ý đánh đổi: {@code hamTinh} chạy BÊN TRONG khóa. Nếu nó gọi database hay HTTP,
     * mọi thread khác sẽ phải chờ. Caffeine cũng chọn như vậy, và đó là chủ ý - nó chặn
     * luôn hiện tượng nhiều thread cùng tính một giá trị (cache stampede).
     */
    @Override
    public synchronized V layHoacTinh(K key, Function<K, V> hamTinh) {
        return Cache.super.layHoacTinh(key, hamTinh);
    }
}
