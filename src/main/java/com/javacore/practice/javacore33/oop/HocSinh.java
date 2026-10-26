package com.javacore.practice.javacore33.oop;

import java.util.Objects;

/**
 * Bài 7 - Class, Object và đóng gói.
 *
 * <p>Object chỉ tồn tại khi dữ liệu hợp lệ: constructor kiểm tra trước, sai thì ném
 * exception chứ không gán giá trị mặc định. Nhờ vậy mọi {@code HocSinh} cầm trên tay
 * đều chắc chắn có tên, tuổi và điểm nằm trong miền cho phép.
 *
 * <p>Thang điểm 0.0-10.0.
 */
public class HocSinh {

    private static final int TUOI_TOI_THIEU = 1;
    private static final int TUOI_TOI_DA = 150;
    private static final double DIEM_TOI_DA = 10.0;
    private static final double DIEM_TOI_THIEU = 0.0;

    /*
     * hoTen phải được gán đúng một lần trên mọi đường thoát bình thường của constructor,
     * và không ai gán lại được nữa.
     * Thêm setHoTen() sẽ không compile.
     * */
    private final String hoTen;
    private int tuoi;
    private double diem;

    /**
     * @param hoTen không null, không rỗng; khoảng trắng đầu/cuối bị cắt
     * @param tuoi  từ 1 đến 150
     * @param diem  từ 0.0 đến 10.0
     * @throws NullPointerException     nếu {@code hoTen} là null
     * @throws IllegalArgumentException nếu bất kỳ tham số nào ngoài miền cho phép
     */
    public HocSinh(String hoTen, int tuoi, double diem) {
        Objects.requireNonNull(hoTen, "hoTen không được null");
        if (hoTen.isBlank()) {
            throw new IllegalArgumentException("hoTen không được rỗng");
        }
        kiemTraTuoi(tuoi);
        kiemTraDiem(diem);

        this.hoTen = hoTen.strip();
        this.tuoi = tuoi;
        this.diem = diem;
    }

    private static void kiemTraTuoi(int tuoi) {
        if (tuoi < TUOI_TOI_THIEU || tuoi > TUOI_TOI_DA) {
            throw new IllegalArgumentException(String.format(
                    "Tuổi phải từ %d đến %d, nhận được: %d", TUOI_TOI_THIEU, TUOI_TOI_DA, tuoi));
        }
    }

    private static void kiemTraDiem(double diem) {
        if (Double.isNaN(diem) || diem < DIEM_TOI_THIEU || diem > DIEM_TOI_DA) {
            throw new IllegalArgumentException(String.format(
                    "Điểm phải từ %s đến %s, nhận được: %s", DIEM_TOI_THIEU, DIEM_TOI_DA, diem));
        }
    }


    public String getHoTen() {
        return hoTen;
    }

    public int getTuoi() {
        return tuoi;
    }

    public double getDiem() {
        return diem;
    }

    public void setTuoi(int tuoi) {
        kiemTraTuoi(tuoi);
        this.tuoi = tuoi;
    }

    public void setDiem(double diem) {
        kiemTraDiem(diem);
        this.diem = diem;
    }

    public String xepLoai() {
        if (this.diem >= 8.5) {
            return "Xuất sắc";
        } else if (this.diem >= 8.0) {
            return "Giỏi";
        } else if (this.diem >= 6.5) {
            return "Khá";
        } else if (this.diem >= 5.0) {
            return "Trung bình";
        } else if (this.diem >= 4.0) {
            return "Yếu";
        } else {
            return "Kém";
        }

    }

    @Override
    public String toString() {
        return String.format("HocSinh{hoTen=%s, diem=%s, tuoi=%s}", getHoTen(), getDiem(), getTuoi());
    }


    /**
     * Hai {@code HocSinh} bằng nhau khi trùng cả ba field.
     *
     * <p>Dùng {@link Double#compare(double, double)} thay cho {@code ==} để nhất quán với
     * {@link #hashCode()}: {@code -0.0 == 0.0} là {@code true} nhưng hai giá trị đó có
     * hashCode khác nhau, nên so sánh bằng {@code ==} sẽ phá vỡ hợp đồng equals/hashCode.
     *
     * <p>{@code hoTen} không cần kiểm tra null vì constructor đã bảo đảm điều đó.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HocSinh khac)) {
            return false;
        }
        return tuoi == khac.tuoi
                && Double.compare(diem, khac.diem) == 0
                && hoTen.equals(khac.hoTen);
    }

    /**
     * Phải dùng đúng bộ field mà {@link #equals(Object)} dùng, nếu không hai object bằng
     * nhau sẽ rơi vào hai bucket khác nhau và {@code HashSet}/{@code HashMap} sẽ coi chúng
     * là hai phần tử riêng biệt.
     */
    @Override
    public int hashCode() {
        return Objects.hash(hoTen, tuoi, diem);
    }

}
