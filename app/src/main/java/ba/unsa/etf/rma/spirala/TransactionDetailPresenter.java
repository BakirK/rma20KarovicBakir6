package ba.unsa.etf.rma.spirala;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;

public class TransactionDetailPresenter implements ITransactionDetailPresenter {
    private ITransactionListInteractor interactor;
    private Context context;
    private Transaction transaction;

    public TransactionDetailPresenter(Context context) {
        interactor = new TransactionListInteractor();
        this.context = context;
    }

    @Override
    public void deleteTransaction() {
        int id = transaction.getId();
        ArrayList<Transaction> transactions = interactor.get();
        int i = 0;
        for (Transaction transaction : transactions) {
            if(transaction.getId() == id) {
                interactor.remove(i);
                return;
            }
            i++;
        }
    }

    private void updateTransaction() {
        ArrayList<Transaction> transactions = interactor.get();
        int i = 0;
        int id = this.transaction.getId();
        for (Transaction t : transactions) {
            if(t.getId() == id) {
                interactor.set(i, this.transaction);
                Log.d("set", "index" + Integer.toString(i));
                return;
            }
            i++;
        }
        Log.d("set", "NOT FOUND" + Integer.toString(i));
    }

    @Override
    public void setTransaction(Transaction t) {
        this.transaction = t;
    }

    @Override
    public Transaction getTransaction() {
        return transaction;
    }

    @Override
    public boolean checkMonthlyBudget() {
        return false;
    }

    @Override
    public boolean checkTotalBudget() {
        return false;
    }

    @Override
    public void updateParameters(Date date, Double amount, String title, Transaction.Type type, @Nullable String itemDescription,
                                 @Nullable Integer transactionInterval, @Nullable Date endDate) {
        if(this.transaction == null) {
            this.transaction = interactor.createTransaction(date, amount, title, type, itemDescription, transactionInterval, endDate);
        } else {
            transaction.setTitle(title);
            transaction.setAmount(amount);
            transaction.setDate(date);
            transaction.setType(type);
            if(type == Transaction.Type.REGULARINCOME || type == Transaction.Type.INDIVIDUALINCOME) {
                transaction.setItemDescription(null);
            } else {
                transaction.setItemDescription(itemDescription);
            }

            if(type == Transaction.Type.REGULARINCOME || type == Transaction.Type.REGULARPAYMENT) {
                transaction.setTransactionInterval(transactionInterval);
                transaction.setEndDate(endDate);
            } else {
                transaction.setTransactionInterval(null);
                transaction.setEndDate(null);
            }
            updateTransaction();
        }

    }
}
