package ba.unsa.etf.rma.spirala.graphs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.List;

import ba.unsa.etf.rma.spirala.R;
import ba.unsa.etf.rma.spirala.budget.BudgetPresenter;
import ba.unsa.etf.rma.spirala.listeners.OnSwipeTouchListener;
import ba.unsa.etf.rma.spirala.listeners.OnItemClick;

public class GraphsFragment extends Fragment {
    private ConstraintLayout layout;
    private OnItemClick onItemClick;
    private LineChart expensesChart, incomeChart, budgetChart;
    private BudgetPresenter presenter;
    private RadioGroup radioGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.graphs_fragment, container, false);
        init(view);
        initListeners();
        return view;
    }

    private BudgetPresenter getPresenter() {
        if(presenter == null) {
            presenter = new BudgetPresenter(getActivity().getApplicationContext());
        }
        return presenter;
    }

    private void showChart(List<Entry> entries, String label, LineChart chart) {
        LineDataSet dataSet = new LineDataSet(entries, label);
        dataSet.setColor(R.color.lightGrrey);
        dataSet.setValueTextColor(R.color.red);
        LineData lineData = new LineData(dataSet);
        chart.setNoDataText("No transactions today of this type.");
        Description d = new Description();
        d.setText("");
        chart.setDescription(d);
        chart.setData(lineData);
        chart.notifyDataSetChanged();
        chart.invalidate(); // refresh
    }

    private void init(View view) {
        expensesChart = (LineChart) view.findViewById(R.id.expensesChart);
        showChart(getPresenter().getDailyExpensesEntries(), "Daily expenses", expensesChart);
        incomeChart = (LineChart) view.findViewById(R.id.incomeChart);
        showChart(getPresenter().getDailyIncomeEntries(), "Daily income", incomeChart);
        budgetChart = (LineChart) view.findViewById(R.id.budgetChart);;
        showChart(getPresenter().getDailyBudgetEntries(), "Daily budget", budgetChart);

        layout = view.findViewById(R.id.layout);
        try {
            onItemClick = (OnItemClick)getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + "Treba implementirati OnItemClick interfejs");
        }
        radioGroup = view.findViewById(R.id.radioGroup);
    }

    private void initListeners() {
        layout.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            @Override
            public void onSwipeLeft() {
                onItemClick.displayList();
            }

            @Override
            public void onSwipeRight() {
                onItemClick.displayAccount();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioDay: {
                        showChart(getPresenter().getDailyExpensesEntries(), "Daily expenses", expensesChart);
                        showChart(getPresenter().getDailyIncomeEntries(), "Daily income", incomeChart);
                        showChart(getPresenter().getDailyBudgetEntries(), "Daily budget", budgetChart);
                        break;
                    }
                    case R.id.radioMonth: {
                        showChart(getPresenter().getMonthlyExpensesEntries(), "Monthly expenses", expensesChart);
                        showChart(getPresenter().getMonthlyIncomeEntries(), "Monthly income", incomeChart);
                        showChart(getPresenter().getMonthlyBudgetEntries(), "Monthly budget", budgetChart);
                        break;
                    }
                    case R.id.radioWeek: {
                        showChart(getPresenter().getWeeklyExpensesEntries(), "Weekly expenses", expensesChart);
                        showChart(getPresenter().getWeeklyIncomeEntries(), "Weekly income", incomeChart);
                        showChart(getPresenter().getWeeklyBudgetEntries(), "Weekly budget", budgetChart);
                        break;
                    }
                }
            }

        });

    }
}