package ba.unsa.etf.rma.spirala.list;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import ba.unsa.etf.rma.spirala.data.Account;
import ba.unsa.etf.rma.spirala.data.AccountInteractor;
import ba.unsa.etf.rma.spirala.data.IAccountInteractor;
import ba.unsa.etf.rma.spirala.data.Transaction;
import ba.unsa.etf.rma.spirala.data.TransactionAmount;
import ba.unsa.etf.rma.spirala.util.ILambda;
import ba.unsa.etf.rma.spirala.util.Lambda;

public class TransactionListPresenter implements ITransactionListPresenter, AccountInteractor.OnAccountSearchDone {
    private ITransactionListView view;
    private Context context;
    private Account account;
    ArrayList<Transaction> transactions = null;

    public TransactionListPresenter(ITransactionListView view, Context context) {
        new TransactionListInteractor(new Lambda(new ILambda() {
            @Override
            public Object callback(Object o) {
                transactions = (ArrayList<Transaction>) o;
                return 0;
            }
        }), context).execute();
        this.view = view;
        this.context = context;
        refreshAccount();
    }

    @Override
    public Account getAccount() {
        if(account == null) {
            Log.e("account is null trLiPr", "null");
        }
        return account;
    }


    @Override
    public void refreshTransactions(Transaction.Type t, String orderBy, Date d) {
        //ArrayList<Transaction> transactions = transactionInteractor.get();
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
    public void refreshAccount() {
        new AccountInteractor((AccountInteractor.OnAccountSearchDone)this, context).execute();
    }

    @Override
    public void getBudget(Lambda l) {
        if(account == null) {
            Log.e("acc null trLiPr gbu", "null");
        }
        new TransactionListInteractor(new Lambda(new ILambda() {
            @Override
            public Object callback(Object o) {
                return l.pass(account.getBudget() - TransactionAmount.getTotalAmount((ArrayList<Transaction>) o));
            }
        }), context).execute();
    }

    @Override
    public double getTotalLimit() {
        if(account == null) {
            Log.e("acc null trLiPr gtl", "null");
        }
        return account.getTotalLimit();
    }

    @Override
    public double getMonthLimit() {
        if(account == null) {
            Log.e("acc null trLiPr gml", "null");
        }
        return account.getMonthLimit();
    }

    @Override
    public void onDone(Account result) {
        this.account = result;
        view.setTextViewText(result);
    }
}
