package ba.unsa.etf.rma.spirala.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import ba.unsa.etf.rma.spirala.R;
import ba.unsa.etf.rma.spirala.data.Transaction;

public class TransactionListAdapter extends ArrayAdapter<Transaction> {
    private int resource;
    public TextView titleView;
    public TextView valueView;
    public ImageView imageView;

    public TransactionListAdapter(@NonNull Context context, int resource, @NonNull List<Transaction> items) {
        super(context, resource, items);
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        // Kreiranje i inflate-anje view klase
        LinearLayout newView;
        if (convertView == null) {
            // Ukoliko je ovo prvi put da se pristupa klasi convertView,
            //odnosno nije upadate
            // Potrebno je kreirati novi objekat i inflate-ati ga
            newView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li;
            li = (LayoutInflater)getContext().getSystemService(inflater);
            li.inflate(resource, newView, true);
        } else {
            // Ukoliko je update potrebno je samo
            //izmjeniti vrijednosti polja
            newView = (LinearLayout)convertView;
        }
        Transaction transaction = getItem(position);
        // Ovdje mozete dohvatiti reference na View
        // i popuniti ga sa vrijednostima polja iz objekta
        titleView = newView.findViewById(R.id.title);
        valueView = newView.findViewById(R.id.value);
        titleView.setText(transaction.getTitle());
        valueView.setText(transaction.getAmount().toString());


        imageView = newView.findViewById(R.id.icon);
        String genreMatch = transaction.getType().toString().toLowerCase();
        try {
            Class res = R.drawable.class;
            Field field = res.getField(genreMatch);
            int drawableId = field.getInt(null);
            imageView.setImageResource(drawableId);
        }
        catch (Exception e) {
            imageView.setImageResource(R.drawable.all);
        }
        return newView;
    }


    public void setTransactions(ArrayList<Transaction> transactions) {
        super.clear();
        this.addAll(transactions);
    }

    public Transaction getTransactionAt(int position) {
        return super.getItem(position);
    }
}
