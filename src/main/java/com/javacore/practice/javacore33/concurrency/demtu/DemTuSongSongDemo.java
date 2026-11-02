package com.javacore.practice.javacore33.concurrency.demtu;

import com.javacore.practice.javacore33.collections.map.WordCounter;

import java.util.List;
import java.util.Map;

/**
 * Bài 35 phần 3 - chạy bằng nút Run trong IDE.
 *
 * <p>Hai khối:
 * <ol>
 *   <li>Tính đúng: HashMap dùng chung sai bao nhiêu lần trong 100 lượt chạy.</li>
 *   <li>Tốc độ: tuần tự và hai bản song song, ai nhanh hơn ai.</li>
 * </ol>
 */
public class DemTuSongSongDemo {

    private static final String DOAN_GOC = """
            Java là ngôn ngữ lập trình hướng đối tượng, chạy trên máy ảo Java.
            Lập trình đa luồng trong Java cần hiểu rõ về bộ nhớ và đồng bộ hóa.
            Một chương trình đa luồng sai thì chạy sai một cách ngẫu nhiên.
            """;

    private static final int SO_LAN_LAP = 2_000;
    private static final int SO_LUOT_KIEM_TRA = 100;
    private static final int SO_LUOT_DO = 5;

    private static final int SO_PHAN = Runtime.getRuntime().availableProcessors();

    void main() throws InterruptedException {
        String vanBan = DOAN_GOC.repeat(SO_LAN_LAP);

        Map<String, Integer> chuan = WordCounter.countWithMerge(vanBan);
        long tongTuChuan = tongSoLan(chuan);

        System.out.printf("Văn bản: %,d từ, %,d từ khác nhau. Chia %d phần (máy có %d nhân).%n%n",
                tongTuChuan, chuan.size(), SO_PHAN, SO_PHAN);

        inKiemTraTinhDung(vanBan, chuan, tongTuChuan);
        inSoSanhTocDo(vanBan);
    }

    /**
     * Chạy bản sai nhiều lượt. Điểm đáng sợ của race condition không phải là
     * "luôn sai", mà là "lúc đúng lúc sai" - nên test một lần rồi thấy xanh
     * không chứng minh được gì.
     */
    private static void inKiemTraTinhDung(String vanBan, Map<String, Integer> chuan, long tongTuChuan)
            throws InterruptedException {
        System.out.printf("--- Khối 1: %d lượt chạy, so với kết quả tuần tự ---%n", SO_LUOT_KIEM_TRA);

        int soLuotLechSo = 0;
        int soLuotNemLoi = 0;
        long tongTuHutToiDa = 0;
        String loiGapPhai = "";

        for (int luot = 0; luot < SO_LUOT_KIEM_TRA; luot++) {
            try {
                Map<String, Integer> ketQua = DemTuSongSong.demBangHashMapChung(vanBan, SO_PHAN);

                if (!ketQua.equals(chuan)) {
                    soLuotLechSo++;
                    tongTuHutToiDa = Math.max(tongTuHutToiDa, tongTuChuan - tongSoLan(ketQua));
                }
            } catch (IllegalStateException e) {
                // HashMap.merge có kiểm tra modCount: phát hiện map bị thread
                // khác sửa giữa chừng thì ném ConcurrentModificationException.
                // Đây là kiểu hỏng "may mắn" - nó la lên. Kiểu hỏng nguy hiểm
                // hơn là lặng lẽ đếm thiếu ở nhánh trên.
                soLuotNemLoi++;
                loiGapPhai = e.getCause().getClass().getSimpleName();
            }
        }

        int soLuotDung = SO_LUOT_KIEM_TRA - soLuotLechSo - soLuotNemLoi;

        System.out.printf("HashMap dùng chung   : đúng %d, đếm thiếu %d, ném %s %d  (trên %d lượt)%n",
                soLuotDung, soLuotLechSo,
                loiGapPhai.isEmpty() ? "exception" : loiGapPhai, soLuotNemLoi,
                SO_LUOT_KIEM_TRA);
        System.out.printf("                       lượt đếm thiếu tệ nhất hụt %,d từ.%n", tongTuHutToiDa);

        // Hai bản đúng chỉ cần kiểm một lượt là đủ - chúng đúng vì cơ chế,
        // không phải vì may.
        boolean demDung = DemTuSongSong.dem(vanBan, SO_PHAN).equals(chuan);
        boolean gopDung = DemTuSongSong.demRoiGop(vanBan, SO_PHAN).equals(chuan);

        System.out.printf("ConcurrentHashMap    : %s%n", demDung ? "khớp tuyệt đối" : "SAI");
        System.out.printf("Map riêng rồi gộp    : %s%n%n", gopDung ? "khớp tuyệt đối" : "SAI");
    }

    private static void inSoSanhTocDo(String vanBan) throws InterruptedException {
        System.out.printf("--- Khối 2: thời gian (bỏ lượt đầu, trung bình %d lượt sau) ---%n",
                SO_LUOT_DO - 1);

        inMotCach("Tuần tự (bài 18)", () -> WordCounter.countWithMerge(vanBan));
        inMotCach("ConcurrentHashMap", () -> DemTuSongSong.dem(vanBan, SO_PHAN));
        inMotCach("Map riêng rồi gộp", () -> DemTuSongSong.demRoiGop(vanBan, SO_PHAN));
    }

    private interface CachDem {
        Map<String, Integer> dem() throws InterruptedException;
    }

    /**
     * Bỏ lượt đầu vì JIT chưa kịp biên dịch sang mã máy nên lượt đó luôn chậm
     * bất thường. Đây không phải benchmark nghiêm túc (muốn nghiêm túc thì dùng
     * JMH) - chỉ đọc được thứ tự nhanh chậm, đừng đọc tỉ lệ.
     */
    private static void inMotCach(String ten, CachDem cach) throws InterruptedException {
        long tongNano = 0;

        for (int luot = 0; luot < SO_LUOT_DO; luot++) {
            long batDau = System.nanoTime();
            cach.dem();
            long nano = System.nanoTime() - batDau;

            if (luot > 0) {
                tongNano += nano;
            }
        }

        System.out.printf("%-20s %8.1f ms%n", ten, tongNano / (SO_LUOT_DO - 1) / 1_000_000.0);
    }

    private static long tongSoLan(Map<String, Integer> tanSuat) {
        return tanSuat.values().stream().mapToLong(Integer::longValue).sum();
    }
}
