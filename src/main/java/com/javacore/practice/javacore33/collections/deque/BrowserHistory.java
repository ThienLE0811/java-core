package com.javacore.practice.javacore33.collections.deque;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Bài 23 - Mô phỏng lịch sử điều hướng của trình duyệt web bằng {@link ArrayDeque}.
 *
 * <p>Sử dụng 2 ngăn xếp (Double Stack pattern):
 * <ul>
 *   <li>{@code backStack}: lưu lịch sử các trang trước đó (LIFO).</li>
 *   <li>{@code forwardStack}: lưu lịch sử các trang phía trước khi bấm back (LIFO).</li>
 * </ul>
 *
 * <p>Tuân thủ chuẩn thiết kế Java hiện đại: dùng {@link ArrayDeque} làm Stack thay vì
 * class cũ kỹ {@link java.util.Stack} từ Java 1.0.
 */
public class BrowserHistory {

    private final Deque<String> backStack;
    private final Deque<String> forwardStack;
    private String currentPage;

    /**
     * Khởi tạo trình duyệt với trang chủ ban đầu.
     *
     * @param homePage URL trang chủ mở đầu (không được null hoặc rỗng)
     */
    public BrowserHistory(String homePage) {
        validateUrl(homePage);
        this.backStack = new ArrayDeque<>();
        this.forwardStack = new ArrayDeque<>();
        this.currentPage = homePage;
    }

    /**
     * Điều hướng tới một trang web mới.
     *
     * <p><b>Quy tắc quan trọng:</b> Trang hiện tại sẽ được đẩy vào {@code backStack}.
     * Đồng thời, <em>toàn bộ lịch sử tiến lên phía trước ({@code forwardStack}) sẽ bị xóa sạch</em>
     * (giống hệt nút Forward của Chrome bị vô hiệu hóa khi bạn mở link mới).
     *
     * @param url địa chỉ trang web mới
     * @throws IllegalArgumentException nếu url null hoặc chỉ chứa khoảng trắng
     */
    public void visit(String url) {
        validateUrl(url);

        // Đẩy trang hiện tại vào ngăn xếp lùi
        backStack.push(currentPage);

        // Chuyển sang trang mới
        currentPage = url;

        // Xóa sạch lịch sử tiến
        forwardStack.clear();
    }

    /**
     * Quay lại trang trước đó (Back).
     *
     * <p><b>Edge case:</b> Nếu đang ở trang đầu tiên (không còn gì trong {@code backStack}),
     * phương thức giữ nguyên trang hiện tại và trả về {@link Optional#empty()}.
     *
     * @return {@link Optional} chứa URL trang vừa quay lại, hoặc rỗng nếu không thể back
     */
    public Optional<String> back() {
        if (!canBack()) {
            return Optional.empty();
        }

        // Đẩy trang hiện tại vào forwardStack để có thể forward lại
        forwardStack.push(currentPage);

        // Rút trang gần nhất từ backStack ra làm trang hiện tại
        currentPage = backStack.pop();

        return Optional.of(currentPage);
    }

    /**
     * Tiến lên trang phía trước (Forward).
     *
     * <p><b>Edge case:</b> Nếu đang ở trang mới nhất (không còn gì trong {@code forwardStack}),
     * phương thức giữ nguyên trang hiện tại và trả về {@link Optional#empty()}.
     *
     * @return {@link Optional} chứa URL trang vừa tiến tới, hoặc rỗng nếu không thể forward
     */
    public Optional<String> forward() {
        if (!canForward()) {
            return Optional.empty();
        }

        // Đẩy trang hiện tại vào backStack
        backStack.push(currentPage);

        // Rút trang từ forwardStack ra làm trang hiện tại
        currentPage = forwardStack.pop();

        return Optional.of(currentPage);
    }

    /**
     * Lấy URL trang hiện tại đang hiển thị.
     */
    public String getCurrentPage() {
        return currentPage;
    }

    /**
     * Kiểm tra có thể bấm nút Back được không.
     */
    public boolean canBack() {
        return !backStack.isEmpty();
    }

    /**
     * Kiểm tra có thể bấm nút Forward được không.
     */
    public boolean canForward() {
        return !forwardStack.isEmpty();
    }

    /**
     * Lấy danh sách lịch sử Back (thứ tự từ trang vừa rời đi tới trang xa nhất).
     */
    public List<String> getBackHistory() {
        return List.copyOf(backStack);
    }

    /**
     * Lấy danh sách lịch sử Forward (thứ tự từ trang kế tiếp tới trang xa nhất).
     */
    public List<String> getForwardHistory() {
        return List.copyOf(forwardStack);
    }

    /**
     * In trạng thái điều hướng trực quan của trình duyệt.
     */
    public String printHistory() {
        return String.format("Back: %s | Current: [%s] | Forward: %s",
                backStack, currentPage, forwardStack);
    }

    private static void validateUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL không được null hoặc để trống");
        }
    }
}
