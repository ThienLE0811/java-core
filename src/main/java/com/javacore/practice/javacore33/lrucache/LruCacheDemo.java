package com.javacore.practice.javacore33.lrucache;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Bài 32 - Chỗ quan sát LRU Cache bằng mắt.
 *
 * <p>Mục đích không phải "chạy cho vui" mà là <b>chứng minh</b> bốn điều, mỗi điều một
 * kịch bản in ra console:
 * <ol>
 *   <li>Cache này là LRU chứ không phải FIFO.</li>
 *   <li>Bỏ {@code accessOrder = true} đi thì nó thành FIFO - nhìn thấy hai output cạnh nhau.</li>
 *   <li>{@link LRUCacheThuCong} cho kết quả khớp {@link LRUCache} ở mọi bước.</li>
 *   <li>{@link LRUCache} KHÔNG thread-safe (kể cả khi chỉ đọc), bọc {@link LRUCacheDongBo} thì an toàn.</li>
 * </ol>
 *
 * <p>Chạy bằng nút Run trong IDE (giống {@code BoDemDemo} của bài 34).
 */
public class LruCacheDemo {

    private static final int CAPACITY = 3;

    private static final int CAPACITY_SO_SANH = 8;
    private static final int SO_BUOC_SO_SANH = 200_000;

    private static final int SO_TASK = 8;
    private static final int SO_LAN_MOI_TASK = 20_000;
    private static final int CAPACITY_DA_LUONG = 50;
    private static final int SO_LUOT_THU = 10;

    void main() {
        lruKhacFifo();
        System.out.println();
        soSanhAccessOrder();
        System.out.println();
        soSanhHaiBanCaiDat();
        System.out.println();
        chungMinhKhongThreadSafe();
    }

    /**
     * Kịch bản phân biệt LRU với FIFO. Đây là kịch bản quan trọng nhất của cả bài.
     */
    private static void lruKhacFifo() {
        System.out.println("=== 1. LRU khác FIFO ===");

        var cache = new LRUCache<String, Integer>(CAPACITY);

        cache.put("A", 1);
        cache.put("B", 2);
        cache.put("C", 3);
        System.out.println("put A, B, C   -> " + cache.cacKeyTheoThuTu());

        // BƯỚC QUYẾT ĐỊNH. Bỏ dòng này đi thì cả kịch bản không phân biệt được
        // LRU với FIFO nữa - rất nhiều test LRU trên mạng thiếu đúng dòng này.
        cache.get("A");
        System.out.println("get A         -> " + cache.cacKeyTheoThuTu() + "   (A nhảy về cuối = tươi nhất)");

        cache.put("D", 4);
        System.out.println("put D         -> " + cache.cacKeyTheoThuTu());

        System.out.println();
        System.out.println("B bị đuổi, A sống sót vì vừa được đọc  -> đúng LRU.");
        System.out.println("Nếu là FIFO thì A phải bị đuổi (vào trước ra trước), kết quả sẽ là [B, C, D].");
        System.out.println("Kiểm chứng: chua(A) = " + cache.chua("A") + ", chua(B) = " + cache.chua("B"));
    }

    /**
     * Cho thấy đúng cái bẫy mà đề bài cảnh báo, bằng cách chạy cùng một kịch bản trên hai
     * {@link LinkedHashMap} chỉ khác nhau một tham số {@code accessOrder}.
     *
     * <p>Cố ý KHÔNG thêm constructor {@code LRUCache(int, boolean)} chỉ để phục vụ demo:
     * một cache cho phép bật/tắt tính LRU là một API vô nghĩa với người dùng thật. Demo thì
     * dựng thẳng {@link LinkedHashMap} ở đây, không phải sửa class production vì lý do học tập.
     */
    private static void soSanhAccessOrder() {
        System.out.println("=== 2. accessOrder quyết định LRU hay FIFO ===");

        System.out.println("accessOrder = true  -> " + chayKichBan(true) + "   (LRU, đuổi B)");
        System.out.println("accessOrder = false -> " + chayKichBan(false) + "   (FIFO, đuổi A)");
        System.out.println();
        System.out.println("Khác nhau đúng một tham số boolean. Đây là lý do quên nó thì cache");
        System.out.println("vẫn chạy, vẫn đúng capacity, chỉ đuổi sai người - và không ai biết.");
    }

