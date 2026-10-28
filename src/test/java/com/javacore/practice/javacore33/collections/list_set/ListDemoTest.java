package com.javacore.practice.javacore33.collections.list_set;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.javacore.practice.javacore33.records.Point;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test chốt lại từng bài học của ListDemo. Các method trong ListDemo đều void và
 * chỉ in ra màn hình nên không test trực tiếp được; thứ cần khoá lại là HÀNH VI
 * của List mà bài học dựa vào. Nếu một ngày nào đó bạn nhớ sai, test này nổ.
 */
class ListDemoTest {

    // --- 1. Thao tác cơ bản ------------------------------------------------

    @Test
    void addTheoChiSoDayPhanTuCuSangPhai() {
        List<String> ds = new ArrayList<>(List.of("Java", "Python"));

        ds.add(1, "Javascript");

        assertEquals(List.of("Java", "Javascript", "Python"), ds);
    }

    @Test
    void setGhiDeChuKhongChen() {
        List<String> ds = new ArrayList<>(List.of("Java", "Javascript", "Python"));

        ds.set(2, "C++");

        assertEquals(List.of("Java", "Javascript", "C++"), ds);
        assertEquals(3, ds.size(), "set không làm đổi size");
    }

    @Test
    void indexOfKhongTimThayTraVeAmMotChuKhongNemException() {
        List<String> ds = new ArrayList<>(List.of("Java", "Go"));

        assertEquals(-1, ds.indexOf("Rust"));
        assertThrows(IndexOutOfBoundsException.class, () -> ds.get(ds.indexOf("Rust")));
    }

    @Test
    void getFirstGetLastTuongDuongGetKhongVaGetSizeTruMot() {
        List<String> ds = new ArrayList<>(List.of("Java", "Javascript", "C++"));

        assertEquals(ds.get(0), ds.getFirst());
        assertEquals(ds.get(ds.size() - 1), ds.getLast());
    }

    // --- 2. contains / indexOf dựa trên equals -----------------------------

    private static final class HopMutable {
        private String nhan;

        HopMutable(String nhan) {
            this.nhan = nhan;
        }
    }

    @Test
    void containsSoSanhBangEqualsChuKhongPhaiDiaChi() {
        List<String> ds = new ArrayList<>(List.of("C++"));
        String khacDiaChi = new String("C++");

        assertNotSame(ds.get(0), khacDiaChi);
        assertTrue(ds.contains(khacDiaChi));
    }

    @Test
    void containsThatBaiKhiKieuPhanTuKhongOverrideEquals() {
        List<HopMutable> ds = new ArrayList<>(List.of(new HopMutable("Áo")));

        // HopMutable thừa Object.equals (tức là so ==) -> object mới không bao giờ khớp.
        assertFalse(ds.contains(new HopMutable("Áo")));
    }

    @Test
    void containsHoatDongVoiRecordVoiEqualsTuSinh() {
        List<Point> ds = new ArrayList<>(List.of(new Point(1, 2)));

        assertTrue(ds.contains(new Point(1, 2)));
    }

    @Test
    void containsNhanObjectNenSoNhamKieuChiLangLeTraFalse() {
        List<String> ds = new ArrayList<>(List.of("1", "2"));

        // Compile được dù list là List<String>: signature là contains(Object).
        assertFalse(ds.contains(1));
    }

    // --- 3. Bẫy remove overload -------------------------------------------

    @Test
    void removeIntXoaTheoChiSoVaTraVePhanTuBiXoa() {
        List<Integer> ds = new ArrayList<>(List.of(2, 0, 1));

        Integer biXoa = ds.remove(2);

        assertEquals(1, biXoa, "remove(int) trả về phần tử bị xóa");
        assertEquals(List.of(2, 0), ds);
    }

    @Test
    void removeObjectXoaTheoGiaTriVaTraVeBoolean() {
        List<Integer> ds = new ArrayList<>(List.of(2, 0, 1));

        boolean daXoa = ds.remove(Integer.valueOf(2));

        assertTrue(daXoa, "remove(Object) trả về có xóa được hay không");
        assertEquals(List.of(0, 1), ds);
    }

