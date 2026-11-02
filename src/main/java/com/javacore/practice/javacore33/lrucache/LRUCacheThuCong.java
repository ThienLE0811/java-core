package com.javacore.practice.javacore33.lrucache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Bài 32 (mở rộng) - LRU Cache tự cài bằng {@code HashMap} + danh sách liên kết kép.
 * Câu phỏng vấn kinh điển (LeetCode 146).
 *
 * <p>Mục tiêu: <b>mọi thao tác O(1)</b>. HashMap lo việc tìm nhanh, danh sách liên kết
 * lo việc giữ thứ tự tươi/cũ. Thiếu một trong hai là mất O(1):
 * <ul>
 *   <li>Chỉ có HashMap: muốn biết ai cũ nhất phải duyệt hết -&gt; O(n).</li>
 *   <li>Chỉ có danh sách: muốn tìm một key phải duyệt từ đầu -&gt; O(n).</li>
 * </ul>
 *
 * <p>Quy ước thứ tự: {@code head.next} là tươi nhất, {@code tail.prev} là ứng viên bị đuổi.
 *
 * <p>Giữ nguyên hợp đồng của {@link LRUCache}: không chấp nhận null ở key lẫn value,
 * {@code get} trả {@link Optional}, và <b>KHÔNG thread-safe</b>.
 */
public class LRUCacheThuCong<K, V> implements Cache<K, V> {

    /**
     * Một ô trong danh sách liên kết kép.
     *
     * <p>{@code static} vì node không cần biết gì về instance cache bao ngoài - nested class
     * không static sẽ âm thầm giữ tham chiếu tới outer object (nguồn rò rỉ bộ nhớ kinh điển).
     */
    private static class Node<K, V> {

        /**
         * Giữ cả key chứ không chỉ value, dù HashMap đã map key -&gt; node.
         *
         * <p>Lý do: lúc đuổi phần tử, ta tìm được node cuối danh sách rồi, nhưng còn phải xóa
         * nó khỏi HashMap nữa - mà {@code map.remove()} cần KEY. Không có trường này thì danh
         * sách vẫn đúng capacity còn map phình mãi: rò rỉ bộ nhớ mà không exception nào báo.
         */
        private final K key;
        private V value;
        private Node<K, V> prev;
        private Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<K, Node<K, V>> map;

    /**
     * Hai node GIẢ (sentinel), không chứa dữ liệu thật và không bao giờ bị đuổi.
     *
     * <p>Nhờ chúng, mọi node thật đều chắc chắn có {@code prev} và {@code next} khác null,
     * nên {@link #goNode} và {@link #dayLenDau} không cần một dòng {@code if} nào để xử lý
     * riêng các trường hợp "node là đầu danh sách" / "là cuối" / "danh sách chỉ có 1 phần tử".
     */
    private final Node<K, V> head = new Node<>(null, null);
    private final Node<K, V> tail = new Node<>(null, null);

    /**
     * @param capacity số phần tử tối đa, phải &gt; 0
     * @throws IllegalArgumentException nếu capacity &lt;= 0
     */
    public LRUCacheThuCong(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity phải lớn hơn 0, nhận được: " + capacity);
        }

        this.capacity = capacity;
        this.map = new HashMap<>((int) (capacity / 0.75f) + 1, 0.75f);

        // Nối hai sentinel lại thành danh sách rỗng. Quên bước này là NPE ngay lần put đầu.
        head.next = tail;
        tail.prev = head;
    }

    // ----- Hai helper. Toàn bộ get/put chỉ là ghép hai cái này lại. -----

