package com.javacore.practice.javacore33.projects.banhang.report;

import com.javacore.practice.javacore33.oop.donhang.OrderStatus;
import com.javacore.practice.javacore33.projects.banhang.Store;
import com.javacore.practice.javacore33.projects.banhang.model.Order;
import com.javacore.practice.javacore33.projects.banhang.model.OrderLine;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

// Chỉ đọc, không đổi state. R15: mọi số liệu lấy từ OrderLine (đã chụp tên + giá), không hỏi
// lại Product - nhờ vậy sản phẩm bị sửa giá hay bị xoá thì báo cáo cũ vẫn đúng.
public class SalesReport {

    private final Store store;

    public SalesReport(Store store) {
        this.store = Objects.requireNonNull(store, "Store không được null");
    }

    public record BanChay(String productId, String tenSanPham, int soLuongBan, long doanhThu) {
    }

    // R13: chỉ tính đơn DELIVERED.
    public long doanhThu() {
        return donDaGiao().stream()
                .mapToLong(Order::tongTien)
                .sum();
    }

    // R14: cùng tập đơn với doanhThu(), xếp theo tổng số lượng bán giảm dần; hoà thì theo
    // doanh thu giảm dần, vẫn hoà thì theo productId tăng dần.
    public List<BanChay> topSanPhamBanChay(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Số sản phẩm cần lấy phải lớn hơn 0: " + n);
        }
        return gomTheoSanPham().values().stream()
                .sorted(thuTuBanChay())
                .limit(n)
                .toList();
    }

    public List<Order> donDaGiao() {
        return store.danhSachDon().stream()
                .filter(don -> don.getStatus() == OrderStatus.DELIVERED)
                .toList();
    }

    private Map<String, BanChay> gomTheoSanPham() {
        Map<String, BanChay> gom = new LinkedHashMap<>();
        for (Order don : donDaGiao()) {
            for (OrderLine dong : don.getDongHang()) {
                BanChay moi = new BanChay(
                        dong.productId(), dong.tenSanPham(), dong.soLuong(), dong.thanhTien());
                // Cộng dồn số lượng và doanh thu; tên lấy theo lần bán gần nhất, vì sản phẩm
                // có thể đã được đổi tên giữa hai đơn (R18).
                gom.merge(dong.productId(), moi, (cu, them) -> new BanChay(
                        cu.productId(),
                        them.tenSanPham(),
                        cu.soLuongBan() + them.soLuongBan(),
                        cu.doanhThu() + them.doanhThu()));
            }
        }
        return gom;
    }

    // Tie-break tới tận productId: thiếu nó thì thứ tự phụ thuộc thứ tự duyệt map và test sẽ
    // lúc xanh lúc đỏ.
    private static Comparator<BanChay> thuTuBanChay() {
        return Comparator.<BanChay>comparingInt(BanChay::soLuongBan).reversed()
                .thenComparing(Comparator.<BanChay>comparingLong(BanChay::doanhThu).reversed())
                .thenComparing(BanChay::productId);
    }
}