    @Test
    void haiOverloadChoKetQuaKhacHanNhauTrenCungMotList() {
        List<Integer> theoChiSo = new ArrayList<>(List.of(2, 0, 1));
        List<Integer> theoGiaTri = new ArrayList<>(List.of(2, 0, 1));

        theoChiSo.remove(2);
        theoGiaTri.remove(Integer.valueOf(2));

        assertNotEquals(theoChiSo, theoGiaTri, "cùng viết remove(2) mà ra hai kết quả khác nhau");
    }

    @Test
    void chiSoKhongHopLeThiNoConGiaTriKhongTonTaiThiIm() {
        List<Integer> ds = new ArrayList<>(List.of(2, 0, 1));

        assertThrows(IndexOutOfBoundsException.class, () -> ds.remove(99));
        assertFalse(ds.remove(Integer.valueOf(99)));
        assertEquals(List.of(2, 0, 1), ds, "cả hai lời gọi trên đều không đổi list");
    }

    // --- 4. Xóa khi đang duyệt --------------------------------------------

    @Test
    void sualistTrongForEachNemConcurrentModificationException() {
        List<String> ds = new ArrayList<>(List.of("Java", "Javascript", "Python", "Go"));

        assertThrows(java.util.ConcurrentModificationException.class, () -> {
            for (String s : ds) {
                if (s.startsWith("J")) {
                    ds.remove(s);
                }
            }
        });
    }

    @Test
    void xoaPhanTuApChotKhongNoNhungBoSotPhanTuCuoi() {
        List<String> ds = new ArrayList<>(List.of("a", "b", "c"));
        List<String> daDuyet = new ArrayList<>();

        // hasNext() chỉ so cursor != size, không kiểm modCount -> vòng lặp thoát sớm
        // một nhịp và không ai phát hiện list đã bị sửa.
        assertDoesNotThrow(() -> {
            for (String s : ds) {
                daDuyet.add(s);
                if (s.equals("b")) {
                    ds.remove(s);
                }
            }
        });

        assertEquals(List.of("a", "b"), daDuyet, "\"c\" bị bỏ qua, im lặng");
        assertEquals(List.of("a", "c"), ds);
    }

    @Test
    void iteratorRemoveVaRemoveIfChoCungKetQua() {
        List<String> quaIterator = new ArrayList<>(List.of("Java", "Javascript", "Python", "Go"));
        List<String> quaRemoveIf = new ArrayList<>(List.of("Java", "Javascript", "Python", "Go"));

        Iterator<String> it = quaIterator.iterator();
        while (it.hasNext()) {
            if (it.next().startsWith("J")) {
                it.remove();
            }
        }
        quaRemoveIf.removeIf(s -> s.startsWith("J"));

        assertEquals(List.of("Python", "Go"), quaIterator);
        assertEquals(quaIterator, quaRemoveIf);
    }

    // --- 5. Comparator -----------------------------------------------------

    private static List<Point> diem() {
        return new ArrayList<>(List.of(
                new Point(2, 1),
                new Point(1, 3),
                new Point(2, 0),
                new Point(1, 1)));
    }

    @Test
    void naturalOrderDungCompareToCuaPointTucLaTheoKhoangCachToiGoc() {
        List<Point> ds = diem();

        ds.sort(Comparator.naturalOrder());

        // KHÔNG phải theo x rồi y: (1,1)=1.41 < (2,0)=2.0 < (2,1)=2.24 < (1,3)=3.16
        assertEquals(List.of(
                new Point(1, 1),
                new Point(2, 0),
                new Point(2, 1),
                new Point(1, 3)), ds);
    }

    @Test
    void reversedOCuoiChuoiDaoToanBoChuKhongChiTieuChiCuoi() {
        List<Point> ds = diem();

        ds.sort(Comparator.comparing(Point::x).thenComparing(Point::y).reversed());

        // x giảm VÀ y cũng giảm
        assertEquals(List.of(
                new Point(2, 1),
                new Point(2, 0),
                new Point(1, 3),
                new Point(1, 1)), ds);
    }

    @Test
    void muonDaoRiengMotTieuChiThiPhaiBocRiengTieuChiDo() {
        List<Point> ds = diem();

        ds.sort(Comparator.comparing(Point::x)
                .thenComparing(Comparator.comparing(Point::y).reversed()));

        // x tăng, y giảm
        assertEquals(List.of(
                new Point(1, 3),
                new Point(1, 1),
                new Point(2, 1),
                new Point(2, 0)), ds);
    }

