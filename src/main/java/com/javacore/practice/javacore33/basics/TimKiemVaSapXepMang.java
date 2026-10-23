package com.javacore.practice.javacore33.basics;

import java.util.Arrays;

public class TimKiemVaSapXepMang {

    /**
     * Sắp xếp tăng dần và trả về bản sao; mảng gốc giữ nguyên.
     */
    public static int[] sapXep(int[] list) {
        int[] ketQua = Arrays.copyOf(list, list.length);
        Arrays.sort(ketQua);
        return ketQua;
    }

    /**
     * Tìm {@code target} bằng tìm kiếm nhị phân.
     *
     * <p>Hàm tự sắp xếp trước khi tìm, nên index trả về là vị trí trong mảng ĐÃ SẮP XẾP,
     * không phải vị trí trong {@code list} mà caller truyền vào.
     *
     * @return index trong mảng đã sắp xếp, hoặc -1 nếu không tìm thấy
     */
    public static int timKiem(int[] list, int target) {
        int[] daSapXep = sapXep(list);
        int viTri = Arrays.binarySearch(daSapXep, target);
        return viTri < 0 ? -1 : viTri;
    }
}
