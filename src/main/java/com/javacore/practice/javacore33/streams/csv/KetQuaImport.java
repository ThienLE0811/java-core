package com.javacore.practice.javacore33.streams.csv;

import java.util.List;
import java.util.Objects;

/**
 * Bài 30 - Kết quả import cả file: phần nhập được và phần lỗi.
 *
 * <p>Đây là kiểu trả về của tầng service cho controller. Import thành công một PHẦN là
 * trạng thái hợp lệ và phổ biến nhất - file 1000 dòng sai 5 dòng thì nhập 995 dòng và
 * trả về 5 lỗi, chứ không bắt người dùng sửa rồi upload lại từ đầu.
 */
public record KetQuaImport(List<NhanVien> hopLe, List<LoiDong> cacLoi) {

    public KetQuaImport {
        Objects.requireNonNull(hopLe, "hopLe không được null");
        Objects.requireNonNull(cacLoi, "cacLoi không được null");
        hopLe = List.copyOf(hopLe);
        cacLoi = List.copyOf(cacLoi);
    }

    public int soDongHopLe() {
        return hopLe.size();
    }

    /**
     * Số LỖI, không phải số dòng lỗi - một dòng có thể sinh nhiều lỗi.
     */
    public int soLoi() {
        return cacLoi.size();
    }

    /**
     * Số DÒNG bị lỗi. Khác {@link #soLoi()}, dùng khi báo cáo cho người dùng.
     */
    public long soDongLoi() {
        return cacLoi.stream().map(LoiDong::soDong).distinct().count();
    }

    public boolean thanhCongHoanToan() {
        return cacLoi.isEmpty();
    }
}
