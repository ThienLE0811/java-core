package com.javacore.practice.javacore33.oop.sanpham;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Demo bài 11 - chạy thẳng file này để nhìn năm thứ tự khác nhau trên cùng một dữ liệu.
 *
 * <p>Dữ liệu cố ý có hai cặp trùng: Chuột và Bàn phím cùng giá 250.000, Bàn phím và Màn hình
 * cùng tồn kho 3. Nếu mọi giá trị đều phân biệt thì
 * {@link Comparator#thenComparing(Comparator)} không bao giờ được gọi tới, và ta không có cách
 * nào biết nó chạy đúng hay không.
 *
 * <p>Mỗi lần demo đều dựng lại danh sách từ đầu bằng {@link #taoDanhSach()} thay vì sort đi
 * sort lại một list. Lý do: {@link List#sort(Comparator)} sắp xếp <em>tại chỗ</em>, và thuật
 * toán của Java là sort <em>ổn định</em> - các phần tử mà comparator coi là bằng nhau giữ
 * nguyên thứ tự tương đối có sẵn. Nên nếu dùng chung một list, kết quả sắp theo tồn kho sẽ phụ
 * thuộc vào lần sắp xếp ngay trước đó, và đọc output sẽ không hiểu vì sao.
 */
public final class SapXepSanPhamDemo {

    /** Class tiện ích chỉ chứa {@code main}, không ai cần tạo instance. */
    private SapXepSanPhamDemo() {
    }

    /**
     * Điểm chạy của demo.
     *
     * @param args không dùng
     */
    public static void main(String[] args) {
        in("Gốc (chưa sắp xếp)", taoDanhSach());

        // null nghĩa là "dùng thứ tự tự nhiên", tức compareTo của Product - theo giá tăng dần.
        // Collections.sort(ds) làm đúng việc này và đọc rõ hơn; viết sort(null) ở đây chỉ để
        // thấy hai cách là một.
        List<Product> theoGia = taoDanhSach();
        theoGia.sort(null);
        in("1. Theo giá tăng dần (Comparable)", theoGia);

        List<Product> theoTen = taoDanhSach();
        theoTen.sort(Product.THEO_TEN);
        in("2. Theo tên A-Z", theoTen);

        List<Product> theoStockGiam = taoDanhSach();
        theoStockGiam.sort(Product.THEO_STOCK.reversed());
        in("3. Theo tồn kho giảm dần", theoStockGiam);

        List<Product> stockRoiTen = taoDanhSach();
        stockRoiTen.sort(Product.THEO_STOCK_ROI_TEN);
        in("4. Tồn kho tăng dần, cùng tồn kho thì tên A-Z", stockRoiTen);

        // BẪY: .reversed() dán vào cuối chuỗi sẽ đảo CẢ CỤM, không phải riêng tiêu chí cuối.
        // So (5) với (4): tồn kho đảo thành giảm dần (đúng ý), nhưng tên cũng đảo thành Z-A
        // (ngoài ý muốn). Lý do: reversed() trả về một comparator mới bọc toàn bộ chuỗi phía
        // trước nó rồi đổi dấu kết quả cuối cùng.
        List<Product> bay = taoDanhSach();
        bay.sort(Product.THEO_STOCK.thenComparing(Product::getTen).reversed());
        in("5. BẪY - .reversed() ở cuối chuỗi đảo cả tồn kho lẫn tên", bay);

        // Cách đúng khi chỉ muốn đảo một tiêu chí: đảo ngay tại tiêu chí đó, trước khi nối
        // tiêu chí sau. So (6) với (5) sẽ thấy tên quay lại A-Z.
        List<Product> dung = taoDanhSach();
        dung.sort(Product.THEO_STOCK.reversed().thenComparing(Product::getTen));
        in("6. Đúng - tồn kho giảm dần, cùng tồn kho thì tên A-Z", dung);
    }

    /**
     * Dựng danh sách mẫu, luôn cùng thứ tự ban đầu.
     *
     * <p>Bọc {@link ArrayList} quanh {@link List#of} vì {@code List.of} trả về list bất biến -
     * gọi {@code sort} trên nó ném {@link UnsupportedOperationException} ngay lúc chạy.
     *
     * @return danh sách mới, sắp xếp được
     */
    private static List<Product> taoDanhSach() {
        return new ArrayList<>(List.of(
                new Product("Chuột", 250_000, 10),
                new Product("Bàn phím", 250_000, 3),
                new Product("Màn hình", 3_500_000, 3),
                new Product("Tai nghe", 800_000, 10)));
    }

    /**
     * In một danh sách kèm nhãn, mỗi sản phẩm một dòng cho dễ so sánh giữa các lần.
     *
     * @param nhan tiêu đề của lần sắp xếp này
     * @param ds   danh sách đã sắp xếp
     */
    private static void in(String nhan, List<Product> ds) {
        System.out.println(nhan);
        for (Product p : ds) {
            System.out.println("    " + p);
        }
        System.out.println();
    }
}
