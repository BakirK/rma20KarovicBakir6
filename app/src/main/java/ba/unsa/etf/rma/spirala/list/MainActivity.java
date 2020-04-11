package ba.unsa.etf.rma.spirala.list;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import ba.unsa.etf.rma.spirala.R;
import ba.unsa.etf.rma.spirala.data.Transaction;
import ba.unsa.etf.rma.spirala.detail.TransactionDetailFragment;

public class MainActivity extends AppCompatActivity implements TransactionListFragment.OnItemClick {


    private boolean twoPaneMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FrameLayout details = findViewById(R.id.transaction_detail);
        if (details != null) {
            twoPaneMode = true;
            TransactionDetailFragment detailFragment = (TransactionDetailFragment)
                                        fragmentManager.findFragmentById(R.id.transaction_detail);
            if (detailFragment == null) {
                detailFragment = new TransactionDetailFragment();
                fragmentManager.beginTransaction().replace(R.id.transaction_detail, detailFragment).commit();
            }
        } else {
            twoPaneMode = false;
        }
        //Fragment listFragment = fragmentManager.findFragmentByTag("list");
        Fragment listFragment = fragmentManager.findFragmentById(R.id.transactions_list);
        if (listFragment == null){
            listFragment = new TransactionListFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.transactions_list, listFragment, "list")
                    .commit();
        } else {
            //slucaj kada mijenjamo orijentaciju uredaja
            //iz portrait (uspravna) u landscape (vodoravna)
            //a u aktivnosti je bio otvoren fragment MovieDetailFragment
            //tada je potrebno skinuti MovieDetailFragment sa steka
            //kako ne bi bio dodan na mjesto fragmenta MovieListFragment
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    @Override
    protected void onResume() {
        //TODO
       /* getPresenter().refreshTransactions((Transaction.Type)filterBySpinner.getSelectedItem(), sortBySpinner.getSelectedItem().toString(), d);
        textViewAmount.setText(String.format("%.2f", getPresenter().getBudget()));
        if(getIntent().getAction().equals(Intent.ACTION_INSERT)) {
            Toast.makeText(this, "Transaction added.", Toast.LENGTH_LONG).show();
        }*/
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

    @Override
    public void onItemClicked(Transaction transaction) {
        Bundle arguments = new Bundle();
        arguments.putParcelable("transaction", transaction);
        TransactionDetailFragment detailFragment = new TransactionDetailFragment();
        detailFragment.setArguments(arguments);
        if (twoPaneMode){
            getSupportFragmentManager().beginTransaction().replace(R.id.transaction_detail, detailFragment).commit();
        }
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.transactions_list,detailFragment).addToBackStack(null).commit();
        }
    }
}

