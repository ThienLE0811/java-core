package com.javacore.practice.javacore33.collections.queue;

import java.util.Comparator;
import java.util.Optional;
import java.util.PriorityQueue;

/**
 * Demo bài 20 - {@link PriorityQueue}: Hàng đợi bệnh viện và các bẫy thực tế.
 */
public class QueueDemo {

    public static void main(String[] args) {
        demoHangDoiBenhVien();
        bayDuyetIteratorTrenPriorityQueue();
        chungMinhTaiSaoCanTieBreaker();
    }

    private static void demoHangDoiBenhVien() {
        System.out.println("=== 1. Demo quy trình tiếp nhận & gọi khám cấp cứu ===");

        HospitalTriageQueue triage = new HospitalTriageQueue();

        // Giả lập tiếp nhận theo thời gian đến viện:
        System.out.println("1. Tiếp nhận bệnh nhân theo trình tự thời gian:");
        System.out.println("  -> " + triage.admit("Nguyễn Văn A", Priority.MEDIUM));    // #1 (Trung bình)
        System.out.println("  -> " + triage.admit("Trần Thị B", Priority.HIGH));       // #2 (Khẩn cấp)
        System.out.println("  -> " + triage.admit("Lê Văn C", Priority.LOW));          // #3 (Nhẹ)
        System.out.println("  -> " + triage.admit("Phạm Thị D", Priority.HIGH));       // #4 (Khẩn cấp - đến sau B)
        System.out.println("  -> " + triage.admit("Hoàng Văn E", Priority.EMERGENCY));  // #5 (Nguy kịch - đến muộn nhất)
        System.out.println("  -> " + triage.admit("Vũ Thị F", Priority.MEDIUM));       // #6 (Trung bình - đến sau A)

        System.out.println("\nTổng số bệnh nhân đang chờ: " + triage.size());

        // Bác sĩ gọi khám lần lượt:
        System.out.println("\n2. Bác sĩ gọi khám lần lượt (thứ tự thực tế):");
        int stt = 1;
        while (!triage.isEmpty()) {
            Optional<Patient> p = triage.nextPatient();
            if (p.isPresent()) {
                System.out.printf("  Lượt %d: %s%n", stt++, p.get());
            }
        }

        System.out.println("\n-> Nhận xét:");
        System.out.println("  - Hoàng Văn E (EMERGENCY) đến thứ #5 nhưng được khám ĐẦU TIÊN.");
        System.out.println("  - Trần Thị B (#2) và Phạm Thị D (#4) cùng mức HIGH, B đến trước nên được khám TRƯỚC D.");
        System.out.println("  - Nguyễn Văn A (#1) và Vũ Thị F (#6) cùng mức MEDIUM, A đến trước nên khám trước F.");
    }

    private static void bayDuyetIteratorTrenPriorityQueue() {
        System.out.println();
        System.out.println("=== 2. Cạm bẫy: Duyệt for-each / iterator() trên PriorityQueue ===");

        PriorityQueue<Integer> pq = new PriorityQueue<>();
        // Thêm các số lộn xộn
        pq.offer(50);
        pq.offer(10);
        pq.offer(30);
        pq.offer(5);
        pq.offer(20);

        // Duyệt bằng for-each (dùng iterator của Heap)
        System.out.println("Duyệt for-each (trông lộn xộn, KHÔNG theo thứ tự ưu tiên):");
        System.out.print("  ");
        for (int x : pq) {
            System.out.print(x + " ");
        }
        System.out.println(" <- BẪY: Iterator duyệt theo mảng heap nội bộ!");

        // Lấy bằng poll() (thực sự rút từng phần tử ưu tiên cao nhất ra)
        System.out.println("Lấy bằng while(!pq.isEmpty()) pq.poll():");
        System.out.print("  ");
        while (!pq.isEmpty()) {
            System.out.print(pq.poll() + " ");
        }
        System.out.println(" <- Thứ tự đúng chuẩn Min-Heap!");

        System.out.println("-> BÀI HỌC: Không dùng for-each để duyệt PriorityQueue nếu muốn xem thứ tự ưu tiên.");
    }

    private static void chungMinhTaiSaoCanTieBreaker() {
        System.out.println();
        System.out.println("=== 3. Cạm bẫy: PriorityQueue KHÔNG ổn định nếu thiếu Tie-Breaker ===");

        // Bản ghi bệnh nhân KHÔNG có số thứ tự đến, chỉ so sánh Priority
        record BenhNhanKhongSoThuTu(String ten, Priority mucDo) {}

        // Comparator CHỈ so theo Priority::level
        PriorityQueue<BenhNhanKhongSoThuTu> pqLoi = new PriorityQueue<>(
                Comparator.comparingInt(b -> b.mucDo().level())
        );

        // Thêm liên tiếp nhiều người CÙNG mức HIGH
        pqLoi.offer(new BenhNhanKhongSoThuTu("Người 1", Priority.HIGH));
        pqLoi.offer(new BenhNhanKhongSoThuTu("Người 2", Priority.HIGH));
        pqLoi.offer(new BenhNhanKhongSoThuTu("Người 3", Priority.HIGH));
        pqLoi.offer(new BenhNhanKhongSoThuTu("Người 4", Priority.HIGH));
        pqLoi.offer(new BenhNhanKhongSoThuTu("Người 5", Priority.HIGH));

        System.out.println("Thêm lần lượt: Người 1 -> Người 2 -> Người 3 -> Người 4 -> Người 5 (cùng mức HIGH)");
        System.out.println("Lấy ra bằng poll():");
        while (!pqLoi.isEmpty()) {
            BenhNhanKhongSoThuTu bn = pqLoi.poll();
            System.out.println("  - " + bn.ten());
        }
        System.out.println("-> Cây nhị phân của Heap không đảm bảo FIFO giữa các phần tử bằng nhau.");
        System.out.println("   Đó là lý do bắt buộc phải có trường 'arrivalOrder'!");
    }
}
