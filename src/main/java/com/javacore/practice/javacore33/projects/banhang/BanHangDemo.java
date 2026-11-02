package com.javacore.practice.javacore33.projects.banhang;

import com.javacore.practice.javacore33.oop.donhang.InvalidStatusTransitionException;
import com.javacore.practice.javacore33.oop.donhang.OrderStatus;
import com.javacore.practice.javacore33.projects.banhang.model.Customer;
import com.javacore.practice.javacore33.projects.banhang.model.Order;
import com.javacore.practice.javacore33.projects.banhang.model.OrderLine;
import com.javacore.practice.javacore33.projects.banhang.model.OutOfStockException;
import com.javacore.practice.javacore33.projects.banhang.model.Product;
import com.javacore.practice.javacore33.projects.banhang.report.SalesReport;

import java.util.LinkedHashMap;
import java.util.Map;

// Mục 3 của đề: dữ liệu mẫu + in báo cáo. Chỉ là lớp vỏ hiển thị - mọi nghiệp vụ nằm trong
// Store, ở đây không có luật nào. Chạy: mvn exec:java -Dexec.mainClass=...BanHangDemo
public class BanHangDemo {

    public static void main(String[] args) {
        Store store = napDuLieuMau();
        SalesReport baoCao = new SalesReport(store);

        inTieuDe("Tồn kho ban đầu");
        inTonKho(store);

        inTieuDe("Đơn hàng đi trọn vòng đời");
        Order don1 = store.datHang("KH01", gio("SP01", 1, "SP02", 2));
        System.out.printf("Đặt %s cho KH01, tổng %s%n", don1.getId(), tien(don1.tongTien()));
        giaoHang(store, don1.getId());

        Order don2 = store.datHang("KH02", gio("SP02", 3, "SP03", 1));
        System.out.printf("Đặt %s cho KH02, tổng %s%n", don2.getId(), tien(don2.tongTien()));
        giaoHang(store, don2.getId());

        inTieuDe("Huỷ đơn - tồn kho được hoàn lại");
        Order don3 = store.datHang("KH03", gio("SP01", 2));
        System.out.printf("Đặt %s cho KH03, SP01 còn %d%n",
                don3.getId(), store.timSanPham("SP01").getTonKho());
        store.huyDon(don3.getId());
        System.out.printf("Huỷ %s   -> SP01 còn %d (đã hoàn)%n",
                don3.getId(), store.timSanPham("SP01").getTonKho());

        inTieuDe("Các trường hợp bị từ chối");
        thuVaIn("Huỷ lại đơn đã huỷ", () -> store.huyDon(don3.getId()));
        thuVaIn("Huỷ đơn đã giao", () -> store.huyDon(don1.getId()));
        int tonTruoc = store.timSanPham("SP01").getTonKho();
        thuVaIn("Đặt vượt tồn kho", () -> store.datHang("KH01", gio("SP01", 999)));
        System.out.printf("   SP01 trước %d, sau %d -> kho không đổi%n",
                tonTruoc, store.timSanPham("SP01").getTonKho());
        thuVaIn("Đặt giỏ có dòng thiếu hàng", () -> store.datHang("KH01", gio("SP02", 1, "SP05", 500)));
        System.out.printf("   SP02 vẫn là %d -> không dòng nào bị trừ%n",
                store.timSanPham("SP02").getTonKho());

        inTieuDe("Tồn kho cuối");
        inTonKho(store);

        inTieuDe("Báo cáo");
        System.out.printf("Doanh thu (chỉ đơn đã giao): %s%n", tien(baoCao.doanhThu()));
        System.out.println("Top 3 sản phẩm bán chạy:");
        int hang = 1;
        for (SalesReport.BanChay banChay : baoCao.topSanPhamBanChay(3)) {
            System.out.printf("  %d. %-18s %3d cái  %s%n",
                    hang++, banChay.tenSanPham(), banChay.soLuongBan(), tien(banChay.doanhThu()));
        }

        inTieuDe("Chi tiết đơn đã giao");
        for (Order don : baoCao.donDaGiao()) {
            System.out.printf("%s - %s - %s%n", don.getId(), don.getCustomerId(), tien(don.tongTien()));
            for (OrderLine dong : don.getDongHang()) {
                System.out.printf("    %-18s %3d x %12s = %s%n",
                        dong.tenSanPham(), dong.soLuong(), tien(dong.donGia()), tien(dong.thanhTien()));
            }
        }
    }

    private static Store napDuLieuMau() {
        Store store = new Store();
        store.themSanPham(new Product("SP01", "Laptop Dell", 18_500_000L, 5));
        store.themSanPham(new Product("SP02", "Chuột Logitech", 450_000L, 20));
        store.themSanPham(new Product("SP03", "Bàn phím cơ", 1_200_000L, 8));
        store.themSanPham(new Product("SP04", "Tai nghe Sony", 2_900_000L, 12));
        store.themSanPham(new Product("SP05", "Màn hình 24\"", 3_400_000L, 3));

        store.themKhachHang(new Customer("KH01", "Lê Văn Thiện", "thien@example.com"));
        store.themKhachHang(new Customer("KH02", "Trần Thị Mai", "mai@example.com"));
        store.themKhachHang(new Customer("KH03", "Nguyễn Văn Nam", "nam@example.com"));
        return store;
    }

    // R6: đơn phải đi lần lượt PENDING -> CONFIRMED -> SHIPPED -> DELIVERED, không nhảy cóc.
    private static void giaoHang(Store store, String orderId) {
        store.capNhatTrangThai(orderId, OrderStatus.CONFIRMED);
        store.capNhatTrangThai(orderId, OrderStatus.SHIPPED);
        store.capNhatTrangThai(orderId, OrderStatus.DELIVERED);
        System.out.printf("   %s -> %s%n", orderId, store.timDon(orderId).getStatus().getDescription());
    }

    private static Map<String, Integer> gio(Object... capMaVaSoLuong) {
        Map<String, Integer> gioHang = new LinkedHashMap<>();
        for (int i = 0; i < capMaVaSoLuong.length; i += 2) {
            gioHang.put((String) capMaVaSoLuong[i], (Integer) capMaVaSoLuong[i + 1]);
        }
        return gioHang;
    }

    // Chỉ bắt đúng các exception nghiệp vụ đã khai trong spec - lỗi khác vẫn ném ra ngoài để
    // không giấu bug.
    private static void thuVaIn(String moTa, Runnable thaoTac) {
        try {
            thaoTac.run();
            System.out.printf("%-28s: KHÔNG bị chặn (sai so với spec!)%n", moTa);
        } catch (OutOfStockException | InvalidStatusTransitionException | IllegalArgumentException e) {
            System.out.printf("%-28s: %s - %s%n", moTa, e.getClass().getSimpleName(), e.getMessage());
        }
    }

    private static void inTonKho(Store store) {
        for (Product sanPham : store.danhSachSanPham()) {
            System.out.printf("  %-6s %-18s %14s  tồn %3d%n",
                    sanPham.getId(), sanPham.getTen(), tien(sanPham.getGia()), sanPham.getTonKho());
        }
    }

    private static void inTieuDe(String tieuDe) {
        System.out.printf("%n=== %s ===%n", tieuDe);
    }

    private static String tien(long soTien) {
        return String.format("%,d đ", soTien);
    }
}