    @Test
    void sortLaOnDinhNenGiuThuTuGocTrongNhomBangNhau() {
        List<Point> ds = diem();

        ds.sort(Comparator.comparing(Point::x));

        // Trong gốc (1,3) đứng trước (1,1) và (2,1) đứng trước (2,0) -> giữ nguyên.
        assertEquals(List.of(
                new Point(1, 3),
                new Point(1, 1),
                new Point(2, 1),
                new Point(2, 0)), ds);
    }

    @Test
    void listSortVaCollectionsSortLaMotThu() {
        List<Point> quaList = diem();
        List<Point> quaCollections = diem();
        Comparator<Point> cmp = Comparator.comparing(Point::x).thenComparing(Point::y);

        quaList.sort(cmp);
        Collections.sort(quaCollections, cmp);

        assertEquals(quaList, quaCollections);
    }

    @Test
    void streamSortedTraBanMoiVaKhongDungToiListGoc() {
        List<Point> goc = diem();
        List<Point> ban = new ArrayList<>(goc);

        List<Point> banMoi = goc.stream().sorted(Comparator.comparing(Point::x)).toList();

        assertEquals(ban, goc, "list gốc không đổi");
        assertNotSame(goc, banMoi);
    }

    // --- 6. Bốn mức "không sửa được" ---------------------------------------

    @Test
    void listOfBatBienHoanToanKeCaSortVaRemoveIfVoHai() {
        List<String> batBien = List.of("Java", "Go");

        assertThrows(UnsupportedOperationException.class, () -> batBien.add("Rust"));
        assertThrows(UnsupportedOperationException.class, () -> batBien.set(0, "Rust"));
        assertThrows(UnsupportedOperationException.class,
                () -> batBien.sort(Comparator.naturalOrder()));
        assertThrows(UnsupportedOperationException.class, () -> batBien.removeIf(s -> false));
    }

    @Test
    void arraysAsListChoSetNhungCamAddViDoDaiCoDinh() {
        List<String> view = Arrays.asList("Java", "Go");

        assertDoesNotThrow(() -> view.set(0, "Rust"));
        assertThrows(UnsupportedOperationException.class, () -> view.add("C"));
    }

    @Test
    void arraysAsListLaViewHaiChieuTrenMangGoc() {
        String[] mang = {"Java", "Go"};
        List<String> view = Arrays.asList(mang);

        view.set(0, "Rust");
        assertEquals("Rust", mang[0], "sửa list -> mảng đổi");

        mang[1] = "Kotlin";
        assertEquals("Kotlin", view.get(1), "sửa mảng -> list đổi");
    }

    @Test
    void unmodifiableListChiChanBanChuKhongDongBangDuLieu() {
        List<String> goc = new ArrayList<>(List.of("Java", "Go"));
        List<String> chiDoc = Collections.unmodifiableList(goc);

        assertThrows(UnsupportedOperationException.class, () -> chiDoc.add("Rust"));

        goc.add("Rust");
        assertEquals(List.of("Java", "Go", "Rust"), chiDoc, "view phản ánh thay đổi của gốc");
    }

    @Test
    void copyThatSuCatDutLienHeVoiNguon() {
        List<String> goc = new ArrayList<>(List.of("Java", "Go"));
        List<String> banSao = new ArrayList<>(goc);

        goc.add("Rust");

        assertEquals(List.of("Java", "Go"), banSao);
    }

    @Test
    void listCopyOfVuaCopyVuaBatBien() {
        List<String> goc = new ArrayList<>(List.of("Java", "Go"));
        List<String> copy = List.copyOf(goc);

        goc.add("Rust");

        assertEquals(List.of("Java", "Go"), copy);
        assertThrows(UnsupportedOperationException.class, () -> copy.add("C"));
    }

    @Test
    void listOfCamNullConArraysAsListThiChoPhep() {
        assertThrows(NullPointerException.class, () -> List.of(1, null, 3));
        assertEquals(3, Arrays.asList(1, null, 3).size());
    }

    @Test
    void streamToListTraVeListBatBien() {
        List<String> ketQua = new ArrayList<>(List.of("Java", "Go")).stream().toList();

        assertThrows(UnsupportedOperationException.class, () -> ketQua.add("Rust"));
    }
}
