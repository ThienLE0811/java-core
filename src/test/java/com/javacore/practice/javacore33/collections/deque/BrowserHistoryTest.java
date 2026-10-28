package com.javacore.practice.javacore33.collections.deque;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test toàn diện cho {@link BrowserHistory} bài 23.
 */
class BrowserHistoryTest {

    private BrowserHistory browser;

    @BeforeEach
    void setUp() {
        browser = new BrowserHistory("google.com");
    }

    @Nested
    @DisplayName("1. Ràng buộc dữ liệu (Validation)")
    class ValidationTest {

        @Test
        void khoiTaoVoiUrlNullHoacRongSeNemException() {
            assertThrows(IllegalArgumentException.class, () -> new BrowserHistory(null));
            assertThrows(IllegalArgumentException.class, () -> new BrowserHistory(""));
            assertThrows(IllegalArgumentException.class, () -> new BrowserHistory("   "));
        }

        @Test
        void visitUrlNullHoacRongSeNemException() {
            assertThrows(IllegalArgumentException.class, () -> browser.visit(null));
            assertThrows(IllegalArgumentException.class, () -> browser.visit(""));
            assertThrows(IllegalArgumentException.class, () -> browser.visit("   "));
        }
    }

    @Nested
    @DisplayName("2. Trạng thái khởi tạo ban đầu")
    class InitialStateTest {

        @Test
        void banDauDungOTrangChuVaHaiNutDeuTat() {
            assertEquals("google.com", browser.getCurrentPage());
            assertFalse(browser.canBack());
            assertFalse(browser.canForward());
            assertTrue(browser.getBackHistory().isEmpty());
            assertTrue(browser.getForwardHistory().isEmpty());
        }
    }

    @Nested
    @DisplayName("3. Điều hướng bình thường (Visit, Back, Forward)")
    class NavigationTest {

        @Test
        void visitCacTrangVaLuiTienDungThuTu() {
            browser.visit("dev.java");
            browser.visit("github.com");

            assertEquals("github.com", browser.getCurrentPage());
            assertTrue(browser.canBack());
            assertFalse(browser.canForward());
            assertEquals(List.of("dev.java", "google.com"), browser.getBackHistory());

            // Back về dev.java
            assertEquals(Optional.of("dev.java"), browser.back());
            assertEquals("dev.java", browser.getCurrentPage());
            assertTrue(browser.canBack());
            assertTrue(browser.canForward());
            assertEquals(List.of("github.com"), browser.getForwardHistory());

            // Back tiếp về google.com
            assertEquals(Optional.of("google.com"), browser.back());
            assertEquals("google.com", browser.getCurrentPage());
            assertFalse(browser.canBack());
            assertTrue(browser.canForward());

            // Forward lại lên dev.java
            assertEquals(Optional.of("dev.java"), browser.forward());
            assertEquals("dev.java", browser.getCurrentPage());

            // Forward tiếp lên github.com
            assertEquals(Optional.of("github.com"), browser.forward());
            assertEquals("github.com", browser.getCurrentPage());
            assertFalse(browser.canForward());
        }
    }

    @Nested
    @DisplayName("4. Các Edge Cases cốt lõi theo yêu cầu đề bài")
    class EdgeCasesTest {

        @Test
        void backKhiDangOTrangDauTienTraVeEmptyVaKhongNemLoi() {
            // Đang ở google.com, canBack = false
            assertFalse(browser.canBack());

            Optional<String> result = browser.back();

            assertTrue(result.isEmpty());
            assertEquals("google.com", browser.getCurrentPage());
            assertFalse(browser.canBack());
            assertFalse(browser.canForward());
        }

        @Test
        void forwardKhiDangOTrangMoiNhatTraVeEmptyVaKhongNemLoi() {
            browser.visit("dev.java");
            assertFalse(browser.canForward());

            Optional<String> result = browser.forward();

            assertTrue(result.isEmpty());
            assertEquals("dev.java", browser.getCurrentPage());
        }

        @Test
        void visitTrangMoiSauKhiDaBackNhieuLanPhaiXoaSachForwardHistory() {
            // Chuỗi duyệt: A -> B -> C -> D
            browser.visit("facebook.com");
            browser.visit("reddit.com");
            browser.visit("twitter.com");

            // Back 2 lần về facebook.com: Forward stack lúc này có [reddit.com, twitter.com]
            browser.back(); // về reddit.com
            browser.back(); // về facebook.com
            assertEquals("facebook.com", browser.getCurrentPage());
            assertTrue(browser.canForward());
            assertEquals(2, browser.getForwardHistory().size());

            // ĐIỂM MẤU CHỐT: Từ facebook.com, người dùng bấm vào youtube.com
            browser.visit("youtube.com");

            // Sau thao tác này:
            // 1. Trang hiện tại phải là youtube.com
            assertEquals("youtube.com", browser.getCurrentPage());
            // 2. Toàn bộ forward history CŨ phải bị xóa sạch! Nút Forward bị tắt
            assertFalse(browser.canForward());
            assertTrue(browser.getForwardHistory().isEmpty());
            // 3. Back stack bây giờ phải là [facebook.com, google.com]
            assertTrue(browser.canBack());
            assertEquals(List.of("facebook.com", "google.com"), browser.getBackHistory());
        }
    }

    @Nested
    @DisplayName("5. In lịch sử điều hướng")
    class PrintHistoryTest {

        @Test
        void printHistoryChuaDayDuThongTin() {
            browser.visit("dev.java");
            String history = browser.printHistory();

            assertTrue(history.contains("google.com"));
            assertTrue(history.contains("dev.java"));
        }
    }
}
