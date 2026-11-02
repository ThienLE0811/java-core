package com.javacore.practice.javacore33.lrucache;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Bài 32 - LRU Cache dựng trên {@link java.util.LinkedHashMap}.
 *
 * <p>LRU = Least Recently Used: khi đầy, phần tử bị <em>đụng tới lâu nhất</em> bị đuổi.
 * Khác FIFO ở chỗ FIFO chỉ nhìn lúc vào, LRU nhìn cả lúc đọc.
 *
 * <p><b>KHÔNG thread-safe.</b> Lưu ý quan trọng hơn người ta tưởng: vì {@code accessOrder = true},
 * ngay cả {@link #get(Object)} cũng SỬA cấu trúc danh sách liên kết bên trong. Nghĩa là hai
 * thread cùng chỉ đọc vẫn đủ làm hỏng cache - khác hẳn {@link java.util.HashMap} thường.
 *
 * <p><b>Không chấp nhận null</b> ở cả key lẫn value. Key null gần như luôn là bug phía gọi.
 * Value null còn tệ hơn: {@code get()} sẽ trả {@link Optional#empty()} y như lúc cache miss,
 * nên code gọi cứ tưởng miss rồi đi query lại database mãi mà không hề có exception nào.
 * Caffeine và {@link java.util.concurrent.ConcurrentHashMap} cũng chọn hướng cấm vì lý do này.
 */
public class LRUCache<K, V> implements Cache<K, V> {

    private final int capacity;
    private final Map<K, V> cache;

    /**
     * @param capacity số phần tử tối đa, phải &gt; 0
     * @throws IllegalArgumentException nếu capacity &lt;= 0
     */
    public LRUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity phải lớn hơn 0, nhận được: " + capacity);
        }

        this.capacity = capacity;

        // LinkedHashMap.put() CHÈN TRƯỚC, ĐUỔI SAU (HashMap.java dòng 669-671: resize()
        // chạy trước afterNodeInsertion()), nên cache có khoảnh khắc chứa capacity + 1
        // phần tử. Bảng băm phải đủ chỗ cho con số đó, nếu không sẽ rehash ở MỌI lần
        // cache chạm trần. Đó là lý do có "+ 1" - không phải truyền thẳng capacity.
        int initialCapacity = (int) (capacity / 0.75f) + 1;

        this.cache = new LinkedHashMap<K, V>(initialCapacity, 0.75f, true) {

            // accessOrder = true ở trên là thứ biến map này từ FIFO thành LRU:
            // get() cũng đẩy entry về cuối, không chỉ put().

            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                // LinkedHashMap tự gọi method này sau mỗi lần chèn. Trả true = đuổi
                // entry cũ nhất. Dùng LRUCache.this.capacity cho rõ là field của cache,
                // không phải một biến nào của map ẩn danh.
                return size() > LRUCache.this.capacity;
            }
        };
    }

    /**
     * Đọc một giá trị. Lần đọc này <b>làm mới</b> độ "tươi" của key (đẩy nó về cuối
     * hàng chờ bị đuổi) - đó chính là điểm khác biệt giữa LRU và FIFO.
     *
     * @return giá trị nếu có, {@link Optional#empty()} nếu miss
     * @throws NullPointerException nếu key null
     */
    @Override
    public Optional<V> get(K key) {
        kiemTraKey(key);
        return Optional.ofNullable(cache.get(key));
    }

    /**
     * Ghi một giá trị. Nếu key đã tồn tại thì ghi đè <b>và</b> làm mới độ tươi.
     * Nếu vượt capacity, phần tử ít được dùng gần đây nhất bị đuổi tự động.
     *
     * @throws NullPointerException nếu key hoặc value null
     */
    @Override
    public void put(K key, V value) {
        kiemTraKey(key);
        Objects.requireNonNull(value, "value không được null");
        cache.put(key, value);
    }

    @Override
    public int size() {
        return cache.size();
    }

    /**
     * Kiểm tra key có trong cache không.
     *
     * <p><b>Cố ý:</b> phép kiểm tra này KHÔNG làm mới độ tươi của key - đã chạy thử
     * trên JDK 25: sau {@code containsKey(A)} thứ tự vẫn là {@code [A, B, C]}, trong khi
     * sau {@code get(A)} thành {@code [B, C, A]}. Một phép "nhìn trộm" không tính là dùng.
     *
     * @throws NullPointerException nếu key null
     */
    @Override
    public boolean chua(K key) {
        kiemTraKey(key);
        return cache.containsKey(key);
    }

    /**
     * Danh sách key theo thứ tự <b>ít tươi nhất trước</b> (phần tử đầu là ứng viên bị đuổi
     * tiếp theo). Chỉ phục vụ demo và test - production không nên lộ thứ tự nội bộ ra ngoài.
     */
    @Override
    public List<K> cacKeyTheoThuTu() {
        return List.copyOf(cache.keySet());
    }

    /**
     * Chặn key null ở MỌI cửa vào, không chỉ {@code put}. Chặn nửa vời thì bug truyền null
     * vào {@code get} sẽ hiện ra dưới dạng cache miss im lặng, rồi mới nổ ở một chỗ khác xa đó.
     */
    private static void kiemTraKey(Object key) {
        Objects.requireNonNull(key, "key không được null");
    }
}
