package ba.unsa.etf.rma.spirala.budget;

import com.github.mikephil.charting.data.Entry;

import java.util.List;

import ba.unsa.etf.rma.spirala.data.Account;

public interface IBudgetPresenter {
    void refreshAccount(boolean network);
    void updateLimits(double totalLimit, double monthlyLimit, boolean network);
}
