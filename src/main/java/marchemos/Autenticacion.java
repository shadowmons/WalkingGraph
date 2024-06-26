/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marchemos;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Victor Gil
 */
public class Autenticacion {

    //auxiliares
    float po1 = 0, po2 = 0, po3 = 0, po4 = 0, po5 = 0, po6 = 0;
    int c1 = 0, c2 = 0, c3 = 0, c4 = 0, c5 = 0, c6 = 0, c7 = 0, c8 = 0;
    double[] sujetomaux, sujetozaux;

    private String identificador;
    //Correlaciones
    private double mediaPM, desviPM;
    private double mediaSM, desviSM;
    private double mediaKM, desviKM;
    private double mediaPZ, desviPZ;
    private double mediaSZ, desviSZ;
    private double mediaKZ, desviKZ;
    private double criterioPM, criterioSM, criterioKM, criterioPZ, criterioSZ, criterioKZ;

    //Vectores de verificacion de parametros
    private double[] mediaparametrosZ, desviparametrosZ;
    private double[] mediaparametrosM, desviparametrosM;
    //Vectores promedio de la persona
    private double[] zprom, mprom;
    private double[] zespectro, mespectro;

    public Autenticacion(String nombre, double[] zprome, double[] mprome, double[] correPM,
            double[] correSM, double[] correKM, double[] correPZ, double[] correSZ, double[] correKZ,
            ArrayList<double[]> matrizM, ArrayList<double[]> matrizZ, double[] zfft, double[] mfft) {
        identificador = nombre;
        zprom = zprome;
        mprom = mprome;
        zespectro = zfft;
        mespectro = mfft;
        //CALCULAR CORRELACIONES
        mediaPM = StatUtils.mean(correPM);
        desviPM = Math.sqrt(StatUtils.variance(correPM));
        mediaSM = StatUtils.mean(correSM);
        desviSM = Math.sqrt(StatUtils.variance(correSM));
        mediaKM = StatUtils.mean(correKM);
        desviKM = Math.sqrt(StatUtils.variance(correKM));
        mediaPZ = StatUtils.mean(correPZ);
        desviPZ = Math.sqrt(StatUtils.variance(correPZ));
        mediaSZ = StatUtils.mean(correSZ);
        desviSZ = Math.sqrt(StatUtils.variance(correSZ));
        mediaKZ = StatUtils.mean(correKZ);
        desviKZ = Math.sqrt(StatUtils.variance(correKZ));
        ConstantesPearson();
        ConstantesSpearman();
        ConstantesKendall();

        //CALCULAR VECTORES Z Y M
        mediaparametrosZ = new double[10];
        desviparametrosZ = new double[10];
        mediaparametrosM = new double[9];
        desviparametrosM = new double[9];
        DescriptiveStatistics[] statsZ = new DescriptiveStatistics[10];
        DescriptiveStatistics[] statsM = new DescriptiveStatistics[9];
        for (int i = 0; i < 9; i++) {
            statsZ[i] = new DescriptiveStatistics();
            statsM[i] = new DescriptiveStatistics();
            for (int j = 0; j < matrizM.size(); j++) {

                statsZ[i].addValue(matrizZ.get(j)[i]);
                statsM[i].addValue(matrizM.get(j)[i]);
            }

        }
        statsZ[9] = new DescriptiveStatistics();
        for (int i = 0; i < matrizM.size(); i++) {
            statsZ[9].addValue(matrizZ.get(i)[9]);

        }

        for (int i = 0; i < 9; i++) {
            mediaparametrosZ[i] = statsZ[i].getMean();
            desviparametrosZ[i] = Math.sqrt(statsZ[i].getVariance());
            mediaparametrosM[i] = statsM[i].getMean();
            desviparametrosM[i] = Math.sqrt(statsM[i].getVariance());
        }
        mediaparametrosZ[9] = statsZ[9].getMean();
        desviparametrosZ[9] = Math.sqrt(statsZ[9].getVariance());

    }

