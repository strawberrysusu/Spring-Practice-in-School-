package chapter07;

public class ExeTimeCalculator implements Calculator {

    private final Calculator delegate;

    public ExeTimeCalculator(Calculator delegate) {
        this.delegate = delegate;
    }

    @Override
    public long factorial(long num) {
        long start = System.nanoTime();
        long result = delegate.factorial(num);
        long end = System.nanoTime();
        System.out.printf(
                "%s.factorial(%d) 실행시간 = %d ns%n",
                delegate.getClass().getSimpleName(),
                num,
                end - start
        );
        return result;
    }
}
