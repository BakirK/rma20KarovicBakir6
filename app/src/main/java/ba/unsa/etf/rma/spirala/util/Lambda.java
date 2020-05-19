package ba.unsa.etf.rma.spirala.util;

public class Lambda {
    private ILambda lambda;
    public Lambda(ILambda lambda) {
        this.lambda = lambda;
    }
    public Object pass(Object o) {
        return lambda.callback(o);
    }
}