    public int verificarPersona(double[] cicloz, double[] ciclom) {
        //PONER UN TRY CATCH AQUI PARA VERIFICAR QUE CICLO Z Y CICLO M TIENEN EL MISMO TAMANO
        double[] sujetoz = cicloz;
        double[] sujetom = ciclom;
        float c = 0;
        int verificacion = 0;
        int tamano = sujetom.length;
        double acumulado = 0;
        double correPM, correSM, correKM;
        double correPZ, correSZ, correKZ;
        //Hacer la interpolacion si hace falta
        if (sujetoz.length != zprom.length) {
            InterpolacionCiclos inter = new InterpolacionCiclos(zprom.length);
            sujetoz = inter.InterCicloAuten(sujetoz, zprom.length);
            sujetom = inter.InterCicloAuten(sujetom, zprom.length);

        }

        double[] parametrosZ = ParametrosTiempo.ParametrosTZ(sujetoz);
        double[] parametrosM = ParametrosTiempo.ParametrosTM(sujetom, tamano);

        Correlacion cor = new Correlacion();
        correPM = cor.CoPearson(getMprom(), sujetom);
        correSM = cor.CoSpearman(getMprom(), sujetom);
        correKM = cor.CoKendall(getMprom(), sujetom);
        correPZ = cor.CoPearson(getZprom(), sujetoz);
        correSZ = cor.CoSpearman(getZprom(), sujetoz);
        correKZ = cor.CoKendall(getZprom(), sujetoz);

        if (tamano > getMediaparametrosM()[8] - 3 * getDesviparametrosM()[8]
                && tamano < getMediaparametrosM()[8] + 3 * getDesviparametrosM()[8]) {

            //Comprobar que cumple con las correlaciones Temporales
            if (correPM > criterioPM && correSM > criterioSM
                    && correPZ > criterioPZ && correSZ > criterioSZ) {
                verificacion = 1;
                //Comprobar correlacion Frecuencial

                int nPuntos = 1024; //numero de puntos de la FFT
                //Vectores para el calculo de la FFT del vector m
                Complex mpromFrecuencia[] = new Complex[nPuntos];
                double mpromFrecuencia_mod[] = new double[nPuntos];
                Complex zpromFrecuencia[] = new Complex[nPuntos];
                double zpromFrecuencia_mod[] = new double[nPuntos];
                FFT fm = new FFT(sujetom, nPuntos, 1);
                mpromFrecuencia = fm.Transformada(sujetom.length);
                FFT fz = new FFT(sujetoz, nPuntos, 1);
                zpromFrecuencia = fz.Transformada(sujetoz.length);
                for (int i = 0; i < mpromFrecuencia.length; i++) {
                    mpromFrecuencia_mod[i] = Math.sqrt(Math.pow(mpromFrecuencia[i].getReal(), 2) + Math.pow(mpromFrecuencia[i].getImaginary(), 2));
                    zpromFrecuencia_mod[i] = Math.sqrt(Math.pow(zpromFrecuencia[i].getReal(), 2) + Math.pow(zpromFrecuencia[i].getImaginary(), 2));

                }
                double mpromFrecuencia_mod2[] = new double[nPuntos];
                mpromFrecuencia_mod2 = fm.FFTShift(mpromFrecuencia_mod);
                double correPMFFT = cor.CoPearson(mespectro, mpromFrecuencia_mod2);
                double correSMFFT = cor.CoSpearman(mespectro, mpromFrecuencia_mod2);
                double zpromFrecuencia_mod2[] = new double[nPuntos];
                zpromFrecuencia_mod2 = fz.FFTShift(zpromFrecuencia_mod);
                double correPZFFT = cor.CoPearson(zespectro, zpromFrecuencia_mod2);

                //sujetomaux = mpromFrecuencia_mod2;
                // sujetozaux = zpromFrecuencia_mod2;
                //System.out.println(correPMFFT  + "  SM> " + correSMFFT);
                if (correPMFFT > 0.989) {
                    //Comparacion de parametros temporales
                    verificacion = 2;
                    for (int i = 0; i < 9; i++) {
                        if (i < 4) {
                            c = 1.5f;
                        } else {
                            c = 1f;
                        }
                        //PARAMETROS Z
                        if (parametrosZ[i] > getMediaparametrosZ()[i] - getDesviparametrosZ()[i]
                                && parametrosZ[i] < getMediaparametrosZ()[i] + getDesviparametrosZ()[i]) {
                            //    System.out.println( 1 + " parametroZ en posi " + i + " : " + parametrosZ[i] ); 
                            acumulado = acumulado + 1 * c;

                        } else if (parametrosZ[i] > getMediaparametrosZ()[i] - 2 * getDesviparametrosZ()[i]
                                && parametrosZ[i] < getMediaparametrosZ()[i] + 2 * getDesviparametrosZ()[i]) {
                            //    System.out.println( 0.8 + " parametroZ en posi " + i + " : " + parametrosZ[i] ); 
                            acumulado = acumulado + 0.8 * c;
                        } else if (parametrosZ[i] > getMediaparametrosZ()[i] - 3 * getDesviparametrosZ()[i]
                                && parametrosZ[i] < getMediaparametrosZ()[i] + 3 * getDesviparametrosZ()[i]) {
                            //     System.out.println( 0.4 + " parametroZ en posi " + i + " : " + parametrosZ[i] ); 
                            acumulado = acumulado + 0.2 * c;
                        }

                        //Parametros M
                        if (parametrosM[i] > getMediaparametrosM()[i] - getDesviparametrosM()[i]
                                && parametrosM[i] < getMediaparametrosM()[i] + getDesviparametrosM()[i]) {
                            //      System.out.println( 1 + " parametroM en posi " + i + " : " + parametrosM[i] ); 
                            acumulado = acumulado + 1 * c;

                        } else if (parametrosM[i] > getMediaparametrosM()[i] - 2 * getDesviparametrosM()[i]
                                && parametrosM[i] < getMediaparametrosM()[i] + 2 * getDesviparametrosM()[i]) {
                            //    System.out.println( 0.8 + " parametroM en posi " + i + " : " + parametrosM[i] ); 
                            acumulado = acumulado + 0.8 * c;
                        } else if (parametrosM[i] > getMediaparametrosM()[i] - 3 * getDesviparametrosM()[i]
                                && parametrosM[i] < getMediaparametrosM()[i] + 3 * getDesviparametrosM()[i]) {
                            //     System.out.println( 0.4 + " parametroM en posi " + i + " : " + parametrosM[i] ); 
                            acumulado = acumulado + 0.2 * c;
                        }

                    }
                    if (parametrosZ[9] > getMediaparametrosZ()[9] - getDesviparametrosZ()[9]
                            && parametrosZ[9] < getMediaparametrosZ()[9] + getDesviparametrosZ()[9]) {
                        //      System.out.println( 1 + " parametroZ en posi " + 9 + " : " + parametrosZ[9] );
                        acumulado = acumulado + 1;

                    } else if (parametrosZ[9] > getMediaparametrosZ()[9] - 2 * getDesviparametrosZ()[9]
                            && parametrosZ[9] < getMediaparametrosZ()[9] + 2 * getDesviparametrosZ()[9]) {
                        //    System.out.println( 0.8 + " parametroZ en posi " + 9 + " : " + parametrosZ[9] );
                        acumulado = acumulado + 0.8;
                    } else if (parametrosZ[9] > getMediaparametrosZ()[9] - 3 * getDesviparametrosZ()[9]
                            && parametrosZ[9] < getMediaparametrosZ()[9] + 3 * getDesviparametrosZ()[9]) {
                        //   System.out.println( 0.4 + " parametroZ en posi " + 9 + " : " + parametrosZ[9] );
                        acumulado = acumulado + 0.2;
                    }

                    if (acumulado >= 15.4) {

                        verificacion = 3;
                    }
                    System.out.println(verificacion + ", acumulado despues de la correlacion: " + acumulado);
                }

            }
        }

        return verificacion;
    }

