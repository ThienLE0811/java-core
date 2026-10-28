package com.javacore.practice.javacore33.collections.queue;

/**
 * Mức độ ưu tiên khám chữa bệnh tại khoa cấp cứu (Triage Priority).
 *
 * <p>Mức số nhỏ hơn biểu thị độ ưu tiên cao hơn (1 là khẩn cấp nhất).
 * Việc gán level tường minh giúp tránh rủi ro nếu ai đó vô tình đổi thứ tự khai báo enum.
 */
public enum Priority {
    /** Nguy kịch, đe dọa tính mạng - xử lý ngay lập tức (Cao nhất). */
    EMERGENCY(1, "Nguy kịch"),

    /** Tình trạng nặng, cần can thiệp sớm. */
    HIGH(2, "Khẩn cấp"),

    /** Tình trạng ổn định, có thể chờ đợi mức độ trung bình. */
    MEDIUM(3, "Trung bình"),

    /** Tình trạng nhẹ, khám thông thường (Thấp nhất). */
    LOW(4, "Nhẹ");

    private final int level;
    private final String description;

    Priority(int level, String description) {
        this.level = level;
        this.description = description;
    }

    public int level() {
        return level;
    }

    public String description() {
        return description;
    }
}
