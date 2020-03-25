package ba.unsa.etf.rma.spirala;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements ITransactionListView {
    private TransactionListAdapter adapter;
    private ITransactionListPresenter presenter;
    private ListView transactionList;
    private TextView textViewAmount;
    private TextView textViewLimit;
    private TextView dateText;
    private Spinner filterBySpinner;
    private Spinner sortBySpinner;
    private ImageButton nextBtn, prevBtn;
    private Account account;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new TransactionListAdapter(getApplicationContext(), R.layout.list_element, new ArrayList<Transaction>());
        transactionList = (ListView) findViewById(R.id.transactionList);
        transactionList.setAdapter(adapter);
        getPresenter().refreshTransactions(null, "Price - Ascending");
        init();
        fillSpinners();

        textViewAmount.setText(Double.toString(account.getBudget()));
        textViewLimit.setText(Double.toString(account.getMonthLimit()));
        dateText.setText(new Date().toString());

    }

    private void init() {
        textViewAmount = (TextView) findViewById(R.id.textViewAmount);
        textViewLimit = (TextView) findViewById(R.id.textViewLimit);
        dateText = (TextView) findViewById(R.id.dateText);
        filterBySpinner = findViewById(R.id.sortBySpinner);
        sortBySpinner = findViewById(R.id.filterBySpinner);
        nextBtn = (ImageButton) findViewById(R.id.nextBtn);
        prevBtn = (ImageButton) findViewById(R.id.prevBtn);
        account = presenter.getAccount();
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
        ArrayAdapter<Transaction.Type> filterArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filterList);
        filterArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterBySpinner.setAdapter(filterArrayAdapter);
        filterBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getPresenter().refreshTransactions((Transaction.Type)parent.getItemAtPosition(position), sortBySpinner.getSelectedItem().toString());
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
                getPresenter().refreshTransactions((Transaction.Type)filterBySpinner.getSelectedItem(), parent.getItemAtPosition(position).toString());
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