    public void AutenticarLista(String persona, String[] nombres, String[] tablas) {

        int conttotal = 0;
        int contmismosujeto = 0, contsujeto1 = 0, contsujeto2 = 0, contsujeto3 = 0;
        int contotro1 = 0, contotro2 = 0, contotro3 = 0;
        float porc1 = 0, porc2 = 0, porc3 = 0, porc4 = 0, porc5 = 0, porc6 = 0;
        int[] vectoriautenti;
        // double[] corre1;
        //double[] corre2;
        //double[] corre3;
        // double[] corre4;

        for (int i = 0; i < nombres.length; i++) {
            Procesador proceso2 = new Procesador(nombres, tablas);
            String[] aux = {nombres[i]};
            proceso2.procesarVariasPersonas(aux, "x");
            vectoriautenti = new int[proceso2.getZciclosListSI().size()];
            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
            //  corre1 = new double[proceso2.getZciclosListSI().size()];
            //  corre2 = new double[proceso2.getZciclosListSI().size()];
            //  corre3 = new double[proceso2.getZciclosListSI().size()];
            // corre4 = new double[proceso2.getZciclosListSI().size()];
            for (int j = 0; j < vectoriautenti.length; j++) {
                vectoriautenti[j] = verificarPersona(proceso2.getZciclosListSI().get(j), proceso2.getMciclosListSI().get(j));
                //  System.out.println(vectoriautenti[j]);
                conttotal += 1;
                //  Correlacion cor = new Correlacion();
                //  corre1[j] = cor.CoPearson(mespectro, sujetomaux);
                //  corre2[j] = cor.CoPearson(zespectro, sujetozaux);
                // corre3[j] = cor.CoSpearman(mespectro, sujetomaux);
                // corre4[j] = cor.CoSpearman(zespectro, sujetozaux);

                if (Leer.ComprobarNombre(persona, nombres[i])) {
                    contmismosujeto += 1;
                    if (vectoriautenti[j] == 1) {
                        contsujeto1 += 1;
                    } else if (vectoriautenti[j] == 2) {
                        contsujeto2 += 1;
                    } else if (vectoriautenti[j] == 3) {
                        contsujeto3 += 1;
                    }
                } else {

                    if (vectoriautenti[j] == 1) {
                        contotro1 += 1;
                    } else if (vectoriautenti[j] == 2) {
                        contotro2 += 1;
                    } else if (vectoriautenti[j] == 3) {
                        contotro3 += 1;
                    }
                }
            }
            /*   XYSeries series2 = new XYSeries("x");
            XYSeriesCollection dataset2 = new XYSeriesCollection();
            series2 = new XYSeries("correlación de Pearson magnitud ");
            establecerdata(series2, corre1);
            dataset2.addSeries(series2);
            series2 = new XYSeries("correlación de Pearson Z ");
            establecerdata(series2, corre2);
            dataset2.addSeries(series2);
            series2 = new XYSeries("correlación de Spearman magnitud ");
            establecerdata(series2, corre3);
            dataset2.addSeries(series2);
            series2 = new XYSeries("correlación de Spearman Z ");
            establecerdata(series2, corre4);
            dataset2.addSeries(series2);

            JFreeChart chart2 = ChartFactory.createXYLineChart(
                    "Correlaciones de la magnitud de acelearación de: " + nombres[0],
                    "Ciclos comparados",
                    "Indice de correlación",
                    dataset2,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false
            );

            XYPlot plot2 = chart2.getXYPlot();
            NumberAxis rangeAxis = (NumberAxis) plot2.getRangeAxis();
            rangeAxis.setRange(0.92, 1);
            plot2.setDomainAxis(1, rangeAxis);
            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

            renderer.setSeriesPaint(0, Color.BLUE);
            renderer.setSeriesStroke(0, new BasicStroke(1.7f));
            renderer.setSeriesShapesVisible(0, false);
            
            renderer.setSeriesPaint(1, Color.RED);
            renderer.setSeriesStroke(1, new BasicStroke(1.7f));
            renderer.setSeriesShapesVisible(1, false);

            renderer.setSeriesPaint(2, Color.YELLOW);
            renderer.setSeriesStroke(2, new BasicStroke(1.7f));
            renderer.setSeriesShapesVisible(2, false);
            
            renderer.setSeriesPaint(3, Color.GREEN);
            renderer.setSeriesStroke(3, new BasicStroke(1.7f));
            renderer.setSeriesShapesVisible(3, false);
            
            
            plot2.setRenderer(renderer);
            plot2.setBackgroundPaint(Color.white);
            plot2.setRangeGridlinesVisible(true);
            plot2.setRangeGridlinePaint(Color.BLACK);
            plot2.setDomainGridlinesVisible(true);
            plot2.setDomainGridlinePaint(Color.BLACK);
            chart2.getLegend().setFrame(BlockBorder.NONE);
            chart2.setTitle(new TextTitle("Correlaciones de la magnitud de acelearación de: " + nombres[0],
                    new Font("Serif", Font.BOLD, 18)));
            ChartPanel chartPanel = new ChartPanel(chart2);
            chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            chartPanel.setBackground(Color.white);

            JFrame frame = new JFrame("Correlaciones de la magnitud de acelearación de: " + nombres[0]);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(600, 600);
            frame.add(chartPanel);
            frame.setVisible(true);
             */
        }
        if (conttotal > 0) {

            DecimalFormat for1 = new DecimalFormat("0.00");
            if (contmismosujeto != 0) {
                porc1 = (float) (contsujeto1 + contsujeto2 + contsujeto3) * 100 / contmismosujeto;
                porc2 = (float) (contsujeto2 + contsujeto3) * 100 / contmismosujeto;
                porc3 = (float) contsujeto3 * 100 / contmismosujeto;
            }
            if ((conttotal - contmismosujeto) != 0) {
                porc4 = (float) (contotro1 + contotro2 + contotro3) * 100 / (conttotal - contmismosujeto);
                porc5 = (float) (contotro2 + contotro3) * 100 / (conttotal - contmismosujeto);
                porc6 = (float) contotro3 * 100 / (conttotal - contmismosujeto);
            }

            System.out.println("Comparaciones Totales: " + conttotal
                    + " , Mismo sujeto: " + contmismosujeto
                    + " , Con otros sujetos: " + (conttotal - contmismosujeto));
            System.out.println("Verificaciones del mismo sujeto: ");
            System.out.println("Por Correlación Temporal: " + (contsujeto3 + contsujeto1 + contsujeto2)
                    + " , Porcentaje: " + for1.format(porc1) + "%");

            System.out.println("Por Correlación Temporal y Espectral: " + (contsujeto3 + contsujeto2)
                    + " , Porcentaje: " + for1.format(porc2) + "%");

            System.out.println("Verificaciones Totales: " + (contsujeto3)
                    + " , Porcentaje: " + for1.format(porc3) + "%");
            System.out.println("Falsos positivos en las verificaciones con otros sujetos: ");
            System.out.println("Por Correlación Temporal : " + (contotro3 + contotro1 + contotro2)
                    + " , Porcentaje: " + for1.format(porc4) + "%");

            System.out.println("Por Correlación Temporal y Espectral: " + (contotro3 + contotro2)
                    + " , Porcentaje: " + for1.format(porc5) + "%");

            System.out.println("Falsos positivos totales : " + contotro3
                    + " , Porcentaje: " + for1.format(porc6) + "%");

        }

    }

