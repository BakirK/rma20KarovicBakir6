package ba.unsa.etf.rma.spirala;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TransactionDetailPresenter implements ITransactionDetailPresenter {
    private ITransactionListInteractor transactionInteractor;
    private IAccountInteractor accountInteractor;
    private Context context;
    private Transaction transaction;

    public TransactionDetailPresenter(Context context) {
        transactionInteractor = new TransactionListInteractor();
        accountInteractor = new AccountInteractor();
        this.context = context;
    }

    @Override
    public void deleteTransaction() {
        int id = transaction.getId();
        ArrayList<Transaction> transactions = transactionInteractor.get();
        int i = 0;
        for (Transaction transaction : transactions) {
            if(transaction.getId() == id) {
                transactionInteractor.remove(i);
                return;
            }
            i++;
        }
    }

    private void updateTransaction() {
        ArrayList<Transaction> transactions = transactionInteractor.get();
        int i = 0;
        int id = this.transaction.getId();
        for (Transaction t : transactions) {
            if(t.getId() == id) {
                transactionInteractor.set(i, this.transaction);
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
    public boolean overMonthLimit(Date date, double amount, String title, Transaction.Type type, @Nullable String itemDescription, @Nullable Integer transactionInterval, @Nullable Date endDate) {
        Double thisAmount = 0.;
        if(this.transaction != null) {
            if(Transaction.isIncome(this.transaction.getType())) {
                thisAmount = -this.transaction.getAmount();
            } else {
                thisAmount = this.transaction.getAmount();
            }
            if(Transaction.isRegular(transaction.getType())) {
                int monthsBetween = Transaction.monthsBetween(transaction.getDate(), transaction.getEndDate());
                int daysBetween = Transaction.getDaysBetween(transaction.getDate(), transaction.getEndDate());
                int averageNumberOfDaysPerMonth = daysBetween/(monthsBetween+1);
                thisAmount = thisAmount * averageNumberOfDaysPerMonth / transaction.getTransactionInterval();
            }
        }

        if(Transaction.isIncome(type)) {
            amount *= -1;
        }

        if(Transaction.isRegular(type)) {
            int monthsBetween = Transaction.monthsBetween(date, endDate);
            int daysBetween = Transaction.getDaysBetween(date, endDate);
            int averageNumberOfDaysPerMonth = daysBetween/(monthsBetween+1);
            amount = amount * averageNumberOfDaysPerMonth / transactionInterval;
        }
        Double monthExpenses = transactionInteractor.getMonthlyAmount(date) - thisAmount + amount;
        return accountInteractor.getMonthLimit() < monthExpenses;
    }


    @Override
    public boolean overGlobalLimit(Date date, double amount, String title, Transaction.Type type, @Nullable String itemDescription, @Nullable Integer transactionInterval, @Nullable Date endDate) {
        Double thisAmount = 0.;
        if(this.transaction != null) {
            if(Transaction.isIncome(this.transaction.getType())) {
                thisAmount = -this.transaction.getAmount();
            } else {
                thisAmount = this.transaction.getAmount();
            }
            if(Transaction.isRegular(transaction.getType())) {
                thisAmount = thisAmount * Transaction.getDaysBetween(transaction.getDate(), transaction.getEndDate());
            }
        }
        if(Transaction.isIncome(type)) {
            amount *= -1;
        }

        if(Transaction.isRegular(type)) {
            amount = amount * Transaction.getDaysBetween(date, endDate);
        }
        Double totalExpenses = transactionInteractor.getTotalAmount() - thisAmount + amount;
        Log.d("total", Double.toString(totalExpenses));
        Log.d("limit", Double.toString(accountInteractor.getTotalLimit()));
        return totalExpenses > accountInteractor.getTotalLimit();
    }

    @Override
    public void updateParameters(Date date, Double amount, String title, Transaction.Type type, @Nullable String itemDescription,
                                 @Nullable Integer transactionInterval, @Nullable Date endDate) {
        if(this.transaction == null) {
            this.transaction = transactionInteractor.createTransaction(date, amount, title, type, itemDescription, transactionInterval, endDate);
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
