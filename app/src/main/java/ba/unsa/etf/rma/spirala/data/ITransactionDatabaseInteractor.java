package ba.unsa.etf.rma.spirala.data;

import android.content.Context;

public interface ITransactionDatabaseInteractor {
    void addTransaction(Context context, Transaction t);
    Transaction getTransaction(Context context, int internalId);
    void updateTransaction(Context context, Transaction t);
    void deleteTransaction(Context context, int internalId);
}
