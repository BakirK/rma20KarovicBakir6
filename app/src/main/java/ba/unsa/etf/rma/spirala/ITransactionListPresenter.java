package ba.unsa.etf.rma.spirala;

import java.util.ArrayList;
import java.util.Date;

public interface ITransactionListPresenter {
    Account getAccount();
    void refreshTransactions(Transaction.Type t, String orderBy, Date d);
    double getBudget();
    double getTotalLimit();
    double getMonthLimit();
}
