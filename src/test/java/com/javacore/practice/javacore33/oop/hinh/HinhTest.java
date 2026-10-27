package com.javacore.practice.javacore33.oop.hinh;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Bài 9 - phần dùng chung của cây {@link Hinh}: tổng diện tích và validate kích thước.
 *
 * <p>Trọng tâm là <em>tính đa hình</em>: {@link Hinh#tongDienTich(Hinh[])} phải cộng đúng mà
 * không cần biết từng phần tử là hình gì. Test
 * {@link #congDuocCaLoaiHinhChuaTungTonTaiLucVietTongDienTich()} kiểm chính xác điều đó bằng
 * một hình được định nghĩa ngay trong test - nếu method tính tổng có {@code instanceof} hay
 * {@code switch} theo loại hình thì nó sẽ gãy.
 *
 * <p>Mọi assert trên {@code double} đều có {@link #DELTA}: {@code 0.1 + 0.2 != 0.3} trong số
 * dấu phẩy động, nên so sánh bằng tuyệt đối là sai nguyên tắc. Khác hẳn bài 8 - ở đó
 * {@code BigDecimal} so bằng {@code equals} được vì nó là số thập phân chính xác.
 */
class HinhTest {

    /** Sai số cho phép khi so sánh double - đủ chặt để bắt lỗi công thức, đủ lỏng để bỏ qua nhiễu nhị phân. */
    private static final double DELTA = 1e-9;

    // --- tongDienTich: trường hợp hợp lệ ---------------------------------

    @Test
    void mangRongTraVeKhong() {
        assertEquals(0.0, Hinh.tongDienTich(new Hinh[0]), DELTA);
    }

    @Test
    void mangMotPhanTuTraVeDienTichCuaChinhNo() {
        assertEquals(12.0, Hinh.tongDienTich(new Hinh[]{new HinhChuNhat(3, 4)}), DELTA);
    }

    @Test
    void congDonDungTongCuaNhieuLoaiHinh() {
        Hinh[] danhSach = {
                new HinhTron(2),        // π·4  = 12.566370614359172
                new HinhChuNhat(3, 4),  //      = 12
                new HinhTamGiac(3, 4, 5)//      = 6
        };

        assertEquals(30.566370614359172, Hinh.tongDienTich(danhSach), DELTA);
    }

    /**
     * Một hình chưa từng tồn tại lúc {@link Hinh#tongDienTich(Hinh[])} được viết, tạo bằng
     * anonymous class ngay tại đây. Tổng vẫn đúng vì method chỉ gọi {@code dienTich()} chứ
     * không hỏi "mày là hình gì".
     *
     * <p>Đây là bài học của bài 9 viết dưới dạng test: thêm loại hình mới không phải sửa
     * một dòng nào trong code tính tổng.
     */
    @Test
    void congDuocCaLoaiHinhChuaTungTonTaiLucVietTongDienTich() {
        Hinh hinhLa = new Hinh() {
            @Override
            public double dienTich() {
                return 10;
            }
        };

        assertEquals(15.0, Hinh.tongDienTich(new Hinh[]{hinhLa, new HinhChuNhat(1, 5)}), DELTA);
    }

    // --- tongDienTich: trường hợp lỗi ------------------------------------

    @Test
    void mangNullThiNem() {
        assertThrows(NullPointerException.class, () -> Hinh.tongDienTich(null));
    }

    @Test
    void phanTuNullThiNemKemViTri() {
        Hinh[] danhSach = {new HinhTron(1), null, new HinhChuNhat(2, 3)};

        NullPointerException ex = assertThrows(NullPointerException.class,
                () -> Hinh.tongDienTich(danhSach));

        assertTrue(ex.getMessage().contains("1"),
                "message phải chỉ ra vị trí phần tử null, nhận được: " + ex.getMessage());
    }

    // --- kiemTraKichThuoc ------------------------------------------------

    /**
     * {@code NaN} và {@code Infinity} nằm trong danh sách này một cách cố ý: cả hai đều lọt
     * qua một phép {@code > 0} viết ẩu, rồi lan vào tổng và phá hỏng kết quả mà không ném lỗi.
     */
    @ParameterizedTest(name = "kích thước {0} bị từ chối")
    @ValueSource(doubles = {
            -1, -0.0001, 0,
            Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY
    })
    void kichThuocKhongHopLeThiNem(double giaTri) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> Hinh.kiemTraKichThuoc(giaTri, "Bán kính"));

        assertTrue(ex.getMessage().contains("Bán kính"),
                "message phải nêu tên tham số sai, nhận được: " + ex.getMessage());
    }

    /**
     * Trả về <em>chính giá trị vừa kiểm</em> chứ không phải {@code void} - nhờ vậy lớp con
     * viết được {@code this.banKinh = kiemTraKichThuoc(banKinh, "Bán kính")}, gán field
     * {@code final} đúng một lần tại đúng một chỗ.
     */
    @ParameterizedTest(name = "kích thước {0} được chấp nhận")
    @ValueSource(doubles = {0.0001, 1, 1000, Double.MAX_VALUE})
    void kichThuocHopLeThiTraVeChinhNo(double giaTri) {
        assertEquals(giaTri, Hinh.kiemTraKichThuoc(giaTri, "Cạnh"), DELTA);
    }
}
