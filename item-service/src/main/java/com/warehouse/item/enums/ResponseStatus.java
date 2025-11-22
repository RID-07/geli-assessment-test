package com.warehouse.item.enums;

import com.warehouse.item.constant.GeneralConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ResponseStatus {
    SUCCESS(HttpStatus.OK.value(), GeneralConstant.SUCCESS),
    BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), GeneralConstant.FAILED),
    ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), GeneralConstant.ERROR),
    NOT_FOUND(HttpStatus.NOT_FOUND.value(), GeneralConstant.FAILED);

    private int code;
    private String message;
}
