package ba.unsa.etf.rma.spirala.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TransactionModel {
    public static ArrayList<Transaction> transactions = new ArrayList<Transaction>() {
        {
            Calendar c = Calendar.getInstance();
            add(new Transaction(c.getTime(), 3.141, "Pi", Transaction.Type.PURCHASE, "Matematicka konstanta", null, null));

            c.set(2020,0,1);
            add(new Transaction(c.getTime(), 3143.17, "PC", Transaction.Type.PURCHASE, "Novi PC", null, null));

            c.set(2020,1,29);
            add(new Transaction(c.getTime(), 315.00, "PURCHASE3", Transaction.Type.PURCHASE, "Novi fon", null, null));

            c.set(2020,4,13);
            add(new Transaction(c.getTime(), 65.00, "PURCHASE4", Transaction.Type.PURCHASE, "Kabl", null, null));

            c.set(2020,8,5);
            add(new Transaction(c.getTime(), 333., "INDIVIDUALPAYMENT1", Transaction.Type.INDIVIDUALPAYMENT, "renta za xyz u okolini tacke epsilon", null, null));

            c.set(2020,10,6);
            add(new Transaction(c.getTime(), 17., "INDIVIDUALPAYMENT2", Transaction.Type.INDIVIDUALPAYMENT, "Dobro je hvala bogu kako su vasi", null, null));

            c.set(2020,7,21);
            add(new Transaction(c.getTime(), 17., "INDIVIDUALPAYMENT3", Transaction.Type.INDIVIDUALPAYMENT, "Dodjite", null, null));

            c.set(2020,10,6);
            add(new Transaction(c.getTime(), 17., "INDIVIDUALPAYMENT4", Transaction.Type.INDIVIDUALPAYMENT, "Docemo", null, null));

            c.set(2020,4,15);
            Date endDate = c.getTime();
            c.set(2020, 2, 15);
            add(new Transaction(c.getTime(), 3.00, "REGULARPAYMENT1", Transaction.Type.REGULARPAYMENT, "i sta ima jos", 7, endDate));

            c.set(2020,11,31);
            Date endDate2 = c.getTime();
            c.set(2020, 0, 1);
            add(new Transaction(c.getTime(), 0.5, "REGULARPAYMENT2", Transaction.Type.REGULARPAYMENT, "Izlaz prati ulaz", 1, endDate2));

            c.set(2020,2,1);
            Date endDate3 = c.getTime();
            c.set(2020, 2, 31);
            add(new Transaction(c.getTime(), 0.05, "REGULARPAYMENT3", Transaction.Type.REGULARPAYMENT, "gibe money pls am poor", 15, endDate3));

            c.set(2020,6,1);
            Date endDatet = c.getTime();
            c.set(2020, 3, 31);
            add(new Transaction(c.getTime(), 0.01, "REGULARPAYMENT4", Transaction.Type.REGULARPAYMENT, "gibe money pls am poor", 15, endDatet));

            c.set(2020, 1, 5);
            add(new Transaction(c.getTime(), 0.17, "INDIVIDUALINCOME1", Transaction.Type.INDIVIDUALINCOME, null, null, null));

            c.set(2020, 2, 5);
            add(new Transaction(c.getTime(), 0.17, "INDIVIDUALINCOME2", Transaction.Type.INDIVIDUALINCOME, null, null, null));

            c.set(2020, 3, 5);
            add(new Transaction(c.getTime(), 0.17, "INDIVIDUALINCOME3", Transaction.Type.INDIVIDUALINCOME, null, null, null));

            c.set(2020, 4, 5);
            add(new Transaction(c.getTime(), 0.17, "INDIVIDUALINCOME4", Transaction.Type.INDIVIDUALINCOME, null, null, null));

            c.set(2020,11,31);
            Date endDate4 = c.getTime();
            c.set(2020, 2, 1);
            add(new Transaction(c.getTime(), 5., "REGULARINCOME1", Transaction.Type.REGULARINCOME, null, 1, endDate4));

            c.set(2020,5,15);
            Date endDate5 = c.getTime();
            c.set(2020, 0, 15);
            add(new Transaction(c.getTime(), 0.313, "REGULARINCOME2", Transaction.Type.REGULARINCOME, null, 10, endDate5));

            c.set(2020,0,1);
            Date endDate6 = c.getTime();
            c.set(2020, 11, 31);
            add(new Transaction(c.getTime(), 0.53, "REGULARINCOME3", Transaction.Type.REGULARINCOME, null, 15, endDate6));

            c.set(2020,0,1);
            Date endDate7 = c.getTime();
            c.set(2020, 1, 1);
            add(new Transaction(c.getTime(), 0.27, "REGULARINCOME4", Transaction.Type.REGULARINCOME, null, 30, endDate7));
        }
    };
}