    public void AutenticarTodo(String persona, String[] nombres, String[] tablas) {
        int conttotal = 0;
        int contmismosujeto = 0, contsujeto1 = 0, contsujeto2 = 0, contsujeto3 = 0;
        int contotro1 = 0, contotro2 = 0, contotro3 = 0;
        int[] vectoriautenti;
        for (int i = 0; i < nombres.length; i++) {

            Procesador proceso2 = new Procesador(nombres, tablas);
            String[] aux = {nombres[i]};
            proceso2.procesarVariasPersonas(aux, "x");
            vectoriautenti = new int[proceso2.getZciclosListSI().size()];
            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
            for (int j = 0; j < vectoriautenti.length; j++) {
                vectoriautenti[j] = verificarPersona(proceso2.getZciclosListSI().get(j), proceso2.getMciclosListSI().get(j));
                //  System.out.println(vectoriautenti[j]);
                conttotal += 1;
                if (Leer.ComprobarNombre(persona, nombres[i])) {
                    contmismosujeto += 1;
                    if (vectoriautenti[j] == 1) {
                        contsujeto1 += 1;
                    } else if (vectoriautenti[j] == 2) {
                        contsujeto2 += 1;
                    } else if (vectoriautenti[j] == 3) {
                        contsujeto3 += 1;
                    }
                } else {
                    if (vectoriautenti[j] == 1) {
                        contotro1 += 1;
                    } else if (vectoriautenti[j] == 2) {
                        contotro2 += 1;
                    } else if (vectoriautenti[j] == 3) {
                        contotro3 += 1;
                    }

                }
            }
        }
        if (conttotal > 0) {

            if (contmismosujeto != 0) {
                po1 = (float) (contsujeto1 + contsujeto2 + contsujeto3) * 100 / contmismosujeto;
                po2 = (float) (contsujeto2 + contsujeto3) * 100 / contmismosujeto;
                po3 = (float) contsujeto3 * 100 / contmismosujeto;
            }
            if ((conttotal - contmismosujeto) != 0) {
                po4 = (float) (contotro1 + contotro2 + contotro3) * 100 / (conttotal - contmismosujeto);
                po5 = (float) (contotro2 + contotro3) * 100 / (conttotal - contmismosujeto);
                po6 = (float) contotro3 * 100 / (conttotal - contmismosujeto);
            }

            c1 = contsujeto1 + contsujeto2 + contsujeto3;
            c2 = contsujeto2 + contsujeto3;
            c3 = contsujeto3;
            c4 = contotro1 + contotro2 + contotro3;
            c5 = contotro2 + contotro3;
            c6 = contotro3;
            c7 = contmismosujeto;
            c8 = conttotal - contmismosujeto;

        }

    }

