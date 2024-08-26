package com.assignement.realtimequoteviewer.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *BlackScholes I
 */
public class BlackScholesFormula {

    // The SND numerical approximation below uses six constant values in its formula.
    private static final double P = 0.2316419;
    private static final double B1 = 0.319381530;
    private static final double B2 = -0.356563782;
    private static final double B3 = 1.781477937;
    private static final double B4 = -1.821255978;
    private static final double B5 = 1.330274429;

    public static BigDecimal calculate(boolean callOption, double spotPrice, double strikePrice,
                                   double riskFreeRate, double timeToExpiry, double returnsImpliedVol) {

        double blackScholesOptionPrice;

        if (callOption) {
            double cd1 = cumulativeDistribution(d1(spotPrice, strikePrice, riskFreeRate, timeToExpiry, returnsImpliedVol));
            double cd2 = cumulativeDistribution(d2(spotPrice, strikePrice, riskFreeRate, timeToExpiry, returnsImpliedVol));

            blackScholesOptionPrice = spotPrice * cd1 - strikePrice * Math.exp(-riskFreeRate * timeToExpiry) * cd2;
        } else {
            double cd1 = cumulativeDistribution(-d1(spotPrice, strikePrice, riskFreeRate, timeToExpiry, returnsImpliedVol));
            double cd2 = cumulativeDistribution(-d2(spotPrice, strikePrice, riskFreeRate, timeToExpiry, returnsImpliedVol));

            blackScholesOptionPrice = strikePrice * Math.exp(-riskFreeRate * timeToExpiry) * cd2 - spotPrice * cd1;
        }
        return BigDecimal.valueOf(blackScholesOptionPrice).setScale(4, RoundingMode.HALF_DOWN);
    }

    private static double d1(double spotPrice, double strikePrice, double riskFreeRate, double timeToExpiry, double returnsImpliedVol) {

        double top = Math.log(spotPrice / strikePrice) + (riskFreeRate + Math.pow(returnsImpliedVol, 2) / 2) * timeToExpiry;
        double bottom = returnsImpliedVol * Math.sqrt(timeToExpiry);

        return top / bottom;
    }

    private static double d2(double spotPrice, double strikePrice, double riskFreeRate, double timeToExpiry, double returnsImpliedVol) {
        return d1(spotPrice, strikePrice, riskFreeRate, timeToExpiry, returnsImpliedVol) - returnsImpliedVol * Math.sqrt(timeToExpiry);
    }

    public static double cumulativeDistribution(double x) {

        double t = 1 / (1 + P * Math.abs(x));
        double t1 = B1 * Math.pow(t, 1);
        double t2 = B2 * Math.pow(t, 2);
        double t3 = B3 * Math.pow(t, 3);
        double t4 = B4 * Math.pow(t, 4);
        double t5 = B5 * Math.pow(t, 5);
        double b = t1 + t2 + t3 + t4 + t5;

        double snd = standardNormalDistribution(x); //for testing
        double cd = 1 - (snd * b);

        double resp = 0.0;
        if (x < 0) {
            resp = 1 - cd;
        } else {
            resp = cd;
        }

        return resp;
    }

    public static double standardNormalDistribution(double x) {

        double top = Math.exp(-0.5 * Math.pow(x, 2));
        double bottom = Math.sqrt(2 * Math.PI);
        double resp = top / bottom;

        return resp;
    }

}