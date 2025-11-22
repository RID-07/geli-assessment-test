package com.warehouse.order.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ResponseType {
    SUCCESS_PING(0, ResponseStatus.SUCCESS, "Pong! 👋", "Pong! 👋"),
    SUCCESS_DATA_FOUND(0, ResponseStatus.SUCCESS, "Data found", "Data ditemukan"),
    SUCCESS_DATA_ADD(0, ResponseStatus.SUCCESS, "Data created", "Data berhasil ditambahkan"),
    SUCCESS_DATA_UPDATED(0, ResponseStatus.SUCCESS, "Data updated", "Data berhasil diubah"),
    SUCCESS_DATA_DELETED(0, ResponseStatus.SUCCESS, "Data deleted", "Data berhasil dihapus"),

    DATA_EXIST(1, ResponseStatus.BAD_REQUEST, "Data already exist", "Data sudah ada"),
    DATA_NOT_FOUND(2, ResponseStatus.NOT_FOUND, "Data not found", "Data tidak ditemukan"),
    DATA_ITEM_NOT_FOUND(3, ResponseStatus.NOT_FOUND, "Data item not found", "Data item tidak ditemukan"),
    INSUFFICIENT_STOCK(4, ResponseStatus.NOT_FOUND, "Insufficient stock", "Stok tidak mencukupi"),
    INACTIVE_STOCK(5, ResponseStatus.NOT_FOUND, "Stock not available", "Stok tidak tesedia"),
    SERVICE_UNAVAILABLE(6, ResponseStatus.ERROR, "Item service is currently unavailable. Please try again later.", "Layanan item sedang tidak tersedia. Silakan coba lagi nanti."),
    STOCK_CONFLICT(7, ResponseStatus.BAD_REQUEST, "Stock conflict detected. Please try again.", "Terjadi konflik stok. Silakan coba lagi."),

    UNKNOWN_ERROR(99, ResponseStatus.ERROR, "Unknown Error", "Error Tidak Diketahui"),
    INTERNAL_SERVER_ERROR(99, ResponseStatus.ERROR, "Internal Server Error", "Kesalahan server internal");

    private int messageCode;
    private ResponseStatus responseStatus;
    private String descriptionEn;
    private String descriptionId;

    public String getMessageCode() {
        return String.format("%02d", this.messageCode);
    }
}
