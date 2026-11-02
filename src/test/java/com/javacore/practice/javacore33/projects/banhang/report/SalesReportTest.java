package com.javacore.practice.javacore33.projects.banhang.report;

import com.javacore.practice.javacore33.oop.donhang.OrderStatus;
import com.javacore.practice.javacore33.projects.banhang.Store;
import com.javacore.practice.javacore33.projects.banhang.model.Customer;
import com.javacore.practice.javacore33.projects.banhang.model.Order;
import com.javacore.practice.javacore33.projects.banhang.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Bài 33 - test phần báo cáo. Trọng tâm: báo cáo đọc từ OrderLine chứ không hỏi lại Product,
// nên sửa giá hay xoá sản phẩm về sau không làm sai số liệu cũ (R15).
class SalesReportTest {

    private Store store;
    private SalesReport baoCao;

    @BeforeEach
    void chuanBi() {
        store = new Store();
        store.themSanPham(new Product("SP01", "Laptop", 10_000_000L, 10));
        store.themSanPham(new Product("SP02", "Chuột", 500_000L, 50));
        store.themSanPham(new Product("SP03", "Bàn phím", 1_000_000L, 50));
        store.themKhachHang(new Customer("KH01", "Thiện", "thien@example.com"));
        baoCao = new SalesReport(store);
    }

    // R13
    @Test
    void doanhThu_chiTinhDonDaGiao() {
        giaoHang(store.datHang("KH01", gio("SP01", 1)));        // 10.000.000 - tính
        store.datHang("KH01", gio("SP01", 1));                  // PENDING      - không tính
        store.huyDon(store.datHang("KH01", gio("SP01", 1)).getId());  // huỷ      - không tính

        assertEquals(10_000_000L, baoCao.doanhThu());
        assertEquals(1, baoCao.donDaGiao().size());
    }

    @Test
    void doanhThu_chuaBanGiDoanhThuBangKhong() {
        assertEquals(0L, baoCao.doanhThu());
        assertEquals(List.of(), baoCao.topSanPhamBanChay(3));
    }

    // R14 - tie-break: cùng 1 cái thì sản phẩm doanh thu cao đứng trước.
    @Test
    void topSanPhamBanChay_xepTheoSoLuongRoiToiDoanhThu() {
        giaoHang(store.datHang("KH01", gio("SP01", 1, "SP02", 5, "SP03", 1)));

        List<SalesReport.BanChay> top = baoCao.topSanPhamBanChay(3);

        assertEquals(List.of("SP02", "SP01", "SP03"),
                top.stream().map(SalesReport.BanChay::productId).toList());
        assertEquals(5, top.getFirst().soLuongBan());
        assertEquals(5 * 500_000L, top.getFirst().doanhThu());
    }

    // R14 - cộng dồn qua nhiều đơn, và limit n hoạt động.
    @Test
    void topSanPhamBanChay_congDonQuaNhieuDonVaCatTheoN() {
        giaoHang(store.datHang("KH01", gio("SP02", 2)));
        giaoHang(store.datHang("KH01", gio("SP02", 3, "SP01", 4)));

        List<SalesReport.BanChay> top = baoCao.topSanPhamBanChay(1);

        assertEquals(1, top.size());
        assertEquals("SP02", top.getFirst().productId());
        assertEquals(5, top.getFirst().soLuongBan());   // 2 + 3
    }

    @Test
    void topSanPhamBanChay_nKhongDuong_nemIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> baoCao.topSanPhamBanChay(0));
        assertThrows(IllegalArgumentException.class, () -> baoCao.topSanPhamBanChay(-1));
    }

    // R15 + R11 - sửa giá rồi xoá sản phẩm, báo cáo cũ vẫn nguyên.
    @Test
    void baoCao_khongDoiKhiSanPhamBiSuaGiaHoacBiXoa() {
        giaoHang(store.datHang("KH01", gio("SP01", 2)));
        long doanhThuTruoc = baoCao.doanhThu();

        store.capNhatSanPham("SP01", "Laptop đời mới", 99_000_000L);
        store.xoaSanPham("SP01");   // đơn đã DELIVERED nên R17 không chặn

        assertEquals(doanhThuTruoc, baoCao.doanhThu());
        assertEquals("Laptop", baoCao.topSanPhamBanChay(1).getFirst().tenSanPham());
    }

    private void giaoHang(Order don) {
        store.capNhatTrangThai(don.getId(), OrderStatus.CONFIRMED);
        store.capNhatTrangThai(don.getId(), OrderStatus.SHIPPED);
        store.capNhatTrangThai(don.getId(), OrderStatus.DELIVERED);
    }

    private static Map<String, Integer> gio(Object... capMaVaSoLuong) {
        Map<String, Integer> gioHang = new LinkedHashMap<>();
        for (int i = 0; i < capMaVaSoLuong.length; i += 2) {
            gioHang.put((String) capMaVaSoLuong[i], (Integer) capMaVaSoLuong[i + 1]);
        }
        return gioHang;
    }
}
