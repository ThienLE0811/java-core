package com.javacore.practice.javacore33.oop.maytinh;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Bài 13 - {@link Calculator} và cây exception của nó.
 *
 * <p>Trọng tâm của file này không phải kiểm tra {@code 2 + 2 = 4}; phép toán thì {@code double}
 * của Java lo rồi. Thứ cần chốt bằng test là <b>những lúc Java KHÔNG báo lỗi</b>:
 * <pre>
 * 10.0 / 0       →  Infinity   (im lặng)
 * 0.0 / 0        →  NaN        (im lặng)
 * Math.sqrt(-1)  →  NaN        (im lặng)
 * </pre>
 * Ba dòng đó là lý do bài 13 tồn tại. Nhóm test {@code hanhViGocCuaJava_*} đóng đinh hành vi của
 * JDK để người đọc sau này thấy ngay vì sao {@link Calculator} phải tự kiểm tra; các nhóm còn
 * lại chốt rằng {@code Calculator} đã biến sự im lặng đó thành exception.
 *
 * <p>Một chi tiết đáng để ý về <em>hình dáng</em> của chính file này: không method test nào phải
 * khai báo {@code throws}. Đó là hệ quả trực tiếp của việc {@link CalculatorException} kế thừa
 * {@link RuntimeException}. Nếu đổi nó thành {@code extends Exception}, mọi method ở đây có gọi
 * {@code calculate} - kể cả method chỉ {@code assertThrows} - sẽ lập tức đỏ và phải thêm
 * {@code throws CalculatorException}. Đổi một dòng rồi chạy {@code mvn test-compile} là đo được
 * cái giá của checked exception.
 */
class CalculatorTest {

    private static final double DELTA = 1e-9;

    private final Calculator calc = new Calculator();

    // --- hành vi gốc của Java: vì sao bài này cần tồn tại ------------------

    /**
     * Không test {@code Calculator}, mà test giả định nền tảng mà {@code Calculator} dựa vào.
     * Nếu một ngày nào đó dòng này đỏ thì nghĩa là hiểu biết của ta về Java đã sai, và toàn bộ
     * thiết kế phía dưới cần xem lại.
     */
    @Test
    void hanhViGocCuaJava_chiaDoubleCho0KhongNemGiCa() {
        assertTrue(Double.isInfinite(10.0 / 0), "10.0 / 0 phải ra Infinity chứ không ném");
        assertTrue(Double.isInfinite(-10.0 / 0));
        assertTrue(Double.isNaN(0.0 / 0));
    }

    @Test
    void hanhViGocCuaJava_sqrtSoAmTraVeNaN() {
        assertTrue(Double.isNaN(Math.sqrt(-1)));
    }

    /** Chỉ phép chia <em>nguyên</em> mới được JVM bảo vệ sẵn. */
    @Test
    void hanhViGocCuaJava_chiaIntCho0ThiNem() {
        int tuSo = 10;
        int mauSo = 0;

        // Phải là lambda dạng khối: `() -> tuSo / mauSo` là biểu thức giá trị, không tương
        // thích với Executable (vốn trả void), nên không biên dịch được.
        assertThrows(ArithmeticException.class, () -> {
            int ketQua = tuSo / mauSo;
        });
    }

    // --- biểu thức hợp lệ ---------------------------------------------------

    @ParameterizedTest(name = "[{0}] = {1}")
    @CsvSource({
            "10 + 5.5,   15.5",
            "20 - 8,     12",
            "4.5 * 2,    9",
            "10 / 4,     2.5",
            "10 % 3,     1",
            "2 ^ 8,      256",
            "sqrt 25,    5",
            "sqrt(144),  12",
            "sqrt 0,     0"
    })
    void tinhDungBieuThucHopLe(String bieuThuc, double mongDoi) {
        assertEquals(mongDoi, calc.calculate(bieuThuc), DELTA);
    }

    /** Nếu ai đó đổi {@code Calculator} sang {@code int} thì đúng dòng này đỏ. */
    @Test
    void chiaRaSoLeChuKhongCatPhanThapPhan() {
        assertEquals(2.5, calc.calculate("10 / 4"), DELTA);
    }

    @ParameterizedTest(name = "[{0}] vẫn tính được")
    @ValueSource(strings = {"  10   /   4  ", "10/4", "sqrt(16)", "SQRT 16"})
    void chapNhanKhoangTrangVaBienTheCuPhap(String bieuThuc) {
        assertFalse(Double.isNaN(calc.calculate(bieuThuc)));
    }

