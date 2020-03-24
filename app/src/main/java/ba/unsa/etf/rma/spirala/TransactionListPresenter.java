package ba.unsa.etf.rma.spirala;

import android.content.Context;

public class TransactionListPresenter implements ITransactionListPresenter {
    private ITransactionListInteractor transactionInteractor;
    private IAccountInteractor accountInteractor;
    private ITransactionListView view;
    private Context context;

    public TransactionListPresenter(ITransactionListView view, Context context) {
        this.transactionInteractor = new TransactionListInteractor();
        accountInteractor = new AccountInteractor();
        this.view = view;
        this.context = context;
    }

    @Override
    public void refreshTransactions() {
        view.setTransactions(transactionInteractor.get());
        view.notifyTransactionListDataSetChanged();
    }

    @Override
    public Account getAccount() {
        return accountInteractor.getAccount();
    }
}
