package com.javacore.practice.javacore33.projects.banhang;

import com.javacore.practice.javacore33.oop.donhang.InvalidStatusTransitionException;
import com.javacore.practice.javacore33.oop.donhang.OrderStatus;
import com.javacore.practice.javacore33.projects.banhang.model.Customer;
import com.javacore.practice.javacore33.projects.banhang.model.Order;
import com.javacore.practice.javacore33.projects.banhang.model.OutOfStockException;
import com.javacore.practice.javacore33.projects.banhang.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Bài 33 - test phần lõi nghiệp vụ. Mỗi test bám vào một quy tắc trong SPEC.md và assert cả
// hai vế: exception ĐÚNG loại, VÀ state không bị đổi sai. Chỉ assert exception là test yếu -
// bug "huỷ hai lần hoàn kho hai lần" vẫn lọt qua.
class StoreTest {

    private Store store;

    @BeforeEach
    void chuanBi() {
        store = new Store();
        store.themSanPham(new Product("SP01", "Laptop", 20_000_000L, 5));
        store.themSanPham(new Product("SP02", "Chuột", 500_000L, 10));
        store.themSanPham(new Product("SP03", "Bàn phím", 1_000_000L, 2));
        store.themKhachHang(new Customer("KH01", "Thiện", "thien@example.com"));
    }

    // ----- Đặt hàng -----

    @Test
    void datHang_truDungTonKhoVaChupGiaTaiThoiDiemDat() {
        Order don = store.datHang("KH01", gio("SP01", 2, "SP02", 3));

        assertEquals(3, store.timSanPham("SP01").getTonKho());
        assertEquals(7, store.timSanPham("SP02").getTonKho());
        assertEquals(OrderStatus.PENDING, don.getStatus());          // R6
        assertEquals(2 * 20_000_000L + 3 * 500_000L, don.tongTien());
    }

    // R1 + R3
    @Test
    void datHang_vuotTonKho_nemLoiVaKhoKhongDoi() {
        OutOfStockException loi = assertThrows(OutOfStockException.class,
                () -> store.datHang("KH01", gio("SP01", 6)));

        assertEquals("SP01", loi.getProductId());
        assertEquals(5, store.timSanPham("SP01").getTonKho());
        assertEquals(List.of(), store.danhSachDon());
    }

    // R3 - đây là test quan trọng nhất của phần đặt hàng: tính nguyên tử.
    @Test
    void datHang_motDongThieuHang_khongDongNaoBiTru() {
        assertThrows(OutOfStockException.class,
                () -> store.datHang("KH01", gio("SP01", 1, "SP02", 2, "SP03", 99)));

        assertEquals(5, store.timSanPham("SP01").getTonKho());
        assertEquals(10, store.timSanPham("SP02").getTonKho());
        assertEquals(2, store.timSanPham("SP03").getTonKho());
        assertEquals(List.of(), store.danhSachDon());
    }

