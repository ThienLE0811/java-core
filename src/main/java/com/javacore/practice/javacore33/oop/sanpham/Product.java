package com.javacore.practice.javacore33.oop.sanpham;

import java.util.Comparator;
import java.util.Objects;

/**
 * Sản phẩm - một value object bất biến, dùng để học hai cách định nghĩa thứ tự ở bài 11.
 *
 * <p>Thứ tự tự nhiên (natural ordering) của Product là theo giá tăng dần. Đây là thứ tự
 * <em>duy nhất</em> mà class tự mang theo mình: {@link Comparable} chỉ có một method, nên mỗi
 * class cài được đúng một kiểu so sánh. Mọi thứ tự khác - theo tên, theo tồn kho, hay kết hợp
 * nhiều tiêu chí - phải nằm ngoài dưới dạng {@link java.util.Comparator}. Đó là toàn bộ sự
 * khác nhau giữa hai interface của bài này: một cái trả lời "sản phẩm mặc định xếp theo gì",
 * cái kia trả lời "lần sắp xếp này xếp theo gì".
 *
 * <p><strong>Thứ tự này không nhất quán với {@code equals}.</strong> Hai sản phẩm khác tên
 * nhưng cùng giá cho {@code compareTo == 0}, trong khi {@code equals} trả {@code false} (class
 * này không override {@code equals}, nên nó so sánh tham chiếu). Javadoc của {@link Comparable}
 * khuyến nghị nêu rõ điều này vì nó gây hậu quả thật: {@link java.util.TreeSet} và
 * {@link java.util.TreeMap} coi hai phần tử là trùng nhau khi {@code compareTo} trả 0 chứ không
 * hỏi {@code equals}, nên nhét hai sản phẩm cùng giá vào một {@code TreeSet} thì cái thứ hai bị
 * nuốt mất. {@link java.util.List#sort} và {@link java.util.HashSet} không bị ảnh hưởng.
 */
public class Product implements Comparable<Product> {

    /**
     * Xếp theo tên A-Z.
     *
     * <p>Đặt {@code public static final} ngay trong class theo đúng idiom của JDK - xem
     * {@link String#CASE_INSENSITIVE_ORDER}. Chỉ tạo một lần và dùng lại ở mọi nơi, vì
     * {@code Comparator} không giữ trạng thái gì.
     */
    public static final Comparator<Product> THEO_TEN = Comparator.comparing(Product::getTen);

    /**
     * Xếp theo tồn kho tăng dần.
     *
     * <p>{@link Comparator#comparingInt} chứ không phải {@code comparing}: bản
     * {@code comparing} nhận {@code Function<T, U extends Comparable>}, nên {@code int} phải
     * autobox thành {@code Integer} ở mỗi lần so sánh. Bản {@code comparingInt} nhận
     * {@code ToIntFunction} và so thẳng trên kiểu nguyên thuỷ.
     */
    public static final Comparator<Product> THEO_STOCK = Comparator.comparingInt(Product::getStock);

    /**
     * Xếp theo tồn kho tăng dần, cùng tồn kho thì theo tên A-Z.
     *
     * <p>Ghép bằng {@link Comparator#thenComparing(Comparator)} chứ không phải toán tử
     * {@code +}: Java không cho nạp chồng toán tử, {@code +} chỉ làm việc với số và
     * {@code String}. Mọi phép kết hợp object đều là lời gọi method.
     *
     * <p>{@code thenComparing} chỉ được hỏi tới khi tiêu chí trước trả 0 - nên thứ tự viết
     * chính là thứ tự ưu tiên. Và coi chừng {@link Comparator#reversed()}: dán vào cuối chuỗi
     * thì nó đảo <em>cả cụm</em>, không phải riêng tiêu chí cuối.
     */
    public static final Comparator<Product> THEO_STOCK_ROI_TEN = THEO_STOCK.thenComparing(THEO_TEN);

    /**
     * Không bao giờ {@code null} - constructor đã chặn.
     */
    private final String ten;

    /**
     * Luôn là số hữu hạn không âm: không {@code NaN}, không âm. Xem constructor.
     */
    private final double gia;

    /**
     * Luôn {@code >= 0}.
     */
    private final int stock;

    /**
     * Tạo một sản phẩm.
     *
     * @param ten   tên sản phẩm, không được {@code null}
     * @param gia   giá bán, phải {@code >= 0} và không được {@code NaN}
     * @param stock số lượng tồn kho, phải {@code >= 0}
     * @throws NullPointerException     nếu {@code ten} là {@code null}
     * @throws IllegalArgumentException nếu {@code gia} hoặc {@code stock} không hợp lệ
     */
    public Product(String ten, double gia, int stock) {
        Objects.requireNonNull(ten, "Tên không được null");
        // Phải hỏi isNaN() riêng: mọi phép so sánh với NaN đều trả false, nên một mình
        // "gia < 0" không chặn được NaN. Lọt qua đây thì Double.compare() xếp NaN lớn hơn
        // mọi số - kể cả Infinity - và cả danh sách sắp xếp ra kết quả vô nghĩa.
        if (Double.isNaN(gia) || gia < 0) {
            throw new IllegalArgumentException("Giá không được âm hoặc NaN: " + gia);
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Stock không được âm: " + stock);
        }

        this.ten = ten;
        this.gia = gia;
        this.stock = stock;
    }

    /**
     * Tên sản phẩm.
     *
     * @return tên sản phẩm, không bao giờ {@code null}
     */
    public String getTen() {
        return ten;
    }

    /**
     * Giá bán.
     *
     * @return giá bán, luôn là số hữu hạn không âm
     */
    public double getGia() {
        return gia;
    }

    /**
     * Số lượng tồn kho.
     *
     * @return số lượng tồn kho, luôn {@code >= 0}
     */
    public int getStock() {
        return stock;
    }

    /**
     * So sánh theo giá tăng dần - thứ tự tự nhiên của Product.
     *
     * <p>Dùng {@link Double#compare} chứ không trừ tay: {@code (int) (this.gia - khac.gia)} ép
     * 250000.9 - 250000.2 = 0.7 xuống 0, báo sai rằng hai sản phẩm ngang giá. Với kiểu
     * {@code int} thì phép trừ còn tràn số khi hai giá trị cách nhau quá
     * {@link Integer#MAX_VALUE}, cho ra dấu ngược hẳn.
     *
     * @param khac sản phẩm đem ra so sánh, không được {@code null}
     * @return âm nếu this rẻ hơn, 0 nếu bằng giá, dương nếu this đắt hơn
     * @throws NullPointerException nếu {@code khac} là {@code null} - hợp đồng của
     *                              {@link Comparable} đòi hỏi đúng như vậy, nên không cần check tay: truy cập
     *                              {@code khac.gia} đã tự ném ra rồi
     */
    @Override
    public int compareTo(Product khac) {
        return Double.compare(this.gia, khac.gia);
    }

    @Override
    public String toString() {
        return String.format("Product{ten='%s', gia=%.1f, stock=%d}", ten, gia, stock);
    }
}
