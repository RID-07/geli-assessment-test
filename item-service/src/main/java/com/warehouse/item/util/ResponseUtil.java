package com.warehouse.item.util;

import com.warehouse.item.enums.ResponseType;
import com.warehouse.item.model.general.GeneralResponse;
import com.warehouse.item.model.general.MessageResponse;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ResponseUtil {

    public ResponseUtil() {
    }

    public static Pair<HttpStatus, GeneralResponse> generateErrorResponse(ResponseType type, @Nullable Object data, String... messages) {
        String messageEn;
        String messageId;

        List<String> enStrings = new ArrayList<>();
        List<String> idStrings = new ArrayList<>();

        for (int i = 0; i < messages.length; i++) {
            if (i % 2 == 0) {
                enStrings.add(messages[i]);
            } else {
                idStrings.add(messages[i]);
            }
        }

        java.util.function.BiFunction<String, List<String>, String> safeFormat = (template, argsList) -> {
            if (template == null || template.isEmpty()) {
                return String.join(" ", argsList);
            }

            if (!template.contains("%s")) {
                String extra = argsList.isEmpty() ? "" : (" " + String.join(" ", argsList));
                return template + extra;
            }

            Object[] args = argsList.toArray(new Object[0]);
            try {
                return String.format(template, args);
            } catch (RuntimeException ex) {
                String extra = argsList.isEmpty() ? "" : (" " + String.join(" ", argsList));
                return template + extra;
            }
        };

        messageEn = safeFormat.apply(type.getDescriptionEn(), enStrings);
        messageId = safeFormat.apply(type.getDescriptionId(), idStrings);

        return processErrorResponse(type, data, messageEn, messageId);
    }

    public static Pair<HttpStatus, GeneralResponse> generateSuccessResponse(ResponseType type, Object data) {
        MessageResponse messageResponse = new MessageResponse(
                type.getDescriptionEn(), type.getDescriptionId()
        );

        GeneralResponse response = new GeneralResponse(
                type.getMessageCode(),
                type.getResponseStatus().getMessage(),
                messageResponse,
                data
        );

        HttpStatus status = HttpStatus.valueOf(type.getResponseStatus().getCode());
        return Pair.of(status, response);
    }

    public static ResponseEntity<Object> toResponseEntity(Pair<HttpStatus, GeneralResponse> response) {
        return new ResponseEntity<>(response.getSecond(), response.getFirst());
    }

    private static Pair<HttpStatus, GeneralResponse> processErrorResponse(ResponseType type, Object data, String messageEn, String messageId) {
        MessageResponse messageResponse = new MessageResponse(
                messageEn, messageId
        );

        GeneralResponse response = new GeneralResponse(
                type.getMessageCode(),
                type.getResponseStatus().getMessage(),
                messageResponse,
                data
        );

        HttpStatus status = HttpStatus.valueOf(type.getResponseStatus().getCode());
        return Pair.of(status, response);
    }
}