    public void ConstantesPearson() {
        criterioPM = 0;
        criterioPZ = 0;
        double mm = 0;
        double dm = 0;
        double mz = 0;
        double dz = 0;

        if (mediaPM > 0.9) {
            mm = (8 * mediaPM - 7) * 0.065;
        } else {
            mm = 0.2 * 0.065;
        }

        if (desviPM < 0.1) {
            dm = (1.20 / ((desviPM * 25) + 0.3)) * desviPM;
        } else {
            dm = 0.04286;
        }

        if (mediaPZ > 0.9) {
            mz = (8 * mediaPZ - 7) * 0.075;
        } else {
            mz = 0.2 * 0.075;
        }

        if (desviPZ < 0.1) {
            dz = (1.3 / ((desviPZ * 25) + 0.3)) * desviPZ;
        } else {
            dz = 0.04393;
        }
        criterioPM = mediaPM - mm - dm;
        if (criterioPM < 0.8) {
            criterioPM = 0.8;
        }
        criterioPZ = mediaPZ - mz - dz;
        if (criterioPZ < 0.8) {
            criterioPZ = 0.8;
        }
    }

    public void ConstantesSpearman() {
        criterioSM = 0;
        criterioSZ = 0;
        double mm = 0;
        double dm = 0;
        double mz = 0;
        double dz = 0;

        if (mediaSM > 0.89) {
            mm = (7.5 * mediaSM - 6.5) * 0.07;
        } else {
            mm = 0.175 * 0.07;
        }

        if (desviSM < 0.1) {
            dm = (1.5 / ((desviSM * 30) + 0.2)) * desviSM;
        } else {
            dm = 0.046875;
        }

        if (mediaSZ > 0.9) {
            mz = (8 * mediaSZ - 7) * 0.1;
        } else {
            mz = 0.2 * 0.1;
        }

        if (desviSZ < 0.1) {
            dz = (1.6 / ((desviSZ * 30) + 0.2)) * desviSZ;
        } else {
            dz = 0.05;
        }
        criterioSM = mediaSM - mm - dm;
        criterioSZ = mediaSZ - mz - dz;

        if (criterioSM < 0.78) {
            criterioSM = 0.8;
        }
        if (criterioSZ < 0.78) {
            criterioSZ = 0.8;
        }
    }

