/*
 * Copyright (c) Honor Device Co., Ltd. 2023-2023. All rights reserved.
 */

package com.zjl.banktransaction.service;

import com.zjl.banktransaction.model.Transaction;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 功能描述
 *
 * @author 00030520
 * @since 2025/4/30
 */
@Service
public class TransactionService {
    private final Map<String, Transaction> transactionMap = new ConcurrentHashMap<>();
    private final Map<String, Lock> lockMap = new ConcurrentHashMap<>();

    public Transaction createTransaction(Transaction transaction) {
        String id = UUID.randomUUID().toString();
        transaction.setId(id);
        transaction.setCreateTime(LocalDateTime.now());
        transactionMap.put(id, transaction);
        lockMap.put(id, new ReentrantLock());
        return transaction;
    }

    public Transaction getTransaction(String id) {
        return transactionMap.get(id);
    }

    public List<Transaction> getAllTransactions(int page, int size) {
        List<Transaction> transactions = new ArrayList<>(transactionMap.values());
        int start = page * size;
        int end = Math.min(start + size, transactions.size());
        if (start > end) {
            return Collections.emptyList();
        }
        return transactions.subList(start, end);
    }

    public Transaction updateTransaction(String id, Transaction transaction) {
        Lock lock = lockMap.get(id);
        if (lock == null) {
            throw new NoSuchElementException("Transaction not found");
        }
        lock.lock();
        try {
            Transaction existingTransaction = transactionMap.get(id);
            if (existingTransaction == null) {
                throw new NoSuchElementException("Transaction not found");
            }
            // 使用反射更新传入的字段，未传入的字段保持不变
            updateNonNullFields(existingTransaction, transaction);
            existingTransaction.setUpdateTime(LocalDateTime.now());
            transactionMap.put(id, existingTransaction);
            return existingTransaction;
        } finally {
            lock.unlock();
        }
    }

    private void updateNonNullFields(Transaction existingTransaction, Transaction newTransaction) {
        Field[] fields = Transaction.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object newValue = field.get(newTransaction);
                if (newValue != null) {
                    field.set(existingTransaction, newValue);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to update field: " + field.getName(), e);
            }
        }
    }

    public void deleteTransaction(String id) {
        Lock lock = lockMap.get(id);
        if (lock == null) {
            throw new NoSuchElementException("Transaction not found");
        }

        lock.lock();
        try {
            if (!transactionMap.containsKey(id)) {
                throw new NoSuchElementException("Transaction not found");
            }
            transactionMap.remove(id);
            lockMap.remove(id);
        } finally {
            lock.unlock();
        }
    }
}
