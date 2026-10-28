package com.javacore.practice.javacore33.collections.queue;

import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Bài 20 - Hàng đợi tiếp nhận và phân loại bệnh nhân cấp cứu (Triage Queue).
 *
 * <p>Sử dụng {@link PriorityQueue} bên dưới cấu trúc Min-Heap để luôn phục vụ bệnh nhân
 * có mức độ khẩn cấp cao nhất trước.
 *
 * <p><b>Điểm cốt lõi:</b> {@link PriorityQueue} của Java vốn <em>không có tính ổn định (unstable)</em>
 * khi các phần tử cùng độ ưu tiên. Lớp này sử dụng một bộ đếm số thứ tự {@code sequenceGenerator}
 * để đảm bảo chặt chẽ nguyên tắc: bệnh nhân cùng mức ưu tiên thì ai đến trước được xử lý trước (FIFO).
 */
public class HospitalTriageQueue {

    private final PriorityQueue<Patient> queue;
    private final AtomicLong sequenceGenerator;

    /**
     * Khởi tạo hàng đợi tiếp nhận bệnh nhân.
     */
    public HospitalTriageQueue() {
        this.queue = new PriorityQueue<>(Patient.TRIAGE_ORDER);
        this.sequenceGenerator = new AtomicLong(0);
    }

    /**
     * Tiếp nhận một bệnh nhân mới vào viện.
     * Tự động cấp phát số thứ tự tiếp nhận tăng dần.
     *
     * @param name     tên bệnh nhân
     * @param priority mức độ khẩn cấp
     * @return đối tượng {@link Patient} vừa được tiếp nhận
     * @throws IllegalArgumentException nếu tên rỗng hoặc priority null
     */
    public Patient admit(String name, Priority priority) {
        long order = sequenceGenerator.incrementAndGet();
        Patient patient = new Patient(name, priority, order);
        queue.offer(patient);
        return patient;
    }

    /**
     * Lấy ra và xử lý bệnh nhân có độ ưu tiên cao nhất kế tiếp.
     *
     * @return {@link Optional} chứa bệnh nhân được gọi, hoặc {@link Optional#empty()} nếu hàng đợi rỗng
     */
    public Optional<Patient> nextPatient() {
        return Optional.ofNullable(queue.poll());
    }

    /**
     * Xem thông tin bệnh nhân có độ ưu tiên cao nhất kế tiếp mà KHÔNG xóa khỏi hàng đợi.
     *
     * @return {@link Optional} chứa bệnh nhân tiếp theo, hoặc rỗng nếu không có ai
     */
    public Optional<Patient> peekNext() {
        return Optional.ofNullable(queue.peek());
    }

    /**
     * Số lượng bệnh nhân hiện đang chờ khám.
     */
    public int size() {
        return queue.size();
    }

    /**
     * Kiểm tra hàng đợi có đang trống không.
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * Lấy toàn bộ danh sách bệnh nhân đang chờ theo đúng thứ tự khám.
     *
     * <p><b>Bẫy cần tránh:</b> Vòng lặp {@code for-each} hay {@code iterator()} trực tiếp trên
     * {@link PriorityQueue} KHÔNG đảm bảo thứ tự ưu tiên (nó duyệt theo mảng heap nội bộ).
     * Phương thức này sắp xếp lại qua Stream để trả về đúng thứ tự khám thực tế mà không phá hủy queue.
     *
     * @return danh sách bất biến các bệnh nhân đang chờ theo đúng thứ tự ưu tiên
     */
    public List<Patient> getWaitingPatients() {
        return queue.stream()
                .sorted(Patient.TRIAGE_ORDER)
                .toList();
    }

    /**
     * Xóa sạch hàng đợi (dùng trong bảo trì / ca trực mới).
     */
    public void clear() {
        queue.clear();
    }
}