    public void ConstantesKendall() {
        criterioKM = 0;
        criterioKZ = 0;

        double dm = 0;
        double dz = 0;

        if (desviKM < 0.02) {
            dm = 5.5;
        } else if (desviKM < 0.03) {
            dm = 5;
        } else if (desviKM < 0.04) {
            dm = 4.6;
        } else if (desviKM < 0.05) {
            dm = 4.1;
        } else if (desviKM < 0.06) {
            dm = 3.7;
        } else if (desviKM < 0.07) {
            dm = 3.2;
        } else {
            dm = 2.8;
        }

        if (desviKZ < 0.02) {
            dz = 5.6;
        } else if (desviKZ < 0.03) {
            dz = 5.3;
        } else if (desviKZ < 0.04) {
            dz = 4.8;
        } else if (desviKZ < 0.05) {
            dz = 4.3;
        } else if (desviKZ < 0.06) {
            dz = 3.8;
        } else if (desviKZ < 0.07) {
            dz = 3.3;
        } else {
            dz = 2.8;
        }

        criterioKM = mediaKM - (Math.pow(mediaKM, 4) * 0.08) - dm * desviKM;
        criterioKZ = mediaKZ - (Math.pow(mediaKZ, 4) * 0.1) - dz * desviKZ;
    }

