package ba.unsa.etf.rma.spirala.graphs;

import com.github.mikephil.charting.data.Entry;

import java.util.List;

public interface IGraphPresenter {
    List<Entry> getDailyExpensesEntries();
    List<Entry> getMonthlyExpensesEntries();
    List<Entry> getWeeklyExpensesEntries();

    List<Entry> getDailyIncomeEntries();
    List<Entry> getMonthlyIncomeEntries();
    List<Entry> getWeeklyIncomeEntries();

    List<Entry> getDailyBudgetEntries();
    List<Entry> getMonthlyBudgetEntries();
    List<Entry> getWeeklyBudgetEntries();
}
