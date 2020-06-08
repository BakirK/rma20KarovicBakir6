package ba.unsa.etf.rma.spirala.list;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ba.unsa.etf.rma.spirala.data.Account;
import ba.unsa.etf.rma.spirala.listeners.OnItemClick;
import ba.unsa.etf.rma.spirala.listeners.OnSwipeTouchListener;
import ba.unsa.etf.rma.spirala.R;
import ba.unsa.etf.rma.spirala.data.Transaction;
import ba.unsa.etf.rma.spirala.util.ICallback;
import ba.unsa.etf.rma.spirala.util.Callback;
import ba.unsa.etf.rma.spirala.util.TransactionDBOpenHelper;

public class TransactionListFragment extends Fragment implements ITransactionListView, InternetConnectivityListener {
    private TransactionListAdapter adapter;
    private TransactionSpinnerAdapter spinnerAdapter;
    private ITransactionListPresenter presenter;
    private ListView transactionList;
    private TextView textViewAmount;
    private TextView textViewLimit;
    private TextView dateText;
    private TextView monthlyLimit, offlineText;
    private Spinner filterBySpinner;
    private Spinner sortBySpinner;
    private ImageButton nextBtn, prevBtn;
    private Date d;
    private OnItemClick onItemClick;
    private Integer previousSelectedItemIndex;
    private ConstraintLayout layout;
    private InternetAvailabilityChecker mInternetAvailabilityChecker;
    private TransactionListCursorAdapter transactionListCursorAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_list, container, false);
        adapter = new TransactionListAdapter(getActivity().getApplicationContext(), R.layout.list_element, new ArrayList<Transaction>());
        transactionListCursorAdapter = new TransactionListCursorAdapter(getActivity(), R.layout.list_element, null, false);

        InternetAvailabilityChecker.init(getActivity());
        mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
        mInternetAvailabilityChecker.addInternetConnectivityListener(this);

        transactionList = (ListView) fragmentView.findViewById(R.id.transactionList);

        transactionList.setAdapter(transactionListCursorAdapter);
        d = new Date();
        getPresenter().refreshCursorTransactions(null, "Price - Ascending", d);

        init(fragmentView);
        fillSpinners();
        initListeners();
        return fragmentView;
    }


    private void init(View fragmentView) {
        textViewAmount = (TextView) fragmentView.findViewById(R.id.textViewAmount);
        textViewLimit = (TextView) fragmentView.findViewById(R.id.textViewLimit);
        dateText = (TextView) fragmentView.findViewById(R.id.dateText);
        monthlyLimit = (TextView) fragmentView.findViewById(R.id.monthlyLimit);
        offlineText = (TextView) fragmentView.findViewById(R.id.offlineText);
        filterBySpinner = fragmentView.findViewById(R.id.filterBySpinner);
        sortBySpinner = fragmentView.findViewById(R.id.sortBySpinner);
        nextBtn = (ImageButton) fragmentView.findViewById(R.id.nextBtn);
        prevBtn = (ImageButton) fragmentView.findViewById(R.id.prevBtn);
        getPresenter().refreshAccount(mInternetAvailabilityChecker.getCurrentInternetAvailabilityStatus());
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        dateText.setText(format.format(d));
        try {
            onItemClick = (OnItemClick)getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + "Treba implementirati OnItemClick interfejs");
        }
        previousSelectedItemIndex = -1;
        layout = (ConstraintLayout) fragmentView.findViewById(R.id.layout);

        if(mInternetAvailabilityChecker.getCurrentInternetAvailabilityStatus()) {
            offlineText.setVisibility(View.INVISIBLE);
        } else {
            offlineText.setVisibility(View.VISIBLE);
        }
    }

    private void initListeners() {
        layout.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            @Override
            public void onSwipeLeft() {
                onItemClick.displayAccount();
            }

            @Override
            public void onSwipeRight() {
                onItemClick.displayGraphs();
            }
        });
        prevBtn.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(d);
            calendar.add(Calendar.MONTH, -1);
            d = calendar.getTime();
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            dateText.setText(format.format(d));
            if (mInternetAvailabilityChecker.getCurrentInternetAvailabilityStatus()) {
                transactionList.setAdapter(adapter);
                transactionList.setOnItemClickListener(listItemClickListener);
                getPresenter().refreshTransactions((Transaction.Type)filterBySpinner.getSelectedItem(), sortBySpinner.getSelectedItem().toString(), d);
            }
            else {
                transactionList.setAdapter(transactionListCursorAdapter);
                transactionList.setOnItemClickListener(listCursorItemClickListener);
                getPresenter().refreshCursorTransactions((Transaction.Type)filterBySpinner.getSelectedItem(), sortBySpinner.getSelectedItem().toString(), d);
            }
        });

        nextBtn.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(d);
            calendar.add(Calendar.MONTH, 1);
            d = calendar.getTime();
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            dateText.setText(format.format(d));
            if (mInternetAvailabilityChecker.getCurrentInternetAvailabilityStatus()) {
                transactionList.setAdapter(adapter);
                transactionList.setOnItemClickListener(listItemClickListener);
                getPresenter().refreshTransactions((Transaction.Type)filterBySpinner.getSelectedItem(), sortBySpinner.getSelectedItem().toString(), d);
            }
            else {
                transactionList.setAdapter(transactionListCursorAdapter);
                transactionList.setOnItemClickListener(listCursorItemClickListener);
                getPresenter().refreshCursorTransactions((Transaction.Type)filterBySpinner.getSelectedItem(), sortBySpinner.getSelectedItem().toString(), d);
            }
        });
        transactionList.setOnItemClickListener(listCursorItemClickListener);
    }

    private AdapterView.OnItemClickListener listItemClickListener = (parent, view, position, id) -> {
        if(previousSelectedItemIndex == position) {
            onItemClick.displayTransaction(null);
            previousSelectedItemIndex = -1;
            getPresenter().refreshTransactions((Transaction.Type)filterBySpinner.getSelectedItem(), sortBySpinner.getSelectedItem().toString(), d);
        } else {
            previousSelectedItemIndex = position;
            Transaction transaction = adapter.getTransactionAt(position);
            onItemClick.displayTransaction(transaction);
        }
    };

    private AdapterView.OnItemClickListener listCursorItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            if(cursor != null) {
                onItemClick.displayDatabaseTransaction(cursor.getInt(cursor.getColumnIndex(TransactionDBOpenHelper.TRANSACTION_INTERNAL_ID)));
            }
        }
    };

    private void fillSpinners() {
        //filterBy
        ArrayList<Transaction.Type> filterList = new ArrayList<>();
        filterList.add(Transaction.Type.ALL);
        filterList.add(Transaction.Type.INDIVIDUALINCOME);
        filterList.add(Transaction.Type.INDIVIDUALPAYMENT);
        filterList.add(Transaction.Type.PURCHASE);
        filterList.add(Transaction.Type.REGULARINCOME);
        filterList.add(Transaction.Type.REGULARPAYMENT);
        spinnerAdapter = new TransactionSpinnerAdapter(getActivity().getApplicationContext(), R.layout.spinner_element, filterList);
        filterBySpinner.setAdapter(spinnerAdapter);
        filterBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mInternetAvailabilityChecker.getCurrentInternetAvailabilityStatus()) {
                    transactionList.setAdapter(adapter);
                    transactionList.setOnItemClickListener(listItemClickListener);
                    getPresenter().refreshTransactions((Transaction.Type)parent.getItemAtPosition(position), sortBySpinner.getSelectedItem().toString(), d);
                }
                else {
                    transactionList.setAdapter(transactionListCursorAdapter);
                    transactionList.setOnItemClickListener(listCursorItemClickListener);
                    getPresenter().refreshCursorTransactions((Transaction.Type)parent.getItemAtPosition(position), sortBySpinner.getSelectedItem().toString(), d);
                }
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
            }
        });



        //sortBy
        ArrayList<String> sortList = new ArrayList<>();
        sortList.add("Price - Ascending");
        sortList.add("Price - Descending");
        sortList.add("Title - Ascending");
        sortList.add("Title - Descending");
        sortList.add("Date - Ascending");
        sortList.add("Date - Descending");
        ArrayAdapter<String> sortArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, sortList);
        sortArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortBySpinner.setAdapter(sortArrayAdapter);
        sortBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mInternetAvailabilityChecker.getCurrentInternetAvailabilityStatus()) {
                    transactionList.setAdapter(adapter);
                    transactionList.setOnItemClickListener(listItemClickListener);
                    getPresenter().refreshTransactions((Transaction.Type)filterBySpinner.getSelectedItem(), parent.getItemAtPosition(position).toString(), d);
                }
                else {
                    transactionList.setAdapter(transactionListCursorAdapter);
                    transactionList.setOnItemClickListener(listCursorItemClickListener);
                    getPresenter().refreshCursorTransactions((Transaction.Type)filterBySpinner.getSelectedItem(), parent.getItemAtPosition(position).toString(), d);
                }
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
            }
        });

    }

    @Override
    public void setTransactions(ArrayList<Transaction> transactions) {
        adapter.setTransactions(transactions);
    }

    @Override
    public void notifyTransactionListDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setTextViewText(Account account) {
        if(account != null) {
            textViewAmount.setText(String.format("%.2f", account.getBudget()));
            textViewLimit.setText(String.format("%.2f", account.getTotalLimit()));
            monthlyLimit.setText(String.format("%.2f", account.getMonthLimit()));
        }

    }

    @Override
    public void setCursor(Cursor cursor) {
        transactionList.setAdapter(transactionListCursorAdapter);
        transactionList.setOnItemClickListener(listCursorItemClickListener);
        transactionListCursorAdapter.changeCursor(cursor);
    }


    public ITransactionListPresenter getPresenter() {
        if(presenter == null) {
            presenter = new TransactionListPresenter(this, getActivity());
        }
        return presenter;
    }

    public void updateTransactionListData() {
        if(mInternetAvailabilityChecker.getCurrentInternetAvailabilityStatus()) {
            transactionList.setAdapter(adapter);
            transactionList.setOnItemClickListener(listItemClickListener);
            getPresenter().refreshTransactions((Transaction.Type)filterBySpinner.getSelectedItem(), sortBySpinner.getSelectedItem().toString(), d);
            getPresenter().getBudget(
                    new Callback(new ICallback() {
                        @Override
                        public Object callback(Object o) {
                            Double budget = (Double) o;
                            textViewAmount.setText(String.format("%.2f", budget));
                            return 0;
                        }
                    })
            );
        } else {
            transactionList.setAdapter(transactionListCursorAdapter);
            transactionList.setOnItemClickListener(listCursorItemClickListener);
            getPresenter().refreshCursorTransactions((Transaction.Type)filterBySpinner.getSelectedItem(), sortBySpinner.getSelectedItem().toString(), d);

        }
    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
        if (isConnected) {
            transactionList.setAdapter(adapter);
            transactionList.setOnItemClickListener(listItemClickListener);
            getPresenter().refreshTransactions((Transaction.Type)filterBySpinner.getSelectedItem(), sortBySpinner.getSelectedItem().toString(), d);
            offlineText.setVisibility(View.INVISIBLE);

            getPresenter().refreshAccount(mInternetAvailabilityChecker.getCurrentInternetAvailabilityStatus());
        }
        else {
            transactionList.setAdapter(transactionListCursorAdapter);
            transactionList.setOnItemClickListener(listCursorItemClickListener);
            getPresenter().refreshCursorTransactions((Transaction.Type)filterBySpinner.getSelectedItem(), sortBySpinner.getSelectedItem().toString(), d);
            offlineText.setVisibility(View.VISIBLE);

            getPresenter().refreshAccount(mInternetAvailabilityChecker.getCurrentInternetAvailabilityStatus());
        }
    }
}
