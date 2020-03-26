package ba.unsa.etf.rma.spirala;

import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Field;

public class TransactionDetailActivity extends AppCompatActivity {
    private EditText amount;
    private EditText title;
    private EditText itemDescription;
    private EditText transactionInterval;
    private TextView dateText;
    private TextView endDateText;
    private Button saveBtn;
    private Button deleteBtn;
    private ImageView icon;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);
        initFields();
        Transaction t = (Transaction) getIntent().getParcelableExtra("TRANSACTION");
        refreshFields(t);



    }

    private void refreshFields(Transaction transaction) {
        amount.setText(transaction.getAmount().toString());
        title.setText(transaction.getTitle());
        if(transaction.getItemDescription() != null) {
            itemDescription.setText(transaction.getItemDescription());
        } else {
            itemDescription.setVisibility(View.INVISIBLE);
        }
        if(transaction.getTransactionInterval() != null) {
            transactionInterval.setText(transaction.getItemDescription());
        } else {
            transactionInterval.setVisibility(View.INVISIBLE);
        }
        dateText.setText(transaction.getDate().toString());
        if(transaction.getEndDate() != null) {
            endDateText.setText(transaction.getEndDate().toString());
        } else {
            endDateText.setVisibility(View.INVISIBLE);
        }



        String genreMatch = transaction.getType().toString().toLowerCase();
        try {
            Class res = R.drawable.class;
            Field field = res.getField(genreMatch);
            int drawableId = field.getInt(null);
            icon.setImageResource(drawableId);
        }
        catch (Exception e) {
            icon.setImageResource(R.drawable.all);
        }
    }


    private void initFields() {
        amount = (EditText) findViewById(R.id.amount);
        title = (EditText) findViewById(R.id.title);
        itemDescription = (EditText) findViewById(R.id.itemDescription);
        transactionInterval = (EditText) findViewById(R.id.transactionInterval);
        dateText = (TextView) findViewById(R.id.dateText);
        endDateText = (TextView) findViewById(R.id.endDateText);
        saveBtn = (Button) findViewById(R.id.saveBtn);
        deleteBtn = (Button) findViewById(R.id.deleteBtn);
        icon = (ImageView) findViewById(R.id.icon);
    }
}