    private void establecerdata(XYSeries data, double[] x) {
        for (int i = 0; i < x.length; i++) {
            data.add(i, x[i]);
        }

    }

    /**
     * @return the identificador
     */
    public String getIdentificador() {
        return identificador;
    }

    /**
     * @param identificador the identificador to set
     */
    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    /**
     * @return the mediaPM
     */
    public double getMediaPM() {
        return mediaPM;
    }

    /**
     * @param mediaPM the mediaPM to set
     */
    public void setMediaPM(double mediaPM) {
        this.mediaPM = mediaPM;
    }

    /**
     * @return the desviPM
     */
    public double getDesviPM() {
        return desviPM;
    }

    /**
     * @param desviPM the desviPM to set
     */
    public void setDesviPM(double desviPM) {
        this.desviPM = desviPM;
    }

    /**
     * @return the mediaSM
     */
    public double getMediaSM() {
        return mediaSM;
    }

    /**
     * @param mediaSM the mediaSM to set
     */
    public void setMediaSM(double mediaSM) {
        this.mediaSM = mediaSM;
    }

    /**
     * @return the desviSM
     */
    public double getDesviSM() {
        return desviSM;
    }

    /**
     * @param desviSM the desviSM to set
     */
    public void setDesviSM(double desviSM) {
        this.desviSM = desviSM;
    }

    /**
     * @return the mediaKM
     */
    public double getMediaKM() {
        return mediaKM;
    }

