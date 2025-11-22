package com.warehouse.item.enums;

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

    DATA_EXIST(1, ResponseStatus.BAD_REQUEST, "data already exist", "data sudah ada"),
    DATA_NOT_FOUND(2, ResponseStatus.NOT_FOUND, "data not found", "data tidak ditemukan"),

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
