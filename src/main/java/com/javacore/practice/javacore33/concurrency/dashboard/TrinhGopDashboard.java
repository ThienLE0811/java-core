package com.javacore.practice.javacore33.concurrency.dashboard;

import com.javacore.practice.javacore33.concurrency.dashboard.DichVuGiaLap.DonHang;
import com.javacore.practice.javacore33.concurrency.dashboard.DichVuGiaLap.GoiY;
import com.javacore.practice.javacore33.concurrency.dashboard.DichVuGiaLap.NguoiDung;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class TrinhGopDashboard {

    private TrinhGopDashboard() {
    }

    // thenCombine chỉ gộp được HAI stage, nên gộp ba phải đi qua một bậc trung
    // gian. Chính chỗ vướng này là lý do allOf tồn tại.
    private record HaiPhan(NguoiDung nguoiDung, List<DonHang> donHang) {
    }

    /**
     * Cách 1 - thenCombine lồng nhau. Tương đương Promise.all cho đúng 2 phần tử,
     * gộp 3 thì phải lồng.
     */
    public static CompletableFuture<Dashboard> gopBangThenCombine(DichVuGiaLap dichVu, Duration hetGioGoiY) {
        Objects.requireNonNull(dichVu, "dichVu không được null");

        // BA DÒNG NÀY LÀ ĐIỂM MẤU CHỐT: gọi xong là task đã nằm trên executor và
        // chạy rồi. Mọi thứ phía dưới chỉ là khai báo "xong thì làm gì tiếp".
        // Chen bất kỳ join()/get() nào vào giữa ba dòng này là mất song song.
        CompletableFuture<NguoiDung> nguoiDung = dichVu.getUser();
        CompletableFuture<List<DonHang>> donHang = dichVu.getOrders();
        CompletableFuture<List<GoiY>> goiY = goiYAnToan(dichVu.getRecommendations(), hetGioGoiY);

        return nguoiDung
                .thenCombine(donHang, HaiPhan::new)
                .thenCombine(goiY, (haiPhan, ds) -> new Dashboard(haiPhan.nguoiDung(), haiPhan.donHang(), ds));
    }

    /**
     * Cách 2 - allOf. Giống Promise.all hơn, nhưng allOf trả CompletableFuture&lt;Void&gt;,
     * nó chỉ báo "xong hết" chứ không gom kết quả. Phải tự nhặt lại.
     */
    public static CompletableFuture<Dashboard> gopBangAllOf(DichVuGiaLap dichVu, Duration hetGioGoiY) {
        Objects.requireNonNull(dichVu, "dichVu không được null");

        CompletableFuture<NguoiDung> nguoiDung = dichVu.getUser();
        CompletableFuture<List<DonHang>> donHang = dichVu.getOrders();
        CompletableFuture<List<GoiY>> goiY = goiYAnToan(dichVu.getRecommendations(), hetGioGoiY);

        return CompletableFuture.allOf(nguoiDung, donHang, goiY)
                // join() ở đây KHÔNG chặn: allOf đã hoàn thành nghĩa là cả ba đã
                // hoàn thành. Đây là chỗ duy nhất join() trong thân chuỗi là đúng.
                .thenApply(khongDung -> new Dashboard(nguoiDung.join(), donHang.join(), goiY.join()));
    }

    /**
     * Cách 3 - thenCompose, cho trường hợp dữ liệu THẬT SỰ phụ thuộc nhau:
     * phải có userId rồi mới truy được đơn hàng của người đó.
     *
     * <p>Chậm hơn hai cách trên và điều đó là đúng, không phải bug. Khi review
     * đừng nhầm phụ thuộc dữ liệu thật với lỗi join() sớm.
     */
    public static CompletableFuture<Dashboard> gopKieuPhuThuoc(DichVuGiaLap dichVu, Duration hetGioGoiY) {
        Objects.requireNonNull(dichVu, "dichVu không được null");

        // goiY không phụ thuộc user nên vẫn khởi động ngay từ đầu, chạy chen vào
        // thời gian chờ của chuỗi user -> orders.
        CompletableFuture<List<GoiY>> goiY = goiYAnToan(dichVu.getRecommendations(), hetGioGoiY);

        CompletableFuture<HaiPhan> nguoiDungVaDonHang = dichVu.getUser()
                // thenCompose chứ không phải thenApply: hàm bên trong trả về một
                // CompletableFuture. Dùng thenApply ở đây sẽ ra
                // CompletableFuture<CompletableFuture<List<DonHang>>> - đúng cái
                // bẫy .then() trả promise trong JS, chỉ khác là JS tự phẳng hoá
                // còn Java thì không.
                .thenCompose(nd -> dichVu.getOrdersTheoNguoiDung(nd.id())
                        .thenApply(ds -> new HaiPhan(nd, ds)));

        return nguoiDungVaDonHang
                .thenCombine(goiY, (haiPhan, ds) -> new Dashboard(haiPhan.nguoiDung(), haiPhan.donHang(), ds));
    }

    /**
     * Bản minh hoạ CÁCH SAI - chỉ dùng cho Demo, đừng bắt chước.
     *
     * <p>Đọc lướt thì thấy "có CompletableFuture, chắc là song song". Thực tế
     * join() ở dòng đầu chặn cho tới khi getUser xong, lúc đó getOrders mới được
     * gọi. Ba cái 500ms nối đuôi thành 1500ms.
     */
    public static Dashboard gopKieuSai(DichVuGiaLap dichVu) {
        Objects.requireNonNull(dichVu, "dichVu không được null");

        NguoiDung nguoiDung = dichVu.getUser().join();
        List<DonHang> donHang = dichVu.getOrders().join();
        List<GoiY> goiY = dichVu.getRecommendations().join();

        return new Dashboard(nguoiDung, donHang, goiY);
    }

    /**
     * Đối chứng cho {@link #gopKieuSai}: vẫn ba lời gọi join(), vẫn chặn thread
     * hiện tại, nhưng nhanh bằng allOf.
     *
     * <p>Khác biệt duy nhất là ba dòng tạo future đứng TRƯỚC mọi lời join().
     * Khi review, câu hỏi đúng không phải "có join() không" mà là "lúc join()
     * chặn, còn task nào chưa kịp khởi động không".
     */
    public static Dashboard gopJoinMuon(DichVuGiaLap dichVu) {
        Objects.requireNonNull(dichVu, "dichVu không được null");

        CompletableFuture<NguoiDung> nguoiDung = dichVu.getUser();
        CompletableFuture<List<DonHang>> donHang = dichVu.getOrders();
        CompletableFuture<List<GoiY>> goiY = dichVu.getRecommendations();

        // join() đầu tiên chặn ~500ms, hai cái sau trả về tức thì vì task của
        // chúng đã chạy song song suốt quãng chờ đó.
        return new Dashboard(nguoiDung.join(), donHang.join(), goiY.join());
    }

    /**
     * Phần 2 của đề: hỏng thì trả rỗng, chậm quá thì cũng trả rỗng.
     *
     * <p>Đặt ở đây - trên từng nhánh, TRƯỚC khi gộp - nên lỗi của gợi ý chỉ làm
     * mất gợi ý. Đẩy exceptionally ra sau thenCombine thì một nhánh hỏng kéo sập
     * cả dashboard.
     */
    public static CompletableFuture<List<GoiY>> goiYAnToan(CompletableFuture<List<GoiY>> goc, Duration hetGio) {
        Objects.requireNonNull(goc, "goc không được null");
        Objects.requireNonNull(hetGio, "hetGio không được null");

        return goc
                // orTimeout chứ không phải completeOnTimeout: cả hai đều trả
                // danh sách rỗng, nhưng orTimeout đẩy quá hạn thành TimeoutException
                // nên nó đi qua đúng một cửa exceptionally cùng với lỗi thật.
                // completeOnTimeout hoàn thành BÌNH THƯỜNG bằng List.of(), nghĩa là
                // exceptionally không chạy và quá hạn trôi qua không một dòng log.
                .orTimeout(hetGio.toMillis(), TimeUnit.MILLISECONDS)
                .exceptionally(loi -> {
                    Throwable that = nguyenNhanGoc(loi);
                    String nguyenNhan = that instanceof TimeoutException
                            ? "quá hạn " + hetGio.toMillis() + " ms"
                            : String.valueOf(that);

                    // Trả rỗng là yêu cầu của đề, nhưng trả rỗng mà IM LẶNG thì
                    // chính là bug "thiếu dữ liệu không ai biết" của bài 35.
                    System.err.println("  [gợi ý] " + nguyenNhan + ", dùng danh sách rỗng");
                    return List.of();
                });
    }

    /**
     * Bản minh hoạ CÁCH SAI - chỉ dùng cho Demo, đừng bắt chước.
     *
     * <p>Nhìn thì y hệt {@link #goiYAnToan}: hỏng trả rỗng, quá hạn trả rỗng.
     * Nhưng nhánh quá hạn hoàn thành BÌNH THƯỜNG nên exceptionally không chạy -
     * dashboard thiếu gợi ý mà log sạch bong. Đúng kiểu bug chỉ lộ ra khi có
     * người dùng hỏi "sao hôm nay không thấy gợi ý".
     */
    public static CompletableFuture<List<GoiY>> goiYImLangKhiHetGio(
            CompletableFuture<List<GoiY>> goc, Duration hetGio) {

        return goc
                .completeOnTimeout(List.of(), hetGio.toMillis(), TimeUnit.MILLISECONDS)
                .exceptionally(loi -> {
                    System.err.println("  [gợi ý] hỏng: " + nguyenNhanGoc(loi));
                    return List.of();
                });
    }

    /**
     * Bản orTimeout để đối chiếu: KHÔNG có giá trị thay thế, hết giờ là Future
     * hỏng với TimeoutException. Ai join() sẽ ăn CompletionException.
     */
    public static CompletableFuture<List<GoiY>> goiYOrTimeout(DichVuGiaLap dichVu, Duration hetGio) {
        return dichVu.getRecommendations().orTimeout(hetGio.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * CompletionException chỉ là lớp vỏ, y hệt ExecutionException ở bài 35.
     * Log thẳng nó ra là log một stack trace vô nghĩa.
     */
    public static Throwable nguyenNhanGoc(Throwable loi) {
        return loi instanceof CompletionException && loi.getCause() != null ? loi.getCause() : loi;
    }
}