    /**
     * @param mediaKM the mediaKM to set
     */
    public void setMediaKM(double mediaKM) {
        this.mediaKM = mediaKM;
    }

    /**
     * @return the desviKM
     */
    public double getDesviKM() {
        return desviKM;
    }

    /**
     * @param desviKM the desviKM to set
     */
    public void setDesviKM(double desviKM) {
        this.desviKM = desviKM;
    }

    /**
     * @return the mediaPZ
     */
    public double getMediaPZ() {
        return mediaPZ;
    }

    /**
     * @param mediaPZ the mediaPZ to set
     */
    public void setMediaPZ(double mediaPZ) {
        this.mediaPZ = mediaPZ;
    }

    /**
     * @return the desviPZ
     */
    public double getDesviPZ() {
        return desviPZ;
    }

    /**
     * @param desviPZ the desviPZ to set
     */
    public void setDesviPZ(double desviPZ) {
        this.desviPZ = desviPZ;
    }

    /**
     * @return the mediaSZ
     */
    public double getMediaSZ() {
        return mediaSZ;
    }

    /**
     * @param mediaSZ the mediaSZ to set
     */
    public void setMediaSZ(double mediaSZ) {
        this.mediaSZ = mediaSZ;
    }

    /**
     * @return the desviSZ
     */
    public double getDesviSZ() {
        return desviSZ;
    }

    /**
     * @param desviSZ the desviSZ to set
     */
    public void setDesviSZ(double desviSZ) {
        this.desviSZ = desviSZ;
    }

    /**
     * @return the mediaKZ
     */
    public double getMediaKZ() {
        return mediaKZ;
    }

    /**
     * @param mediaKZ the mediaKZ to set
     */
    public void setMediaKZ(double mediaKZ) {
        this.mediaKZ = mediaKZ;
    }

    /**
     * @return the desviKZ
     */
    public double getDesviKZ() {
        return desviKZ;
    }

    /**
     * @param desviKZ the desviKZ to set
     */
    public void setDesviKZ(double desviKZ) {
        this.desviKZ = desviKZ;
    }

    /**
     * @return the mediaparametrosZ
     */
    public double[] getMediaparametrosZ() {
        return mediaparametrosZ;
    }

    /**
     * @param mediaparametrosZ the mediaparametrosZ to set
     */
    public void setMediaparametrosZ(double[] mediaparametrosZ) {
        this.mediaparametrosZ = mediaparametrosZ;
    }

    /**
     * @return the desviparametrosZ
     */
    public double[] getDesviparametrosZ() {
        return desviparametrosZ;
    }

    /**
     * @param desviparametrosZ the desviparametrosZ to set
     */
    public void setDesviparametrosZ(double[] desviparametrosZ) {
        this.desviparametrosZ = desviparametrosZ;
    }

    /**
     * @return the mediaparametrosM
     */
    public double[] getMediaparametrosM() {
        return mediaparametrosM;
    }

    /**
     * @param mediaparametrosM the mediaparametrosM to set
     */
    public void setMediaparametrosM(double[] mediaparametrosM) {
        this.mediaparametrosM = mediaparametrosM;
    }

    /**
     * @return the desviparametrosM
     */
    public double[] getDesviparametrosM() {
        return desviparametrosM;
    }

    /**
     * @param desviparametrosM the desviparametrosM to set
     */
    public void setDesviparametrosM(double[] desviparametrosM) {
        this.desviparametrosM = desviparametrosM;
    }

    /**
     * @return the zprom
     */
    public double[] getZprom() {
        return zprom;
    }

    /**
     * @param zprom the zprom to set
     */
    public void setZprom(double[] zprom) {
        this.zprom = zprom;
    }

    /**
     * @return the mprom
     */
    public double[] getMprom() {
        return mprom;
    }

    /**
     * @param mprom the mprom to set
     */
    public void setMprom(double[] mprom) {
        this.mprom = mprom;
    }

}
