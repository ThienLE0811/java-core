package com.javacore.practice.javacore33.concurrency.demtu;

import com.javacore.practice.javacore33.collections.map.WordCounter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Bài 35 phần 3 - viết lại bài 18 theo kiểu đa luồng.
 *
 * <p>Ba cách đếm trên cùng một đầu vào:
 * <ul>
 *   <li>{@link #dem} - một {@link ConcurrentHashMap} chung, gộp bằng {@code merge}.</li>
 *   <li>{@link #demRoiGop} - mỗi phần một {@link HashMap} riêng, gộp ở cuối.</li>
 *   <li>{@link #demBangHashMapChung} - bản SAI cố ý, để chạy ra và thấy nó sai.</li>
 * </ul>
 *
 * <p>Cả ba phải cho ra kết quả giống hệt {@code WordCounter.countWithMerge} - trừ
 * cái thứ ba.
 */
public final class DemTuSongSong {

    private DemTuSongSong() {
    }

    /**
     * Cách đúng 1: {@code ConcurrentHashMap.merge} là thao tác nguyên tử trên
     * từng key, nhiều thread gọi cùng lúc trên cùng một từ vẫn cộng đủ.
     *
     * <p>Luật khi dùng merge: hàm gộp phải ngắn và thuần tính toán. Không khoá,
     * không gọi I/O, không đụng tới key khác của chính map đó - vi phạm là treo.
     */
    public static Map<String, Integer> dem(String vanBan, int soPhan) throws InterruptedException {
        Map<String, Integer> ketQua = new ConcurrentHashMap<>();
        demVaoMapChung(vanBan, soPhan, ketQua);
        return ketQua;
    }

    /**
     * Bản SAI cố ý - KHÔNG dùng ở đâu ngoài Demo.
     *
     * <p>{@link HashMap} không được thiết kế cho nhiều thread ghi cùng lúc. Hai
     * thread cùng {@code merge} một từ có thể cùng đọc giá trị cũ rồi cùng ghi
     * đè - mất lượt đếm. Nặng hơn: ghi đồng thời lúc map đang mở rộng bảng có
     * thể làm hỏng cấu trúc bên trong.
     */
    public static Map<String, Integer> demBangHashMapChung(String vanBan, int soPhan)
            throws InterruptedException {
        Map<String, Integer> ketQua = new HashMap<>();
        demVaoMapChung(vanBan, soPhan, ketQua);
        return ketQua;
    }

    /**
     * Cách đúng 2, và là cách thường dùng thật: không chia sẻ gì cả.
     *
     * <p>Mỗi thread đếm vào {@link HashMap} của riêng nó nên không có tranh chấp,
     * không cần cấu trúc đồng bộ. Chỉ bước gộp cuối cùng chạy tuần tự. Cách
     * nhanh nhất để hết race condition là đừng tạo ra state dùng chung.
     */
    public static Map<String, Integer> demRoiGop(String vanBan, int soPhan) throws InterruptedException {
        List<List<String>> cacPhan = chia(WordCounter.extractWords(vanBan), soPhan);

        List<Callable<Map<String, Integer>>> congViec = cacPhan.stream()
                .map(phan -> (Callable<Map<String, Integer>>) () -> demMot(phan, new HashMap<>()))
                .toList();

        Map<String, Integer> ketQua = new HashMap<>();
        for (Map<String, Integer> mapRieng : chaySongSong(congViec, soPhan)) {
            mapRieng.forEach((tu, soLan) -> ketQua.merge(tu, soLan, Integer::sum));
        }
        return ketQua;
    }

    /**
     * Chia danh sách từ thành {@code soPhan} phần gần bằng nhau.
     *
     * <p>Chia trên danh sách <em>từ</em> chứ không cắt chuỗi văn bản theo chỉ số
     * ký tự - cắt chuỗi sẽ xẻ đôi một từ thành hai từ sai ở ranh giới.
     *
     * <p>{@code subList} trả về khung nhìn (view) trên list gốc, không sao chép.
     * An toàn ở đây vì list gốc của {@code extractWords} là bất biến.
     */
    static List<List<String>> chia(List<String> tu, int soPhan) {
        if (soPhan <= 0) {
            throw new IllegalArgumentException("soPhan phải dương: " + soPhan);
        }
        if (tu == null || tu.isEmpty()) {
            return List.of();
        }

        int kichThuocCoBan = tu.size() / soPhan;
        int du = tu.size() % soPhan;

        List<List<String>> cacPhan = new ArrayList<>(soPhan);
        int batDau = 0;

        for (int i = 0; i < soPhan; i++) {
            // Rải phần dư vào các phần đầu, mỗi phần thêm đúng 1 từ.
            int kichThuoc = kichThuocCoBan + (i < du ? 1 : 0);

            // Gặp khi soPhan > số từ: các phần cuối rỗng, bỏ đi cho khỏi tạo
            // task chẳng làm gì.
            if (kichThuoc == 0) {
                continue;
            }

            cacPhan.add(tu.subList(batDau, batDau + kichThuoc));
            batDau += kichThuoc;
        }

        return Collections.unmodifiableList(cacPhan);
    }

    private static void demVaoMapChung(String vanBan, int soPhan, Map<String, Integer> ketQua)
            throws InterruptedException {
        List<List<String>> cacPhan = chia(WordCounter.extractWords(vanBan), soPhan);

        List<Callable<Void>> congViec = cacPhan.stream()
                .map(phan -> (Callable<Void>) () -> {
                    demMot(phan, ketQua);
                    return null;
                })
                .toList();

        chaySongSong(congViec, soPhan);
    }

    private static Map<String, Integer> demMot(List<String> phan, Map<String, Integer> dich) {
        for (String tu : phan) {
            dich.merge(tu, 1, Integer::sum);
        }
        return dich;
    }

    private static <T> List<T> chaySongSong(List<Callable<T>> congViec, int soThread)
            throws InterruptedException {
        if (congViec.isEmpty()) {
            return List.of();
        }

        // Đếm từ là việc ngốn CPU, không phải chờ I/O - nên dùng pool cố định
        // theo số nhân, không dùng virtual thread. Virtual thread không làm CPU
        // chạy nhanh hơn, chỉ thêm chi phí.
        //
        // try-with-resources: ExecutorService là AutoCloseable từ Java 19,
        // close() = shutdown() + awaitTermination() chờ không giới hạn. Gọn hơn
        // bản làm tay ở TrinhTaiFile, đổi lại không đặt được hạn chờ.
        try (ExecutorService pool = Executors.newFixedThreadPool(soThread)) {
            List<T> ketQua = new ArrayList<>(congViec.size());

            for (Future<T> future : pool.invokeAll(congViec)) {
                try {
                    ketQua.add(future.get());
                } catch (ExecutionException e) {
                    throw new IllegalStateException("Đếm từ thất bại: " + e.getCause(), e.getCause());
                }
            }

            return ketQua;
        }
    }
}
