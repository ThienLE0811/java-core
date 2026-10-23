package com.javacore.practice.javacore33.basics;

public class MangCoBan {

    record KetQuaDemChanLe(int tongSoChan, int tongSoLe) {
    }

    public static long tong(int[] list) {
        long tong = 0;
        for (int i = 0; i < list.length; i++) {
            tong += list[i];
        }
        return tong;
    }

    public static int max(int[] list) {
        int max = list[0];
        for (int i = 1; i < list.length; i++) {
            if (list[i] > max) max = list[i];
        }
        return max;
    }

    public static int min(int[] list) {
        int min = list[0];
        for (int i = 1; i < list.length; i++) {
            if (list[i] < min) min = list[i];
        }
        return min;
    }

    public static double trungBinh(int[] list) {
        long tong = tong(list);
        int length = list.length;
        return (double) tong / length;
    }

    public static int[] daoNguoc(int[] list) {
        int[] ketQua = new int[list.length];
        int length = list.length;
        for (int i = 0; i < length; i++) {
            ketQua[i] = list[length - 1 - i];
        }
        return ketQua;
    }

    public static KetQuaDemChanLe demChanLe(int[] list) {
        int chan = 0;
        int le = 0;
        int length = list.length;
        for (int i = 0; i < length; i++) {
            if (list[i] % 2 == 0) {
                chan++;
            } else {
                le++;
            }
        }
        return new KetQuaDemChanLe(chan, le);
    }
}
