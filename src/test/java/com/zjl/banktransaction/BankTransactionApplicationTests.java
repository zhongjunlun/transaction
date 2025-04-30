package com.zjl.banktransaction;

import com.zjl.banktransaction.model.Transaction;
import com.zjl.banktransaction.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class BankTransactionApplicationTests {

    private TransactionService transactionService;

    @BeforeEach
    public void setup() {
        transactionService = new TransactionService();
    }

    @Test
    public void testConcurrentCreateTransaction() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 100; i++) {
            executorService.submit(() -> {
                Transaction transaction = new Transaction();
                transaction.setType(Transaction.TransactionType.DEPOSIT);
                transaction.setAmount(BigDecimal.valueOf(100));
                transaction.setDescription("Test transaction");
                transactionService.createTransaction(transaction);
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        assertEquals(100, transactionService.getAllTransactions(0,1000).size());
    }

    @Test
    public void testUpdateTransaction() {
        Transaction transaction = new Transaction();
        transaction.setType(Transaction.TransactionType.TRANSFER);
        transaction.setAmount(BigDecimal.valueOf(100));
        transaction.setDescription("Test transaction");
        Transaction createdTransaction = transactionService.createTransaction(transaction);

        assertNotNull(transactionService.getTransaction(createdTransaction.getId()));

        Transaction updateTransaction = new Transaction();
        updateTransaction.setType(Transaction.TransactionType.DEPOSIT);

        transactionService.updateTransaction(createdTransaction.getId(), updateTransaction);
        Transaction updatedTransaction = transactionService.getTransaction(createdTransaction.getId());
        assertNotNull(updatedTransaction);
        assertEquals(Transaction.TransactionType.DEPOSIT, updatedTransaction.getType());
    }

    @Test
    public void testDeleteTransaction() {
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(100));
        transaction.setDescription("Test transaction");
        Transaction createdTransaction = transactionService.createTransaction(transaction);

        assertEquals(1, transactionService.getAllTransactions(0,100).size());

        transactionService.deleteTransaction(createdTransaction.getId());

        assertEquals(0, transactionService.getAllTransactions(0,100).size());
    }



}
