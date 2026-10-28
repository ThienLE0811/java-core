package com.javacore.practice.javacore33.collections;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import com.javacore.practice.javacore33.records.Point;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test chốt lại từng bài học của SetDemo.
 *
 * Nguyên tắc xuyên suốt file này: CHỈ assert những gì spec bảo đảm. Thứ tự duyệt của
 * HashSet không nằm trong bảo đảm nào, nên không có test nào ở đây khoá nó lại -
 * viết test như vậy là tự tạo ra một test đỏ vào một ngày JDK đổi cách băm.
 */
class SetDemoTest {

    // --- 1. Lọc trùng ------------------------------------------------------

    private static final List<String> CO_TRUNG =
            List.of("Go", "Java", "Python", "Java", "Go", "Rust", "Java");

    @Test
    void linkedHashSetGiuThuTuXuatHienDauTien() {
        List<String> ketQua = new ArrayList<>(new LinkedHashSet<>(CO_TRUNG));

        assertEquals(List.of("Go", "Java", "Python", "Rust"), ketQua);
    }

    @Test
    void lanXuatHienSauKhongDayPhanTuLenViTriMoi() {
        // "Java" xuất hiện ở index 1, 3, 6 -> vị trí được quyết định bởi lần ĐẦU.
        List<String> ketQua = new ArrayList<>(new LinkedHashSet<>(CO_TRUNG));

        assertEquals(1, ketQua.indexOf("Java"));
    }

    @Test
    void streamDistinctChoCungKetQuaVoiLinkedHashSet() {
        assertEquals(new ArrayList<>(new LinkedHashSet<>(CO_TRUNG)),
                CO_TRUNG.stream().distinct().toList());
    }

    @Test
    void hashSetDedupeDungNhungKhongHuaThuTu() {
        Set<String> ketQua = new HashSet<>(CO_TRUNG);

        // Chỉ khoá NỘI DUNG. Thứ tự duyệt cố tình không assert.
        assertEquals(Set.of("Go", "Java", "Python", "Rust"), ketQua);
        assertEquals(4, ketQua.size());
    }

    // --- 2. Ba loại Set ----------------------------------------------------

    @Test
    void linkedHashSetGiuThuTuChenConTreeSetSapTheoCompareTo() {
        List<String> chu = List.of("Go", "Java", "Python", "Rust", "C++");

        assertEquals(List.of("Go", "Java", "Python", "Rust", "C++"),
                new ArrayList<>(new LinkedHashSet<>(chu)));
        assertEquals(List.of("C++", "Go", "Java", "Python", "Rust"),
                new ArrayList<>(new TreeSet<>(chu)));
    }

    @Test
    void baLoaiSetKhacNhauVeThuTuNhungBangNhauVeNoiDung() {
        List<Integer> so = List.of(5, 3, 9, 1, 7);

        Set<Integer> hash = new HashSet<>(so);
        Set<Integer> linked = new LinkedHashSet<>(so);
        Set<Integer> tree = new TreeSet<>(so);

        // Set.equals so theo NỘI DUNG, không quan tâm thứ tự - khác hẳn List.equals.
        assertEquals(hash, linked);
        assertEquals(linked, tree);
        assertNotEqualsAsList(linked, tree);
    }

    private static void assertNotEqualsAsList(Set<Integer> a, Set<Integer> b) {
        assertFalse(new ArrayList<>(a).equals(new ArrayList<>(b)),
                "đổ ra List thì thứ tự lộ ra và hai cái khác nhau");
    }

    // --- 3. "Trùng" là equals hay compareTo? -------------------------------

    @Test
    void hashSetXacDinhTrungBangEquals() {
        Point p1 = new Point(1, 5);
        Point p2 = new Point(1, 99);

        assertFalse(p1.equals(p2));
        assertEquals(2, new HashSet<>(List.of(p1, p2)).size());
    }

    @Test
    void treeSetXacDinhTrungBangComparatorNenNuotMatPhanTu() {
        Point p1 = new Point(1, 5);
        Point p2 = new Point(1, 99);

        Set<Point> tree = new TreeSet<>(Comparator.comparing(Point::x));
        tree.add(p1);
        boolean themDuoc = tree.add(p2);

        assertFalse(themDuoc, "add trả false, KHÔNG ném exception");
        assertEquals(1, tree.size());
        assertTrue(tree.contains(p1));
    }

    @Test
    void compareToCuaPointKhongNhatQuanVoiEqualsNenTreeSetSaiNgayVoiNaturalOrder() {
        Point a = new Point(2, 0);
        Point b = new Point(0, 2);

        assertFalse(a.equals(b), "hai điểm khác nhau");
        assertEquals(0, a.compareTo(b), "nhưng cùng khoảng cách tới gốc");
        assertEquals(1, new TreeSet<>(List.of(a, b)).size(), "TreeSet coi là một");
    }

    @Test
    void comparatorDuTieuChiThiTreeSetGiuDuPhanTu() {
        Set<Point> tree = new TreeSet<>(Comparator.comparing(Point::x).thenComparing(Point::y));

        tree.addAll(List.of(new Point(1, 5), new Point(1, 99), new Point(2, 0), new Point(0, 2)));

        assertEquals(4, tree.size());
    }

    @Test
    void treeSetSapXepTheoComparatorChuKhongTheoThuTuChen() {
        Set<Point> tree = new TreeSet<>(Comparator.comparing(Point::x).thenComparing(Point::y));

        tree.addAll(List.of(new Point(2, 0), new Point(1, 99), new Point(1, 5)));

        assertEquals(List.of(new Point(1, 5), new Point(1, 99), new Point(2, 0)),
                new ArrayList<>(tree));
    }

