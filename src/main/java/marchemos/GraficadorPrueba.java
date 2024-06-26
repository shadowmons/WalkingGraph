package marchemos;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import org.apache.commons.math3.complex.Complex;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author victo
 */
public class GraficadorPrueba {

    public void GraficadorPrueba() {

    }

    public void Su2(double[] valor, int number, String referencia, int n) {
       
       
       HistogramDataset dataset = new HistogramDataset();
       dataset.setType(HistogramType.FREQUENCY);
       dataset.addSeries("Histogram",valor,number);

        JFreeChart chart1 = ChartFactory.createHistogram(
                "Histograma de autenticación",
                referencia,
                "Frecuencia",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot1 = chart1.getXYPlot();
        XYBarRenderer renderer = new XYBarRenderer();
    Color color1;
        if (n==0){
            color1 = new Color(167, 155, 255); 
        }else {
      color1 = new Color(40, 0, 85); 
        }
            renderer.setSeriesPaint(0, color1);
        plot1.setRenderer(renderer);
        plotear(plot1);
        chart1.getLegend().setFrame(BlockBorder.NONE);
        chart1.setTitle(new TextTitle("Criterio mm",
                new Font("Serif", Font.BOLD, 18)));
        CrearFrame2("Criterio mm", chart1);
    }
    
    public void Su() {
        double[] mm = new double[50];
        double[] auxm = new double[50];
        double[] auxd = new double[50];
        double[] dm = new double[50];

        for (int i = 0; i < dm.length; i++) {

            auxm[i] = (double) i *0.005 +0.75;

            if (i > 30) {
                mm[i] = (8 * auxm[i] - 7) * 0.065;

            } else {
                mm[i] = 0.2*0.065;
            }
        }

        for (int i = 0; i < dm.length; i++) {
            auxd[i] = (double) (i) * 0.004;
            if (auxd[i] < 0.1) {
                dm[i] = (1.2/ ((auxd[i] * 25) + 0.3)) * auxd[i];
            } else {
                dm[i] = 0.04286;
            }

        }

        XYSeries series1 = new XYSeries("Espectro de Z promedio");
        XYSeriesCollection dataset1 = new XYSeriesCollection();
        //establecerdata(series1, dm, auxd);
       establecerdata(series1, dm, auxd);
        dataset1.addSeries(series1);
        JFreeChart chart1 = ChartFactory.createXYLineChart(
                "Criterio mm",
                "Valor de entrada",
                "Coeficiente de desviación",
                dataset1,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot1 = chart1.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        renderer.setSeriesPaint(0, Color.BLACK);
        renderer.setSeriesStroke(0, new BasicStroke(1.2f));
        renderer.setSeriesShapesVisible(0, false);
        plot1.setRenderer(renderer);
        plotear(plot1);
        chart1.getLegend().setFrame(BlockBorder.NONE);
        chart1.setTitle(new TextTitle("Criterio mm",
                new Font("Serif", Font.BOLD, 18)));
        CrearFrame2("Criterio mm", chart1);
    }
    
    public void Su3() {
        double[] mm = new double[100];
        double[] auxm = new double[100];
        double[] mm2 = new double[335];
        double[] auxm2 = new double[335];

        for (int i = 0; i < mm.length; i++) {

            auxm[i] = 0.06283*i;
            mm[i] = Math.sin(auxm[i]);

           
        }
        
        for (int i = 0; i < mm2.length; i++) {

            auxm2[i] = 0.06283*i*1.2;
            mm2[i] = Math.sin(auxm2[i])+0.5*Math.sin(auxm2[i]*4)+2*Math.sin(auxm2[i]*8);

           
        }
        int nPuntos = 1024; //numero de puntos de la FFT 
                    Complex zpromFrecuencia[] = new Complex[nPuntos];
                    double zpromFrecuencia_mod[] = new double[nPuntos];
                    FFT fz = new FFT(mm2, nPuntos, 1);
                    zpromFrecuencia = fz.Transformada(mm2.length);
                    for (int i = 0; i < zpromFrecuencia.length; i++) {
                        zpromFrecuencia_mod[i] = Math.sqrt(Math.pow(zpromFrecuencia[i].getReal(), 2) + Math.pow(zpromFrecuencia[i].getImaginary(), 2));
                    }
                    //Desplazamiento de la FFT
                    double zpromFrecuencia_mod2[] = new double[nPuntos];
                    zpromFrecuencia_mod2 = fz.FFTShift(zpromFrecuencia_mod);
                    //Eje normalizado
                    double ejeX[] = new double[nPuntos];
                    ejeX = fz.EjeNormalizado();


        XYSeries series1 = new XYSeries("Espectro de Z promedio");
        XYSeriesCollection dataset1 = new XYSeriesCollection();
      //   establecerdata(series1, mm2, auxm2);
        establecerdata(series1, zpromFrecuencia_mod2 , ejeX);
        dataset1.addSeries(series1);
        JFreeChart chart1 = ChartFactory.createXYLineChart(
                "Criterio mm",
                "Valor de entrada",
                "Coeficiente de desviación",
                dataset1,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot1 = chart1.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        renderer.setSeriesPaint(0, Color.BLACK);
        renderer.setSeriesStroke(0, new BasicStroke(1.2f));
        renderer.setSeriesShapesVisible(0, false);
        plot1.setRenderer(renderer);
        plotear(plot1);
        chart1.getLegend().setFrame(BlockBorder.NONE);
        chart1.setTitle(new TextTitle("Criterio mm",
                new Font("Serif", Font.BOLD, 18)));
        CrearFrame2("Criterio mm", chart1);
    }

    private void establecerdata(XYSeries data, double[] x) {
        for (int i = 0; i < x.length; i++) {
            data.add(i, x[i]);
        }

    }

    private void establecerdata(XYSeries data, double[] x, double[] y) {
        for (int i = 0; i < x.length; i++) {
            data.add(y[i], x[i]);
        }

    }

    private void plotear(XYPlot plot) {
        plot.setBackgroundPaint(Color.white);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

    }

    private void CrearFrame2(String titulo, JFreeChart chart) {
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.white);

        JFrame frame = new JFrame(titulo);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 600);
        frame.add(chartPanel);
        frame.setVisible(true);
    }

}
