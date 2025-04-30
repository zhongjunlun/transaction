/*
 * Copyright (c) Honor Device Co., Ltd. 2023-2023. All rights reserved.
 */

package com.zjl.banktransaction.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 功能描述
 *
 * @author 00030520
 * @since 2025/4/30
 */
@Data
public class Transaction {
    private String id;
    private String accountId;
    private BigDecimal amount;
    private TransactionType type;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, TRANSFER, PAYMENT, REFUND
    }


}
