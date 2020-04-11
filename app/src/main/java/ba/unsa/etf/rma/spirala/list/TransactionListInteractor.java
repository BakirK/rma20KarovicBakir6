package ba.unsa.etf.rma.spirala.list;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;

import ba.unsa.etf.rma.spirala.data.Transaction;
import ba.unsa.etf.rma.spirala.data.TransactionModel;

public class TransactionListInteractor implements ITransactionListInteractor {
    @Override
    public ArrayList<Transaction> get() {
        return TransactionModel.transactions;
    }

    @Override
    public void addTransaction(Transaction t) {
        TransactionModel.transactions.add(t);
    }

    @Override
    public void remove(int i) {
        TransactionModel.transactions.remove(i);
    }

    @Override
    public void set(int i, Transaction updatedTransaction) {
        TransactionModel.transactions.set(i, updatedTransaction);
    }

    @Override
    public Transaction createTransaction(Date date,
                                         Double amount,
                                         String title,
                                         Transaction.Type type,
                                         @Nullable String itemDescription,
                                         @Nullable Integer transactionInterval,
                                         @Nullable Date endDate) {
        Transaction transaction = new Transaction(date, amount, title, type, itemDescription, transactionInterval, endDate);
        TransactionModel.transactions.add(transaction);
        return  transaction;
    }

    @Override
    public Double getTotalAmount() {
        Double sum = 0.;
        for (Transaction transaction: TransactionModel.transactions) {
            if(Transaction.isIncome(transaction.getType())) {
                continue;
            }
            if(Transaction.isIndividual(transaction.getType())) {
                /*if(Transaction.isIncome(transaction.getType())) {
                    sum -= transaction.getAmount();
                } else {*/
                    sum += transaction.getAmount();
                //}
            } else {
                Double thisAmount = transaction.getAmount();
                int monthsBetween = Transaction.monthsBetween(transaction.getDate(), transaction.getEndDate());
                int daysBetween = Transaction.getDaysBetween(transaction.getDate(), transaction.getEndDate());
                int averageNumberOfDaysPerMonth = daysBetween/(monthsBetween+1);
                thisAmount = thisAmount * averageNumberOfDaysPerMonth / transaction.getTransactionInterval();
                /*if(Transaction.isIncome(transaction.getType())) {
                    sum -= thisAmount;
                } else {*/
                    sum += thisAmount;
                //}
            }
        }
        return sum;
    }

    @Override
    public Double getMonthlyAmount(Date date) {
        Double sum = 0.;
        if(date != null){
            for (Transaction transaction: TransactionModel.transactions) {
                if(Transaction.isIncome(transaction.getType())) {
                    continue;
                }
                if(Transaction.isIndividual(transaction.getType())) {
                    if(Transaction.sameMonth(date, transaction.getDate())) {
                        /*if(Transaction.isIncome(transaction.getType())) {
                            sum -= transaction.getAmount();
                        } else {*/
                            sum += transaction.getAmount();
                        //}
                    }
                } else {
                    if(Transaction.dateOverlapping(date, transaction)) {
                        Double thisAmount = transaction.getAmount();
                        int monthsBetween = Transaction.monthsBetween(transaction.getDate(), transaction.getEndDate());
                        int daysBetween = Transaction.getDaysBetween(transaction.getDate(), transaction.getEndDate());
                        int averageNumberOfDaysPerMonth = daysBetween/(monthsBetween+1);
                        thisAmount = thisAmount * averageNumberOfDaysPerMonth / transaction.getTransactionInterval();
                        /*if(Transaction.isIncome(transaction.getType())) {
                            sum -= thisAmount;
                        } else {*/
                            sum += thisAmount;
                        //}
                    }
                }
            }
        }
        return sum;
    }
}
