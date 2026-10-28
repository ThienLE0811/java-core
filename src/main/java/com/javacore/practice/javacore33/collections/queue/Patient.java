package com.javacore.practice.javacore33.collections.queue;

import java.util.Comparator;
import java.util.Objects;

/**
 * Bệnh nhân trong hàng đợi tiếp nhận.
 *
 * @param name         tên bệnh nhân (không được null hoặc rỗng)
 * @param priority     mức độ ưu tiên khám (không được null)
 * @param arrivalOrder số thứ tự tiếp nhận tại viện (tăng dần đơn điệu, dùng làm tie-breaker)
 */
public record Patient(String name, Priority priority, long arrivalOrder) implements Comparable<Patient> {

    /**
     * Quy tắc sắp xếp hàng đợi bệnh viện:
     * 1. Ưu tiên mức độ khẩn cấp (level nhỏ hơn được khám trước).
     * 2. Nếu cùng mức độ khẩn cấp: ai đến trước (arrivalOrder nhỏ hơn) được khám trước (FIFO).
     */
    public static final Comparator<Patient> TRIAGE_ORDER = Comparator
            .comparingInt((Patient p) -> p.priority().level())
            .thenComparingLong(Patient::arrivalOrder);

    public Patient {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Tên bệnh nhân không được để trống");
        }
        Objects.requireNonNull(priority, "Mức độ ưu tiên không được null");
        if (arrivalOrder < 0) {
            throw new IllegalArgumentException("Số thứ tự đến không được âm: " + arrivalOrder);
        }
    }

    @Override
    public int compareTo(Patient other) {
        return TRIAGE_ORDER.compare(this, other);
    }

    @Override
    public String toString() {
        return String.format("Patient[%s | %s | Đến thứ #%d]", name, priority.name(), arrivalOrder);
    }
}
