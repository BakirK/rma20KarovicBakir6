package ba.unsa.etf.rma.spirala.graphs;

import android.annotation.SuppressLint;
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

import java.util.ArrayList;
import java.util.List;

import ba.unsa.etf.rma.spirala.R;
import ba.unsa.etf.rma.spirala.budget.BudgetPresenter;
import ba.unsa.etf.rma.spirala.listeners.OnSwipeTouchListener;
import ba.unsa.etf.rma.spirala.listeners.OnItemClick;
import ba.unsa.etf.rma.spirala.util.ILambda;
import ba.unsa.etf.rma.spirala.util.Lambda;

public class GraphsFragment extends Fragment implements IGraphView {
    private ConstraintLayout layout;
    private OnItemClick onItemClick;
    private LineChart expensesChart, incomeChart, budgetChart;
    private GraphPresenter presenter;
    private RadioGroup radioGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.graphs_fragment, container, false);
        init(view);
        initListeners();
        return view;
    }

    private GraphPresenter getPresenter() {
        if(presenter == null) {
            presenter = new GraphPresenter(this, getActivity().getApplicationContext());
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

    private void showEntries(String label) {
        Lambda expenses = new Lambda(new ILambda() {
            @Override
            public Object callback(Object o) {
                List<Entry> sumEntries = (ArrayList)o;
                showChart(sumEntries, label + " expenses", expensesChart);
                return 0;
            }
        });
        Lambda income = new Lambda(new ILambda() {
            @Override
            public Object callback(Object o) {
                List<Entry> sumEntries = (ArrayList)o;
                showChart(sumEntries, label + " income", incomeChart);
                return 0;
            }
        });
        Lambda budget = new Lambda(new ILambda() {
            @Override
            public Object callback(Object o) {
                List<Entry> sumEntries = (ArrayList)o;
                showChart(sumEntries, label + " budget", budgetChart);
                return 0;
            }
        });
        if(label.startsWith("Daily")) {
            getPresenter().getDailyEntries(expenses, income, budget);
        } else if(label.startsWith("Monthly")) {
            getPresenter().getMonthlyEntries(expenses, income, budget);
        } else {
            getPresenter().getWeeklyEntries(expenses, income, budget);
        }
    }

    private void init(View view) {
        expensesChart = (LineChart) view.findViewById(R.id.expensesChart);
        incomeChart = (LineChart) view.findViewById(R.id.incomeChart);
        budgetChart = (LineChart) view.findViewById(R.id.budgetChart);;
        showEntries("Daily");
        layout = view.findViewById(R.id.layout);
        try {
            onItemClick = (OnItemClick)getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + "Treba implementirati OnItemClick interfejs");
        }
        radioGroup = view.findViewById(R.id.radioGroup);
    }

    @SuppressLint("ClickableViewAccessibility")
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
                        showEntries("Daily");
                        break;
                    }
                    case R.id.radioMonth: {
                        showEntries("Monthly");
                        break;
                    }
                    case R.id.radioWeek: {
                        showEntries("Weekly");
                        break;
                    }
                }
            }

        });

    }
}