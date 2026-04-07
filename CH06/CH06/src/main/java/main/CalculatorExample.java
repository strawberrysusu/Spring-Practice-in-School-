package main;

import chapter07.ExeTimeCalculator;
import chapter07.ImpleCalculator;
import chapter07.RecCalculator;

public class CalculatorExample {

    public static void main(String[] args) {
        ExeTimeCalculator calculator1 = new ExeTimeCalculator(new ImpleCalculator());
        System.out.println(calculator1.factorial(20));

        ExeTimeCalculator calculator2 = new ExeTimeCalculator(new RecCalculator());
        System.out.println(calculator2.factorial(20));
    }
}
