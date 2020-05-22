package ba.unsa.etf.rma.spirala.list;

import java.util.Date;

import ba.unsa.etf.rma.spirala.data.Account;
import ba.unsa.etf.rma.spirala.data.Transaction;
import ba.unsa.etf.rma.spirala.util.Callback;

public interface ITransactionListPresenter {
    Account getAccount();
    void refreshTransactions(Transaction.Type t, String orderBy, Date d);
    void refreshAccount();
    void getBudget(Callback l);
    void getTotalLimit(Callback l);
    void getMonthLimit(Callback l);
}
