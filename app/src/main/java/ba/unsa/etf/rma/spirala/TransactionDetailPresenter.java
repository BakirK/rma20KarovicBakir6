package ba.unsa.etf.rma.spirala;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

public class TransactionDetailPresenter implements ITransactionDetailPresenter {
    private ITransactionListInteractor interactor;
    private Context context;

    public TransactionDetailPresenter(Context context) {
        interactor = new TransactionListInteractor();
        this.context = context;
    }

    @Override
    public void deleteTransaction(int id) {
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

    @Override
    public void updateTransaction(Transaction updatedTransaction) {
        ArrayList<Transaction> transactions = interactor.get();
        int i = 0;
        int id = updatedTransaction.getId();
        for (Transaction transaction : transactions) {
            if(transaction.getId() == id) {
                interactor.set(i, updatedTransaction);
                //Log.d("INDEX:", Integer.toString(i));
                return;
            }
            i++;
        }
    }
}
