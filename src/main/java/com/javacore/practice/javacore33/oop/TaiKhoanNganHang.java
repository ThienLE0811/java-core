package com.javacore.practice.javacore33.oop;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TaiKhoanNganHang {
    private static final int SO_CHU_SO_THAP_PHAN = 2;

    /**
     * Dấu của giao dịch. Dùng enum thay String để gõ sai là lỗi compile, không phải lỗi runtime.
     */
    public enum LoaiGiaoDich {
        NAP, RUT
    }

    /**
     * Một sự kiện đã xảy ra trên tài khoản. Bất biến: giao dịch đã ghi thì không sửa được.
     *
     * <p>{@code soTien} luôn dương - dấu nằm ở {@link LoaiGiaoDich}, không nằm ở con số.
     * Nhờ vậy không có cách nào biểu diễn "nạp -100".
     *
     * <p>{@code soDuSau} là số dư ngay sau khi giao dịch này hoàn tất, giống cột số dư trên
     * sổ phụ ngân hàng: đọc một dòng là biết trạng thái tài khoản tại thời điểm đó, không
     * cần cộng dồn lại từ đầu.
     */
    public record GiaoDich(LoaiGiaoDich loai, BigDecimal soTien, BigDecimal soDuSau, Instant thoiDiem) {

        /**
         * Compact constructor: validate trước, gán field tự động sau.
         *
         * @throws NullPointerException     nếu bất kỳ component nào null
         * @throws IllegalArgumentException nếu {@code soTien} không dương hoặc {@code soDuSau} âm
         */
        public GiaoDich {
            Objects.requireNonNull(loai, "loai không được null");
            Objects.requireNonNull(soTien, "soTien không được null");
            Objects.requireNonNull(soDuSau, "soDuSau không được null");
            Objects.requireNonNull(thoiDiem, "thoiDiem không được null");

            if (soTien.signum() <= 0) {
                throw new IllegalArgumentException("Số tiền giao dịch phải dương, nhận được: " + soTien);
            }
            if (soDuSau.signum() < 0) {
                throw new IllegalArgumentException("Số dư sau giao dịch không được âm, nhận được: " + soDuSau);
            }

            // Chuẩn hoá scale để equals/hashCode của record hoạt động đúng - xem Javadoc dưới.
            soTien = soTien.setScale(SO_CHU_SO_THAP_PHAN, RoundingMode.HALF_UP);
            soDuSau = soDuSau.setScale(SO_CHU_SO_THAP_PHAN, RoundingMode.HALF_UP);
        }
    }

    /**
     * Ném khi số dư không đủ cho thao tác rút tiền.
     *
     * <p>Đây là lỗi <em>nghiệp vụ</em>, không phải lỗi lập trình: tham số hoàn toàn hợp lệ,
     * chỉ là trạng thái tài khoản tại thời điểm đó không cho phép. Vì vậy nó tách riêng khỏi
     * {@link IllegalArgumentException} - tầng trên cần phân biệt hai loại để trả về mã lỗi
     * khác nhau cho người dùng.
     */
    public static class SoDuKhongDuException extends RuntimeException {

        private final BigDecimal soDuHienTai;
        private final BigDecimal soTienYeuCau;

        public SoDuKhongDuException(BigDecimal soDuHienTai, BigDecimal soTienYeuCau) {
            super(String.format("Số dư không đủ: cần rút %s nhưng chỉ có %s", soTienYeuCau, soDuHienTai));
            this.soDuHienTai = soDuHienTai;
            this.soTienYeuCau = soTienYeuCau;
        }

        public BigDecimal getSoDuHienTai() {
            return soDuHienTai;
        }

        public BigDecimal getSoTienYeuCau() {
            return soTienYeuCau;
        }
    }


    private static int totalAccounts;

    private final String soTaiKhoan;
    private final String chuTaiKhoan;
    private BigDecimal soDu;
    private final List<GiaoDich> lichSu = new ArrayList<>();

    public TaiKhoanNganHang(String soTaiKhoan, String chuTaiKhoan, BigDecimal soDuBanDau) {
        Objects.requireNonNull(soTaiKhoan, "soTaiKhoan không được null");
        Objects.requireNonNull(chuTaiKhoan, "chuTaiKhoan không được null");
        Objects.requireNonNull(soDuBanDau, "soDuBanDau không được null");

        if (soTaiKhoan.isBlank()) {
            throw new IllegalArgumentException("soTaiKhoan không được rỗng");
        }

        if (chuTaiKhoan.isBlank()) {
            throw new IllegalArgumentException("chuTaiKhoan không được rỗng");
        }

        if (soDuBanDau.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Số dư ban đầu không được âm, nhận được: " + soDuBanDau);
        }

        this.soTaiKhoan = soTaiKhoan.strip();
        this.chuTaiKhoan = chuTaiKhoan.strip();
        this.soDu = soDuBanDau.setScale(SO_CHU_SO_THAP_PHAN, RoundingMode.HALF_UP);
        totalAccounts++;
    }

    private static BigDecimal chuanHoaSoTien(BigDecimal soTien, String tenThaoTac) {
        Objects.requireNonNull(soTien, "soTien không được null");
        if (soTien.signum() <= 0) {
            throw new IllegalArgumentException(String.format("Số tiền %s phải dương, nhận được: %s", tenThaoTac, soTien));
        }
        BigDecimal soTienChuan = soTien.setScale(SO_CHU_SO_THAP_PHAN, RoundingMode.HALF_UP);
        if (soTienChuan.signum() == 0) {
            throw new IllegalArgumentException(String.format(
                    "Số tiền %s quá nhỏ: %s bị làm tròn thành %s", tenThaoTac, soTien, soTienChuan));

        }
        return soTienChuan;
    }


    public void deposit(BigDecimal soTien) {
        BigDecimal soTienChuan = chuanHoaSoTien(soTien, "nạp");
        BigDecimal soDuMoi = soDu.add(soTienChuan);
        GiaoDich gd = new GiaoDich(LoaiGiaoDich.NAP, soTienChuan, soDuMoi, Instant.now());
        this.soDu = soDuMoi;
        this.lichSu.add(gd);
    }

    public void withdraw(BigDecimal soTien) {
        BigDecimal soTienChuan = chuanHoaSoTien(soTien, "rút");
        if (soDu.compareTo(soTienChuan) < 0) {
            throw new SoDuKhongDuException(soDu, soTienChuan);
        }
        BigDecimal soDuMoi = soDu.subtract(soTienChuan);
        GiaoDich gd = new GiaoDich(LoaiGiaoDich.RUT, soTienChuan, soDuMoi, Instant.now());
        this.soDu = soDuMoi;
        this.lichSu.add(gd);
    }

    public BigDecimal getSoDu() {
        return this.soDu;
    }

    public List<GiaoDich> getLichSu() {
        return List.copyOf(lichSu);
    }

    public static int getTotalAccounts() {
        return totalAccounts;
    }

    /**
     * Cố ý KHÔNG in nội dung {@code lichSu}, chỉ in số lượng.
     *
     * <p>{@code toString()} chủ yếu được gọi trong log và trong thông báo lỗi của test. Một
     * tài khoản dùng lâu năm có hàng nghìn giao dịch, in hết ra sẽ biến một dòng log thành
     * vài trăm KB và làm chậm chính đường xử lý lỗi.
     */
    @Override
    public String toString() {
        return String.format("TaiKhoanNganHang{soTaiKhoan=%s, chuTaiKhoan=%s, soDu=%s, soGiaoDich=%d}",
                soTaiKhoan, chuTaiKhoan, soDu, lichSu.size());
    }

    /**
     * Chỉ so sánh {@code soTaiKhoan} - khác hẳn {@link HocSinh} vốn so cả ba field.
     *
     * <p>{@code HocSinh} là <em>value object</em>: nó chính là dữ liệu của nó, đổi một field
     * là thành một giá trị khác. Còn {@code TaiKhoanNganHang} là <em>entity</em>: nó có danh
     * tính riêng tồn tại độc lập với trạng thái. Hai object cùng số tài khoản nhưng khác số
     * dư không phải hai tài khoản, mà là cùng một tài khoản chụp ở hai thời điểm.
     *
     * <p>Đưa {@code soDu} vào {@code equals} sẽ gây hậu quả cụ thể: cất tài khoản vào một
     * {@code HashSet} rồi gọi {@code deposit()}, hashCode đổi theo, và object biến mất khỏi
     * set - {@code contains()} trả về {@code false} cho chính object đang nằm trong đó.
     *
     * <p>Đây cũng là quy tắc cho {@code @Entity} của JPA sau này: equals/hashCode dựa trên
     * khoá nghiệp vụ, không dựa trên các field thay đổi được.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaiKhoanNganHang khac)) {
            return false;
        }
        return soTaiKhoan.equals(khac.soTaiKhoan);
    }

    /**
     * Dùng đúng field mà {@link #equals(Object)} dùng. {@code soTaiKhoan} là {@code final}
     * nên hashCode của một object không bao giờ đổi trong suốt vòng đời của nó.
     */
    @Override
    public int hashCode() {
        return soTaiKhoan.hashCode();
    }
}

