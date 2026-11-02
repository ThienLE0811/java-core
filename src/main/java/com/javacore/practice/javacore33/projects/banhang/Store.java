package com.javacore.practice.javacore33.projects.banhang;

import com.javacore.practice.javacore33.oop.donhang.OrderStatus;
import com.javacore.practice.javacore33.projects.banhang.model.Customer;
import com.javacore.practice.javacore33.projects.banhang.model.Order;
import com.javacore.practice.javacore33.projects.banhang.model.OrderLine;
import com.javacore.practice.javacore33.projects.banhang.model.OutOfStockException;
import com.javacore.practice.javacore33.projects.banhang.model.Product;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

// R19: khoá thô - mọi method đổi state đều synchronized trên chính Store.
public class Store {

    private final Map<String, Product> sanPhams = new LinkedHashMap<>();
    private final Map<String, Customer> khachHangs = new LinkedHashMap<>();
    private final Map<String, Order> donHangs = new LinkedHashMap<>();
    private int soThuTuDon = 0;

    // ----- Danh mục sản phẩm -----

    public synchronized void themSanPham(Product sanPham) {
        Objects.requireNonNull(sanPham, "Sản phẩm không được null");
        if (sanPhams.containsKey(sanPham.getId())) {           // R16
            throw new IllegalStateException("Mã sản phẩm đã tồn tại: " + sanPham.getId());
        }
        sanPhams.put(sanPham.getId(), sanPham);
    }

    public synchronized void capNhatSanPham(String productId, String tenMoi, long giaMoi) {
        timSanPham(productId).doiThongTin(tenMoi, giaMoi);     // R18
    }

    public synchronized void nhapKho(String productId, int soLuong) {
        timSanPham(productId).nhapThem(soLuong);               // R5
    }

    // R17: còn đơn chưa kết thúc đang giữ sản phẩm thì không cho xoá, vì đơn đó còn có thể bị
    // huỷ và cần chỗ để hoàn kho.
    public synchronized void xoaSanPham(String productId) {
        Product sanPham = timSanPham(productId);
        boolean dangBiGiu = donHangs.values().stream()
                .filter(don -> don.getStatus() == OrderStatus.PENDING
                        || don.getStatus() == OrderStatus.CONFIRMED)
                .flatMap(don -> don.getDongHang().stream())
                .anyMatch(dong -> dong.productId().equals(productId));
        if (dangBiGiu) {
            throw new IllegalStateException(
                    "Không xoá được sản phẩm '" + productId + "': còn đơn chưa kết thúc đang giữ.");
        }
        sanPhams.remove(sanPham.getId());
    }

    public synchronized Product timSanPham(String productId) {
        Objects.requireNonNull(productId, "Mã sản phẩm không được null");
        Product sanPham = sanPhams.get(productId);
        if (sanPham == null) {
            throw new NoSuchElementException("Không tìm thấy sản phẩm: " + productId);
        }
        return sanPham;
    }

    public synchronized List<Product> danhSachSanPham() {
        return List.copyOf(sanPhams.values());
    }

    // ----- Khách hàng -----

    public synchronized void themKhachHang(Customer khachHang) {
        Objects.requireNonNull(khachHang, "Khách hàng không được null");
        if (khachHangs.containsKey(khachHang.id())) {          // R16
            throw new IllegalStateException("Mã khách hàng đã tồn tại: " + khachHang.id());
        }
        khachHangs.put(khachHang.id(), khachHang);
    }

    public synchronized Customer timKhachHang(String customerId) {
        Objects.requireNonNull(customerId, "Mã khách hàng không được null");
        Customer khachHang = khachHangs.get(customerId);
        if (khachHang == null) {
            throw new NoSuchElementException("Không tìm thấy khách hàng: " + customerId);
        }
        return khachHang;
    }

    // ----- Đơn hàng (phần lõi - mục 2 của đề, tự viết) -----