    /**
     * Chạy kịch bản put A, B, C - get A - put D trên một LinkedHashMap trần.
     *
     * @return thứ tự key còn lại, ít tươi nhất đứng trước
     */
    private static List<String> chayKichBan(boolean accessOrder) {
        Map<String, Integer> map = new LinkedHashMap<>(5, 0.75f, accessOrder) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
                return size() > CAPACITY;
            }
        };

        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);
        map.get("A");
        map.put("D", 4);

        return List.copyOf(map.keySet());
    }

    /**
     * Hai bản cài đặt phải cho cùng một kết quả ở <b>mọi</b> bước.
     *
     * <p>Không chọn vài kịch bản bằng tay rồi kết luận - sinh ngẫu nhiên hàng trăm nghìn
     * thao tác và so sau từng bước. Đây là kiểu kiểm thử đối chiếu (differential testing):
     * lấy một bản đã tin được ({@link LRUCache} dựa trên thư viện chuẩn) làm thước đo cho
     * bản tự cài. Bug nối con trỏ sai thường chỉ lộ ra ở một tổ hợp hiếm, test tay khó chạm tới.
     */
    private static void soSanhHaiBanCaiDat() {
        System.out.println("=== 3. Bản tự cài phải khớp bản LinkedHashMap ===");

        var chuan = new LRUCache<Integer, Integer>(CAPACITY_SO_SANH);
        var thuCong = new LRUCacheThuCong<Integer, Integer>(CAPACITY_SO_SANH);

        // Seed cố định để lượt chạy nào cũng ra cùng một chuỗi thao tác: hỏng thì
        // lần chạy sau tái hiện được y hệt, không phải đoán.
        var random = new Random(42);

        for (int buoc = 1; buoc <= SO_BUOC_SO_SANH; buoc++) {
            int key = random.nextInt(CAPACITY_SO_SANH * 2);

            if (random.nextBoolean()) {
                chuan.put(key, buoc);
                thuCong.put(key, buoc);
            } else if (!chuan.get(key).equals(thuCong.get(key))) {
                System.out.printf("LỆCH ở bước %d: get(%d) trả về khác nhau%n", buoc, key);
                return;
            }

            if (!chuan.cacKeyTheoThuTu().equals(thuCong.cacKeyTheoThuTu())) {
                System.out.printf("LỆCH ở bước %d (key = %d)%n", buoc, key);
                System.out.println("  LinkedHashMap: " + chuan.cacKeyTheoThuTu());
                System.out.println("  tự cài       : " + thuCong.cacKeyTheoThuTu());
                return;
            }
        }

        System.out.printf("%,d thao tác ngẫu nhiên (put/get xen kẽ, capacity = %d): khớp hoàn toàn.%n",
                SO_BUOC_SO_SANH, CAPACITY_SO_SANH);
        System.out.println("Khớp cả thứ tự LRU lẫn giá trị trả về, không chỉ khớp size.");
    }

    /**
     * Cho thấy vì sao cache LRU không thread-safe, và một cách sửa.
     */
    private static void chungMinhKhongThreadSafe() {
        System.out.println("=== 4. Thread-safety: bọc LRUCacheDongBo hay không ===");
        System.out.printf("%d task x %d thao tác, capacity = %d, chạy %d lượt%n%n",
                SO_TASK, SO_LAN_MOI_TASK, CAPACITY_DA_LUONG, SO_LUOT_THU);

        demVoLuot("LRUCache        ", false);
        demVoLuot("LRUCacheDongBo  ", true);

        System.out.println();
        System.out.println("Vì sao hai thread chỉ cùng get() thôi mà vẫn hỏng?");
        System.out.println("Vì accessOrder = true khiến get() SỬA danh sách liên kết bên trong:");
        System.out.println("nó gỡ entry ra khỏi vị trí cũ rồi nối vào cuối. Đọc mà vẫn là ghi.");
        System.out.println("Với HashMap thường thì nhiều thread cùng get() hoàn toàn an toàn.");
    }

    private static void demVoLuot(String nhan, boolean coKhoa) {
        int soLuotVo = 0;
        int sizeLonNhat = 0;
        // LinkedHashSet để gom các loại triệu chứng khác nhau mà vẫn giữ thứ tự gặp
        // (bài 22: lọc trùng nhưng giữ thứ tự chèn).
        var cacTrieuChung = new LinkedHashSet<String>();

        for (int luot = 0; luot < SO_LUOT_THU; luot++) {
            KetQua ketQua = chayDaLuong(coKhoa);
            if (ketQua.trieuChung() != null) {
                soLuotVo++;
                cacTrieuChung.add(ketQua.trieuChung());
            }
            sizeLonNhat = Math.max(sizeLonNhat, ketQua.size());
        }

        System.out.printf("%s vỡ %2d/%d lượt   size lớn nhất thấy được: %3d   %s%n",
                nhan, soLuotVo, SO_LUOT_THU, sizeLonNhat,
                cacTrieuChung.isEmpty() ? "" : cacTrieuChung);
    }

    /**
     * @param trieuChung mô tả kiểu hỏng, {@code null} nếu lượt chạy bình thường
     * @param size số phần tử còn lại sau khi mọi task xong
     */
    private record KetQua(String trieuChung, int size) {
    }

    /**
     * Nhiều task cùng put/get vào một cache capacity nhỏ.
     *
     * <p>Hai lượt chạy khác nhau đúng một chỗ: có bọc {@link LRUCacheDongBo} hay không.
     * Phần còn lại của method không hề biết mình đang cầm bản nào - đó là ích lợi của
     * {@link Cache}: thêm một thuộc tính (thread-safe) mà không phải sửa code người dùng.
     *
     * @return triệu chứng hỏng (nếu có) kèm số phần tử còn lại
     */
    private static KetQua chayDaLuong(boolean coKhoa) {
        Cache<Integer, Integer> cache = coKhoa
                ? new LRUCacheDongBo<>(new LRUCache<>(CAPACITY_DA_LUONG))
                : new LRUCache<>(CAPACITY_DA_LUONG);

        var loi = new AtomicReference<String>();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < SO_TASK; i++) {
                executor.submit(() -> {
                    try {
                        for (int j = 0; j < SO_LAN_MOI_TASK; j++) {
                            int key = j % (CAPACITY_DA_LUONG * 2);
                            cache.put(key, j);
                            cache.get(key);
                        }
                    } catch (RuntimeException e) {
                        loi.compareAndSet(null, e.getClass().getSimpleName());
                    }
                });
            }
        }

        // Kiểm tra bất biến quan trọng nhất của cache: không bao giờ vượt capacity.
        int size = cache.size();

        if (loi.get() != null) {
            return new KetQua(loi.get(), size);
        }
        if (size > CAPACITY_DA_LUONG) {
            return new KetQua("size vượt capacity", size);
        }

        return new KetQua(null, size);
    }
}
