/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marchemos;
//hh

import java.util.ArrayList;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

public class Ciclosxyz {

    public ArrayList<Double> tramaz;
    public ArrayList<Double> tramax;
    public ArrayList<Double> tramay;
    private ArrayList<Integer> pospicos;
    public ArrayList<ArrayList<Double>> ciclosZ;
    public ArrayList<ArrayList<Double>> ciclosX;
    public ArrayList<ArrayList<Double>> ciclosY;

    public Ciclosxyz(ArrayList x, ArrayList y, ArrayList z) {

        tramax = new ArrayList<Double>(x);
        tramay = new ArrayList<Double>(y);
        tramaz = new ArrayList<Double>(z);
        pospicos = new ArrayList<Integer>();

    }

    public void CalcCiclos() {
        for (int i = 25; i < tramaz.size() - 25; i++) {
            if ((tramaz.get(i - 25) < tramaz.get(i)) && (tramaz.get(i - 24) < tramaz.get(i))
                    && (tramaz.get(i - 23) < tramaz.get(i)) && (tramaz.get(i - 22) < tramaz.get(i))
                    && (tramaz.get(i - 21) < tramaz.get(i)) && (tramaz.get(i - 20) < tramaz.get(i)) && (tramaz.get(i - 19) < tramaz.get(i))
                    && (tramaz.get(i - 18) < tramaz.get(i)) && (tramaz.get(i - 17) < tramaz.get(i))
                    && (tramaz.get(i - 16) < tramaz.get(i)) && (tramaz.get(i - 15) < tramaz.get(i))
                    && (tramaz.get(i - 14) < tramaz.get(i)) && (tramaz.get(i - 13) < tramaz.get(i))
                    && (tramaz.get(i - 12) < tramaz.get(i)) && (tramaz.get(i - 11) < tramaz.get(i))
                    && (tramaz.get(i - 10) < tramaz.get(i)) && (tramaz.get(i - 9) < tramaz.get(i))
                    && (tramaz.get(i - 8) < tramaz.get(i)) && (tramaz.get(i - 7) < tramaz.get(i))
                    && (tramaz.get(i - 6) < tramaz.get(i)) && (tramaz.get(i - 5) < tramaz.get(i))
                    && (tramaz.get(i - 4) < tramaz.get(i)) && (tramaz.get(i - 3) < tramaz.get(i)) && (tramaz.get(i - 2) < tramaz.get(i))
                    && (tramaz.get(i - 1) < tramaz.get(i)) && (tramaz.get(i) >= tramaz.get(i + 1)) && (tramaz.get(i) > tramaz.get(i + 2))
                    && (tramaz.get(i) > tramaz.get(i + 3)) && (tramaz.get(i) > tramaz.get(i + 4)) && (tramaz.get(i) > tramaz.get(i + 5))
                    && (tramaz.get(i) > tramaz.get(i + 6)) && (tramaz.get(i) > tramaz.get(i + 7)) && (tramaz.get(i) > tramaz.get(i + 8))
                    && (tramaz.get(i) > tramaz.get(i + 9)) && (tramaz.get(i) > tramaz.get(i + 10)) && (tramaz.get(i) > tramaz.get(i + 11))
                    && (tramaz.get(i) > tramaz.get(i + 12)) && (tramaz.get(i) > tramaz.get(i + 13)) && (tramaz.get(i) > tramaz.get(i + 14))
                    && (tramaz.get(i) > tramaz.get(i + 15)) && (tramaz.get(i) > tramaz.get(i + 16))
                    && (tramaz.get(i) > tramaz.get(i + 17)) && (tramaz.get(i) > tramaz.get(i + 18))
                    && (tramaz.get(i) > tramaz.get(i + 19)) && (tramaz.get(i) > tramaz.get(i + 20)) && (tramaz.get(i) > tramaz.get(i + 21))
                    && (tramaz.get(i) > tramaz.get(i + 22)) && (tramaz.get(i) > tramaz.get(i + 23))
                    && (tramaz.get(i) > tramaz.get(i + 24)) && (tramaz.get(i) > tramaz.get(i + 25))) {
                if (tramaz.get(i + 6) > tramaz.get(i) - tramaz.get(i) * 0.5) {
                    int k = i;
                    int j = i;
                    while (tramaz.get(i) < tramaz.get(j) + tramaz.get(i) * 0.6) {
                        j = j + 1;
                    }
                    while (tramaz.get(i) < tramaz.get(k) + tramaz.get(i) * 0.3) {
                        k = k - 1;
                    }
                    int p = Math.round((j + k) / 2);
                    pospicos.add(p);
                } else {
                    pospicos.add(i);

                }

            }
        }

        ciclosZ = new ArrayList<ArrayList<Double>>();
        ciclosX = new ArrayList<ArrayList<Double>>();
        ciclosY = new ArrayList<ArrayList<Double>>();

        for (int i = 0; i < pospicos.size() - 1; i++) {
            ciclosZ.add(new ArrayList<Double>());
            ciclosX.add(new ArrayList<Double>());
            ciclosY.add(new ArrayList<Double>());

            for (int j = pospicos.get(i); j < pospicos.get(i + 1); j++) {
                ciclosZ.get(i).add(tramaz.get(j));
                ciclosX.get(i).add(tramax.get(j));
                ciclosY.get(i).add(tramay.get(j));
            }
        }

    }

    public static int MediaCiclos(ArrayList<ArrayList<Double>> M) {
        int med = 0;
        for (int i = 0; i < M.size(); i++) {
            med = med + M.get(i).size();
        }
        return Math.round((float) med / M.size());
    }

    public static double[] Promediado(ArrayList<ArrayList<Double>> M) {
        double[] prom = new double[M.get(0).size()];
        double acum = 0;
        for (int i = 0; i < prom.length; i++) {
            acum = 0;
            for (int j = 0; j < M.size(); j++) {
                acum = acum + M.get(j).get(i);
            }
            prom[i] = acum / M.size();

        }

        return prom;
    }

    public static double[] Promediado2(ArrayList<ArrayList<Double>> M, int n) {
        double[] prom = new double[M.get(0).size()];
        double aux = 0;
        WeightedObservedPoints obs = new WeightedObservedPoints();;
        for (int i = 0; i < M.size(); i++) {
            for (int j = 0; j < M.get(0).size(); j++) {
                obs.add(j, M.get(i).get(j));
            }
        }
        PolynomialCurveFitter fitter = PolynomialCurveFitter.create(n);
        double[] coeff = fitter.fit(obs.toList());

        for (int i = 0; i < M.get(0).size(); i++) {

            aux = coeff[0];
            for (int j = 1; j < n + 1; j++) {
                aux = aux + (Math.pow(i, j) * coeff[j]);

            }
            prom[i] = aux;

        }

        return prom;
    }
}

