package ba.unsa.etf.rma.spirala.graphs;

import android.content.Context;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import ba.unsa.etf.rma.spirala.data.Transaction;
import ba.unsa.etf.rma.spirala.list.TransactionListInteractor;
import ba.unsa.etf.rma.spirala.util.ICallback;
import ba.unsa.etf.rma.spirala.util.Callback;

public class GraphPresenter implements IGraphPresenter {
    private Context context;
    private IGraphView view;

    public GraphPresenter(IGraphView view, Context context) {
        this.context = context;
        this.view = view;
    }

    @Override
    public void getDailyExpensesEntries(ArrayList<Transaction> transactions, Callback callback) {
        Date today = new Date();
        Vector<Float> entries = new Vector<>();
        entries.setSize(24);
        for (Transaction transaction: transactions) {
            if(!Transaction.isIncome(transaction.getType())) {
                if(Transaction.isRegular(transaction.getType()) && Transaction.dateOverlapping(today, transaction)) {
                    int daysBetween = Transaction.getDaysBetween(transaction.getDate(), today);
                    boolean isTransactionToday = (daysBetween % transaction.getTransactionInterval()) == 0;
                    if(isTransactionToday) {
                        Calendar date = Transaction.toCalendar(transaction.getDate().getTime());
                        int hour = date.get(Calendar.HOUR);
                        if(entries.elementAt(hour) == null) {
                            entries.setElementAt(transaction.getAmount().floatValue(), hour);
                        } else {
                            entries.setElementAt(entries.get(hour) + transaction.getAmount().floatValue(), hour);
                        }
                    }
                } else if(Transaction.sameDay(today, transaction.getDate())) {
                    Calendar date = Transaction.toCalendar(transaction.getDate().getTime());
                    int hour = date.get(Calendar.HOUR);
                    if(entries.elementAt(hour) == null) {
                        entries.setElementAt(transaction.getAmount().floatValue(), hour);
                    } else {
                        entries.setElementAt(entries.get(hour) + transaction.getAmount().floatValue(), hour);
                    }
                }
            }
        }
        List<Entry> sumEntries = new ArrayList<>();
        int i = 1;
        for (Float dayExpenses : entries) {
            if(dayExpenses == null) {
                sumEntries.add(new Entry(i, 0));
            } else {
                sumEntries.add(new Entry(i, dayExpenses));
            }
            i++;
        }
        callback.pass(sumEntries);
    }

    public void getDailyEntries(Callback expenses, Callback income, Callback budget) {
        new TransactionListInteractor(new Callback(new ICallback() {
            @Override
            public Object callback(Object o) {
                ArrayList<Transaction> transactions = (ArrayList<Transaction>)o;
                getDailyExpensesEntries(transactions, expenses);
                getDailyIncomeEntries(transactions, income);
                getDailyBudgetEntries(transactions, budget);
                return 0;
            }
        }), context).execute();
    }

    @Override
    public void getMonthlyEntries(Callback expenses, Callback income, Callback budget) {
        new TransactionListInteractor(new Callback(new ICallback() {
            @Override
            public Object callback(Object o) {
                ArrayList<Transaction> transactions = (ArrayList<Transaction>)o;
                getMonthlyExpensesEntries(transactions, expenses);
                getMonthlyIncomeEntries(transactions, income);
                getMonthlyBudgetEntries(transactions, budget);
                return 0;
            }
        }), context).execute();
    }

    @Override
    public void getWeeklyEntries(Callback expenses, Callback income, Callback budget) {
        new TransactionListInteractor(new Callback(new ICallback() {
            @Override
            public Object callback(Object o) {
                ArrayList<Transaction> transactions = (ArrayList<Transaction>)o;
                getWeeklyExpensesEntries(transactions, expenses);
                getWeeklyIncomeEntries(transactions, income);
                getWeeklyBudgetEntries(transactions, budget);
                return 0;
            }
        }), context).execute();
    }

