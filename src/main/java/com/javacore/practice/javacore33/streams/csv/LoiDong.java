package com.javacore.practice.javacore33.streams.csv;

import java.util.Objects;

/**
 * Bài 30 - Một lỗi dữ liệu tại một dòng cụ thể.
 *
 * <p>Bốn thông tin này là tối thiểu để người dùng tự sửa được file: sai ở DÒNG nào,
 * CỘT nào, họ đã gõ GÌ, và SAI vì lý do gì. Thiếu một trong bốn là họ phải đoán.
 *
 * @param soDong   số dòng trong file gốc, đếm từ 1 (dòng 1 là header)
 * @param cot      tên cột theo header
 * @param giaTriGoc giá trị người dùng gõ, giữ nguyên không chuẩn hoá
 * @param lyDo     mô tả ngắn, viết cho người dùng cuối đọc chứ không phải cho lập trình viên
 */
public record LoiDong(long soDong, String cot, String giaTriGoc, String lyDo) {

    public LoiDong {
        Objects.requireNonNull(cot, "cot không được null");
        Objects.requireNonNull(lyDo, "lyDo không được null");
        // Thiếu hẳn cột thì giá trị gốc là null - quy về chuỗi rỗng để chỗ in không phải
        // kiểm tra null, và để người đọc thấy rõ "ở đây trống".
        giaTriGoc = giaTriGoc == null ? "" : giaTriGoc;
    }

    @Override
    public String toString() {
        return "dòng %-3d | %-12s | %-20s | %s".formatted(soDong, cot, giaTriGoc, lyDo);
    }
}
