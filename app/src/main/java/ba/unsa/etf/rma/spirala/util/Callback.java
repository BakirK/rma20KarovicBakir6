package ba.unsa.etf.rma.spirala.util;

public class Callback {
    private ICallback callback;
    public Callback(ICallback callback) {
        this.callback = callback;
    }
    public Object pass(Object o) {
        return callback.callback(o);
    }
}
