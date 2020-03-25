package ba.unsa.etf.rma.spirala;

import android.content.Context;

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

    private boolean isIndividualPayment(Transaction.Type t) {
        return t == Transaction.Type.INDIVIDUALPAYMENT || t == Transaction.Type.INDIVIDUALINCOME || t == Transaction.Type.PURCHASE;
    }

    private boolean dateOverlapping(Date d, Transaction transaction) {
        return ((transaction.getEndDate().after(d) || transaction.getEndDate().equals(d)) && (transaction.getDate().before(d) || transaction.getDate().equals(d)));
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
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(d);
        int yearNow = calendar.get(Calendar.YEAR), monthNow = calendar.get(Calendar.MONTH) + 1, dayNow = calendar.get(Calendar.DAY_OF_MONTH);

        if(d != null){
            ArrayList<Transaction> result = new ArrayList<>();
            for (Transaction transaction: transactions) {
                if(isIndividualPayment(transaction.getType())) {
                    calendar.setTime(transaction.getDate());
                    int year = calendar.get(Calendar.YEAR), month = calendar.get(Calendar.MONTH) + 1;
                    if(year == yearNow && month == monthNow) {
                        result.add(transaction);
                    }
                } else {
                    if(dateOverlapping(d, transaction)) {
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
}
