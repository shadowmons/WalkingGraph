package marchemos;

import org.apache.commons.math3.stat.correlation.*;

import java.util.ArrayList;

public class Correlacion {

    private PearsonsCorrelation cor;
    private SpearmansCorrelation spear;
    private KendallsCorrelation kend;

    public Correlacion(){
        cor = new PearsonsCorrelation();
        spear = new SpearmansCorrelation();
        kend = new KendallsCorrelation();

    }

    
    public double CoPearson(double[] x, double[] y){
        return cor.correlation(x, y);
    }
    
    public double CoSpearman(double[] x, double[] y){
        return spear.correlation(x, y);
    }
    
    public double CoKendall(double[] x, double[] y){
        return kend.correlation(x, y);
    }
    public double[] PearsonCorrelacion(ArrayList<ArrayList<Float>> M, double[] pro){
        double[] pc = new double[M.size()];
        float acum =0;
        for (int i = 0; i < M.size(); i++) {
            double[] fou = new double[M.get(i).size()];
            for (int j = 0; j < M.get(i).size(); j++) {
                fou[j] = M.get(i).get(j);
            }
            pc[i] = cor.correlation(pro, fou);

            }

        return pc;
    }
     public double[] SpearmanCorrelacion(ArrayList<ArrayList<Float>> M, double[] pro){
        double[] pc = new double[M.size()];
        float acum =0;
        for (int i = 0; i < M.size(); i++) {
            double[] fou = new double[M.get(i).size()];
            for (int j = 0; j < M.get(i).size(); j++) {
                fou[j] = M.get(i).get(j);
            }
            pc[i] = spear.correlation(pro, fou);

            }

        return pc;
    }
      public double[] KendallCorrelacion(ArrayList<ArrayList<Float>> M, double[] pro){
        double[] pc = new double[M.size()];
        float acum =0;
        for (int i = 0; i < M.size(); i++) {
            double[] fou = new double[M.get(i).size()];
            for (int j = 0; j < M.get(i).size(); j++) {
                fou[j] = M.get(i).get(j);
            }
            pc[i] = kend.correlation(pro, fou);

            }

        return pc;
    }
}
