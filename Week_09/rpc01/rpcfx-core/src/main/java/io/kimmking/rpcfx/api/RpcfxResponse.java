package io.kimmking.rpcfx.api;

import lombok.Data;

@Data
public class RpcfxResponse {
    private String result;
    private boolean status;
    private Exception exception;
}
