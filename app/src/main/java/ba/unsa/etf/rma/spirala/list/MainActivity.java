package ba.unsa.etf.rma.spirala.list;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import ba.unsa.etf.rma.spirala.graphs.GraphsFragment;
import ba.unsa.etf.rma.spirala.budget.BudgetFragment;
import ba.unsa.etf.rma.spirala.listeners.OnItemClick;
import ba.unsa.etf.rma.spirala.R;
import ba.unsa.etf.rma.spirala.data.Account;
import ba.unsa.etf.rma.spirala.data.Transaction;
import ba.unsa.etf.rma.spirala.detail.TransactionDetailFragment;

public class MainActivity extends AppCompatActivity implements OnItemClick {
    private boolean twoPaneMode;
    private Fragment listFragment;

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
        listFragment = fragmentManager.findFragmentById(R.id.transactions_list);
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
       /*
        if(getIntent().getAction().equals(Intent.ACTION_INSERT)) {
            Toast.makeText(this, "Transaction added.", Toast.LENGTH_LONG).show();
        }*/
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Exit app?").setTitle("Confirmation.");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                MainActivity.this.onBackPressed();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(MainActivity.this, "Exiting canceled", Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();*/
    }

    @Override
    public void displayTransaction(Transaction transaction) {
        Bundle arguments = new Bundle();
        if(transaction != null) {
            arguments.putParcelable("transaction", transaction);
        }
        TransactionDetailFragment detailFragment = new TransactionDetailFragment();
        detailFragment.setArguments(arguments);
        if (twoPaneMode){
            getSupportFragmentManager().beginTransaction().replace(R.id.transaction_detail, detailFragment).commit();
        }
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.transactions_list, detailFragment).addToBackStack("main").commit();
        }
    }

    @Override
    public void updateTransactionListData() {
        ((TransactionListFragment)listFragment).updateTransactionListData();
    }

    @Override
    public void displayAdded() {
        Toast.makeText(this, "Transaction added", Toast.LENGTH_LONG).show();
        if (twoPaneMode){
            displayTransaction(null);
        }
        else {
            getSupportFragmentManager().popBackStack("main", getSupportFragmentManager().POP_BACK_STACK_INCLUSIVE);
        }
    }

    @Override
    public void displayDeleted() {
        Toast.makeText(this, "Transaction deleted", Toast.LENGTH_LONG).show();
        if (twoPaneMode){
            displayTransaction(null);
        }
        else {
            getSupportFragmentManager().popBackStack("main", getSupportFragmentManager().POP_BACK_STACK_INCLUSIVE);
        }
    }

    @Override
    public void displayAccount() {
        if (!twoPaneMode){
            BudgetFragment budgetFragment = new BudgetFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.transactions_list, budgetFragment).addToBackStack("main").commit();
        }
        else {
            Toast.makeText(this, "Switch to portrait for account details.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void displayList() {
        getSupportFragmentManager().popBackStack("main", getSupportFragmentManager().POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void displayGraphs() {
        //Bundle arguments = new Bundle();
        GraphsFragment graphsFragment = new GraphsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.transactions_list, graphsFragment).addToBackStack("main").commit();
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        // etc.
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
    }
}

