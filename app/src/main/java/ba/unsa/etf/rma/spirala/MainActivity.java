package ba.unsa.etf.rma.spirala;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements ITransactionListView {
    private TransactionListAdapter adapter;
    private TransactionSpinnerAdapter spinnerAdapter;
    private ITransactionListPresenter presenter;
    private ListView transactionList;
    private TextView textViewAmount;
    private TextView textViewLimit;
    private TextView dateText;
    private Spinner filterBySpinner;
    private Spinner sortBySpinner;
    private ImageButton nextBtn, prevBtn;
    private Account account;
    private Date d;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new TransactionListAdapter(getApplicationContext(), R.layout.list_element, new ArrayList<Transaction>());
        transactionList = (ListView) findViewById(R.id.transactionList);
        transactionList.setAdapter(adapter);
        d = new Date();
        getPresenter().refreshTransactions(null, "Price - Ascending", d);
        init();
        fillSpinners();
        initListeners();
    }

    @Override
    protected void onResume() {
        getPresenter().refreshTransactions((Transaction.Type)filterBySpinner.getSelectedItem(), sortBySpinner.getSelectedItem().toString(), d);
        if(getIntent().getAction().equals(Intent.ACTION_INSERT)) {
            Toast.makeText(this, "Transaction added.", Toast.LENGTH_LONG).show();
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if(!getIntent().getAction().equals(Intent.ACTION_DEFAULT)) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Prethodna transakcija je izbrisana!", Toast.LENGTH_LONG).show();
        }
    }

    private void init() {
        textViewAmount = (TextView) findViewById(R.id.textViewAmount);
        textViewLimit = (TextView) findViewById(R.id.textViewLimit);
        dateText = (TextView) findViewById(R.id.dateText);
        filterBySpinner = findViewById(R.id.filterBySpinner);
        sortBySpinner = findViewById(R.id.sortBySpinner);
        nextBtn = (ImageButton) findViewById(R.id.nextBtn);
        prevBtn = (ImageButton) findViewById(R.id.prevBtn);
        account = presenter.getAccount();
        textViewAmount.setText(Double.toString(account.getBudget()));
        textViewLimit.setText(Double.toString(account.getMonthLimit()));
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        dateText.setText(format.format(d));
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
            Intent transactionDetailIntent = new Intent(MainActivity.this, TransactionDetailActivity.class);
            transactionDetailIntent.setAction(Intent.ACTION_ATTACH_DATA);
            Transaction transaction = adapter.getTransactionAt(position);

            /*transactionDetailIntent.putExtra("AMOUNT", transaction.getAmount().toString());
            transactionDetailIntent.putExtra("TITLE", transaction.getTitle());
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(transaction.getDate());
            transactionDetailIntent.putExtra("DATE_YEAR", calendar.get(Calendar.YEAR));
            transactionDetailIntent.putExtra("DATE_MONTH", calendar.get(Calendar.MONTH));
            transactionDetailIntent.putExtra("DATE_DAY", calendar.get(Calendar.DAY_OF_MONTH));

            transactionDetailIntent.putExtra("ITEM_DESCRIPTION", transaction.getItemDescription() == null ? "" : transaction.getTransactionInterval());
            transactionDetailIntent.putExtra("TYPE", transaction.getType().toString());
            transactionDetailIntent.putExtra("INTERVAL", transaction.getTransactionInterval() == null ? "" : transaction.getTransactionInterval().toString());
            if(transaction.getEndDate() != null) {
                calendar.setTime(transaction.getEndDate());
                transactionDetailIntent.putExtra("END_DATE_YEAR", calendar.get(Calendar.YEAR));
                transactionDetailIntent.putExtra("END_DATE_MONTH", calendar.get(Calendar.MONTH));
                transactionDetailIntent.putExtra("END_DATE_DAY", calendar.get(Calendar.DAY_OF_MONTH));
            } else {
                transactionDetailIntent.putExtra("END_DATE_YEAR", "");
                transactionDetailIntent.putExtra("END_DATE_MONTH", "");
                transactionDetailIntent.putExtra("END_DATE_DAY", "");
            }*/
            transactionDetailIntent.putExtra("TRANSACTION", transaction);
            MainActivity.this.startActivity(transactionDetailIntent);
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
        spinnerAdapter = new TransactionSpinnerAdapter(getApplicationContext(), R.layout.spinner_element, filterList);
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
        ArrayAdapter<String> sortArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sortList);
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
            presenter = new TransactionListPresenter(this, this);
        }
        return presenter;
    }
}