    // --- bất biến lớn nhất của bài ------------------------------------------

    /**
     * Không một đầu vào nào được phép trả về {@code Infinity} hay {@code NaN}. Hoặc ra số thật,
     * hoặc ném exception - không có cửa thứ ba. Đây là test quan trọng nhất file.
     */
    @ParameterizedTest(name = "[{0}] ném chứ không trả Infinity/NaN")
    @ValueSource(strings = {"10 / 0", "-10 / 0", "0 / 0", "15 % 0", "0 ^ -2", "sqrt -4", "sqrt(-25)"})
    void khongBaoGioTraVeInfinityHoacNaN(String bieuThuc) {
        assertThrows(CalculatorException.class, () -> calc.calculate(bieuThuc));
    }

    // --- DivisionByZeroException --------------------------------------------

    @Test
    void chiaCho0ThiNemKemDuLieuHienTruong() {
        DivisionByZeroException ex = assertThrows(DivisionByZeroException.class,
                () -> calc.calculate("10 / 0"));

        assertEquals(10, ex.getLeftOperand(), DELTA);
        assertEquals("/", ex.getOperator());
        assertEquals(0, ex.getRightOperand(), DELTA);
        assertEquals("10 / 0", ex.getExpression());
        assertTrue(ex.getMessage().contains("10"), "message phải nêu số bị chia: " + ex.getMessage());
        assertNull(ex.getCause(), "lỗi này không bọc exception nào khác");
    }

    @Test
    void layPhanDuCho0ThiNem() {
        DivisionByZeroException ex = assertThrows(DivisionByZeroException.class,
                () -> calc.calculate("15 % 0"));

        assertEquals("%", ex.getOperator());
        assertEquals(15, ex.getLeftOperand(), DELTA);
    }

    /** {@code 0 ^ -2} là {@code 1 / (0 * 0)} - cũng là chia cho 0; {@code Math.pow} trả Infinity. */
    @Test
    void luyThuaAmCuaKhongThiNem() {
        DivisionByZeroException ex = assertThrows(DivisionByZeroException.class,
                () -> calc.calculate("0 ^ -2"));

        assertEquals("^", ex.getOperator());
        assertEquals(-2, ex.getRightOperand(), DELTA);
    }

    /**
     * {@code -0.0} là một giá trị riêng trong IEEE 754, khác {@code 0.0} khi so bằng
     * {@link Double#compare(double, double)}. Mẫu số {@code -0.0} vẫn phải bị chặn.
     */
    @Test
    void mauSoAmKhongVanBiChan() {
        assertThrows(DivisionByZeroException.class, () -> calc.divide(10, -0.0));
    }

    /** API số học thuần cũng phải ném, không chỉ riêng đường đi qua {@code calculate}. */
    @Test
    void apiSoHocThuanCungNem() {
        assertThrows(DivisionByZeroException.class, () -> calc.divide(10, 0));
        assertThrows(DivisionByZeroException.class, () -> calc.modulo(10, 0));
        assertThrows(DivisionByZeroException.class, () -> calc.power(0, -2));
    }

    @Test
    void tuSoBang0NhungMauSoKhac0ThiVanTinhBinhThuong() {
        assertEquals(0, calc.calculate("0 / 5"), DELTA);
    }

    // --- NegativeSqrtException ----------------------------------------------

    @Test
    void canBacHaiSoAmThiNemKemGiaTri() {
        NegativeSqrtException ex = assertThrows(NegativeSqrtException.class,
                () -> calc.calculate("sqrt -4"));

        assertEquals(-4, ex.getNegativeValue(), DELTA);
        assertTrue(ex.getMessage().contains("-4"), "message phải nêu giá trị âm: " + ex.getMessage());
    }

    @ParameterizedTest(name = "[{0}] ném NegativeSqrtException")
    @ValueSource(strings = {"sqrt -4", "sqrt(-25)", "sqrt -0.5"})
    void moiDangCuPhapSqrtAmDeuNem(String bieuThuc) {
        assertThrows(NegativeSqrtException.class, () -> calc.calculate(bieuThuc));
    }

    @Test
    void canBacHaiCuaKhongVanHopLe() {
        assertEquals(0, calc.sqrt(0), DELTA);
    }

    // --- InvalidOperationException -------------------------------------------

