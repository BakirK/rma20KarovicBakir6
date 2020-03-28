package ba.unsa.etf.rma.spirala;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.stream.Collectors;

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
    public Account getAccount() {
        return accountInteractor.getAccount();
    }


    @Override
    public void refreshTransactions(Transaction.Type t, String orderBy, Date d) {
        ArrayList<Transaction> transactions = transactionInteractor.get();
        if(t != null && t != Transaction.Type.ALL) {
            ArrayList<Transaction> result = new ArrayList<>();
            for (Transaction transaction: transactions) {
                if(transaction.getType() == t) {
                    result.add(transaction);
                }
            }
            transactions = result;
        }
        if(d != null){
            ArrayList<Transaction> result = new ArrayList<>();
            //Log.d("Naziv", "BEGIN");
            for (Transaction transaction: transactions) {
                if(Transaction.isIndividual(transaction.getType())) {
                    if(Transaction.sameMonth(d, transaction.getDate())) {
                        result.add(transaction);
                    }
                } else {
                    if(Transaction.dateOverlapping(d, transaction)) {
                        result.add(transaction);
                    }
                }
            }
            transactions = result;
        }

        if(orderBy.startsWith("Price")) {
            if(orderBy.endsWith("Ascending")) {
                Collections.sort(transactions, (a, b) -> a.getAmount().compareTo(b.getAmount()));
            } else {
                Collections.sort(transactions, (a, b) -> b.getAmount().compareTo(a.getAmount()));
            }
        } else if(orderBy.startsWith("Title")) {
            if(orderBy.endsWith("Ascending")) {
                Collections.sort(transactions, (a, b) -> a.getTitle().compareTo(b.getTitle()));
            } else {
                Collections.sort(transactions, (a, b) -> b.getTitle().compareTo(a.getTitle()));
            }
        } else {
            if(orderBy.endsWith("Ascending")) {
                Collections.sort(transactions, (a, b) -> a.getDate().compareTo(b.getDate()));
            } else {
                Collections.sort(transactions, (a, b) -> b.getDate().compareTo(a.getDate()));
            }
        }
        view.setTransactions(transactions);
        view.notifyTransactionListDataSetChanged();
    }

    @Override
    public double getBudget() {
        return accountInteractor.getBudget() - transactionInteractor.getTotalAmount();
    }

    @Override
    public double getTotalLimit() {
        return accountInteractor.getTotalLimit();
    }

    @Override
    public double getMonthLimit() {
        return accountInteractor.getMonthLimit();
    }
}
