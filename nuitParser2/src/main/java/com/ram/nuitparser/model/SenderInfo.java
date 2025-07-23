package com.ram.nuitparser.model;

import lombok.Data;

@Data
public class SenderInfo {
    private String senderCode;
    private String senderStation;
    private String receiverCode;
    private String state;
}