    @Override
    public void getMonthlyExpensesEntries(ArrayList<Transaction> transactions, Callback callback) {
        Date today = new Date();
        Vector<Float> entries = new Vector<>();

        Calendar firstDay = Transaction.toCalendar(today.getTime());
        firstDay.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDayDate = firstDay.getTime();

        Calendar lastDay = Transaction.toCalendar(today.getTime());
        lastDay.set(Calendar.DAY_OF_MONTH, lastDay.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date lastDayDate = lastDay.getTime();

        entries.setSize(lastDay.getActualMaximum(Calendar.DAY_OF_MONTH));
        for (Transaction transaction: transactions) {
            if(!Transaction.isIncome(transaction.getType())) {
                if(Transaction.isRegular(transaction.getType()) && Transaction.dateOverlapping(today, transaction)) {
                    Integer daysBetween;
                    //transakcija je pocela prije prvog dana u mjesecu
                    if(transaction.getDate().before(firstDayDate)) {
                        //transakcija zavrsava prije zadnjeg dana u mjesecu
                        if(transaction.getEndDate().before(lastDayDate)) {
                            daysBetween = Transaction.getDaysBetween(firstDayDate, transaction.getEndDate());
                        } else {
                            daysBetween = Transaction.getDaysBetween(firstDayDate, lastDayDate);
                        }
                    } else {
                        //transakcija zavrsava prije zadnjeg dana u mjesecu
                        if(transaction.getEndDate().before(lastDayDate)) {
                            daysBetween = Transaction.getDaysBetween(transaction.getDate(), transaction.getEndDate());
                        } else {
                            daysBetween = Transaction.getDaysBetween(transaction.getDate(), lastDayDate);
                        }
                    }
                    Float amount = ((int)(daysBetween/transaction.getTransactionInterval())) * transaction.getAmount().floatValue();
                    Calendar date = Transaction.toCalendar(transaction.getDate().getTime());
                    int day = date.get(Calendar.DAY_OF_MONTH) - 1;
                    if(entries.elementAt(day) == null) {
                        entries.setElementAt(amount, day);
                    } else {
                        entries.setElementAt(entries.get(day) + amount, day);
                    }
                } else if(Transaction.sameMonth(today, transaction.getDate())) {
                    Calendar date = Transaction.toCalendar(transaction.getDate().getTime());
                    int day = date.get(Calendar.DAY_OF_MONTH) - 1;
                    if(entries.elementAt(day) == null) {
                        entries.setElementAt(transaction.getAmount().floatValue(), day);
                    } else {
                        entries.setElementAt(entries.get(day) + transaction.getAmount().floatValue(), day);
                    }
                }
            }
        }
        List<Entry> sumEntries = new ArrayList<>();
        int i = 0;
        for (Float dayExpenses : entries) {
            if(dayExpenses == null) {
                sumEntries.add(new Entry(i, 0));
            } else {
                sumEntries.add(new Entry(i, dayExpenses));
            }
            i++;
        }
        callback.pass(sumEntries);
    }

    @Override
    public void getWeeklyExpensesEntries(ArrayList<Transaction> transactions, Callback callback) {
        Date today = new Date();
        Vector<Float> entries = new Vector<>();
        entries.setSize(7);

        Calendar firstDay = Transaction.toCalendar(today.getTime());
        firstDay.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        firstDay.clear(Calendar.MINUTE);
        firstDay.clear(Calendar.SECOND);
        firstDay.clear(Calendar.MILLISECOND);
        firstDay.set(Calendar.DAY_OF_WEEK, firstDay.getFirstDayOfWeek());

        Calendar lastDay = firstDay;
        lastDay.add(Calendar.DAY_OF_MONTH, 7);
        Date lastDayDate = lastDay.getTime();
        for (Transaction transaction: transactions) {
            if(!Transaction.isIncome(transaction.getType())) {
                if(Transaction.isRegular(transaction.getType()) && Transaction.dateOverlapping(today, transaction)) {
                    Float amount;
                    Calendar start = Transaction.toCalendar(transaction.getDate().getTime());
                    Calendar end = Transaction.toCalendar(transaction.getEndDate().getTime());
                    while (start.getTime().before(end.getTime()) && start.getTime().before(lastDayDate)) {
                        if(Transaction.sameWeek(start.getTime(), today)) {
                            if(transaction.getTransactionInterval() <= 7) {
                                amount = transaction.getAmount().floatValue();
                                Calendar date = Transaction.toCalendar(transaction.getDate().getTime());
                                int day = date.get(Calendar.DAY_OF_WEEK) - 1;
                                if(entries.elementAt(day) == null) {
                                    entries.setElementAt(amount, day);
                                } else {
                                    entries.setElementAt(entries.get(day) + amount, day);
                                }
                                while (start.getTime().before(end.getTime()) && start.getTime().before(lastDayDate)) {
                                    start.add(Calendar.DAY_OF_MONTH, transaction.getTransactionInterval());
                                    date = Transaction.toCalendar(transaction.getDate().getTime());
                                    day = date.get(Calendar.DAY_OF_WEEK) - 1;
                                    if(entries.elementAt(day) == null) {
                                        entries.setElementAt(amount, day);
                                    } else {
                                        entries.setElementAt(entries.get(day) + amount, day);
                                    }
                                }
                            } else {
                                amount = transaction.getAmount().floatValue();
                                Calendar date = Transaction.toCalendar(transaction.getDate().getTime());
                                int day = date.get(Calendar.DAY_OF_WEEK) - 1;
                                if(entries.elementAt(day) == null) {
                                    entries.setElementAt(amount, day);
                                } else {
                                    entries.setElementAt(entries.get(day) + amount, day);
                                }
                            }
                            break;
                        }
                        start.add(Calendar.DAY_OF_MONTH, transaction.getTransactionInterval());
                    }
                } else if(Transaction.sameMonth(today, transaction.getDate())) {
                    Calendar date = Transaction.toCalendar(transaction.getDate().getTime());
                    int day = date.get(Calendar.DAY_OF_WEEK) - 1;
                    if(entries.elementAt(day) == null) {
                        entries.setElementAt(transaction.getAmount().floatValue(), day);
                    } else {
                        entries.setElementAt(entries.get(day) + transaction.getAmount().floatValue(), day);
                    }
                }
            }
        }
        List<Entry> sumEntries = new ArrayList<>();
        int i = 1;
        for (Float dayExpenses : entries) {
            if(dayExpenses == null) {
                sumEntries.add(new Entry(i, 0));
            } else {
                sumEntries.add(new Entry(i, dayExpenses));
            }
            i++;
        }
        callback.pass(sumEntries);
    }

    @Override
    public void getDailyIncomeEntries(ArrayList<Transaction> transactions, Callback callback) {
        Date today = new Date();
        Vector<Float> entries = new Vector<>();
        entries.setSize(24);
        for (Transaction transaction: transactions) {
            if(Transaction.isIncome(transaction.getType())) {
                if(Transaction.isRegular(transaction.getType()) && Transaction.dateOverlapping(today, transaction)) {
                    int daysBetween = Transaction.getDaysBetween(transaction.getDate(), today);
                    boolean isTransactionToday = (daysBetween % transaction.getTransactionInterval()) == 0;
                    if(isTransactionToday) {
                        Calendar date = Transaction.toCalendar(transaction.getDate().getTime());
                        int hour = date.get(Calendar.HOUR);
                        if(entries.elementAt(hour) == null) {
                            entries.setElementAt(transaction.getAmount().floatValue(), hour);
                        } else {
                            entries.setElementAt(entries.get(hour) + transaction.getAmount().floatValue(), hour);
                        }
                    }

                } else if(Transaction.sameDay(today, transaction.getDate())) {
                    Calendar date = Transaction.toCalendar(transaction.getDate().getTime());
                    int hour = date.get(Calendar.HOUR);
                    if(entries.elementAt(hour) == null) {
                        entries.setElementAt(transaction.getAmount().floatValue(), hour);
                    } else {
                        entries.setElementAt(entries.get(hour) + transaction.getAmount().floatValue(), hour);
                    }
                }
            }
        }
        List<Entry> sumEntries = new ArrayList<>();
        int i = 1;
        for (Float dayExpenses : entries) {
            if(dayExpenses == null) {
                sumEntries.add(new Entry(i, 0));
            } else {
                sumEntries.add(new Entry(i, dayExpenses));
            }
            i++;
        }
        callback.pass(sumEntries);
    }

    @Override
    public void getMonthlyIncomeEntries(ArrayList<Transaction> transactions, Callback callback) {
        Date today = new Date();
        Vector<Float> entries = new Vector<>();

        Calendar firstDay = Transaction.toCalendar(today.getTime());
        firstDay.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDayDate = firstDay.getTime();

        Calendar lastDay = firstDay;
        lastDay.set(Calendar.DAY_OF_MONTH, lastDay.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date lastDayDate = lastDay.getTime();

        entries.setSize(lastDay.getActualMaximum(Calendar.DAY_OF_MONTH));
        for (Transaction transaction: transactions) {
            if(Transaction.isIncome(transaction.getType())) {
                if(Transaction.isRegular(transaction.getType()) && Transaction.dateOverlapping(today, transaction)) {
                    Integer daysBetween;
                    //transakcija je pocela prije prvog dana u mjesecu
                    if(transaction.getDate().before(firstDayDate)) {
                        //transakcija zavrsava prije zadnjeg dana u mjesecu
                        if(transaction.getEndDate().before(lastDayDate)) {
                            daysBetween = Transaction.getDaysBetween(firstDayDate, transaction.getEndDate());
                        } else {
                            daysBetween = Transaction.getDaysBetween(firstDayDate, lastDayDate);
                        }
                    } else {
                        //transakcija zavrsava prije zadnjeg dana u mjesecu
                        if(transaction.getEndDate().before(lastDayDate)) {
                            daysBetween = Transaction.getDaysBetween(transaction.getDate(), transaction.getEndDate());
                        } else {
                            daysBetween = Transaction.getDaysBetween(transaction.getDate(), lastDayDate);
                        }
                    }
                    Float amount = ((int)(daysBetween/transaction.getTransactionInterval())) * transaction.getAmount().floatValue();
                    Calendar date = Transaction.toCalendar(transaction.getDate().getTime());
                    int day = date.get(Calendar.DAY_OF_MONTH) - 1;
                    if(entries.elementAt(day) == null) {
                        entries.setElementAt(amount, day);
                    } else {
                        entries.setElementAt(entries.get(day) + amount, day);
                    }
                } else if(Transaction.sameMonth(today, transaction.getDate())) {
                    Calendar date = Transaction.toCalendar(transaction.getDate().getTime());
                    int day = date.get(Calendar.DAY_OF_MONTH) - 1;
                    if(entries.elementAt(day) == null) {
                        entries.setElementAt(transaction.getAmount().floatValue(), day);
                    } else {
                        entries.setElementAt(entries.get(day) + transaction.getAmount().floatValue(), day);
                    }
                }
            }
        }
        List<Entry> sumEntries = new ArrayList<>();
        int i = 0;
        for (Float dayExpenses : entries) {
            if(dayExpenses == null) {
                sumEntries.add(new Entry(i, 0));
            } else {
                sumEntries.add(new Entry(i, dayExpenses));
            }
            i++;
        }
        callback.pass(sumEntries);
    }

    @Override
    public void getWeeklyIncomeEntries(ArrayList<Transaction> transactions, Callback callback) {
        Date today = new Date();
        Vector<Float> entries = new Vector<>();
        entries.setSize(7);

        Calendar firstDay = Transaction.toCalendar(today.getTime());
        firstDay.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        firstDay.clear(Calendar.MINUTE);
        firstDay.clear(Calendar.SECOND);
        firstDay.clear(Calendar.MILLISECOND);
        firstDay.set(Calendar.DAY_OF_WEEK, firstDay.getFirstDayOfWeek());

        Calendar lastDay = firstDay;
        lastDay.add(Calendar.DAY_OF_MONTH, 7);
        Date lastDayDate = lastDay.getTime();
        for (Transaction transaction: transactions) {
            if(Transaction.isIncome(transaction.getType())) {
                if(Transaction.isRegular(transaction.getType()) && Transaction.dateOverlapping(today, transaction)) {
                    Float amount;
                    Calendar start = Transaction.toCalendar(transaction.getDate().getTime());
                    Calendar end = Transaction.toCalendar(transaction.getEndDate().getTime());
                    while (start.getTime().before(end.getTime()) && start.getTime().before(lastDayDate)) {
                        if(Transaction.sameWeek(start.getTime(), today)) {
                            if(transaction.getTransactionInterval() <= 7) {
                                amount = transaction.getAmount().floatValue();
                                Calendar date = Transaction.toCalendar(transaction.getDate().getTime());
                                int day = date.get(Calendar.DAY_OF_WEEK) - 1;
                                if(entries.elementAt(day) == null) {
                                    entries.setElementAt(amount, day);
                                } else {
                                    entries.setElementAt(entries.get(day) + amount, day);
                                }
                                while (start.getTime().before(end.getTime()) && start.getTime().before(lastDayDate)) {
                                    start.add(Calendar.DAY_OF_MONTH, transaction.getTransactionInterval());
                                    date = Transaction.toCalendar(transaction.getDate().getTime());
                                    day = date.get(Calendar.DAY_OF_WEEK) - 1;
                                    if(entries.elementAt(day) == null) {
                                        entries.setElementAt(amount, day);
                                    } else {
                                        entries.setElementAt(entries.get(day) + amount, day);
                                    }
                                }
                            } else {
                                amount = transaction.getAmount().floatValue();
                                Calendar date = Transaction.toCalendar(transaction.getDate().getTime());
                                int day = date.get(Calendar.DAY_OF_WEEK) - 1;
                                if(entries.elementAt(day) == null) {
                                    entries.setElementAt(amount, day);
                                } else {
                                    entries.setElementAt(entries.get(day) + amount, day);
                                }
                            }
                            break;
                        }
                        start.add(Calendar.DAY_OF_MONTH, transaction.getTransactionInterval());
                    }
                } else if(Transaction.sameMonth(today, transaction.getDate())) {
                    Calendar date = Transaction.toCalendar(transaction.getDate().getTime());
                    int day = date.get(Calendar.DAY_OF_WEEK) - 1;
                    if(entries.elementAt(day) == null) {
                        entries.setElementAt(transaction.getAmount().floatValue(), day);
                    } else {
                        entries.setElementAt(entries.get(day) + transaction.getAmount().floatValue(), day);
                    }
                }
            }
        }
        List<Entry> sumEntries = new ArrayList<>();
        int i = 1;
        for (Float dayExpenses : entries) {
            if(dayExpenses == null) {
                sumEntries.add(new Entry(i, 0));
            } else {
                sumEntries.add(new Entry(i, dayExpenses));
            }
            i++;
        }
        callback.pass(sumEntries);
    }

    @Override
    public void getDailyBudgetEntries(ArrayList<Transaction> transactions, Callback callback) {
        Date today = new Date();
        Vector<Float> entries = new Vector<>();
        entries.setSize(24);
        for (Transaction transaction: transactions) {
            if(Transaction.isRegular(transaction.getType()) && Transaction.dateOverlapping(today, transaction)) {
                int daysBetween = Transaction.getDaysBetween(transaction.getDate(), today);
                boolean isTransactionToday = (daysBetween % transaction.getTransactionInterval()) == 0;
                if(isTransactionToday) {
                    Calendar date = Transaction.toCalendar(transaction.getDate().getTime());
                    int hour = date.get(Calendar.HOUR);
                    if(!Transaction.isIncome(transaction.getType())) {
                        if(entries.elementAt(hour) == null) {
                            entries.setElementAt(-transaction.getAmount().floatValue(), hour);
                        } else {
                            entries.setElementAt(entries.get(hour) - transaction.getAmount().floatValue(), hour);
                        }
                    } else {
                        if(entries.elementAt(hour) == null) {
                            entries.setElementAt(transaction.getAmount().floatValue(), hour);
                        } else {
                            entries.setElementAt(entries.get(hour) + transaction.getAmount().floatValue(), hour);
                        }
                    }

                }

            } else if(Transaction.sameDay(today, transaction.getDate())) {
                Calendar date = Transaction.toCalendar(transaction.getDate().getTime());
                int hour = date.get(Calendar.HOUR);
                if(!Transaction.isIncome(transaction.getType())) {
                    if(entries.elementAt(hour) == null) {
                        entries.setElementAt(-transaction.getAmount().floatValue(), hour);
                    } else {
                        entries.setElementAt(entries.get(hour) - transaction.getAmount().floatValue(), hour);
                    }
                } else {
                    if(entries.elementAt(hour) == null) {
                        entries.setElementAt(transaction.getAmount().floatValue(), hour);
                    } else {
                        entries.setElementAt(entries.get(hour) + transaction.getAmount().floatValue(), hour);
                    }
                }

            }
        }
        List<Entry> sumEntries = new ArrayList<>();
        int i = 1;
        for (Float dayExpenses : entries) {
            if(dayExpenses == null) {
                sumEntries.add(new Entry(i, 0));
            } else {
                sumEntries.add(new Entry(i, dayExpenses));
            }
            i++;
        }
        callback.pass(sumEntries);
    }

    @Override
    public void getMonthlyBudgetEntries(ArrayList<Transaction> transactions, Callback callback) {
        Date today = new Date();
        Vector<Float> entries = new Vector<>();

        Calendar firstDay = Transaction.toCalendar(today.getTime());
        firstDay.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDayDate = firstDay.getTime();

        Calendar lastDay = firstDay;
        lastDay.set(Calendar.DAY_OF_MONTH, lastDay.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date lastDayDate = lastDay.getTime();

        entries.setSize(lastDay.getActualMaximum(Calendar.DAY_OF_MONTH));
        for (Transaction transaction: transactions) {
            if(Transaction.isRegular(transaction.getType()) && Transaction.dateOverlapping(today, transaction)) {
                Integer daysBetween;
                //transakcija je pocela prije prvog dana u mjesecu
                if(transaction.getDate().before(firstDayDate)) {
                    //transakcija zavrsava prije zadnjeg dana u mjesecu
                    if(transaction.getEndDate().before(lastDayDate)) {
                        daysBetween = Transaction.getDaysBetween(firstDayDate, transaction.getEndDate());
                    } else {
                        daysBetween = Transaction.getDaysBetween(firstDayDate, lastDayDate);
                    }
                } else {
                    //transakcija zavrsava prije zadnjeg dana u mjesecu
                    if(transaction.getEndDate().before(lastDayDate)) {
                        daysBetween = Transaction.getDaysBetween(transaction.getDate(), transaction.getEndDate());
                    } else {
                        daysBetween = Transaction.getDaysBetween(transaction.getDate(), lastDayDate);
                    }
                }
                Float amount = ((int)(daysBetween/transaction.getTransactionInterval())) * transaction.getAmount().floatValue();
                Calendar date = Transaction.toCalendar(transaction.getDate().getTime());
                int day = date.get(Calendar.DAY_OF_MONTH) - 1;
                if(!Transaction.isIncome(transaction.getType())) {
                    if(entries.elementAt(day) == null) {
                        entries.setElementAt(-amount, day);
                    } else {
                        entries.setElementAt(entries.get(day) - amount, day);
                    }
                } else {
                    if(entries.elementAt(day) == null) {
                        entries.setElementAt(amount, day);
                    } else {
                        entries.setElementAt(entries.get(day) + amount, day);
                    }
                }

            } else if(Transaction.sameMonth(today, transaction.getDate())) {
                Calendar date = Transaction.toCalendar(transaction.getDate().getTime());
                int day = date.get(Calendar.DAY_OF_MONTH) - 1;
                if(!Transaction.isIncome(transaction.getType())) {
                    if(entries.elementAt(day) == null) {
                        entries.setElementAt(-transaction.getAmount().floatValue(), day);
                    } else {
                        entries.setElementAt(entries.get(day) - transaction.getAmount().floatValue(), day);
                    }
                } else {
                    if(entries.elementAt(day) == null) {
                        entries.setElementAt(transaction.getAmount().floatValue(), day);
                    } else {
                        entries.setElementAt(entries.get(day) + transaction.getAmount().floatValue(), day);
                    }
                }

            }
        }
        List<Entry> sumEntries = new ArrayList<>();
        int i = 0;
        for (Float dayExpenses : entries) {
            if(dayExpenses == null) {
                sumEntries.add(new Entry(i, 0));
            } else {
                sumEntries.add(new Entry(i, dayExpenses));
            }
            i++;
        }
        callback.pass(sumEntries);
    }

    @Override
    public void getWeeklyBudgetEntries(ArrayList<Transaction> transactions, Callback callback) {
        Date today = new Date();
        Vector<Float> entries = new Vector<>();
        entries.setSize(7);

        Calendar firstDay = Transaction.toCalendar(today.getTime());
        firstDay.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        firstDay.clear(Calendar.MINUTE);
        firstDay.clear(Calendar.SECOND);
        firstDay.clear(Calendar.MILLISECOND);
        firstDay.set(Calendar.DAY_OF_WEEK, firstDay.getFirstDayOfWeek());

        Calendar lastDay = firstDay;
        lastDay.add(Calendar.DAY_OF_MONTH, 7);
        Date lastDayDate = lastDay.getTime();
        for (Transaction transaction: transactions) {
            if(Transaction.isRegular(transaction.getType()) && Transaction.dateOverlapping(today, transaction)) {
                Float amount;
                Calendar start = Transaction.toCalendar(transaction.getDate().getTime());
                Calendar end = Transaction.toCalendar(transaction.getEndDate().getTime());
                while (start.getTime().before(end.getTime()) && start.getTime().before(lastDayDate)) {
                    if(Transaction.sameWeek(start.getTime(), today)) {
                        if(transaction.getTransactionInterval() <= 7) {
                            amount = transaction.getAmount().floatValue();
                            Calendar date = Transaction.toCalendar(transaction.getDate().getTime());
                            int day = date.get(Calendar.DAY_OF_WEEK) - 1;
                            if(!Transaction.isIncome(transaction.getType())) {
                                if(entries.elementAt(day) == null) {
                                    entries.setElementAt(-amount, day);
                                } else {
                                    entries.setElementAt(entries.get(day) - amount, day);
                                }
                            } else {
                                if(entries.elementAt(day) == null) {
                                    entries.setElementAt(amount, day);
                                } else {
                                    entries.setElementAt(entries.get(day) + amount, day);
                                }
                            }

                            while (start.getTime().before(end.getTime()) && start.getTime().before(lastDayDate)) {
                                start.add(Calendar.DAY_OF_MONTH, transaction.getTransactionInterval());
                                date = Transaction.toCalendar(transaction.getDate().getTime());
                                day = date.get(Calendar.DAY_OF_WEEK) - 1;
                                if(!Transaction.isIncome(transaction.getType())) {
                                    if(entries.elementAt(day) == null) {
                                        entries.setElementAt(-amount, day);
                                    } else {
                                        entries.setElementAt(entries.get(day) - amount, day);
                                    }
                                } else {
                                    if(entries.elementAt(day) == null) {
                                        entries.setElementAt(amount, day);
                                    } else {
                                        entries.setElementAt(entries.get(day) + amount, day);
                                    }
                                }

                            }
                        } else {
                            amount = transaction.getAmount().floatValue();
                            Calendar date = Transaction.toCalendar(transaction.getDate().getTime());
                            int day = date.get(Calendar.DAY_OF_WEEK) - 1;
                            if(!Transaction.isIncome(transaction.getType())) {
                                if(entries.elementAt(day) == null) {
                                    entries.setElementAt(-amount, day);
                                } else {
                                    entries.setElementAt(entries.get(day) - amount, day);
                                }
                            } else {
                                if(entries.elementAt(day) == null) {
                                    entries.setElementAt(amount, day);
                                } else {
                                    entries.setElementAt(entries.get(day) + amount, day);
                                }
                            }

                        }
                        break;
                    }
                    start.add(Calendar.DAY_OF_MONTH, transaction.getTransactionInterval());
                }
            } else if(Transaction.sameMonth(today, transaction.getDate())) {
                Calendar date = Transaction.toCalendar(transaction.getDate().getTime());
                int day = date.get(Calendar.DAY_OF_WEEK) - 1;
                if(entries.elementAt(day) == null) {
                    entries.setElementAt(transaction.getAmount().floatValue(), day);
                } else {
                    entries.setElementAt(entries.get(day) + transaction.getAmount().floatValue(), day);
                }
            }
        }
        List<Entry> sumEntries = new ArrayList<>();
        int i = 1;
        for (Float dayExpenses : entries) {
            if(dayExpenses == null) {
                sumEntries.add(new Entry(i, 0));
            } else {
                sumEntries.add(new Entry(i, dayExpenses));
            }
            i++;
        }
        callback.pass(sumEntries);
    }
}