    // R4
    @Test
    void datHang_gioRongHoacSoLuongKhongDuong_nemIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> store.datHang("KH01", Map.of()));
        assertThrows(IllegalArgumentException.class,
                () -> store.datHang("KH01", gio("SP01", 0)));
        assertThrows(IllegalArgumentException.class,
                () -> store.datHang("KH01", gio("SP01", -1)));

        assertEquals(5, store.timSanPham("SP01").getTonKho());
        assertEquals(List.of(), store.danhSachDon());
    }

    @Test
    void datHang_khachHoacSanPhamKhongTonTai_nemNoSuchElement() {
        assertThrows(NoSuchElementException.class,
                () -> store.datHang("KH-KHONG-CO", gio("SP01", 1)));
        assertThrows(NoSuchElementException.class,
                () -> store.datHang("KH01", gio("SP-KHONG-CO", 1)));

        assertEquals(5, store.timSanPham("SP01").getTonKho());
    }

    // ----- Huỷ đơn -----

    // R9
    @Test
    void huyDon_hoanLaiDungTonKho() {
        Order don = store.datHang("KH01", gio("SP01", 2, "SP02", 3));
        assertEquals(3, store.timSanPham("SP01").getTonKho());

        store.huyDon(don.getId());

        assertEquals(OrderStatus.CANCELLED, don.getStatus());
        assertEquals(5, store.timSanPham("SP01").getTonKho());
        assertEquals(10, store.timSanPham("SP02").getTonKho());
    }

    // R8 - case bắt buộc của đề. Vế assert tồn kho mới là chỗ bắt bug: nếu huyDon hoàn kho
    // trước rồi mới đổi trạng thái thì exception vẫn ném đúng, nhưng kho đã phình lên 7.
    @Test
    void huyDon_haiLan_lanHaiNemLoiVaKhoKhongPhinh() {
        Order don = store.datHang("KH01", gio("SP01", 2));
        store.huyDon(don.getId());

        assertThrows(InvalidStatusTransitionException.class, () -> store.huyDon(don.getId()));

        assertEquals(5, store.timSanPham("SP01").getTonKho());
        assertEquals(OrderStatus.CANCELLED, don.getStatus());
    }

    // R7
    @Test
    void huyDon_donDaGiao_nemLoiVaKhoKhongDoi() {
        Order don = store.datHang("KH01", gio("SP01", 2));
        giaoHang(don.getId());

        InvalidStatusTransitionException loi = assertThrows(InvalidStatusTransitionException.class,
                () -> store.huyDon(don.getId()));

        assertEquals(OrderStatus.DELIVERED, loi.getFrom());
        assertEquals(OrderStatus.CANCELLED, loi.getTo());
        assertEquals(3, store.timSanPham("SP01").getTonKho());
    }

    // R7 - đơn đã rời kho thì cũng không huỷ được.
    @Test
    void huyDon_donDaGuiHang_nemLoi() {
        Order don = store.datHang("KH01", gio("SP01", 1));
        store.capNhatTrangThai(don.getId(), OrderStatus.CONFIRMED);
        store.capNhatTrangThai(don.getId(), OrderStatus.SHIPPED);

        assertThrows(InvalidStatusTransitionException.class, () -> store.huyDon(don.getId()));
        assertEquals(4, store.timSanPham("SP01").getTonKho());
    }

    // R6 - không nhảy cóc trạng thái.
    @Test
    void capNhatTrangThai_nhayCoc_nemLoi() {
        Order don = store.datHang("KH01", gio("SP02", 1));

        assertThrows(InvalidStatusTransitionException.class,
                () -> store.capNhatTrangThai(don.getId(), OrderStatus.SHIPPED));
        assertEquals(OrderStatus.PENDING, don.getStatus());
    }

    // ----- Danh mục -----

    // R11 + R18 - đổi giá sau khi đặt không làm sai đơn cũ.
    @Test
    void capNhatSanPham_doiGia_khongLamDoiDonDaDat() {
        Order don = store.datHang("KH01", gio("SP01", 1));
        long tongTruoc = don.tongTien();

        store.capNhatSanPham("SP01", "Laptop đời mới", 30_000_000L);

        assertEquals(tongTruoc, don.tongTien());
        assertEquals("Laptop", don.getDongHang().getFirst().tenSanPham());
        assertEquals(30_000_000L, store.timSanPham("SP01").getGia());
    }

    // R17
    @Test
    void xoaSanPham_conDonChuaKetThuc_bichan_huyDonRoiThiXoaDuoc() {
        Order don = store.datHang("KH01", gio("SP01", 1));

        assertThrows(IllegalStateException.class, () -> store.xoaSanPham("SP01"));
        assertEquals("Laptop", store.timSanPham("SP01").getTen());   // vẫn còn trong danh mục

        store.huyDon(don.getId());
        store.xoaSanPham("SP01");

        assertThrows(NoSuchElementException.class, () -> store.timSanPham("SP01"));
    }

    // R16
    @Test
    void themSanPham_trungMa_nemLoi() {
        assertThrows(IllegalStateException.class,
                () -> store.themSanPham(new Product("SP01", "Hàng khác", 1L, 1)));
        assertEquals("Laptop", store.timSanPham("SP01").getTen());
    }

    // ----- Đồng thời -----

    // R20 - case bắt buộc của đề. Lặp nhiều vòng vì race chỉ thỉnh thoảng mới lộ; chạy một
    // lần thì bản KHÔNG đồng bộ cũng có thể xanh.
    @Test
    void haiThreadCungDatMonHangCuoiCung_chiMotThreadThanhCong() throws InterruptedException {
        for (int vong = 0; vong < 500; vong++) {
            Store kho = new Store();
            kho.themSanPham(new Product("SP01", "Món cuối", 1_000L, 1));
            kho.themKhachHang(new Customer("KH01", "A", "a@example.com"));
            kho.themKhachHang(new Customer("KH02", "B", "b@example.com"));

            CountDownLatch xuatPhat = new CountDownLatch(1);
            CountDownLatch veDich = new CountDownLatch(2);
            AtomicInteger thanhCong = new AtomicInteger();
            AtomicInteger hetHang = new AtomicInteger();

            for (String khach : List.of("KH01", "KH02")) {
                Thread.ofVirtual().start(() -> {
                    try {
                        xuatPhat.await();
                        kho.datHang(khach, gio("SP01", 1));
                        thanhCong.incrementAndGet();
                    } catch (OutOfStockException e) {
                        hetHang.incrementAndGet();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        veDich.countDown();
                    }
                });
            }

            xuatPhat.countDown();   // thả cả hai cùng lúc
            veDich.await();

            assertEquals(1, thanhCong.get(), "Vòng " + vong + ": phải đúng 1 thread đặt được");
            assertEquals(1, hetHang.get(), "Vòng " + vong + ": thread còn lại phải hết hàng");
            assertEquals(0, kho.timSanPham("SP01").getTonKho(), "Vòng " + vong + ": tồn kho phải về 0");
            assertEquals(1, kho.danhSachDon().size(), "Vòng " + vong + ": chỉ được tạo 1 đơn");
        }
    }

    // ----- Tiện ích -----

    private void giaoHang(String orderId) {
        store.capNhatTrangThai(orderId, OrderStatus.CONFIRMED);
        store.capNhatTrangThai(orderId, OrderStatus.SHIPPED);
        store.capNhatTrangThai(orderId, OrderStatus.DELIVERED);
    }

    private static Map<String, Integer> gio(Object... capMaVaSoLuong) {
        Map<String, Integer> gioHang = new LinkedHashMap<>();
        for (int i = 0; i < capMaVaSoLuong.length; i += 2) {
            gioHang.put((String) capMaVaSoLuong[i], (Integer) capMaVaSoLuong[i + 1]);
        }
        return gioHang;
    }
}
