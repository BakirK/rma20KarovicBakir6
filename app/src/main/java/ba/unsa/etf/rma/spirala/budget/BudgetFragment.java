package ba.unsa.etf.rma.spirala.budget;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import ba.unsa.etf.rma.spirala.R;
import ba.unsa.etf.rma.spirala.listeners.OnItemClick;

public class BudgetFragment extends Fragment {
    private TextView amountText;
    private Button saveBtn;
    private EditText monthlyLimitText;
    private EditText globalLimitText;
    private ConstraintLayout layout;
    private BudgetPresenter presenter;
    private OnItemClick onItemClick;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.budget_fragment, container, false);
        init(fragmentView);
        if (getArguments() != null && getArguments().containsKey("account")) {
            presenter = new BudgetPresenter(getActivity().getApplicationContext(), getArguments().getParcelable("account"));
            refreshFields();
        }
        initListeners();
        return fragmentView;
    }

    private void refreshFields() {
        amountText.setText(Double.toString(getPresenter().getBudget()));
        monthlyLimitText.setText(Double.toString(getPresenter().getMonthyLimit()));
        globalLimitText.setText(Double.toString(getPresenter().getTotalLimit()));
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
                if(!monthlyLimitText.getText().toString().trim().matches("[0-9]+")) {
                    showToast("Monthly limit must contain numbers only!");
                    return;
                }
                if(!globalLimitText.getText().toString().trim().matches("[0-9]+")) {
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
                getPresenter().updateAccount(globalLimit, monthlyLimit);
                refreshFields();
                showToast("Changes saved.");
            }
        });
    }


    private void init(View view) {
        amountText = view.findViewById(R.id.amountText);
        saveBtn = view.findViewById(R.id.saveBtn);
        monthlyLimitText = view.findViewById(R.id.monthlyLimitText);
        globalLimitText = view.findViewById(R.id.globalLimitText);
        layout = (ConstraintLayout) view.findViewById(R.id.layout);
        try {
            onItemClick = (OnItemClick)getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + "Treba implementirati OnItemClick interfejs");
        }
    }

    private BudgetPresenter getPresenter() {
        if(presenter == null) {
            presenter = new BudgetPresenter(getActivity().getApplicationContext());
        }
        return presenter;
    }
    private void showToast(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
    }
}
