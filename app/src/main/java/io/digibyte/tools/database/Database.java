package io.digibyte.tools.database;

import android.arch.persistence.room.Room;
import android.os.Handler;
import android.os.Looper;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.digibyte.DigiByte;

public class Database {
    public static Database instance = new Database();
    public TransactionDao transactionDao;
    public AddressBookDao addressBookDao;
    private Executor transactionExecutor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());
    public List<DigiTransaction> transactions = new LinkedList<>();

    public Database() {
        AppDatabase database = Room.databaseBuilder(DigiByte.getContext(),
                AppDatabase.class, "transaction_database")
                .addMigrations(Migrations.MIGRATION_6_7)
                .fallbackToDestructiveMigration().build();
        transactionDao = database.transactionDao();
        addressBookDao = database.addressBookDao();
        updateTransaction(null);
    }

    public void saveTransaction(byte[] txHash, String amount) {
        transactionExecutor.execute(() -> {
            DigiTransaction digiTransaction = new DigiTransaction();
            digiTransaction.setTxHash(txHash);
            digiTransaction.setTxAmount(amount);
            transactionDao.insertAll(digiTransaction);
            transactions = transactionDao.getAll();
        });
    }

    public void updateTransaction(TransactionStoreListener transactionStoreListener) {
        transactionExecutor.execute(() -> {
            transactions = transactionDao.getAll();
            handler.post(() -> {
                if (transactionStoreListener != null) {
                    transactionStoreListener.onTransactionsUpdate();
                }
            });
        });
    }

    public interface TransactionStoreListener {
        void onTransactionsUpdate();
    }

    public boolean containsTransaction(byte[] txHash) {
        return findTransaction(txHash) != null;
    }

    public DigiTransaction findTransaction(byte[] txHash) {
        for (DigiTransaction digiTransaction : transactions) {
            if (Arrays.equals(txHash, digiTransaction.getTxHash())) {
                return digiTransaction;
            }
        }
        return null;
    }
}

