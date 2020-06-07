package ba.unsa.etf.rma.spirala.list;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import java.lang.reflect.Field;

import ba.unsa.etf.rma.spirala.R;
import ba.unsa.etf.rma.spirala.util.TransactionDBOpenHelper;

public class TransactionListCursorAdapter extends ResourceCursorAdapter {
    private int resource;
    public TextView titleView;
    public TextView valueView;
    public ImageView imageView;

    public TransactionListCursorAdapter(Context context, int layout, Cursor c, boolean autoRequery) {
        super(context, layout, c, autoRequery);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        titleView = view.findViewById(R.id.title);
        valueView = view.findViewById(R.id.value);
        imageView = view.findViewById(R.id.icon);
        CharSequence cs = cursor.getString(cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_TITLE));

        titleView.setText(cs);
        valueView.setText(cursor.getString(cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_AMOUNT)));
        String genreMatch = cursor.getString(cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_TYPE));
        try {
            Class res = R.drawable.class;
            Field field = res.getField(genreMatch.toLowerCase());
            int drawableId = field.getInt(null);
            imageView.setImageResource(drawableId);
        }
        catch (Exception e) {
            imageView.setImageResource(R.drawable.all);
        }


    }
}
