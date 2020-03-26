package ba.unsa.etf.rma.spirala;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TransactionDetailActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private EditText amount;
    private EditText title;
    private EditText itemDescription;
    private EditText transactionInterval;
    private TextView dateText;
    //private TextView dateTextLabel;
    private TextView endDateText;
    private TextView endDateTextLabel;
    private Button saveBtn;
    private Button deleteBtn;
    private ImageView icon;
    private TransactionSpinnerAdapter spinnerAdapter;
    private Spinner typeSpinner;
    private Date inputDate, inputEndDate;
    boolean endDateDialog;
    boolean preventFirstFire;
    private ITransactionDetailPresenter presenter;
    int transactionID;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);
        preventFirstFire = true;
        initFields();
        Transaction t = (Transaction) getIntent().getParcelableExtra("TRANSACTION");
        inputDate = t.getDate();
        if(t.getEndDate() != null) inputEndDate = t.getEndDate();
        transactionID = t.getId();
        fillSpinner(t.getType());
        refreshFields(t);
        initListeners();
    }

    private void refreshFields(Transaction transaction) {
        amount.setText(transaction.getAmount().toString());
        title.setText(transaction.getTitle());
        showHide(transaction.getType());
        if(transaction.getItemDescription() != null) {
            itemDescription.setText(transaction.getItemDescription());
        }
        if(transaction.getTransactionInterval() != null) {
            transactionInterval.setText(transaction.getTransactionInterval().toString());
        }
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        dateText.setText(format.format(transaction.getDate()));
        if(transaction.getEndDate() != null) {
            endDateText.setText(format.format(transaction.getEndDate()));
        }

        //set icon according to transaction type
        setIcon(transaction.getType());
    }

    private void setIcon(Transaction.Type t) {
        String genreMatch = t.toString().toLowerCase();
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

    private void initListeners() {
        endDateText.setOnClickListener(v -> {
            endDateDialog = true;
            try {
                if(endDateText.getText().equals("NOT SELECTED")) {
                    showDatePickerDialog(new Date());
                } else {
                    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                    showDatePickerDialog(format.parse(endDateText.getText().toString()));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        });
        dateText.setOnClickListener(v -> {
            endDateDialog = false;
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            try {
                showDatePickerDialog(format.parse(dateText.getText().toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        deleteBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure about that?").setTitle("Confirm deletion");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    getPresenter().deleteTransaction(transactionID);
                    Intent deleteTransaction = new Intent(TransactionDetailActivity.this, MainActivity.class);
                    deleteTransaction.setAction(Intent.ACTION_DEFAULT);
                    TransactionDetailActivity.this.startActivity(deleteTransaction);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Toast.makeText(TransactionDetailActivity.this, "Deletion cancelled", Toast.LENGTH_LONG).show();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Transaction transaction = (Transaction) getIntent().getParcelableExtra("TRANSACTION");

                transaction.setTitle(title.getText().toString());
                transaction.setAmount(Double.parseDouble(amount.getText().toString()));
                transaction.setDate(inputDate);
                Transaction.Type type = (Transaction.Type)typeSpinner.getSelectedItem();
                transaction.setType(type);
                if(type == Transaction.Type.REGULARINCOME || type == Transaction.Type.INDIVIDUALINCOME) {
                    transaction.setItemDescription(null);
                } else {
                    transaction.setItemDescription(itemDescription.getText().toString());
                }

                if(type == Transaction.Type.REGULARINCOME || type == Transaction.Type.REGULARPAYMENT) {
                    transaction.setTransactionInterval(Integer.parseInt(transactionInterval.getText().toString()));
                    transaction.setEndDate(inputEndDate);
                } else {
                    transaction.setTransactionInterval(null);
                    transaction.setEndDate(null);
                }

                getPresenter().updateTransaction(transaction);
                resetBackgroundColor();
                setIcon(type);
                Toast.makeText(TransactionDetailActivity.this, "Changes saved", Toast.LENGTH_LONG).show();
            }
        });

        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                amount.setBackgroundColor(0xFF228B22);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                title.setBackgroundColor(0xFF228B22);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        itemDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                itemDescription.setBackgroundColor(0xFF228B22);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        transactionInterval.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                transactionInterval.setBackgroundColor(0xFF228B22);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void showDatePickerDialog(Date d) {
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        DatePickerDialog  datePickerDialog = new DatePickerDialog(
                this,
                this,
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void resetBackgroundColor() {
        endDateText.setBackgroundColor(0x00228B22);
        dateText.setBackgroundColor(0x00228B22);
        typeSpinner.setBackgroundColor(0x00228B22);
        amount.setBackgroundColor(0x00228B22);
        title.setBackgroundColor(0x00228B22);
        transactionInterval.setBackgroundColor(0x00228B22);
        itemDescription.setBackgroundColor(0x00228B22);
    }


    private void initFields() {
        amount = (EditText) findViewById(R.id.amount);
        title = (EditText) findViewById(R.id.title);
        itemDescription = (EditText) findViewById(R.id.itemDescription);
        transactionInterval = (EditText) findViewById(R.id.transactionInterval);
        dateText = (TextView) findViewById(R.id.dateText);
        //dateTextLabel = (TextView) findViewById(R.id.dateTextLabel);
        endDateText = (TextView) findViewById(R.id.endDateText);
        endDateTextLabel = (TextView) findViewById(R.id.endDateTextLabel);

        saveBtn = (Button) findViewById(R.id.saveBtn);
        deleteBtn = (Button) findViewById(R.id.deleteBtn);
        icon = (ImageView) findViewById(R.id.icon);
        typeSpinner = (Spinner) findViewById(R.id.spinner);

        inputDate = null;
        inputEndDate = null;
    }

    private void showHide(Transaction.Type type) {
        if(type == Transaction.Type.REGULARINCOME || type == Transaction.Type.INDIVIDUALINCOME) {
            itemDescription.setVisibility(View.INVISIBLE);
        } else {
            itemDescription.setVisibility(View.VISIBLE);
        }
        if(type == Transaction.Type.REGULARINCOME || type == Transaction.Type.REGULARPAYMENT) {
            transactionInterval.setVisibility(View.VISIBLE);
            endDateText.setVisibility(View.VISIBLE);
            endDateTextLabel.setVisibility(View.VISIBLE);
        } else {
            transactionInterval.setVisibility(View.INVISIBLE);
            endDateText.setVisibility(View.INVISIBLE);
            endDateTextLabel.setVisibility(View.INVISIBLE);
        }
    }

    private void fillSpinner(Transaction.Type type) {
        //filterBy
        ArrayList<Transaction.Type> filterList = new ArrayList<>();
        filterList.add(Transaction.Type.INDIVIDUALINCOME);
        filterList.add(Transaction.Type.INDIVIDUALPAYMENT);
        filterList.add(Transaction.Type.PURCHASE);
        filterList.add(Transaction.Type.REGULARINCOME);
        filterList.add(Transaction.Type.REGULARPAYMENT);
        spinnerAdapter = new TransactionSpinnerAdapter(getApplicationContext(), R.layout.spinner_element, filterList);
        typeSpinner.setAdapter(spinnerAdapter);
        int i = 0;
        for (Transaction.Type t: filterList) {
            if(t == type) {
                typeSpinner.setSelection(i);
            }
            i++;
        }

        /*ArrayAdapter<Transaction.Type> filterArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filterList);
        filterArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterBySpinner.setAdapter(filterArrayAdapter);*/
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //getPresenter().refreshTransactions((Transaction.Type) parent.getItemAtPosition(position), sortBySpinner.getSelectedItem().toString(), d);
                if(preventFirstFire) {
                    preventFirstFire = false;
                    return;
                }
                showHide((Transaction.Type) parent.getItemAtPosition(position));
                typeSpinner.setBackgroundColor(0xFF228B22);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = new GregorianCalendar();
        calendar.set(year, month, dayOfMonth);
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        if(endDateDialog) {
            inputEndDate = new Date(calendar.getTimeInMillis());
            endDateText.setText(format.format(inputEndDate));
            endDateText.setBackgroundColor(0xFF228B22);
        } else {
            inputDate = new Date(calendar.getTimeInMillis());
            dateText.setText(format.format(inputDate));
            dateText.setBackgroundColor(0xFF228B22);
        }
    }

    public ITransactionDetailPresenter getPresenter() {
        if(presenter == null) {
            presenter = new TransactionDetailPresenter(this);
        }
        return presenter;
    }
}