    // Trừ tồn kho và tạo đơn mới ở trạng thái PENDING.
    //
    // Phải thoả:
    //   R3  - nguyên tử: kiểm đủ tồn cho TOÀN BỘ giỏ trước, tất cả đủ mới trừ. Giỏ 3 dòng mà
    //         dòng thứ 3 thiếu hàng thì không dòng nào bị trừ và không đơn nào được tạo.
    //   R4  - giỏ rỗng hoặc có soLuong <= 0 thì từ chối.
    //   R11 - dòng hàng chụp tên + giá tại thời điểm đặt: dùng OrderLine.chupTu(sanPham, n).
    //
    // Dùng sinhMaDon() cho mã đơn và bayGio() cho thời gian đặt, rồi put vào donHangs.
    public synchronized Order datHang(String customerId, Map<String, Integer> gioHang) {
        Customer khach = timKhachHang(customerId);
        Objects.requireNonNull(gioHang, "Giỏ hàng không được null");
        if (gioHang.isEmpty()) {                               // R4
            throw new IllegalArgumentException("Giỏ hàng không được rỗng");
        }

        // Lượt 1 - chỉ kiểm tra, chưa đụng vào state. Mọi exception của R4/R1 đều ném ở đây,
        // lúc kho còn nguyên vẹn (R3).
        Map<Product, Integer> canTru = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> muc : gioHang.entrySet()) {
            Product sanPham = timSanPham(muc.getKey());
            Integer soLuong = muc.getValue();
            Objects.requireNonNull(soLuong, "Số lượng không được null: " + muc.getKey());
            if (soLuong <= 0) {                                // R4
                throw new IllegalArgumentException(
                        "Số lượng phải lớn hơn 0 (" + muc.getKey() + "): " + soLuong);
            }
            if (!sanPham.duTon(soLuong)) {                     // R1
                throw new OutOfStockException(
                        sanPham.getId(), sanPham.getTen(), sanPham.getTonKho(), soLuong);
            }
            canTru.put(sanPham, soLuong);
        }

        // Lượt 2 - tới đây chắc chắn cả giỏ đều đủ hàng, mới trừ kho (R3).
        List<OrderLine> dongHang = new ArrayList<>();
        canTru.forEach((sanPham, soLuong) -> {
            dongHang.add(OrderLine.chupTu(sanPham, soLuong));  // R11: chụp trước khi kho đổi
            sanPham.giamTon(soLuong);
        });

        Order don = new Order(sinhMaDon(), khach.id(), dongHang, bayGio());
        donHangs.put(don.getId(), don);
        return don;
    }

    // Chuyển đơn sang CANCELLED và hoàn lại tồn kho.
    //
    // Phải thoả:
    //   R7  - chỉ huỷ được PENDING/CONFIRMED. Hỏi OrderStatus.canTransitionTo, đừng tự viết
    //         lại luật; Order.updateStatus đã ném InvalidStatusTransitionException sẵn.
    //   R8  - huỷ đơn đã CANCELLED là lỗi, không im lặng bỏ qua.
    //   R9  - hoàn đúng số lượng của từng dòng, đúng một lần.
    //   R10 - đổi trạng thái và hoàn kho nằm trong cùng vùng đồng bộ; đổi trạng thái TRƯỚC
    //         rồi mới hoàn kho, để đơn đã huỷ không bao giờ hoàn kho lần hai.
    public synchronized void huyDon(String orderId) {
        Order don = timDon(orderId);

        // Đổi trạng thái TRƯỚC: nếu đơn đã CANCELLED/SHIPPED/DELIVERED thì ném ngay tại đây,
        // chưa đồng nào được hoàn (R7, R8). Làm ngược lại thì huỷ lần hai vẫn kịp hoàn kho
        // thêm một lượt trước khi lỗi được ném ra.
        don.updateStatus(OrderStatus.CANCELLED);

        for (OrderLine dong : don.getDongHang()) {             // R9
            // R17 bảo đảm sản phẩm của đơn PENDING/CONFIRMED chưa bị xoá, nên timSanPham()
            // không ném ở đây. Nếu có ngày nó ném thật thì đó là bug của R17 và nên lộ ra.
            timSanPham(dong.productId()).hoanTon(dong.soLuong());
        }
    }

    public synchronized void capNhatTrangThai(String orderId, OrderStatus next) {
        timDon(orderId).updateStatus(next);
    }

    public synchronized Order timDon(String orderId) {
        Objects.requireNonNull(orderId, "Mã đơn không được null");
        Order don = donHangs.get(orderId);
        if (don == null) {
            throw new NoSuchElementException("Không tìm thấy đơn hàng: " + orderId);
        }
        return don;
    }

    public synchronized List<Order> danhSachDon() {
        return List.copyOf(donHangs.values());
    }

    // Chỉ gọi từ trong vùng synchronized nên một biến đếm thường là đủ, không cần AtomicInteger.
    private String sinhMaDon() {
        return String.format("DH-%04d", ++soThuTuDon);
    }

    private LocalDateTime bayGio() {
        return LocalDateTime.now();
    }
}
