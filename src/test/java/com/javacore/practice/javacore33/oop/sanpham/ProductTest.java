package com.javacore.practice.javacore33.oop.sanpham;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Bài 11 - {@link Comparable} và {@link Comparator} trên {@link Product}.
 *
 * <p>Hai nhóm test chính không trùng nhau: nhóm {@code compareTo} kiểm tra thứ tự <em>tự
 * nhiên</em> mà class tự mang theo, nhóm {@code Comparator} kiểm tra các thứ tự <em>bên
 * ngoài</em>. Dữ liệu mẫu cố ý có hai cặp trùng - Chuột và Bàn phím cùng giá, Bàn phím và Màn
 * hình cùng tồn kho - vì không có cặp trùng thì
 * {@link Comparator#thenComparing(Comparator)} không bao giờ được gọi tới và test sẽ xanh cả
 * khi nó bị viết sai.
 *
 * <p>Khác bài 10: ở đó so {@code int} nên so bằng tuyệt đối. Ở đây giá là {@code double},
 * nhưng ta không assert trên giá trị giá mà assert trên <em>thứ tự</em>, nên vẫn không cần
 * {@code DELTA}.
 */
class ProductTest {

    private static final Product CHUOT = new Product("Chuột", 250_000, 10);
    private static final Product BAN_PHIM = new Product("Bàn phím", 250_000, 3);
    private static final Product MAN_HINH = new Product("Màn hình", 3_500_000, 3);
    private static final Product TAI_NGHE = new Product("Tai nghe", 800_000, 10);

    /** Danh sách mới mỗi lần gọi: {@code sort} sửa tại chỗ nên không được dùng chung. */
    private static List<Product> danhSach() {
        return new ArrayList<>(List.of(CHUOT, BAN_PHIM, MAN_HINH, TAI_NGHE));
    }

    /** Trích tên ra để assert đọc được; so cả object thì khi đỏ không biết sai ở đâu. */
    private static List<String> ten(List<Product> ds) {
        return ds.stream().map(Product::getTen).toList();
    }

    // --- constructor: trường hợp hợp lệ ----------------------------------

    @Test
    void giuNguyenGiaTriTruyenVao() {
        Product p = new Product("Chuột", 250_000, 10);

        assertEquals("Chuột", p.getTen());
        assertEquals(250_000, p.getGia());
        assertEquals(10, p.getStock());
    }

    @ParameterizedTest(name = "giá {0} hợp lệ")
    @ValueSource(doubles = {0, 0.5, 250_000, Double.MAX_VALUE})
    void giaKhongAmThiChapNhan(double gia) {
        assertEquals(gia, new Product("X", gia, 0).getGia());
    }

    // --- constructor: trường hợp lỗi -------------------------------------

    @Test
    void tenNullThiNem() {
        assertThrows(NullPointerException.class, () -> new Product(null, 1, 1));
    }

    @ParameterizedTest(name = "giá {0} bị từ chối")
    @ValueSource(doubles = {-0.01, -1, -250_000, Double.NEGATIVE_INFINITY})
    void giaAmThiNem(double gia) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Product("X", gia, 1));

        assertTrue(ex.getMessage().contains(String.valueOf(gia)),
                "message phải nêu giá trị sai, nhận được: " + ex.getMessage());
    }

    /**
     * {@code NaN} phải bị chặn, dù {@code NaN < 0} là {@code false}.
     *
     * <p>Mọi phép so sánh với {@code NaN} đều trả {@code false}, nên một mình điều kiện
     * {@code gia < 0} để lọt. Lọt qua rồi thì {@link Double#compare} xếp {@code NaN} lớn hơn
     * mọi số - kể cả {@link Double#POSITIVE_INFINITY} - và cả danh sách sắp xếp sai. Nếu ai đó
     * bỏ {@link Double#isNaN} khỏi constructor, test này đỏ.
     */
    @Test
    void giaNaNThiNem() {
        assertThrows(IllegalArgumentException.class, () -> new Product("X", Double.NaN, 1));
    }

    @ParameterizedTest(name = "tồn kho {0} bị từ chối")
    @ValueSource(ints = {-1, -100, Integer.MIN_VALUE})
    void stockAmThiNem(int stock) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Product("X", 1, stock));

        assertTrue(ex.getMessage().contains(String.valueOf(stock)),
                "message phải nêu giá trị sai, nhận được: " + ex.getMessage());
    }

    // --- compareTo: thứ tự tự nhiên --------------------------------------

    @Test
    void sapXepMacDinhLaTheoGiaTangDan() {
        List<Product> ds = danhSach();

        ds.sort(null);   // null = dùng Comparable, tương đương Collections.sort(ds)

        assertIterableEquals(List.of("Chuột", "Bàn phím", "Tai nghe", "Màn hình"), ten(ds));
    }

    @Test
    void reHonThiTraVeAm() {
        assertTrue(CHUOT.compareTo(MAN_HINH) < 0);
    }

    @Test
    void datHonThiTraVeDuong() {
        assertTrue(MAN_HINH.compareTo(CHUOT) > 0);
    }

    @Test
    void cungGiaThiTraVeKhong() {
        assertEquals(0, CHUOT.compareTo(BAN_PHIM));
    }

    /**
     * Chênh lệch nhỏ hơn 1 đồng vẫn phải phân biệt được.
     *
     * <p>Đây là test chống hồi quy cho {@link Double#compare}: nếu ai đó thay thân
     * {@code compareTo} bằng {@code (int) (this.gia - khac.gia)} thì 0.7 bị ép xuống 0 và test
     * này đỏ. Viết đúng thì không phụ thuộc vào phần nguyên.
     */
    @Test
    void chenhLechNhoHonMotDongVanPhanBietDuoc() {
        Product re = new Product("Rẻ", 250_000.2, 1);
        Product dat = new Product("Đắt", 250_000.9, 1);

        assertTrue(re.compareTo(dat) < 0, "0.7 đồng chênh lệch bị nuốt mất");
        assertTrue(dat.compareTo(re) > 0);
    }

    /** Hợp đồng của {@link Comparable} đòi ném NPE, không phải trả về một giá trị quy ước. */
    @Test
    void soSanhVoiNullThiNem() {
        assertThrows(NullPointerException.class, () -> CHUOT.compareTo(null));
    }

    @Test
    void soSanhVoiChinhNoThiTraVeKhong() {
        assertEquals(0, CHUOT.compareTo(CHUOT));
    }

    // --- Comparator: một tiêu chí ----------------------------------------

    @Test
    void theoTenSapAZ() {
        List<Product> ds = danhSach();

        ds.sort(Product.THEO_TEN);

        assertIterableEquals(List.of("Bàn phím", "Chuột", "Màn hình", "Tai nghe"), ten(ds));
    }

    @Test
    void theoStockSapTangDan() {
        List<Product> ds = danhSach();

        ds.sort(Product.THEO_STOCK);

        assertIterableEquals(List.of(3, 3, 10, 10), ds.stream().map(Product::getStock).toList());
    }

    // --- Comparator: nhiều tiêu chí --------------------------------------

    /**
     * Cùng tồn kho thì mới xét tới tên - đây là chỗ {@code thenComparing} thực sự chạy.
     *
     * <p>Bàn phím và Màn hình cùng {@code stock = 3}, Chuột và Tai nghe cùng {@code stock =
     * 10}. Nếu {@code THEO_STOCK_ROI_TEN} bị viết thiếu phần {@code thenComparing} thì thứ tự
     * trong từng cặp sẽ là thứ tự ban đầu của danh sách (Chuột trước Tai nghe - vô tình đúng,
     * nhưng Bàn phím trước Màn hình cũng vô tình đúng). Nên test này được chốt bằng cả bốn vị
     * trí chứ không chỉ một cặp.
     */
    @Test
    void cungStockThiXetTiepTheoTen() {
        List<Product> ds = danhSach();

        ds.sort(Product.THEO_STOCK_ROI_TEN);

        assertIterableEquals(List.of("Bàn phím", "Màn hình", "Chuột", "Tai nghe"), ten(ds));
    }

    /**
     * Ghi lại cái bẫy: {@link Comparator#reversed()} dán vào cuối chuỗi đảo <em>cả cụm</em>.
     *
     * <p>So với {@link #cungStockThiXetTiepTheoTen()}: kết quả ở đây là danh sách đó đọc ngược
     * từ dưới lên. Tồn kho chuyển thành giảm dần như mong muốn, nhưng tên cũng lật thành Z-A -
     * Tai nghe vượt lên trước Chuột. Test này không khẳng định hành vi đó là tốt, nó chỉ chốt
     * lại để người sau không hiểu nhầm.
     */
    @Test
    void reversedOCuoiChuoiDaoCaHaiTieuChi() {
        List<Product> ds = danhSach();

        ds.sort(Product.THEO_STOCK.thenComparing(Product::getTen).reversed());

        assertIterableEquals(List.of("Tai nghe", "Chuột", "Màn hình", "Bàn phím"), ten(ds));
    }

    /**
     * Cách đúng khi chỉ muốn đảo một tiêu chí: đảo ngay tại tiêu chí đó, rồi mới nối tiếp.
     *
     * <p>Tồn kho giảm dần mà tên vẫn A-Z - so với {@link #reversedOCuoiChuoiDaoCaHaiTieuChi()}
     * để thấy khác biệt do vị trí của {@code reversed()} gây ra.
     */
    @Test
    void daoRiengMotTieuChiThiGiuNguyenTieuChiCon() {
        List<Product> ds = danhSach();

        ds.sort(Product.THEO_STOCK.reversed().thenComparing(Product::getTen));

        assertIterableEquals(List.of("Chuột", "Tai nghe", "Bàn phím", "Màn hình"), ten(ds));
    }

    // --- hệ quả của việc compareTo không nhất quán với equals -------------

    /**
     * Chứng minh cảnh báo trong javadoc của {@link Product} là thật, không phải lo xa.
     *
     * <p>{@link TreeSet} phân loại phần tử bằng {@code compareTo} chứ không hỏi
     * {@code equals}. Chuột và Bàn phím là hai sản phẩm khác nhau nhưng cùng giá, nên
     * {@code compareTo} trả 0 và {@code TreeSet} coi cái thứ hai là bản trùng rồi bỏ đi.
     * {@link HashSet} dùng {@code equals}/{@code hashCode} nên giữ đủ cả hai.
     *
     * <p>Đây là lý do thực tế để quan tâm tới tính nhất quán giữa {@code compareTo} và
     * {@code equals} - không phải chuyện lý thuyết suông.
     */
    @Test
    void treeSetNuotSanPhamCungGiaConHashSetThiKhong() {
        Set<Product> cay = new TreeSet<>(List.of(CHUOT, BAN_PHIM, MAN_HINH, TAI_NGHE));
        Set<Product> bam = new HashSet<>(List.of(CHUOT, BAN_PHIM, MAN_HINH, TAI_NGHE));

        assertEquals(3, cay.size(), "TreeSet phải nuốt mất một trong hai sản phẩm cùng giá");
        assertEquals(4, bam.size(), "HashSet không dùng compareTo nên giữ đủ bốn");
    }

    /** Dùng Comparator thì thoát được hệ quả trên, vì tên là duy nhất. */
    @Test
    void treeSetTheoTenThiGiuDuBonSanPham() {
        Set<Product> cay = new TreeSet<>(Product.THEO_TEN);
        cay.addAll(List.of(CHUOT, BAN_PHIM, MAN_HINH, TAI_NGHE));

        assertEquals(4, cay.size());
    }
}
