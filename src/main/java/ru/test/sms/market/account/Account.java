package ru.test.sms.market.account;

import lombok.Builder;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Builder
public class Account {
    private final UUID accountId;

    private Long balance;
    private final Lock lock = new ReentrantLock();
    private final AtomicInteger reservedBalance = new AtomicInteger(0);

    public void reserveMoney(Integer amount) {
        lock.lock();
        try {
            if (balance < amount) throw new IllegalArgumentException("Не хватает денег на счете");
            balance -= amount;
            reservedBalance.addAndGet(amount);
        } finally {
            lock.unlock();
        }
    }

    public void addMoney(Integer amount) {
        lock.lock();
        try {
            balance += amount;
        } finally {
            lock.unlock();
        }
    }

    public void commitTransaction(Integer amount) {
        lock.lock();
        try {
            reservedBalance.set(reservedBalance.get() - amount);
        } finally {
            lock.unlock();
        }
    }

    public void rollbackTransaction(Integer amount) {
        lock.lock();
        try {
            reservedBalance.set(reservedBalance.get() - amount);
            balance += amount;
        } finally {
            lock.unlock();
        }
    }

    public UUID getAccountId() {
        return accountId;
    }

    public Long getBalance() {
        return balance;
    }

    public Integer getReservedBalance() {
        return reservedBalance.get();
    }
}