    // --- 4. null trong Set -------------------------------------------------

    @Test
    void hashSetChuaDuocDungMotNull() {
        Set<String> set = new HashSet<>();

        set.add(null);
        boolean themLanHai = set.add(null);
        set.add("Java");

        assertFalse(themLanHai, "null thứ hai bị coi là trùng");
        assertEquals(2, set.size());
        assertTrue(set.contains(null));
    }

    @Test
    void treeSetKhongChuaDuocNullViPhaiSoSanh() {
        Set<String> tree = new TreeSet<>();
        tree.add("Java");

        assertThrows(NullPointerException.class, () -> tree.add(null));
    }

    @Test
    void treeSetRongCungNemNpeKhiThemNull() {
        // Ngay cả khi set rỗng (không có gì để so) thì TreeSet vẫn từ chối null.
        assertThrows(NullPointerException.class, () -> new TreeSet<String>().add(null));
    }

    @Test
    void setOfCamNull() {
        assertThrows(NullPointerException.class, () -> Set.of(1, null));
    }

    @Test
    void setOfCamPhanTuTrungConHashSetThiImLangGop() {
        assertThrows(IllegalArgumentException.class, () -> Set.of(1, 1));
        assertEquals(Set.of(1), new HashSet<>(List.of(1, 1)));
    }

    @Test
    void setOfLaBatBien() {
        Set<Integer> batBien = Set.of(1, 2, 3);

        assertThrows(UnsupportedOperationException.class, () -> batBien.add(4));
        assertThrows(UnsupportedOperationException.class, () -> batBien.retainAll(Set.of(1)));
    }

    // --- 5. Mutate phần tử sau khi đã vào Set ------------------------------

    /** Bản sao của SetDemo.Hop (vốn private) để test chạm được vào. */
    private static final class Hop {
        private String nhan;

        Hop(String nhan) {
            this.nhan = nhan;
        }

        void setNhan(String nhan) {
            this.nhan = nhan;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Hop khac)) {
                return false;
            }
            return Objects.equals(nhan, khac.nhan);
        }

        @Override
        public int hashCode() {
            return Objects.hash(nhan);
        }
    }

    @Test
    void doiFieldDungTrongHashCodeLamPhanTuBienMatKhoiTamTim() {
        Hop h = new Hop("Áo");
        Set<Hop> set = new HashSet<>();
        set.add(h);
        assertTrue(set.contains(h));

        h.setNhan("Quần");

        assertFalse(set.contains(h), "không tìm ra, dù object vẫn nằm trong set");
        assertEquals(1, set.size(), "size vẫn là 1");
        assertTrue(set.iterator().hasNext(), "duyệt thì vẫn thấy nó");
    }

    @Test
    void khiDoHaiThaoTacRemoveVaAddDeuHongTheo() {
        Hop h = new Hop("Áo");
        Set<Hop> set = new HashSet<>();
        set.add(h);
        h.setNhan("Quần");

        assertFalse(set.remove(h), "xóa cũng trượt");

        set.add(new Hop("Quần"));

        // Set giờ chứa HAI phần tử equals() nhau - vi phạm chính định nghĩa của Set.
        assertEquals(2, set.size());
        List<Hop> tatCa = new ArrayList<>(set);
        assertEquals(tatCa.get(0), tatCa.get(1));
    }

    @Test
    void recordBatBienNenKhongDinhVanDeNay() {
        Point p = new Point(1, 2);
        Set<Point> set = new HashSet<>(List.of(p));

        // Không có setter nào để gọi. tinhTien trả instance MỚI, p không đổi.
        Point moi = p.tinhTien(10, 10);

        assertTrue(set.contains(p));
        assertFalse(set.contains(moi));
        assertEquals(new Point(1, 2), p, "p vẫn nguyên vẹn");
    }

    // --- 6. Phép toán tập hợp ----------------------------------------------

    @Test
    void addAllLaHopRetainAllLaGiaoRemoveAllLaHieu() {
        Set<Integer> a = Set.of(1, 2, 3, 4);
        Set<Integer> b = Set.of(3, 4, 5);

        Set<Integer> hop = new TreeSet<>(a);
        hop.addAll(b);
        Set<Integer> giao = new TreeSet<>(a);
        giao.retainAll(b);
        Set<Integer> hieu = new TreeSet<>(a);
        hieu.removeAll(b);

        assertEquals(Set.of(1, 2, 3, 4, 5), hop);
        assertEquals(Set.of(3, 4), giao);
        assertEquals(Set.of(1, 2), hieu);
    }

    @Test
    void baPhepToanSuaTaiChoVaTraVeBoolean() {
        Set<Integer> set = new HashSet<>(Set.of(1, 2, 3));

        boolean coDoi = set.retainAll(Set.of(1, 2, 3, 99));

        assertFalse(coDoi, "trả false khi không có gì thay đổi");
        assertEquals(Set.of(1, 2, 3), set);

        assertTrue(set.removeAll(Set.of(3)));
        assertEquals(Set.of(1, 2), set, "đã bị sửa TẠI CHỖ");
    }

    @Test
    void containsAllKhongSuaGiCa() {
        Set<Integer> a = new HashSet<>(Set.of(1, 2, 3, 4));

        assertTrue(a.containsAll(Set.of(3, 4)));
        assertFalse(a.containsAll(Set.of(3, 5)));
        assertEquals(Set.of(1, 2, 3, 4), a);
    }
}
