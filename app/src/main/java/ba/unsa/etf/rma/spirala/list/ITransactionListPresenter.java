package ba.unsa.etf.rma.spirala.list;

import java.util.Date;

import ba.unsa.etf.rma.spirala.data.Account;
import ba.unsa.etf.rma.spirala.data.Transaction;

public interface ITransactionListPresenter {
    Account getAccount();
    void refreshTransactions(Transaction.Type t, String orderBy, Date d);
    double getBudget();
    double getTotalLimit();
    double getMonthLimit();
}
