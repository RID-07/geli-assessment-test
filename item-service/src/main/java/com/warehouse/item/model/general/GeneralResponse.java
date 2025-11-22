package com.warehouse.item.model.general;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GeneralResponse {
    private String code;
    private String status;
    private MessageResponse message;
    private Object data;

    public GeneralResponse(String code, String status, MessageResponse message, Object data) {
        this.setCode(code);
        this.setStatus(status);
        this.setMessage(message);
        this.data = data;
    }
}
