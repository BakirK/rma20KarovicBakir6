package ba.unsa.etf.rma.spirala.detail;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import ba.unsa.etf.rma.spirala.data.TransactionDatabaseInteractor;
import ba.unsa.etf.rma.spirala.listeners.OnItemClick;
import ba.unsa.etf.rma.spirala.R;
import ba.unsa.etf.rma.spirala.data.Transaction;
import ba.unsa.etf.rma.spirala.list.TransactionSpinnerAdapter;
import ba.unsa.etf.rma.spirala.util.ICallback;
import ba.unsa.etf.rma.spirala.util.Callback;

public class TransactionDetailFragment extends Fragment implements DatePickerDialog.OnDateSetListener, InternetConnectivityListener {
    private EditText amount;
    private EditText title;
    private EditText itemDescription;
    private EditText transactionInterval;
    private TextView dateText;
    //private TextView dateTextLabel;
    private TextView endDateText;
    private TextView endDateTextLabel;
    private TextView transactionIntervalLabel, itemDescriptionLabel, offlineText;
    private Button saveBtn;
    private Button deleteBtn;
    private Button addButton;
    private ImageView icon;
    private TransactionSpinnerAdapter spinnerAdapter;
    private Spinner typeSpinner;
    private Date inputDate, inputEndDate;
    boolean endDateDialog;
    boolean preventFirstFire;
    private ITransactionDetailPresenter presenter;
    private OnItemClick onItemClick;
    private InternetAvailabilityChecker mInternetAvailabilityChecker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_detail, container, false);
        preventFirstFire = true;
        initFields(view);
        InternetAvailabilityChecker.init(getActivity());
        mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
        mInternetAvailabilityChecker.addInternetConnectivityListener(this);
        if(mInternetAvailabilityChecker.getCurrentInternetAvailabilityStatus()) {
            offlineText.setVisibility(View.INVISIBLE);
        } else {
            offlineText.setVisibility(View.VISIBLE);
        }
        if (getArguments() != null && getArguments().containsKey("transaction")) {
            getPresenter().setTransaction(getArguments().getParcelable("transaction"));
            Transaction t = getPresenter().getTransaction();
            inputDate = t.getDate();
            if(t.getEndDate() != null) inputEndDate = t.getEndDate();
            fillSpinner(t.getType());
            refreshFields(t);
        } else if(getArguments() != null && getArguments().containsKey("internalId")) {
            int internalId = getArguments().getInt("internalId");
            Transaction t = ITransactionDetailPresenter.getDatabaseTransaction(getActivity(), internalId);
            getPresenter().setTransaction(t);
            inputDate = t.getDate();
            if(t.getEndDate() != null) inputEndDate = t.getEndDate();
            fillSpinner(t.getType());
            refreshFields(t);
        } else {
            deleteBtn.setEnabled(false);
            addButton.setEnabled(false);
            fillSpinner(null);
            setEmptyFields();
        }
        initListeners();
        return view;
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

    private void setEmptyFields() {
        amount.setText("");
        title.setText("");
        showHide(Transaction.Type.INDIVIDUALINCOME);
        itemDescription.setText("");
        transactionInterval.setText("");
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

        //set icon according to transaction type
        setIcon(Transaction.Type.INDIVIDUALINCOME);
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

    private void showToast(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
    }

    private void initListeners() {
        endDateText.setOnClickListener(v -> {
            endDateDialog = true;
            showDatePickerDialog(inputEndDate);

        });
        dateText.setOnClickListener(v -> {
            endDateDialog = false;
            showDatePickerDialog(inputDate);
        });

        deleteBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Are you sure about that?").setTitle("Confirm deletion");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    getPresenter().deleteTransaction(mInternetAvailabilityChecker.getCurrentInternetAvailabilityStatus());
                    onItemClick.updateTransactionListData();
                    onItemClick.displayDeleted();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Toast.makeText(getActivity(), "Deletion cancelled", Toast.LENGTH_LONG).show();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        });

        addButton.setOnClickListener(v -> {
            onItemClick.displayTransaction(null);
        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.getText().length() < 3 || title.getText().length() > 15) {
                    showToast("Title must be longer than 3 characters and shorter than 15.");
                    return;
                }
                if(amount.getText().toString().isEmpty()) {
                    showToast("Amount can't be empty.");
                    return;
                }

                Double amountDouble;
                try {
                    amountDouble = Double.parseDouble(amount.getText().toString());
                } catch (Exception e) {
                    showToast("Amount must contain numbers only!");
                    return;
                }
                Integer interval;
                Transaction.Type type = (Transaction.Type)typeSpinner.getSelectedItem();
                if(Transaction.isRegular(type)) {
                    try {
                        interval = Integer.parseInt(transactionInterval.getText().toString());
                    } catch (Exception e) {
                        showToast("Interval must contain numbers only!");
                        return;
                    }
                } else {
                    interval = null;
                }
                if(inputDate == null) {
                    showToast("Date not selected. Tap it to select it.");
                    return;
                }
                if(Transaction.isRegular(type) &&  inputEndDate == null) {
                    showToast("End date not selected. Tap it to select it.");
                    return;
                }
                if(!Transaction.isIncome(type) && itemDescription.getText().toString().trim().isEmpty()) {
                    showToast("Description can't be empty.");
                    return;
                }
                if(Transaction.isRegular(type)) {
                    if(inputEndDate.before(inputDate)) {
                        showToast("End date must be after start date.");
                        return;
                    }
                    else if(inputEndDate.equals(inputDate)) {
                        showToast("End date can't be same as start date.");
                        return;
                    }
                    else if (Transaction.getDaysBetween(inputDate, inputEndDate) < interval) {
                        showToast("Interval can't be bigger than days difference between two dates.");
                        return;
                    }
                }
                if(Transaction.isIncome(type)) {
                    updateTransaction(
                            inputDate,
                            amountDouble,
                            title.getText().toString(),
                            type,
                            itemDescription.getText().toString(),
                            interval,
                            inputEndDate
                    );
                } else {
                    getPresenter().overMonthLimit(
                            new Callback(new ICallback() {
                                @Override
                                public Object callback(Object o) {
                                    boolean overMonthLimit = (Boolean)o;
                                    getPresenter().overGlobalLimit(
                                            new Callback(new ICallback() {
                                                @Override
                                                public Object callback(Object o) {
                                                    boolean overGlobalLimit = (Boolean)o;
                                                    if(overGlobalLimit || overMonthLimit) {
                                                        String text = "Total expenses are over ";
                                                        if(overMonthLimit) {
                                                            text += "monthly limit ";
                                                            if(overGlobalLimit) {
                                                                text += "and global limit";
                                                            }
                                                        } else if(overGlobalLimit) {
                                                            text += "global limit";
                                                        }

                                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                        builder.setMessage("Continue anyway?").setTitle(text);
                                                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                updateTransaction(
                                                                        inputDate,
                                                                        amountDouble,
                                                                        title.getText().toString(),
                                                                        type,
                                                                        itemDescription.getText().toString(),
                                                                        interval,
                                                                        inputEndDate
                                                                );
                                                            }
                                                        });
                                                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                showToast("Saving cancelled");
                                                            }
                                                        });
                                                        AlertDialog alert = builder.create();
                                                        alert.show();


                                                    } else {
                                                        updateTransaction(
                                                                inputDate,
                                                                amountDouble,
                                                                title.getText().toString(),
                                                                type,
                                                                itemDescription.getText().toString(),
                                                                interval,
                                                                inputEndDate
                                                        );
                                                    }
                                                    return 0;
                                                }
                                            }),
                                            inputDate,
                                            amountDouble,
                                            title.getText().toString(),
                                            type,
                                            itemDescription.getText().toString(),
                                            interval,
                                            inputEndDate,
                                            mInternetAvailabilityChecker.getCurrentInternetAvailabilityStatus()
                                    );
                                    return 0;
                                }
                            }),
                            inputDate,
                            amountDouble,
                            title.getText().toString(),
                            type,
                            itemDescription.getText().toString(),
                            interval,
                            inputEndDate,
                            mInternetAvailabilityChecker.getCurrentInternetAvailabilityStatus()
                    );
                }
                    /*WARNING - SPANISH SPAGHETTI LANGUAGE AHEAD* - DANGER*/

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

    private void updateTransaction(Date date, Double amountDouble, String title, Transaction.Type type, @Nullable String itemDescription,
                                   @Nullable Integer transactionInterval, @Nullable Date endDate) {
        getPresenter().updateParameters(
                date,
                amountDouble,
                title,
                type,
                itemDescription,
                transactionInterval,
                endDate,
                mInternetAvailabilityChecker.getCurrentInternetAvailabilityStatus()
        );
        onItemClick.updateTransactionListData();
        if(getArguments() != null && getArguments().containsKey("transaction")) {
            resetBackgroundColor();
            setIcon(type);
            Toast.makeText(getActivity(), "Changes saved", Toast.LENGTH_LONG).show();
        } else {
            onItemClick.displayAdded();
        }


    }

    private void showDatePickerDialog(Date d) {
        if(d == null) {
            d = new Date();
        }
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                TransactionDetailFragment.this,
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


    private void initFields(View view) {
        amount = (EditText) view.findViewById(R.id.amount);
        title = (EditText) view.findViewById(R.id.title);
        itemDescription = (EditText) view.findViewById(R.id.monthlyLimitText);
        transactionInterval = (EditText) view.findViewById(R.id.globalLimitText);
        dateText = (TextView) view.findViewById(R.id.dateText);
        endDateText = (TextView) view.findViewById(R.id.endDateText);
        endDateTextLabel = (TextView) view.findViewById(R.id.endDateTextLabel);
        transactionIntervalLabel = (TextView) view.findViewById(R.id.globalLimitLabel);
        itemDescriptionLabel = (TextView) view.findViewById(R.id.monthlyLimitLabel);
        offlineText = (TextView) view.findViewById(R.id.offlineText);
        saveBtn = (Button) view.findViewById(R.id.saveBtn);
        deleteBtn = (Button) view.findViewById(R.id.deleteBtn);
        addButton = (Button) view.findViewById(R.id.addButton);
        icon = (ImageView) view.findViewById(R.id.icon);
        typeSpinner = (Spinner) view.findViewById(R.id.spinner);
        try {
            onItemClick = (OnItemClick)getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + "Treba implementirati OnItemClick interfejs");
        }
        inputDate = null;
        inputEndDate = null;
    }

    private void showHide(Transaction.Type type) {
        if(type == Transaction.Type.REGULARINCOME || type == Transaction.Type.INDIVIDUALINCOME) {
            itemDescription.setVisibility(View.INVISIBLE);
            itemDescriptionLabel.setVisibility(View.INVISIBLE);
        } else {
            itemDescription.setVisibility(View.VISIBLE);
            itemDescriptionLabel.setVisibility(View.VISIBLE);
        }
        if(type == Transaction.Type.REGULARINCOME || type == Transaction.Type.REGULARPAYMENT) {
            transactionInterval.setVisibility(View.VISIBLE);
            transactionIntervalLabel.setVisibility(View.VISIBLE);
            endDateText.setVisibility(View.VISIBLE);
            endDateTextLabel.setVisibility(View.VISIBLE);
        } else {
            transactionInterval.setVisibility(View.INVISIBLE);
            transactionIntervalLabel.setVisibility(View.INVISIBLE);
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
        spinnerAdapter = new TransactionSpinnerAdapter(getActivity().getApplicationContext(), R.layout.spinner_element, filterList);
        typeSpinner.setAdapter(spinnerAdapter);
        int i = 0;
        if(type != null) {
            for (Transaction.Type t: filterList) {
                if(t == type) {
                    typeSpinner.setSelection(i);
                    break;
                }
                i++;
            }
        }

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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
            presenter = new TransactionDetailPresenter(getActivity());
            presenter.refreshAccount(mInternetAvailabilityChecker.getCurrentInternetAvailabilityStatus());
        }
        return presenter;
    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
        getPresenter().refreshAccount(isConnected);
        if (isConnected) {
            offlineText.setVisibility(View.INVISIBLE);
        }
        else {
            offlineText.setVisibility(View.VISIBLE);
        }
    }
}
