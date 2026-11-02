package com.javacore.practice.javacore33.projects.banhang.model;

public class OutOfStockException extends IllegalStateException {

    private final String productId;
    private final int conLai;
    private final int canDat;

    public OutOfStockException(String productId, String tenSanPham, int conLai, int canDat) {
        super(String.format("Sản phẩm '%s' (%s) chỉ còn %d, không đủ cho %d.",
                tenSanPham, productId, conLai, canDat));
        this.productId = productId;
        this.conLai = conLai;
        this.canDat = canDat;
    }

    public String getProductId() {
        return this.productId;
    }

    public int getConLai() {
        return this.conLai;
    }

    public int getCanDat() {
        return this.canDat;
    }
}
