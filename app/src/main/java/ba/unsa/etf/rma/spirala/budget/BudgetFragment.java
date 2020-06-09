package ba.unsa.etf.rma.spirala.budget;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import ba.unsa.etf.rma.spirala.R;
import ba.unsa.etf.rma.spirala.data.Account;
import ba.unsa.etf.rma.spirala.listeners.OnItemClick;
import ba.unsa.etf.rma.spirala.listeners.OnSwipeTouchListener;

public class BudgetFragment extends Fragment implements IBudgetView, InternetConnectivityListener {
    private TextView amountText, offlineText;
    private Button saveBtn;
    private EditText monthlyLimitText;
    private EditText globalLimitText;
    private ConstraintLayout layout;
    private BudgetPresenter presenter;
    private OnItemClick onItemClick;
    private InternetAvailabilityChecker mInternetAvailabilityChecker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.budget_fragment, container, false);
        init(fragmentView);
        initListeners();
        getPresenter().refreshAccount(mInternetAvailabilityChecker.getCurrentInternetAvailabilityStatus());
        return fragmentView;
    }
    @Override
    public void refreshFields(Account account) {
        if(account != null) {
            amountText.setText(Double.toString(account.getBudget()));
            monthlyLimitText.setText(Double.toString(account.getMonthLimit()));
            globalLimitText.setText(Double.toString(account.getTotalLimit()));
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListeners() {
        layout.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            @Override
            public void onSwipeRight() {
                onItemClick.displayList();
            }

            @Override
            public void onSwipeLeft() {
                onItemClick.displayGraphs();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //empty check
                if(monthlyLimitText.getText().toString().trim().isEmpty()) {
                    showToast("Monthly limit can't be empty!");
                    return;
                }
                if(globalLimitText.getText().toString().trim().isEmpty()) {
                    showToast("Monthly limit can't be empty!");
                    return;
                }
                //characters check
                if(!monthlyLimitText.getText().toString().trim().matches("^\\d*\\.?\\d*$")) {
                    showToast("Monthly limit must contain numbers only!");
                    return;
                }
                if(!globalLimitText.getText().toString().trim().matches("^\\d*\\.?\\d*$")) {
                    showToast("Monthly limit must contain numbers only!");
                    return;
                }

                //negative number check
                Double monthlyLimit = Double.parseDouble(monthlyLimitText.getText().toString().trim());
                Double globalLimit = Double.parseDouble(globalLimitText.getText().toString().trim());
                if(monthlyLimit < 0.0) {
                    showToast("Monthly limit can't be nagative!");
                    return;
                }
                if(globalLimit < 0.0) {
                    showToast("Global limit can't be negative!");
                    return;
                }
                getPresenter().updateLimits(globalLimit, monthlyLimit, mInternetAvailabilityChecker.getCurrentInternetAvailabilityStatus());
            }
        });
    }


    private void init(View view) {
        amountText = view.findViewById(R.id.amountText);
        saveBtn = view.findViewById(R.id.saveBtn);
        monthlyLimitText = view.findViewById(R.id.monthlyLimitText);
        globalLimitText = view.findViewById(R.id.globalLimitText);
        offlineText = (TextView) view.findViewById(R.id.offlineText);
        layout = (ConstraintLayout) view.findViewById(R.id.layout);
        try {
            onItemClick = (OnItemClick)getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + "Treba implementirati OnItemClick interfejs");
        }

        InternetAvailabilityChecker.init(getActivity());
        mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
        mInternetAvailabilityChecker.removeAllInternetConnectivityChangeListeners();
        mInternetAvailabilityChecker.addInternetConnectivityListener(this);
        if(mInternetAvailabilityChecker.getCurrentInternetAvailabilityStatus()) {
            offlineText.setVisibility(View.INVISIBLE);
        } else {
            offlineText.setVisibility(View.VISIBLE);
        }
    }

    private BudgetPresenter getPresenter() {
        if(presenter == null) {
            presenter = new BudgetPresenter(this, getActivity().getApplicationContext());
        }
        return presenter;
    }
    @Override
    public void showToast(String text) {
        if(getActivity() != null) {
            Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
        getPresenter().refreshAccount(isConnected);
        if (isConnected) {
            Log.d("online","online");
            offlineText.setVisibility(View.INVISIBLE);
        }
        else {
            Log.d("online","offline");
            offlineText.setVisibility(View.VISIBLE);
        }
    }
}

