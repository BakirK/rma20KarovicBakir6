package ba.unsa.etf.rma.spirala;

import java.util.ArrayList;

public interface ITransactionListPresenter {
    Account getAccount();
    void refreshTransactions(Transaction.Type t, String orderBy);
}
