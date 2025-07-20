package com.play2gather.iam.common.dto.response;

import lombok.Data;

public class DataResponse<T> extends Response {
    private T data;

    public DataResponse(String id, String message, T data) {
        super(id, message);
        this.data = data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
