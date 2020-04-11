package ba.unsa.etf.rma.spirala.list;

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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ba.unsa.etf.rma.spirala.OnItemClick;
import ba.unsa.etf.rma.spirala.R;
import ba.unsa.etf.rma.spirala.data.Transaction;

public class TransactionListFragment extends Fragment implements ITransactionListView {
    private TransactionListAdapter adapter;
    private TransactionSpinnerAdapter spinnerAdapter;
    private ITransactionListPresenter presenter;
    private ListView transactionList;
    private TextView textViewAmount;
    private TextView textViewLimit;
    private TextView dateText;
    private TextView monthlyLimit;
    private Spinner filterBySpinner;
    private Spinner sortBySpinner;
    private ImageButton nextBtn, prevBtn;
    private Date d;
    private OnItemClick onItemClick;
    private Integer previousSelectedItemIndex;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_list, container, false);
        adapter = new TransactionListAdapter(getActivity().getApplicationContext(), R.layout.list_element, new ArrayList<Transaction>());
        transactionList = (ListView) fragmentView.findViewById(R.id.transactionList);
        transactionList.setAdapter(adapter);
        d = new Date();
        getPresenter().refreshTransactions(null, "Price - Ascending", d);
        init(fragmentView);
        fillSpinners();
        initListeners();
        return fragmentView;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        boolean myBoolean = savedInstanceState.getBoolean("MyBoolean");
        double myDouble = savedInstanceState.getDouble("myDouble");
        int myInt = savedInstanceState.getInt("MyInt");
        String myString = savedInstanceState.getString("MyString");
    }

    private void init(View fragmentView) {
        textViewAmount = (TextView) fragmentView.findViewById(R.id.textViewAmount);
        textViewLimit = (TextView) fragmentView.findViewById(R.id.textViewLimit);
        dateText = (TextView) fragmentView.findViewById(R.id.dateText);
        monthlyLimit = (TextView) fragmentView.findViewById(R.id.monthlyLimit);
        filterBySpinner = fragmentView.findViewById(R.id.filterBySpinner);
        sortBySpinner = fragmentView.findViewById(R.id.sortBySpinner);
        nextBtn = (ImageButton) fragmentView.findViewById(R.id.nextBtn);
        prevBtn = (ImageButton) fragmentView.findViewById(R.id.prevBtn);
        textViewAmount.setText(String.format("%.2f", getPresenter().getBudget()));
        textViewLimit.setText(String.format("%.2f", getPresenter().getTotalLimit()));
        monthlyLimit.setText(String.format("%.2f", getPresenter().getMonthLimit()));
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        dateText.setText(format.format(d));
        try {
            onItemClick = (OnItemClick)getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + "Treba implementirati OnItemClick interfejs");
        }
        previousSelectedItemIndex = -1;
    }

    private void initListeners() {
        prevBtn.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(d);
            calendar.add(Calendar.MONTH, -1);
            d = calendar.getTime();
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            dateText.setText(format.format(d));
            getPresenter().refreshTransactions((Transaction.Type)filterBySpinner.getSelectedItem(), sortBySpinner.getSelectedItem().toString(), d);
        });

        nextBtn.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(d);
            calendar.add(Calendar.MONTH, 1);
            d = calendar.getTime();
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            dateText.setText(format.format(d));
            getPresenter().refreshTransactions((Transaction.Type)filterBySpinner.getSelectedItem(), sortBySpinner.getSelectedItem().toString(), d);
        });

        AdapterView.OnItemClickListener listItemClickListener = (parent, view, position, id) -> {
            /*Intent transactionDetailIntent = new Intent(MainActivity.this, TransactionDetailActivity.class);
            transactionDetailIntent.setAction(Intent.ACTION_ATTACH_DATA);
            Transaction transaction = adapter.getTransactionAt(position);
            transactionDetailIntent.putExtra("TRANSACTION", transaction);
            MainActivity.this.startActivity(transactionDetailIntent);*/
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

        transactionList.setOnItemClickListener(listItemClickListener);
    }

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
                getPresenter().refreshTransactions((Transaction.Type)parent.getItemAtPosition(position), sortBySpinner.getSelectedItem().toString(), d);
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
                //getPresenter().sak();
                getPresenter().refreshTransactions((Transaction.Type)filterBySpinner.getSelectedItem(), parent.getItemAtPosition(position).toString(), d);
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


    public ITransactionListPresenter getPresenter() {
        if(presenter == null) {
            presenter = new TransactionListPresenter(this, getActivity());
        }
        return presenter;
    }

    public void updateTransactionListData() {
        getPresenter().refreshTransactions((Transaction.Type)filterBySpinner.getSelectedItem(), sortBySpinner.getSelectedItem().toString(), d);
        textViewAmount.setText(String.format("%.2f", getPresenter().getBudget()));
    }
}
