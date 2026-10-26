package com.javacore.practice.javacore33.oop;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Bài 7 - Class, Object và đóng gói.
 *
 * <p>Trọng tâm không phải {@link HocSinh#xepLoai()} mà là bất biến của object: không có
 * cách nào tạo ra hay làm cho một {@code HocSinh} rơi vào trạng thái sai. Vì vậy mỗi luật
 * validate được kiểm ở cả hai lối vào - constructor và setter.
 *
 * <p>Các test exception assert cả <em>loại chính xác</em> lẫn <em>nội dung message</em>,
 * không chỉ assert "có ném gì đó". Lý do: nhiều exception của JDK kế thừa từ
 * {@link IllegalArgumentException} (ví dụ {@code IllegalFormatConversionException} khi
 * viết sai chuỗi format), nên {@code assertThrows(IllegalArgumentException.class, ...)}
 * một mình vẫn xanh trong khi message đã hỏng hoàn toàn.
 */
class HocSinhTest {

    // --- constructor: trường hợp hợp lệ ---------------------------------

    @Test
    void taoHocSinhHopLe() {
        HocSinh hocSinh = new HocSinh("Nam", 16, 9.0);

        assertEquals("Nam", hocSinh.getHoTen());
        assertEquals(16, hocSinh.getTuoi());
        assertEquals(9.0, hocSinh.getDiem());
    }

    @ParameterizedTest(name = "hoTen [{0}] -> [{1}]")
    @CsvSource({
            "'  Nam  ', 'Nam'",
            "'\tNam\n', 'Nam'",
            "'Nguyễn Văn Nam  ', 'Nguyễn Văn Nam'"
    })
    void hoTenBiCatKhoangTrangDauCuoi(String dauVao, String mongDoi) {
        assertEquals(mongDoi, new HocSinh(dauVao, 16, 9.0).getHoTen());
    }

    @ParameterizedTest(name = "tuoi = {0} hợp lệ")
    @ValueSource(ints = {1, 2, 149, 150})
    void tuoiTrongMienDuocChapNhan(int tuoi) {
        assertEquals(tuoi, new HocSinh("Nam", tuoi, 9.0).getTuoi());
    }

    @ParameterizedTest(name = "diem = {0} hợp lệ")
    @ValueSource(doubles = {0.0, 0.1, 5.5, 9.99, 10.0})
    void diemTrongMienDuocChapNhan(double diem) {
        assertEquals(diem, new HocSinh("Nam", 16, diem).getDiem());
    }

    // --- constructor: trường hợp bị từ chối ------------------------------

    @Test
    void hoTenNullNemNullPointerException() {
        NullPointerException loi = assertThrows(NullPointerException.class,
                () -> new HocSinh(null, 16, 9.0));

        assertEquals("hoTen không được null", loi.getMessage());
    }

    @ParameterizedTest(name = "hoTen [{0}] bị từ chối")
    @ValueSource(strings = {"", " ", "   ", "\t", "\n", " \t \n "})
    void hoTenRongNemIllegalArgumentException(String hoTen) {
        IllegalArgumentException loi = assertThrows(IllegalArgumentException.class,
                () -> new HocSinh(hoTen, 16, 9.0));

        assertEquals("hoTen không được rỗng", loi.getMessage());
    }

    @ParameterizedTest(name = "tuoi = {0} bị từ chối")
    @ValueSource(ints = {0, -1, -150, 151, 1000, Integer.MIN_VALUE, Integer.MAX_VALUE})
    void tuoiNgoaiMienBiTuChoi(int tuoi) {
        IllegalArgumentException loi = assertThrows(IllegalArgumentException.class,
                () -> new HocSinh("Nam", tuoi, 9.0));

        assertEquals(IllegalArgumentException.class, loi.getClass(),
                "phải là IllegalArgumentException thuần, không phải lớp con nào khác");
        assertEquals("Tuổi phải từ 1 đến 150, nhận được: " + tuoi, loi.getMessage());
    }

    @ParameterizedTest(name = "diem = {0} bị từ chối")
    @ValueSource(doubles = {-0.1, -1.0, 10.01, 999.0,
            Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY,
            Double.MAX_VALUE, -Double.MAX_VALUE})
    void diemNgoaiMienBiTuChoi(double diem) {
        IllegalArgumentException loi = assertThrows(IllegalArgumentException.class,
                () -> new HocSinh("Nam", 16, diem));

        assertEquals(IllegalArgumentException.class, loi.getClass(),
                "phải là IllegalArgumentException thuần, không phải lớp con nào khác");
        assertEquals("Điểm phải từ 0.0 đến 10.0, nhận được: " + diem, loi.getMessage());
    }

    // --- setter: validate và tính nguyên vẹn của state -------------------

    @Test
    void setTuoiHopLeCapNhatState() {
        HocSinh hocSinh = new HocSinh("Nam", 16, 9.0);

        hocSinh.setTuoi(17);

        assertEquals(17, hocSinh.getTuoi());
    }

    @Test
    void setDiemHopLeCapNhatStateVaXepLoai() {
        HocSinh hocSinh = new HocSinh("Nam", 16, 9.0);

        hocSinh.setDiem(5.0);

        assertEquals(5.0, hocSinh.getDiem());
        assertEquals("Trung bình", hocSinh.xepLoai());
    }

    @ParameterizedTest(name = "setTuoi({0}) bị chặn")
    @ValueSource(ints = {0, -7, 151, Integer.MAX_VALUE})
    void setTuoiNgoaiMienBiChanVaGiuNguyenState(int tuoiSai) {
        HocSinh hocSinh = new HocSinh("Nam", 16, 9.0);

        IllegalArgumentException loi = assertThrows(IllegalArgumentException.class,
                () -> hocSinh.setTuoi(tuoiSai));

        assertEquals(IllegalArgumentException.class, loi.getClass());
        assertEquals("Tuổi phải từ 1 đến 150, nhận được: " + tuoiSai, loi.getMessage());
        assertEquals(16, hocSinh.getTuoi(), "validate phải chạy trước khi gán");
    }

    @ParameterizedTest(name = "setDiem({0}) bị chặn")
    @ValueSource(doubles = {-0.1, 10.01, 1000.0, Double.NaN, Double.POSITIVE_INFINITY})
    void setDiemNgoaiMienBiChanVaGiuNguyenState(double diemSai) {
        HocSinh hocSinh = new HocSinh("Nam", 16, 9.0);

        IllegalArgumentException loi = assertThrows(IllegalArgumentException.class,
                () -> hocSinh.setDiem(diemSai));

        assertEquals(IllegalArgumentException.class, loi.getClass());
        assertEquals("Điểm phải từ 0.0 đến 10.0, nhận được: " + diemSai, loi.getMessage());
        assertEquals(9.0, hocSinh.getDiem(), "validate phải chạy trước khi gán");
        assertEquals("Xuất sắc", hocSinh.xepLoai());
    }

    // --- xepLoai ---------------------------------------------------------

    @ParameterizedTest(name = "diem {0} -> {1}")
    @CsvSource({
            "10.0,  'Xuất sắc'",
            "9.0,   'Xuất sắc'",
            "8.5,   'Xuất sắc'",
            "8.49,  'Giỏi'",
            "8.0,   'Giỏi'",
            "7.99,  'Khá'",
            "6.5,   'Khá'",
            "6.49,  'Trung bình'",
            "5.0,   'Trung bình'",
            "4.99,  'Yếu'",
            "4.0,   'Yếu'",
            "3.99,  'Kém'",
            "0.0,   'Kém'"
    })
    void xepLoaiTheoNguong(double diem, String xepLoaiMongDoi) {
        assertEquals(xepLoaiMongDoi, new HocSinh("Nam", 16, diem).xepLoai());
    }

    // --- toString --------------------------------------------------------

    @Test
    void toStringChuaTenClassVaToanBoState() {
        String moTa = new HocSinh("Nam", 16, 9.0).toString();

        assertTrue(moTa.startsWith("HocSinh"), "phải mở đầu bằng tên class, nhận được: " + moTa);
        assertTrue(moTa.contains("Nam"), "thiếu hoTen trong: " + moTa);
        assertTrue(moTa.contains("16"), "thiếu tuoi trong: " + moTa);
        assertTrue(moTa.contains("9.0"), "thiếu diem trong: " + moTa);
    }

    @Test
    void toStringNamTrenMotDong() {
        assertEquals(-1, new HocSinh("Nam", 16, 9.0).toString().indexOf('\n'),
                "toString() nhiều dòng sẽ làm vỡ log");
    }

    // --- equals / hashCode: hợp đồng ------------------------------------

    @Test
    void equalsCoTinhPhanXa() {
        HocSinh hocSinh = new HocSinh("Nam", 16, 9.0);

        assertEquals(hocSinh, hocSinh);
    }

    @Test
    void equalsCoTinhDoiXung() {
        HocSinh a = new HocSinh("Nam", 16, 9.0);
        HocSinh b = new HocSinh("Nam", 16, 9.0);

        assertEquals(a, b);
        assertEquals(b, a);
    }

    @Test
    void equalsCoTinhBacCau() {
        HocSinh a = new HocSinh("Nam", 16, 9.0);
        HocSinh b = new HocSinh("Nam", 16, 9.0);
        HocSinh c = new HocSinh("Nam", 16, 9.0);

        assertEquals(a, b);
        assertEquals(b, c);
        assertEquals(a, c);
    }

    @Test
    void equalsNhatQuanQuaNhieuLanGoi() {
        HocSinh a = new HocSinh("Nam", 16, 9.0);
        HocSinh b = new HocSinh("Nam", 16, 9.0);

        for (int i = 0; i < 3; i++) {
            assertEquals(a, b);
        }
    }

    @Test
    void khongBangNullVaKhongBangKieuKhac() {
        HocSinh hocSinh = new HocSinh("Nam", 16, 9.0);

        assertNotEquals(null, hocSinh);
        assertNotEquals("Nam", hocSinh);
        assertFalse(hocSinh.equals(null), "equals(null) phải trả false, không được ném NPE");
    }

    // --- equals / hashCode: phân biệt theo từng field --------------------

    @Test
    void khacHoTenThiKhongBang() {
        assertNotEquals(new HocSinh("Nam", 16, 9.0), new HocSinh("Lan", 16, 9.0));
    }

    @Test
    void khacTuoiThiKhongBang() {
        assertNotEquals(new HocSinh("Nam", 16, 9.0), new HocSinh("Nam", 17, 9.0));
    }

    @Test
    void khacDiemThiKhongBang() {
        assertNotEquals(new HocSinh("Nam", 16, 9.0), new HocSinh("Nam", 16, 8.0));
    }

    @Test
    void hoTenKhacKhoangTrangVanBangNhauVIStripTrongConstructor() {
        assertEquals(new HocSinh("  Nam  ", 16, 9.0), new HocSinh("Nam", 16, 9.0));
    }

    @Test
    void bangNhauThiHashCodeBangNhau() {
        HocSinh a = new HocSinh("Nam", 16, 9.0);
        HocSinh b = new HocSinh("  Nam  ", 16, 9.0);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode(), "equals() true thì hashCode() bắt buộc trùng");
    }

    /**
     * {@code -0.0 == 0.0} là {@code true} nhưng {@code Double.hashCode} của chúng khác nhau.
     * Dùng {@link Double#compare(double, double)} trong {@code equals} nên hai object này
     * không bằng nhau - nhất quán với {@code hashCode}. Nếu đổi sang {@code ==} thì chúng
     * bằng nhau mà hashCode lại lệch, tức là vi phạm hợp đồng.
     */
    @Test
    void khongDauVaDuongKhongLaHaiGiaTriKhacNhau() {
        HocSinh am = new HocSinh("Nam", 16, -0.0);
        HocSinh duong = new HocSinh("Nam", 16, 0.0);

        assertNotEquals(am, duong);
        assertNotEquals(am.hashCode(), duong.hashCode());
    }

    // --- equals / hashCode: hoạt động thật trong Collections -------------

    @Test
    void hashSetLoaiBoBanTrung() {
        Set<HocSinh> lop = new HashSet<>();

        lop.add(new HocSinh("Nam", 16, 9.0));
        lop.add(new HocSinh("Nam", 16, 9.0));
        lop.add(new HocSinh("Lan", 17, 7.0));

        assertEquals(2, lop.size());
    }

    @Test
    void hashMapTimThayBangKeyLaObjectKhac() {
        Map<HocSinh, String> ghiChu = new HashMap<>();
        ghiChu.put(new HocSinh("Nam", 16, 9.0), "lớp trưởng");

        assertEquals("lớp trưởng", ghiChu.get(new HocSinh("Nam", 16, 9.0)));
    }

    @Test
    void listContainsSoSanhTheoNoiDung() {
        var lop = java.util.List.of(new HocSinh("Nam", 16, 9.0));

        assertTrue(lop.contains(new HocSinh("Nam", 16, 9.0)));
    }

    /**
     * Ghi lại một hệ quả có thật, không phải bug: {@code equals}/{@code hashCode} dựa trên
     * field thay đổi được, nên sửa {@code diem} sau khi đã bỏ object vào {@code HashSet}
     * sẽ làm nó lạc bucket và không tìm lại được. Đây là lý do thực tế khiến entity nên
     * so sánh theo id bất biến thay vì theo toàn bộ state.
     */
    @Test
    void doiFieldSauKhiVaoHashSetThiKhongTimLaiDuoc() {
        HocSinh hocSinh = new HocSinh("Nam", 16, 9.0);
        Set<HocSinh> lop = new HashSet<>();
        lop.add(hocSinh);
        assertTrue(lop.contains(hocSinh));

        hocSinh.setDiem(5.0);

        assertFalse(lop.contains(hocSinh), "object vẫn nằm trong set nhưng đã lạc bucket");
        assertEquals(1, lop.size());
    }
}
