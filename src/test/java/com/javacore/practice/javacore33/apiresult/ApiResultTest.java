package com.javacore.practice.javacore33.apiresult;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiResultTest {

    private static final User THIEN = new User(1, "Thiên", "thienlv@paraline.io");

    // --- sealed: tập subtype đóng -----------------------------------------

    @Test
    void apiResultLaSealed() {
        assertTrue(ApiResult.class.isSealed());
    }

    @Test
    void permitsDungBaNhanhVaKhongHonTheo() {
        List<String> ten = Arrays.stream(ApiResult.class.getPermittedSubclasses())
                .map(Class::getSimpleName)
                .toList();

        assertEquals(List.of("Success", "Failure", "Loading"), ten);
    }

    @Test
    void moiNhanhDeuFinal() {
        // Mắt xích giữ exhaustiveness: subtype final -> không ai kế thừa tiếp ->
        // tập giá trị runtime hữu hạn -> switch phủ đủ 3 nhánh là phủ hết.
        for (Class<?> nhanh : ApiResult.class.getPermittedSubclasses()) {
            assertTrue(Modifier.isFinal(nhanh.getModifiers()),
                    nhanh.getSimpleName() + " phải final");
            assertTrue(nhanh.isRecord(), nhanh.getSimpleName() + " phải là record");
        }
    }

    // --- render: mỗi nhánh một kết quả ------------------------------------

    @Test
    void renderDuBaNhanh() {
        assertEquals("OK   | Thiên", ApiResultDemo.render(new Success<>(THIEN)));
        assertEquals("LOI  | 404 không tìm thấy",
                ApiResultDemo.render(new Failure<>("không tìm thấy", 404)));
        assertEquals("LOAD | đang tải...", ApiResultDemo.render(new Loading<>()));
    }

    @Test
    void renderGonHonChoKetQuaGiongRenderDayDu() {
        // Success(var u) và Success<User>(User u) chỉ khác cách viết, không khác hành vi.
        List<ApiResult<User>> mau = List.of(
                new Success<>(THIEN), new Failure<>("lỗi", 500), new Loading<>());

        for (ApiResult<User> r : mau) {
            assertEquals(ApiResultDemo.render(r), ApiResultDemo.renderGonHon(r));
        }
    }

    @Test
    void renderChiTietTachToiTungFieldCuaUser() {
        assertEquals("OK   | #1 Thiên <thienlv@paraline.io>",
                ApiResultDemo.renderChiTiet(new Success<>(THIEN)));
    }

    // --- guard when --------------------------------------------------------

    // Nháy đơn bắt buộc: @CsvSource cắt cột tại dấu phẩy, mà kỳ vọng có phẩy bên trong.
    @ParameterizedTest
    @CsvSource({
            "503, '5xx  | 503 sập (lỗi server, thử lại sau)'",
            "500, '5xx  | 500 sập (lỗi server, thử lại sau)'",
            "404, '404  | sập (không có, đừng thử lại)'",
            "401, '4xx  | 401 sập'",
            "400, '4xx  | 400 sập'",
    })
    void guardChonNhanhTheoGiaTriCuaCode(int code, String mongDoi) {
        assertEquals(mongDoi, ApiResultDemo.renderCoGuard(new Failure<>("sập", code)));
    }

    @Test
    void nhanhGuardDungTruocNenThuTuQuyetDinhKetQua() {
        // 404 khớp CẢ hai nhánh "when code == 404" và nhánh Failure không guard.
        // Nhánh đứng trước thắng - đây là lý do thứ tự case không hoán đổi tuỳ tiện được.
        assertTrue(ApiResultDemo.renderCoGuard(new Failure<>("x", 404)).startsWith("404"));
    }

    // --- null --------------------------------------------------------------

    @Test
    void switchTrenNullNemNpeChuKhongRoiVaoNhanhNao() {
        assertThrows(NullPointerException.class, () -> ApiResultDemo.render(null));
    }

    @Test
    void caseNullTuongMinhThiNuotDuocNull() {
        assertEquals("NULL | không có kết quả nào", ApiResultDemo.renderChiuNull(null));
    }

    // --- cái bẫy: component null -------------------------------------------

    @Test
    void typePatternVanBindNullRoiNoMuonLucGoiAccessor() {
        // case Success<User>(User u) KHỚP với data = null, u nhận null.
        // Lỗi lùi tới tận lúc u.name() - không phải lúc match.
        ApiResult<User> rong = new Success<>(null);

        assertThrows(NullPointerException.class, () -> ApiResultDemo.render(rong));
    }

    @Test
    void nestedPatternKhongKhopComponentNullNenNemMatchException() {
        // case Success<User>(User(long id, ...)) đòi data thật sự là một User.
        // null trượt nhánh này -> không nhánh nào khớp -> MatchException.
        // Đây là lỗ hổng duy nhất của exhaustiveness: nó đúng ở mức KIỂU,
        // không hứa gì về null bên trong component.
        ApiResult<User> rong = new Success<>(null);

        assertThrows(MatchException.class, () -> ApiResultDemo.renderChiTiet(rong));
    }

    // --- record sinh sẵn những gì ------------------------------------------

    @Test
    void equalsTheoGiaTriChuKhongTheoThamChieu() {
        ApiResult<User> a = new Failure<>("lỗi", 500);
        ApiResult<User> b = new Failure<>("lỗi", 500);

        assertNotSame(a, b);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void loadingRongVanEqualsNhungKhongPhaiSingleton() {
        ApiResult<User> a = new Loading<>();
        ApiResult<User> b = new Loading<>();

        assertFalse(a == b, "mỗi new là một object mới - record không cache");
        assertEquals(a, b, "không có component nào để khác nhau");
        assertEquals("Loading[]", a.toString());
    }

    @Test
    void failureKhacNhanhSuccessDuCungMangGiaTri() {
        // Nominal typing: cùng shape vẫn khác type. TS structural thì ngược lại.
        assertFalse(new Success<>(THIEN).equals(new Loading<User>()));
    }
}