    /**
     * Gỡ node ra khỏi vị trí hiện tại, khâu hai hàng xóm của nó lại với nhau.
     *
     * <p>Đây là chỗ bắt buộc phải dùng danh sách liên kết <b>kép</b>: muốn gỡ node X ra, ta
     * phải sửa node đứng TRƯỚC X. Singly linked list không có đường đi ngược nên phải duyệt
     * từ đầu để tìm nó - O(n), mất sạch lý do dùng cấu trúc này.
     */
    private void goNode(Node<K, V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    /**
     * Chèn node vào ngay sau {@code head}, tức đánh dấu nó là tươi nhất.
     */
    private void dayLenDau(Node<K, V> node) {
        // Nối node với hai hàng xóm tương lai TRƯỚC...
        node.prev = head;
        node.next = head.next;

        // ...rồi mới cắt liên kết cũ. Làm ngược thứ tự (gán head.next = node trước)
        // là mất tham chiếu tới node đang đứng sau head.
        head.next.prev = node;
        head.next = node;
    }

    /**
     * Gỡ ra rồi đẩy lên đầu - thao tác "làm mới độ tươi" dùng chung cho get và put.
     */
    private void lamMoi(Node<K, V> node) {
        goNode(node);
        dayLenDau(node);
    }

    /**
     * Đọc một giá trị. Lần đọc này <b>làm mới</b> độ tươi của key.
     *
     * @return giá trị nếu có, {@link Optional#empty()} nếu miss
     * @throws NullPointerException nếu key null
     */
    @Override
    public Optional<V> get(K key) {
        kiemTraKey(key);

        Node<K, V> node = map.get(key);
        if (node == null) {
            return Optional.empty();
        }

        // Chính lời gọi này biến cache từ FIFO thành LRU. Bỏ đi thì code vẫn chạy,
        // test "put 4 phần tử vào cache capacity 3" vẫn xanh - và ta có FIFO đội lốt LRU.
        lamMoi(node);

        return Optional.of(node.value);
    }

    /**
     * Ghi một giá trị. Nếu key đã tồn tại thì ghi đè <b>và</b> làm mới độ tươi.
     * Nếu vượt capacity, phần tử ít được dùng gần đây nhất bị đuổi.
     *
     * @throws NullPointerException nếu key hoặc value null
     */
    @Override
    public void put(K key, V value) {
        kiemTraKey(key);
        Objects.requireNonNull(value, "value không được null");

        Node<K, V> node = map.get(key);

        if (node != null) {
            // Cập nhật node cũ, KHÔNG tạo node mới: tạo mới thì map trỏ sang node mới
            // còn node cũ vẫn nằm trong danh sách -> danh sách dài hơn map -> sai capacity.
            node.value = value;
            lamMoi(node);
            return;
        }

        Node<K, V> nodeMoi = new Node<>(key, value);
        map.put(key, nodeMoi);
        dayLenDau(nodeMoi);

        if (map.size() > capacity) {
            duoiNodeCuoi();
        }
    }

    /**
     * Đuổi phần tử ít được dùng gần đây nhất: node nằm sát {@code tail}.
     */
    private void duoiNodeCuoi() {
        Node<K, V> cuNhat = tail.prev;

        goNode(cuNhat);
        map.remove(cuNhat.key);   // thiếu dòng này là rò rỉ bộ nhớ: map phình, danh sách thì không
    }

    @Override
    public int size() {
        return map.size();
    }

    /**
     * Kiểm tra key có trong cache không. Cố ý KHÔNG làm mới độ tươi, giống
     * {@link LRUCache#chua(Object)} - một phép "nhìn trộm" không tính là dùng.
     *
     * @throws NullPointerException nếu key null
     */
    @Override
    public boolean chua(K key) {
        kiemTraKey(key);
        return map.containsKey(key);
    }

    /**
     * Key theo thứ tự <b>ít tươi nhất trước</b> - cùng quy ước với
     * {@link LRUCache#cacKeyTheoThuTu()} để demo so sánh hai bản cài đặt bằng phép so list thẳng.
     *
     * <p>Duyệt ngược từ {@code tail} về {@code head} vì đầu danh sách là tươi NHẤT.
     * Không duyệt theo {@code map.keySet()} - HashMap không có thứ tự nào cả.
     */
    @Override
    public List<K> cacKeyTheoThuTu() {
        var ketQua = new ArrayList<K>(map.size());

        for (Node<K, V> node = tail.prev; node != head; node = node.prev) {
            ketQua.add(node.key);
        }

        return List.copyOf(ketQua);
    }

    private static void kiemTraKey(Object key) {
        Objects.requireNonNull(key, "key không được null");
    }
}
