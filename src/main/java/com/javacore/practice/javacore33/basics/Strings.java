package com.javacore.practice.javacore33.basics;

/**
 * Bài 5 - Xử lý chuỗi (Strings).
 */
public class Strings {

    /**
     * Đếm số ký tự không phải khoảng trắng.
     *
     * <p>Khoảng trắng hiểu theo {@link Character#isWhitespace(char)}, nên tab và xuống dòng
     * cũng bị loại, không riêng dấu cách.
     *
     * @return số ký tự, hoặc 0 nếu {@code chuoi} là null
     */
    public static int demKyTu(String chuoi) {
        if (chuoi == null) return 0;

        int soKyTu = 0;
        for (int i = 0; i < chuoi.length(); i++) {
            if (!Character.isWhitespace(chuoi.charAt(i))) {
                soKyTu++;
            }
        }
        return soKyTu;
    }

    /**
     * Đếm số từ, coi mọi cụm ký tự trắng là một dấu phân cách.
     *
     * <p>Đếm bằng cách nhận ra thời điểm một từ bắt đầu - ký tự không trắng đầu tiên sau
     * một ký tự trắng - nên không cần xử lý riêng chuỗi rỗng, chuỗi toàn khoảng trắng,
     * khoảng trắng thừa ở đầu/cuối hay nhiều khoảng trắng liên tiếp.
     *
     * @return số từ, hoặc 0 nếu {@code chuoi} là null
     */
    public static int demTu(String chuoi) {
        if (chuoi == null) return 0;

        int soTu = 0;
        boolean dangTrongTu = false;
        for (int i = 0; i < chuoi.length(); i++) {
            if (Character.isWhitespace(chuoi.charAt(i))) {
                dangTrongTu = false;
            } else if (!dangTrongTu) {
                dangTrongTu = true;
                soTu++;
            }
        }
        return soTu;
    }

    /**
     * Kiểm tra chuỗi đối xứng (palindrome), không phân biệt hoa thường và bỏ qua ký tự đặc biệt.
     *
     * <p>Chỉ so sánh các ký tự chữ và số (alphanumeric). Các ký tự như khoảng trắng, dấu câu
     * được bỏ qua.
     *
     * @param chuoi chuỗi cần kiểm tra
     * @return {@code true} nếu chuỗi là palindrome, {@code false} nếu chuỗi là null hoặc không đối xứng
     */
    public static boolean laPalindrome(String chuoi) {
        if (chuoi == null) return false;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chuoi.length(); i++) {
            char c = chuoi.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                sb.append(Character.toLowerCase(c));
            }
        }

        String daLoc = sb.toString();
        String daoNguoc = sb.reverse().toString();
        return daLoc.equals(daoNguoc);
    }
}
