package ba.unsa.etf.rma.spirala.data;

import java.util.ArrayList;
import java.util.Date;

public class TransactionAmount {

    public static Double getTotalAmount(ArrayList<Transaction> transactions) {
        Double sum = 0.;
        for (Transaction transaction: transactions) {
            if(Transaction.isIncome(transaction.getType())) {
                continue;
            }
            if(Transaction.isIndividual(transaction.getType())) {
                sum += transaction.getAmount();
            } else {
                Double thisAmount = transaction.getAmount();
                int monthsBetween = Transaction.monthsBetween(transaction.getDate(), transaction.getEndDate());
                int daysBetween = Transaction.getDaysBetween(transaction.getDate(), transaction.getEndDate());
                int averageNumberOfDaysPerMonth = daysBetween/(monthsBetween+1);
                thisAmount = thisAmount * averageNumberOfDaysPerMonth / transaction.getTransactionInterval();
                sum += thisAmount;
            }
        }
        return sum;
    }

    public static Double getMonthlyAmount(ArrayList<Transaction> transactions, Date date) {
        Double sum = 0.;
        if(date != null){
            for (Transaction transaction: transactions) {
                if(Transaction.isIncome(transaction.getType())) {
                    continue;
                }
                if(Transaction.isIndividual(transaction.getType())) {
                    if(Transaction.sameMonth(date, transaction.getDate())) {
                        sum += transaction.getAmount();
                    }
                } else {
                    if(Transaction.dateOverlapping(date, transaction)) {
                        Double thisAmount = transaction.getAmount();
                        int monthsBetween = Transaction.monthsBetween(transaction.getDate(), transaction.getEndDate());
                        int daysBetween = Transaction.getDaysBetween(transaction.getDate(), transaction.getEndDate());
                        int averageNumberOfDaysPerMonth = daysBetween/(monthsBetween+1);
                        thisAmount = thisAmount * averageNumberOfDaysPerMonth / transaction.getTransactionInterval();
                        sum += thisAmount;
                    }
                }
            }
        }
        return sum;
    }
}
