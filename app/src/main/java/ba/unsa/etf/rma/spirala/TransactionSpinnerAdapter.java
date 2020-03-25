package ba.unsa.etf.rma.spirala;

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

public class TransactionSpinnerAdapter extends ArrayAdapter<Transaction.Type> {
    private int resource;
    public TextView titleView;
    public ImageView imageView;

    public TransactionSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<Transaction.Type> items) {
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
        Transaction.Type type = getItem(position);
        // Ovdje mozete dohvatiti reference na View
        // i popuniti ga sa vrijednostima polja iz objekta
        titleView = newView.findViewById(R.id.title);
        titleView.setText(type.toString());


        imageView = newView.findViewById(R.id.icon);
        String genreMatch = type.toString().toLowerCase();
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

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    public void setFilters(ArrayList<Transaction.Type> types) {
        super.clear();
        this.addAll(types);
    }
}
