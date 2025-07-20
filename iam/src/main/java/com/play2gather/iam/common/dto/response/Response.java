package com.play2gather.iam.common.dto.response;

import com.play2gather.iam.application.exception.ExceptionFlags;
import lombok.Data;

@Data
public class Response {
    private String id;
    private String message;
    private ExceptionFlags flag;

    public Response(String id, String message) {
        this.id = id;
        this.message = message;
        this.flag = ExceptionFlags.SUCCESS;
    }
}