    @ParameterizedTest(name = "toán tử [{0}] không được hỗ trợ")
    @ValueSource(strings = {"&", "@", "//", "**"})
    void toanTuLaThiNem(String toanTu) {
        String bieuThuc = "10 " + toanTu + " 2";

        InvalidOperationException ex = assertThrows(InvalidOperationException.class,
                () -> calc.calculate(bieuThuc));

        assertEquals(toanTu, ex.getOperator());
        assertEquals(bieuThuc, ex.getExpression());
    }

    /**
     * Chỗ duy nhất trong bài có {@code cause}. {@link Double#parseDouble(String)} ném
     * {@link NumberFormatException}; ta bọc lại thành lỗi nghiệp vụ nhưng phải <em>giữ</em>
     * exception gốc. Mất {@code cause} là mất stack trace của nguyên nhân thật.
     */
    @Test
    void toanHangKhongPhaiSoThiNemVaGiuNguyenCause() {
        InvalidOperationException ex = assertThrows(InvalidOperationException.class,
                () -> calc.calculate("abc + 5"));

        assertInstanceOf(NumberFormatException.class, ex.getCause());
        assertTrue(ex.getMessage().contains("abc"), "message phải nêu token hỏng: " + ex.getMessage());
    }

    @ParameterizedTest(name = "biểu thức rỗng [{0}]")
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    void bieuThucRongThiNem(String bieuThuc) {
        InvalidOperationException ex = assertThrows(InvalidOperationException.class,
                () -> calc.calculate(bieuThuc));

        assertNull(ex.getOperator(), "lỗi rỗng không gắn với toán tử nào");
    }

    /** Biểu thức {@code null} thì {@code getExpression()} cũng {@code null} - đúng hiện trường. */
    @Test
    void bieuThucNullThiGetExpressionTraVeNull() {
        InvalidOperationException ex = assertThrows(InvalidOperationException.class,
                () -> calc.calculate(null));

        assertNull(ex.getExpression());
    }

    @Test
    void cuPhapSqrtSaiThiNemKemToanTuSqrt() {
        InvalidOperationException ex = assertThrows(InvalidOperationException.class,
                () -> calc.calculate("sqrt abc"));

        assertEquals("sqrt", ex.getOperator());
    }

    /**
     * Thiếu hoặc thừa thành phần. Test chỉ chốt rằng biểu thức bị từ chối, không chốt
     * <em>lý do</em> cụ thể: hiện {@code BINARY_PATTERN} dùng {@code \d*} nên khớp được toán
     * hạng rỗng và lỗi rơi vào nhánh "không phải số"; nếu sửa thành {@code \d+} thì nó sẽ rơi
     * vào nhánh "sai cú pháp". Cả hai đều chấp nhận được nên test không khoá vào một nhánh.
     */
    @ParameterizedTest(name = "[{0}] bị từ chối")
    @ValueSource(strings = {"10 +", "+ 10", "10", "10 + 5 + 2", "sqrt", "++"})
    void bieuThucThieuHoacThuaThanhPhanThiNem(String bieuThuc) {
        assertThrows(InvalidOperationException.class, () -> calc.calculate(bieuThuc));
    }

    // --- cây kế thừa: phần OOP của bài ----------------------------------------

    /**
     * Lý do cả ba exception có chung một class cha: người gọi bắt được cả cụm bằng một
     * {@code catch}, mà không phải dùng {@code catch (Exception e)} - thứ sẽ nuốt luôn
     * {@link NullPointerException} do lập trình viên viết sai và biến bug thành lỗi ngầm.
     */
    @ParameterizedTest(name = "[{0}] bắt được bằng CalculatorException")
    @ValueSource(strings = {"10 / 0", "sqrt -4", "10 & 2"})
    void catchClassChaBatDuocCaBaLoai(String bieuThuc) {
        CalculatorException ex = assertThrows(CalculatorException.class,
                () -> calc.calculate(bieuThuc));

        assertFalse(ex.getMessage().isBlank(), "mọi lỗi đều phải có message cho người đọc log");
    }

    /**
     * Chốt quyết định thiết kế: unchecked. Đổi {@code CalculatorException} sang
     * {@code extends Exception} thì dòng này đỏ, và cả file test cũng không biên dịch được nữa
     * vì thiếu {@code throws}.
     */
    @Test
    void laUncheckedException() {
        CalculatorException ex = assertThrows(CalculatorException.class,
                () -> calc.calculate("10 / 0"));

        assertInstanceOf(RuntimeException.class, ex);
    }
}
