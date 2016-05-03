package ru.ifmo.pp;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Bank implementation.
 *
 * @author Semenov
 */
public class BankImpl implements Bank {
    /**
     * An array of accounts by index.
     */
    private final Account[] accounts;

    /**
     * Creates new bank instance.
     *
     * @param n the number of accounts (numbered from 0 to n-1).
     */
    public BankImpl(int n) {
        accounts = new Account[n];
        for (int i = 0; i < n; i++) {
            accounts[i] = new Account();
        }
    }

    private static void lock(Account... accounts) {
        for (Account account : accounts) {
            account.lock();
        }
    }

    private static void unlock(Account... accounts) {
        for (Account account : accounts) {
            account.unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfAccounts() {
        return accounts.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getAmount(int index) {
        final Account account = accounts[index];
        account.lock();
        try {
            return account.amount;
        } finally {
            account.unlock();
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getTotalAmount() {
        lock(accounts);
        try {
            long sum = 0;
            for (Account account : accounts) {
                sum += account.amount;
            }
            return sum;
        } finally {
            unlock(accounts);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long deposit(int index, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Invalid amount: " + amount);
        }
        final Account account = accounts[index];
        account.lock();
        try {
            if (amount > MAX_AMOUNT || account.amount + amount > MAX_AMOUNT) {
                throw new IllegalStateException("Overflow");
            }
            account.amount += amount;
            return account.amount;
        } finally {
            account.unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long withdraw(int index, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Invalid amount: " + amount);
        }
        final Account account = accounts[index];
        account.lock();
        try {
            if (account.amount - amount < 0) {
                throw new IllegalStateException("Underflow");
            }
            account.amount -= amount;
            amount = account.amount;
            return amount;
        } finally {
            account.unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void transfer(int fromIndex, int toIndex, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Invalid amount: " + amount);
        }
        if (fromIndex == toIndex) {
            throw new IllegalArgumentException("fromIndex == toIndex");
        }
        final Account from = accounts[fromIndex];
        final Account to = accounts[toIndex];
        final Account first = fromIndex < toIndex ? from : to;
        final Account second = fromIndex < toIndex ? to : from;
        lock(first, second);
        try {
            if (amount > from.amount) {
                throw new IllegalStateException("Underflow");
            } else if (amount > MAX_AMOUNT || to.amount + amount > MAX_AMOUNT) {
                throw new IllegalStateException("Overflow");
            }
            from.amount -= amount;
            to.amount += amount;
        } finally {
            unlock(first, second);
        }
    }

    /**
     * Private account data structure.
     */
    private static class Account {
        /**
         * Amount of funds in this account.
         */
        long amount;
        // get -5 points for naming it `mutex` :/
        Lock lock = new ReentrantLock();

        void lock() {
            lock.lock();
        }

        void unlock() {
            lock.unlock();
        }
    }
}
