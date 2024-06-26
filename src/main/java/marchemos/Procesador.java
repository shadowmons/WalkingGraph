/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marchemos;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.IOException;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.stat.StatUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
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
import org.math.plot.Plot2DPanel;

/**
 *
 * @author Marinany Guzman
 */
public class Procesador {

    //Informacion de base de datos
    private String nombres[];
    private String tablas[];
    private String nombre;
    SQLiteConnection conexion;

    //Valores x,y,z de la base de datos (todos los ciclos)
    private ArrayList<Double> xCompleta, yCompleta, zCompleta, xy, m, t;
    //Vector para almacenar ciclos de una tabla para graficar
    private double[] xyciclos, mciclos, zciclos;
    private double[] mciclosSI, zciclosSI;
    //Vector promedio de los ciclos
    private double[] zprom, xyprom, mprom;
    //Coleccion de ciclos de todas las tablas
    private ArrayList<double[]> mciclosList;
    private ArrayList<double[]> xyciclosList;
    private ArrayList<double[]> zciclosList;
    //Coleccion de ciclos de todas las tablas sin interpolacion
    private ArrayList<double[]> mciclosListSI;
    private ArrayList<double[]> zciclosListSI;
    //Coleccion de todas las tablas
    private ArrayList<ArrayList<Double>> xytotal, mtotal, ztotal;
    //Coleccion de todas las tablas Sin Interpolacion
    private ArrayList<ArrayList<Double>> xytotalSI, mtotalSI, ztotalSI;
    private ArrayList<Integer> muestraslongitud;
    //Vectores de correlación
    private double[] pearsonM;
    private double[] spearmanM;
    private double[] kendallM;
    private double[] pearsonZ;
    private double[] spearmanZ;
    private double[] kendallZ;
    //Matriz de caracteristicas
    private ArrayList<double[]> matrizZ;
    private ArrayList<double[]> matrizZ2;
    private ArrayList<double[]> matrizM;
    //Vectores FFT
    private double[] zfft, mfft;
    private double[][] fftespectroM;

    public Procesador(String nombres[], String tablas[]) {
        this.nombres = new String[nombres.length];
        this.tablas = new String[tablas.length];
        System.arraycopy(nombres, 0, this.nombres, 0, nombres.length);
        System.arraycopy(tablas, 0, this.tablas, 0, tablas.length);
    }

    public Procesador(String nombre, String tablas[]) {
        this.nombre = nombre;
        this.tablas = new String[tablas.length];
        System.arraycopy(tablas, 0, this.tablas, 0, tablas.length);
    }

    public Procesador() {

    }

    public void procesarPorTabla(String tipo) throws IOException {
        conexion = new SQLiteConnection(nombre);
        conexion.connect();

        for (int v = 0; v < tablas.length; v++) {
            //Conexion base de datos
            xCompleta = new ArrayList<>();
            yCompleta = new ArrayList<>();
            zCompleta = new ArrayList<>();
            xy = new ArrayList<>();
            m = new ArrayList<>();
            t = new ArrayList<>();
            conexion.selectAll(nombre, tablas[v], xCompleta, yCompleta, zCompleta, t);

            //Obtener Plano XY Y MAGNITUD
            for (int j = 0; j < xCompleta.size(); j++) {
                xy.add(Math.sqrt(Math.pow(xCompleta.get(j), 2) + Math.pow(yCompleta.get(j), 2)));
                m.add(Math.sqrt(Math.pow(xCompleta.get(j), 2) + Math.pow(yCompleta.get(j), 2) + Math.pow(zCompleta.get(j), 2)));
            }
            //Graficar de acuerdo a la opcion requerida
            //Grafica de patrones completos
            if (tipo.equals("Patrón de caminar")) {
                XYSeries series1 = new XYSeries("X");
                XYSeries series2 = new XYSeries("Y");
                XYSeries series3 = new XYSeries("Z");
                XYSeries series4 = new XYSeries("Magnitud aceleración");
                establecerdata(series1, convertir(xCompleta));
                establecerdata(series2, convertir(yCompleta));
                establecerdata(series3, convertir(zCompleta));
                establecerdata(series4, convertir(m));
                XYSeriesCollection dataset = new XYSeriesCollection();
                dataset.addSeries(series4);
                dataset.addSeries(series3);
                dataset.addSeries(series2);
                dataset.addSeries(series1);
                JFreeChart chart = ChartFactory.createXYLineChart(
                        "Patrones en x, y, z y magnitud de la tabla " + (v + 1) + " de: " + nombre,
                        "Muestras a 50Hz",
                        "Aceleración m/s^2",
                        dataset,
                        PlotOrientation.VERTICAL,
                        true,
                        true,
                        false
                );

                XYPlot plot = chart.getXYPlot();

                XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

                renderer.setSeriesPaint(0, Color.BLUE);
                renderer.setSeriesStroke(0, new BasicStroke(1.5f));
                renderer.setSeriesShapesVisible(0, false);

                renderer.setSeriesPaint(1, Color.RED);
                renderer.setSeriesStroke(1, new BasicStroke(1.5f));
                renderer.setSeriesShapesVisible(1, false);

                renderer.setSeriesPaint(2, Color.YELLOW);
                renderer.setSeriesStroke(2, new BasicStroke(1.5f));
                renderer.setSeriesShapesVisible(2, false);

                renderer.setSeriesPaint(3, Color.GREEN);
                renderer.setSeriesStroke(3, new BasicStroke(1.5f));
                renderer.setSeriesShapesVisible(3, false);

                plot.setRenderer(renderer);
                plotear(plot);

                chart.getLegend().setFrame(BlockBorder.NONE);

                chart.setTitle(new TextTitle("Patrones en x, y, z y magnitud de la tabla " + (v + 1) + " de: " + nombre,
                        new Font("Serif", Font.BOLD, 18)));

                CrearFrame2("Patrones en x, y, z y magnitud de la tabla " + (v + 1) + " de: " + nombre, chart);
                //ChartUtils.saveChartAsPNG(new File("line_chart.png"), chart, 450, 400);
            } else if (tipo.equals("Ciclos sin Interpolación")) {
                //CALCULO DE LOS CICLOS
                int n = 64; //Numero de puntos
                Ciclosxyz ciclos = new Ciclosxyz(xy, m, zCompleta);
                ciclos.CalcCiclos();
                zciclosList = new ArrayList<double[]>();
                xyciclosList = new ArrayList<double[]>();
                mciclosList = new ArrayList<double[]>();
                XYSeries series1 = new XYSeries("x");
                XYSeriesCollection dataset1 = new XYSeriesCollection();
                XYSeries series2 = new XYSeries("y");
                XYSeriesCollection dataset2 = new XYSeriesCollection();

                for (int i = 0; i < ciclos.ciclosZ.size(); i++) {
                    xyciclos = new double[ciclos.ciclosZ.get(i).size()];
                    mciclos = new double[ciclos.ciclosZ.get(i).size()];
                    zciclos = new double[ciclos.ciclosZ.get(i).size()];
                    for (int j = 0; j < ciclos.ciclosZ.get(i).size(); j++) {
                        zciclos[j] = ciclos.ciclosZ.get(i).get(j);
                        xyciclos[j] = ciclos.ciclosX.get(i).get(j);
                        mciclos[j] = ciclos.ciclosY.get(i).get(j);
                    }
                    zciclosList.add(zciclos);
                    mciclosList.add(mciclos);
                    xyciclosList.add(xyciclos);
                    series1 = new XYSeries(i);
                    establecerdata(series1, mciclosList.get(i));
                    dataset1.addSeries(series1);
                    series2 = new XYSeries(i);
                    establecerdata(series2, zciclosList.get(i));
                    dataset2.addSeries(series2);
                }
                JFreeChart chart1 = ChartFactory.createXYLineChart(
                        "Ciclos sin interpolación de magnitud de la tabla " + (v + 1) + " de: " + nombre,
                        "Muestras a 50Hz",
                        "Aceleración m/s^2",
                        dataset1,
                        PlotOrientation.VERTICAL,
                        true,
                        true,
                        false
                );
                JFreeChart chart2 = ChartFactory.createXYLineChart(
                        "Ciclos sin interpolación de z de la tabla " + (v + 1) + " de: " + nombre,
                        "Muestras a 50Hz",
                        "Aceleración m/s^2",
                        dataset2,
                        PlotOrientation.VERTICAL,
                        true,
                        true,
                        false
                );

                XYPlot plot1 = chart1.getXYPlot();
                XYPlot plot2 = chart2.getXYPlot();
                plotear(plot1);
                chart1.getLegend().setFrame(BlockBorder.NONE);
                chart1.setTitle(new TextTitle("Ciclos sin interpolación de magnitud de la tabla " + (v + 1) + " de: " + nombre,
                        new Font("Serif", Font.BOLD, 18)));
                CrearFrame2("Ciclos sin interpolación de magnitud de la tabla " + (v + 1) + " de: " + nombre, chart1);

                plotear(plot2);
                chart2.getLegend().setFrame(BlockBorder.NONE);
                chart2.setTitle(new TextTitle("Ciclos sin interpolación de z de la tabla " + (v + 1) + " de: " + nombre,
                        new Font("Serif", Font.BOLD, 18)));
                CrearFrame2("Ciclos sin interpolación de z de la tabla " + (v + 1) + " de: " + nombre, chart2);

            } else {
                //CALCULO DE LOS CICLOS

                Ciclosxyz ciclos = new Ciclosxyz(xy, m, zCompleta);
                ciclos.CalcCiclos();
                int n = Ciclosxyz.MediaCiclos(ciclos.ciclosZ); //Numero de puntos
                //Interpolacion
                for (int i = 0; i < ciclos.ciclosZ.size(); i++) {
                    InterpolacionCiclos interpolacionXY = new InterpolacionCiclos(ciclos.ciclosX.get(i), n);
                    interpolacionXY.NuevoCicloD(ciclos.ciclosX.get(i));
                    InterpolacionCiclos interpolacionMag = new InterpolacionCiclos(ciclos.ciclosY.get(i), n);
                    interpolacionMag.NuevoCicloD(ciclos.ciclosY.get(i));
                    InterpolacionCiclos interpolacionZ = new InterpolacionCiclos(ciclos.ciclosZ.get(i), n);
                    interpolacionZ.NuevoCicloD(ciclos.ciclosZ.get(i));
                }

                zprom = ciclos.Promediado(ciclos.ciclosZ);
                xyprom = ciclos.Promediado(ciclos.ciclosX);
                mprom = ciclos.Promediado(ciclos.ciclosY);

                zciclosList = new ArrayList<double[]>();
                xyciclosList = new ArrayList<double[]>();
                mciclosList = new ArrayList<double[]>();

                if (tipo.equals("Ciclos")) {
                    //Graficar ciclos de z, xy y aceleracion
                    XYSeries series1 = new XYSeries("patrón de ciclos de z promedio");
                    XYSeriesCollection dataset1 = new XYSeriesCollection();
                    establecerdata(series1, zprom);
                    dataset1.addSeries(series1);
                    series1 = new XYSeries("patrón de ciclos de aceleración promedio");
                    establecerdata(series1, mprom);
                    dataset1.addSeries(series1);
                    for (int i = 0; i < ciclos.ciclosZ.size(); i++) {
                        xyciclos = new double[ciclos.ciclosZ.get(i).size()];
                        mciclos = new double[ciclos.ciclosZ.get(i).size()];
                        zciclos = new double[ciclos.ciclosZ.get(i).size()];
                        for (int j = 0; j < ciclos.ciclosZ.get(i).size(); j++) {
                            zciclos[j] = ciclos.ciclosZ.get(i).get(j);
                            xyciclos[j] = ciclos.ciclosX.get(i).get(j);
                            mciclos[j] = ciclos.ciclosY.get(i).get(j);
                        }
                        zciclosList.add(zciclos);
                        mciclosList.add(mciclos);
                        // xyciclosList.add(xyciclos);

                        series1 = new XYSeries("Ciclo de z " + i);
                        establecerdata(series1, zciclosList.get(i));
                        dataset1.addSeries(series1);
                        series1 = new XYSeries("Ciclo de aceleración " + i);
                        establecerdata(series1, mciclosList.get(i));
                        dataset1.addSeries(series1);
                        // plot.addLinePlot("Ciclo de aceleración " + i, Color.GREEN, mciclosList.get(i));

                        // plot.addLinePlot("Ciclo de z " + i, Color.YELLOW, zciclosList.get(i));
                    }
                    JFreeChart chart1 = ChartFactory.createXYLineChart(
                            "ciclos sin interpolación de magnitud de la tabla " + (v + 1) + " de: " + nombre,
                            "Muestras promediadas",
                            "Aceleración m/s^2",
                            dataset1,
                            PlotOrientation.VERTICAL,
                            true,
                            true,
                            false
                    );

                    XYPlot plot = chart1.getXYPlot();
                    XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

                    renderer.setSeriesPaint(0, Color.BLUE);
                    renderer.setSeriesStroke(0, new BasicStroke(1.5f));
                    renderer.setSeriesShapesVisible(0, false);
                    renderer.setSeriesPaint(1, Color.RED);
                    renderer.setSeriesStroke(1, new BasicStroke(1.5f));
                    renderer.setSeriesShapesVisible(1, false);

                    for (int i = 2; i < dataset1.getSeriesCount(); i = i + 2) {
                        renderer.setSeriesPaint(i, Color.YELLOW);
                        renderer.setSeriesStroke(i, new BasicStroke(1.5f));
                        renderer.setSeriesShapesVisible(i, false);
                        renderer.setSeriesPaint(i + 1, Color.GREEN);
                        renderer.setSeriesStroke(i + 1, new BasicStroke(1.5f));
                        renderer.setSeriesShapesVisible(i + 1, false);

                    }

                    plot.setRenderer(renderer);
                    plotear(plot);
                    chart1.getLegend().setFrame(BlockBorder.NONE);
                    chart1.setTitle(new TextTitle("ciclos de z y magnitud de aceleración promedio de la tabla " + (v + 1) + " de: " + nombre,
                            new Font("Serif", Font.BOLD, 18)));
                    CrearFrame2("ciclos de z y magnitud de aceleración promedio de la tabla " + (v + 1) + " de: " + nombre, chart1);

                } else if (tipo.equals("Espectro de frecuencias Z promedio")) {
                    //Calculo de la FFT para los Zpromedio
                    int nPuntos = 1024; //numero de puntos de la FFT 
                    Complex zpromFrecuencia[] = new Complex[nPuntos];
                    double zpromFrecuencia_mod[] = new double[nPuntos];
                    FFT fz = new FFT(zprom, nPuntos, 1);
                    zpromFrecuencia = fz.Transformada(zprom.length);
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
                    establecerdata(series1, zpromFrecuencia_mod2, ejeX);
                    dataset1.addSeries(series1);
                    JFreeChart chart1 = ChartFactory.createXYLineChart(
                            "Espectro de Z promedio de la tabla " + (v + 1) + " de: " + nombre,
                            "Frecuencia Hz",
                            "Magnitud",
                            dataset1,
                            PlotOrientation.VERTICAL,
                            true,
                            true,
                            false
                    );

                    XYPlot plot = chart1.getXYPlot();
                    XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

                    renderer.setSeriesPaint(0, Color.BLACK);
                    renderer.setSeriesStroke(0, new BasicStroke(1.2f));
                    renderer.setSeriesShapesVisible(0, false);
                    plot.setRenderer(renderer);
                    plotear(plot);
                    chart1.getLegend().setFrame(BlockBorder.NONE);
                    chart1.setTitle(new TextTitle("Espectro de Z promedio de la tabla " + (v + 1) + " de: " + nombre,
                            new Font("Serif", Font.BOLD, 18)));
                    CrearFrame2("Espectro de Z promedio de la tabla " + (v + 1) + " de: " + nombre, chart1);

                } else if (tipo.equals("Espectro de frecuencias magnitud promedio")) {
                    //Calculo de la FFT para  magnitud promedio
                    int nPuntos = 1024; //numero de puntos de la FFT 
                    Complex mpromFrecuencia[] = new Complex[nPuntos];
                    double mpromFrecuencia_mod[] = new double[nPuntos];
                    FFT mz = new FFT(mprom, nPuntos, 1);
                    mpromFrecuencia = mz.Transformada(mprom.length);
                    for (int i = 0; i < mpromFrecuencia.length; i++) {
                        mpromFrecuencia_mod[i] = Math.sqrt(Math.pow(mpromFrecuencia[i].getReal(), 2) + Math.pow(mpromFrecuencia[i].getImaginary(), 2));
                    }
                    //Desplazamiento de la FFT
                    double mpromFrecuencia_mod2[] = new double[nPuntos];
                    mpromFrecuencia_mod2 = mz.FFTShift(mpromFrecuencia_mod);
                    //Eje normalizado
                    double ejeX[] = new double[nPuntos];
                    ejeX = mz.EjeNormalizado();
                    XYSeries series1 = new XYSeries("Espectro de magnitud promedio");
                    XYSeriesCollection dataset1 = new XYSeriesCollection();
                    establecerdata(series1, mpromFrecuencia_mod2, ejeX);
                    dataset1.addSeries(series1);
                    JFreeChart chart1 = ChartFactory.createXYLineChart(
                            "Espectro de magnitud promedio de la tabla " + (v + 1) + " de: " + nombre,
                            "Frecuencia Hz",
                            "Magnitud",
                            dataset1,
                            PlotOrientation.VERTICAL,
                            true,
                            true,
                            false
                    );

                    XYPlot plot = chart1.getXYPlot();
                    XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

                    renderer.setSeriesPaint(0, Color.BLACK);
                    renderer.setSeriesStroke(0, new BasicStroke(1.2f));
                    renderer.setSeriesShapesVisible(0, false);
                    plot.setRenderer(renderer);
                    plotear(plot);
                    chart1.getLegend().setFrame(BlockBorder.NONE);
                    chart1.setTitle(new TextTitle("Espectro de magnitud promedio de la tabla " + (v + 1) + " de: " + nombre,
                            new Font("Serif", Font.BOLD, 18)));
                    CrearFrame2("Espectro de magnitud promedio de la tabla " + (v + 1) + " de: " + nombre, chart1);

                }

            }
        }
        conexion.close();
    }

    public void procesarVariasPersonas(String[] nombres, String tipo) {

        //Vectores con las senales totales de cada tabla
        xytotal = new ArrayList<ArrayList<Double>>();
        mtotal = new ArrayList<ArrayList<Double>>();
        ztotal = new ArrayList<ArrayList<Double>>();
        xytotalSI = new ArrayList<ArrayList<Double>>();
        mtotalSI = new ArrayList<ArrayList<Double>>();
        ztotalSI = new ArrayList<ArrayList<Double>>();
        muestraslongitud = new ArrayList<Integer>();
        matrizM = new ArrayList<double[]>();
        matrizZ = new ArrayList<double[]>();
        matrizZ2 = new ArrayList<double[]>();
        Plot2DPanel plot;
        //Procesamiento por cada persona
        for (int k = 0; k < nombres.length; k++) {

            conexion = new SQLiteConnection(nombres[k]);
            conexion.connect();
            for (int v = 0; v < tablas.length; v++) {
                //Conexion base de datos
                xCompleta = new ArrayList<>();
                yCompleta = new ArrayList<>();
                zCompleta = new ArrayList<>();
                xy = new ArrayList<>();
                m = new ArrayList<>();
                t = new ArrayList<>();
                conexion.selectAll(nombres[k], tablas[v], xCompleta, yCompleta, zCompleta, t);

                //Obtener Plano XY Y MAGNITUD
                for (int j = 0; j < xCompleta.size(); j++) {
                    xy.add(Math.sqrt(Math.pow(xCompleta.get(j), 2) + Math.pow(yCompleta.get(j), 2)));
                    m.add(Math.sqrt(Math.pow(xCompleta.get(j), 2) + Math.pow(yCompleta.get(j), 2) + Math.pow(zCompleta.get(j), 2)));
                }

                //CALCULO DE LOS CICLOS
                Ciclosxyz ciclos = new Ciclosxyz(xy, m, zCompleta);
                ciclos.CalcCiclos();

                for (int i = 0; i < ciclos.ciclosZ.size(); i++) {
                    ztotalSI.add(new ArrayList<Double>(ciclos.ciclosZ.get(i)));
                    xytotalSI.add(new ArrayList<Double>(ciclos.ciclosX.get(i)));
                    mtotalSI.add(new ArrayList<Double>(ciclos.ciclosY.get(i)));
                    ztotal.add(new ArrayList<Double>(ciclos.ciclosZ.get(i)));
                    xytotal.add(new ArrayList<Double>(ciclos.ciclosX.get(i)));
                    mtotal.add(new ArrayList<Double>(ciclos.ciclosY.get(i)));
                    muestraslongitud.add(ciclos.ciclosZ.get(i).size());
                }

            }
            conexion.close();
        }

        //CALCULO DE LOS CICLOS
        int n = Ciclosxyz.MediaCiclos(ztotal); //Numero de puntos

        //Interpolacion
        for (int i = 0; i < ztotal.size(); i++) {
            InterpolacionCiclos interpolacionXY = new InterpolacionCiclos(xytotal.get(i), n);
            interpolacionXY.NuevoCicloD(xytotal.get(i));
            InterpolacionCiclos interpolacionMag = new InterpolacionCiclos(mtotal.get(i), n);
            interpolacionMag.NuevoCicloD(mtotal.get(i));
            InterpolacionCiclos interpolacionZ = new InterpolacionCiclos(ztotal.get(i), n);
            interpolacionZ.NuevoCicloD(ztotal.get(i));
        }

        //promediado 
        zprom = Ciclosxyz.Promediado(ztotal);
        xyprom = Ciclosxyz.Promediado(xytotal);
        mprom = Ciclosxyz.Promediado(mtotal);
        descartarciclos(mprom, ztotal, mtotal);
        zprom = Ciclosxyz.Promediado(ztotal);
        mprom = Ciclosxyz.Promediado(mtotal);

        //lista de ciclos
        zciclosList = new ArrayList<double[]>();
        mciclosList = new ArrayList<double[]>();
        zciclosListSI = new ArrayList<double[]>();
        mciclosListSI = new ArrayList<double[]>();
        //Correlaciones

        Correlacion correlacion = new Correlacion();
        kendallZ = new double[ztotal.size()];
        pearsonZ = new double[ztotal.size()];
        spearmanZ = new double[ztotal.size()];
        kendallM = new double[ztotal.size()];
        pearsonM = new double[ztotal.size()];
        spearmanM = new double[ztotal.size()];
        String paramtetros_txt = "";

        for (int i = 0; i < ztotal.size(); i++) {
            mciclosSI = new double[ztotalSI.get(i).size()];
            zciclosSI = new double[ztotalSI.get(i).size()];
            for (int j = 0; j < ztotalSI.get(i).size(); j++) {
                zciclosSI[j] = ztotalSI.get(i).get(j);
                mciclosSI[j] = mtotalSI.get(i).get(j);
            }
            zciclosListSI.add(zciclosSI);
            mciclosListSI.add(mciclosSI);
            mciclos = new double[ztotal.get(i).size()];
            zciclos = new double[ztotal.get(i).size()];
            for (int j = 0; j < ztotal.get(i).size(); j++) {
                zciclos[j] = ztotal.get(i).get(j);
                mciclos[j] = mtotal.get(i).get(j);
            }
            pearsonZ[i] = correlacion.CoPearson(zprom, zciclos);
            pearsonM[i] = correlacion.CoPearson(mprom, mciclos);
            spearmanZ[i] = correlacion.CoSpearman(zprom, zciclos);
            spearmanM[i] = correlacion.CoSpearman(mprom, mciclos);
            kendallZ[i] = correlacion.CoKendall(zprom, zciclos);
            kendallM[i] = correlacion.CoKendall(mprom, mciclos);
            zciclosList.add(zciclos);
            mciclosList.add(mciclos);
            matrizZ.add(ParametrosTiempo.ParametrosTZ(zciclos));
            matrizZ2.add(ParametrosTiempo.parametrosTemporalesZ(zciclos));
            matrizM.add(ParametrosTiempo.ParametrosTM(mciclos, muestraslongitud.get(i)));
        }
        //Calculo de la FFT
        int nPuntos = 1024; //numero de puntos de la FFT
        //Vectores para el calculo de la FFT 
        Complex zpromFrecuencia[] = new Complex[nPuntos];
        double zpromFrecuencia_mod[] = new double[nPuntos];
        Complex mpromFrecuencia[] = new Complex[nPuntos];
        double mpromFrecuencia_mod[] = new double[nPuntos];
        FFT fz = new FFT(zprom, nPuntos, 1);
        zpromFrecuencia = fz.Transformada(zprom.length);
        FFT fm = new FFT(mprom, nPuntos, 1);
        mpromFrecuencia = fm.Transformada(mprom.length);

        for (int i = 0; i < zpromFrecuencia.length; i++) {
            zpromFrecuencia_mod[i] = Math.sqrt(Math.pow(zpromFrecuencia[i].getReal(), 2) + Math.pow(zpromFrecuencia[i].getImaginary(), 2));
            mpromFrecuencia_mod[i] = Math.sqrt(Math.pow(mpromFrecuencia[i].getReal(), 2) + Math.pow(mpromFrecuencia[i].getImaginary(), 2));
        }
        zfft = new double[307];
        zfft = fz.FFTShift(zpromFrecuencia_mod);
        mfft = new double[307];
        mfft = fm.FFTShift(mpromFrecuencia_mod);
        //Eje normalizado
        double ejeX[] = new double[256];
        ejeX = fz.EjeNormalizado();
        //Elegir la grafica
        if (tipo.equals("Ciclos")) {
            //Graficar ciclos de z, xy y aceleracion
            XYSeries series1 = new XYSeries("z promedio");
            XYSeriesCollection dataset1 = new XYSeriesCollection();
            establecerdata(series1, zprom);
            dataset1.addSeries(series1);
            series1 = new XYSeries("magintud aceleración promedio");
            establecerdata(series1, mprom);
            dataset1.addSeries(series1);
            for (int i = 0; i < ztotal.size(); i++) {
                series1 = new XYSeries("Ciclo de z " + i);
                establecerdata(series1, zciclosList.get(i));
                dataset1.addSeries(series1);
                series1 = new XYSeries("Ciclo de aceleración " + i);
                establecerdata(series1, mciclosList.get(i));
                dataset1.addSeries(series1);
            }
            JFreeChart chart1 = ChartFactory.createXYLineChart(
                    "Patrones promedios " + "de: " + nombres[0],
                    "Muestras promediadas",
                    "Aceleración m/s^2",
                    dataset1,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false
            );

            XYPlot plot1 = chart1.getXYPlot();
            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

            renderer.setSeriesPaint(0, Color.BLUE);
            renderer.setSeriesStroke(0, new BasicStroke(1.5f));
            renderer.setSeriesShapesVisible(0, false);
            renderer.setSeriesPaint(1, Color.RED);
            renderer.setSeriesStroke(1, new BasicStroke(1.5f));
            renderer.setSeriesShapesVisible(1, false);

            for (int i = 2; i < dataset1.getSeriesCount(); i = i + 2) {
                renderer.setSeriesPaint(i, Color.YELLOW);
                renderer.setSeriesStroke(i, new BasicStroke(1.5f));
                renderer.setSeriesShapesVisible(i, false);
                renderer.setSeriesVisibleInLegend(i, Boolean.FALSE);
                renderer.setSeriesPaint(i + 1, Color.GREEN);
                renderer.setSeriesStroke(i + 1, new BasicStroke(1.5f));
                renderer.setSeriesShapesVisible(i + 1, false);
                renderer.setSeriesVisibleInLegend(i + 1, Boolean.FALSE);

            }

            plot1.setRenderer(renderer);
            plotear(plot1);
            chart1.getLegend().setFrame(BlockBorder.NONE);
            chart1.setTitle(new TextTitle("Patrones promedios " + "de: " + nombres[0],
                    new Font("Serif", Font.BOLD, 18)));
            CrearFrame2("Patrones promedios " + "de: " + nombres[0], chart1);

        } else if (tipo.equals("Ciclosg")) {

            for (int i = 0; i < 3; i++) {
                plot = new Plot2DPanel();
                zprom = Ciclosxyz.Promediado(ztotal);
                mprom = Ciclosxyz.Promediado2(ztotal, 5 * (i + 1));
                XYSeries series1 = new XYSeries("z promedio aritmetico");
                XYSeriesCollection dataset1 = new XYSeriesCollection();
                establecerdata(series1, zprom);
                dataset1.addSeries(series1);
                series1 = new XYSeries("z regresion");
                establecerdata(series1, mprom);
                dataset1.addSeries(series1);
                for (int j = 0; j < zciclosList.size(); j++) {
                    series1 = new XYSeries("Ciclo de z " + j);
                    establecerdata(series1, zciclosList.get(j));
                    dataset1.addSeries(series1);
                }
                JFreeChart chart1 = ChartFactory.createXYLineChart(
                        "Patrones promedios " + "de: " + nombres[0],
                        "Muestras promediadas",
                        "Aceleración m/s^2",
                        dataset1,
                        PlotOrientation.VERTICAL,
                        true,
                        true,
                        false
                );

                XYPlot plot1 = chart1.getXYPlot();
                XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

                renderer.setSeriesPaint(0, Color.BLUE);
                renderer.setSeriesStroke(0, new BasicStroke(1.5f));
                renderer.setSeriesShapesVisible(0, false);
                renderer.setSeriesPaint(1, Color.RED);
                renderer.setSeriesStroke(1, new BasicStroke(1.5f));
                renderer.setSeriesShapesVisible(1, false);

                for (int k = 2; k < dataset1.getSeriesCount(); k++) {
                    renderer.setSeriesPaint(k, Color.YELLOW);
                    renderer.setSeriesStroke(k, new BasicStroke(1.5f));
                    renderer.setSeriesShapesVisible(k, false);
                    renderer.setSeriesVisibleInLegend(k, Boolean.FALSE);

                }
                plot1.setRenderer(renderer);
                plotear(plot1);
                chart1.getLegend().setFrame(BlockBorder.NONE);
                chart1.setTitle(new TextTitle("Patrones promedios " + "de: " + nombres[0],
                        new Font("Serif", Font.BOLD, 18)));
                CrearFrame2("Patrones promedios " + "de: " + nombres[0], chart1);

            }

        } else if (tipo.equals("Ciclos sin Interpolación")) {

            XYSeries series1 = new XYSeries("x");
            XYSeriesCollection dataset1 = new XYSeriesCollection();
            XYSeries series2 = new XYSeries("y");
            XYSeriesCollection dataset2 = new XYSeriesCollection();
            for (int i = 0; i < zciclosListSI.size(); i++) {

                series1 = new XYSeries("Ciclos  Z" + i);
                establecerdata(series1, zciclosListSI.get(i));
                dataset1.addSeries(series1);
                series2 = new XYSeries("Ciclo magnitud" + i);
                establecerdata(series2, mciclosListSI.get(i));
                dataset2.addSeries(series2);
            }

            JFreeChart chart1 = ChartFactory.createXYLineChart(
                    "Ciclos sin interpolar Z " + "de: " + nombres[0],
                    "Muestras a 50Hz",
                    "Aceleración m/s^2",
                    dataset1,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false
            );
            JFreeChart chart2 = ChartFactory.createXYLineChart(
                    "Ciclos sin interpolar magnitud " + "de: " + nombres[0],
                    "Muestras a 50Hz",
                    "Aceleración m/s^2",
                    dataset2,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false
            );

            XYPlot plot1 = chart1.getXYPlot();
            XYPlot plot2 = chart2.getXYPlot();
            plotear(plot1);
            chart1.getLegend().setFrame(BlockBorder.NONE);
            chart1.setTitle(new TextTitle("Ciclos sin interpolar Z " + "de: " + nombres[0],
                    new Font("Serif", Font.BOLD, 18)));
            CrearFrame2("Ciclos sin interpolar Z " + "de: " + nombres[0], chart1);

            plotear(plot2);
            chart2.getLegend().setFrame(BlockBorder.NONE);
            chart2.setTitle(new TextTitle("Ciclos sin interpolar magnitud " + "de: " + nombres[0],
                    new Font("Serif", Font.BOLD, 18)));
            CrearFrame2("Ciclos sin interpolar magnitud " + "de: " + nombres[0], chart2);

        } else if (tipo.equals("Correlación")) {

            XYSeries series1 = new XYSeries("x");
            XYSeriesCollection dataset1 = new XYSeriesCollection();
            XYSeries series2 = new XYSeries("y");
            XYSeriesCollection dataset2 = new XYSeriesCollection();

            series1 = new XYSeries("correlación de Pearson");
            establecerdata(series1, pearsonZ);
            dataset1.addSeries(series1);
            series1 = new XYSeries("correlación de Spearman");
            establecerdata(series1, spearmanZ);
            dataset1.addSeries(series1);
            series1 = new XYSeries("correlación de Kendall");
            establecerdata(series1, kendallZ);
            dataset1.addSeries(series1);

            series2 = new XYSeries("correlación de Pearson");
            establecerdata(series2, pearsonM);
            dataset2.addSeries(series2);
            series2 = new XYSeries("correlación de Spearman");
            establecerdata(series2, spearmanM);
            dataset2.addSeries(series2);
            series2 = new XYSeries("correlación de Kendall");
            establecerdata(series2, kendallM);
            dataset2.addSeries(series2);
            JFreeChart chart1 = ChartFactory.createXYLineChart(
                    "Correlaciones en z de: " + nombres[0],
                    "Ciclos comparados",
                    "Indice de correlación",
                    dataset1,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false
            );
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

            XYPlot plot1 = chart1.getXYPlot();
            XYPlot plot2 = chart2.getXYPlot();
            NumberAxis rangeAxis = (NumberAxis) plot1.getRangeAxis();
            rangeAxis.setRange(0.6, 1);
            plot1.setDomainAxis(1, rangeAxis);
            NumberAxis rangeAxis2 = (NumberAxis) plot2.getRangeAxis();
            rangeAxis2.setRange(0.6, 1);
            plot2.setDomainAxis(1, rangeAxis2);

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

            plot1.setRenderer(renderer);
            plot2.setRenderer(renderer);

            plotear(plot1);
            chart1.getLegend().setFrame(BlockBorder.NONE);
            chart1.setTitle(new TextTitle("Correlaciones en z de: " + nombres[0],
                    new Font("Serif", Font.BOLD, 18)));
            CrearFrame2("Correlaciones en z de: " + nombres[0], chart1);

            plotear(plot2);
            chart2.getLegend().setFrame(BlockBorder.NONE);
            chart2.setTitle(new TextTitle("Correlaciones de la magnitud de acelearación de: " + nombres[0],
                    new Font("Serif", Font.BOLD, 18)));
            CrearFrame2("Correlaciones de la magnitud de acelearación de: " + nombres[0], chart2);

        } else if (tipo.equals("Espectro de frecuencias Z promedio")) {

            XYSeries series1 = new XYSeries("Espectro de Z promedio");
            XYSeriesCollection dataset1 = new XYSeriesCollection();
            establecerdata(series1, zfft, ejeX);
            dataset1.addSeries(series1);
            JFreeChart chart1 = ChartFactory.createXYLineChart(
                    "Espectro de frecuencias de z promedio" + "de: " + nombres[0],
                    "Frecuencia Hz",
                    "Magnitud",
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
            chart1.setTitle(new TextTitle("Espectro de frecuencias de z promedio" + "de: " + nombres[0],
                    new Font("Serif", Font.BOLD, 18)));
            CrearFrame2("Espectro de frecuencias de z promedio" + "de: " + nombres[0], chart1);
        } else if (tipo.equals("Espectro de frecuencias magnitud promedio")) {

            XYSeries series1 = new XYSeries("Espectro de la magnitud de la aceleración promedio");
            XYSeriesCollection dataset1 = new XYSeriesCollection();
            establecerdata(series1, mfft, ejeX);
            dataset1.addSeries(series1);
            JFreeChart chart1 = ChartFactory.createXYLineChart(
                    "Espectro de la magnitud de la aceleración promedio " + "de: " + nombres[0],
                    "Frecuencia Hz",
                    "Magnitud",
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
            chart1.setTitle(new TextTitle("Espectro de la magnitud de la aceleración promedio " + "de: " + nombres[0],
                    new Font("Serif", Font.BOLD, 18)));
            CrearFrame2("Espectro de la magnitud de la aceleración promedio " + "de: " + nombres[0], chart1);

        } else if (tipo.equals("Espectrograma de magnitud de aceleración")) {
            fftespectroM = new double[mciclosList.size()][nPuntos];
            for (int i = 0; i < mciclosList.size(); i++) {
                Complex mEspectrograma[] = new Complex[nPuntos];
                double mEspectrograma_mod[] = new double[nPuntos];
                FFT fes = new FFT(mciclosList.get(i), nPuntos, 1);
                mEspectrograma = fes.Transformada(mciclosList.get(i).length);

                for (int j = 0; j < mEspectrograma.length; j++) {
                    mEspectrograma_mod[j] = Math.sqrt(Math.pow(mEspectrograma[j].getReal(), 2) + Math.pow(mEspectrograma[j].getImaginary(), 2));
                }
                //Desplazamiento de la FFT
                fftespectroM[i] = fes.FFTShift(mEspectrograma_mod);

            }
            GrillaEspectro frameE = new GrillaEspectro(fftespectroM, "Espectrograma de Magnitud de aceleración de " + nombres[0]);
            frameE.setVisible(true);

        } else if (tipo.equals("Espectrograma de magnitud de aceleración segunda opcion")) {
            fftespectroM = new double[mciclosListSI.size()][nPuntos];
            for (int i = 0; i < mciclosListSI.size(); i++) {

                Complex mEspectrograma[] = new Complex[nPuntos];
                double mEspectrograma_mod[] = new double[nPuntos];
                FFT fes = new FFT(mciclosListSI.get(i), nPuntos, 1);
                mEspectrograma = fes.Transformada(mciclosListSI.get(i).length);

                for (int j = 0; j < mEspectrograma.length; j++) {
                    mEspectrograma_mod[j] = Math.sqrt(Math.pow(mEspectrograma[j].getReal(), 2) + Math.pow(mEspectrograma[j].getImaginary(), 2));
                }
                //Desplazamiento de la FFT
                fftespectroM[i] = fes.FFTShift(mEspectrograma_mod);

            }
            GrillaEspectro frameE = new GrillaEspectro(fftespectroM, "Espectrograma de Magnitud de aceleración de " + nombres[0]);
            frameE.setVisible(true);

        }else if (tipo.equals("Espectrograma de señales Z")) {
            fftespectroM = new double[zciclosList.size()][nPuntos];
            for (int i = 0; i < zciclosList.size(); i++) {
                Complex zEspectrograma[] = new Complex[nPuntos];
                double zEspectrograma_mod[] = new double[nPuntos];
                FFT fes = new FFT(zciclosList.get(i), nPuntos, 1);
                zEspectrograma = fes.Transformada(zciclosList.get(i).length);

                for (int j = 0; j < zEspectrograma.length; j++) {
                    zEspectrograma_mod[j] = Math.sqrt(Math.pow(zEspectrograma[j].getReal(), 2) + Math.pow(zEspectrograma[j].getImaginary(), 2));
                }
                //Desplazamiento de la FFT
                fftespectroM[i] = fes.FFTShift(zEspectrograma_mod);

            }
            GrillaEspectro frameE = new GrillaEspectro(fftespectroM, "Espectrograma de señales Z de " + nombres[0]);
            frameE.setVisible(true);
            }
       

    }

    public void ObtenerParametros() {

        Object[][] matriz_Obj = new Object[matrizZ.size()][10];
        for (int i = 0; i < matrizZ.size(); i++) {
            for (int j = 0; j < 10; j++) {
                matriz_Obj[i][j] = matrizZ.get(i)[j];

            }

        }
        String[] nombreCol = {"RMS", "Energia", "WL", "Desviación estandar", "Media", "Skeness", "Curtosis", "Aceleracion max", "Aceleracion min", "Pico a Pico"};
        DefaultTableModel modelo = new DefaultTableModel(matriz_Obj, nombreCol);
        JTable table = new JTable(modelo);
        JScrollPane scrollpane = new JScrollPane(table);
        JFrame frame = new JFrame("Parametros del eje Z de " + nombre);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.add(scrollpane, BorderLayout.CENTER);
        frame.setVisible(true);

        matriz_Obj = new Object[matrizM.size()][9];
        for (int i = 0; i < matrizM.size(); i++) {
            for (int j = 0; j < 9; j++) {
                matriz_Obj[i][j] = matrizM.get(i)[j];

            }

        }
        String[] nombreCol2 = {"RMS", "Energia", "WL", "Desviación estandar", "Media", "Skeness", "Curtosis", "Aceleracion max", "Longitud"};
        modelo = new DefaultTableModel(matriz_Obj, nombreCol2);
        table = new JTable(modelo);
        scrollpane = new JScrollPane(table);
        frame = new JFrame("Parametros de la mag. de la aceleración de " + nombre);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.add(scrollpane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public ArrayList<double[]> ConstruirModelo(ArrayList<String> impostores) {
        //Vectores con las senales totales de cada tabl

       ArrayList<double[]> matrizZ = new ArrayList<double[]>();
     
        //Procesamiento por cada persona
        for (int k = 0; k < impostores.size(); k++) {
            double[] zprom;
            ArrayList<ArrayList<Double>> ztotal = new ArrayList<ArrayList<Double>>();
            
            conexion = new SQLiteConnection(impostores.get(k));
            conexion.connect();
            for (int v = 0; v < tablas.length; v++) {
                //Conexion base de datos
                xCompleta = new ArrayList<>();
                yCompleta = new ArrayList<>();
                zCompleta = new ArrayList<>();
                t = new ArrayList<>();
                conexion.selectAll(impostores.get(k), tablas[v], xCompleta, yCompleta, zCompleta, t);

                //CALCULO DE LOS CICLOS
                Ciclosxyz ciclos = new Ciclosxyz(xy, m, zCompleta);
                ciclos.CalcCiclos();

                for (int i = 0; i < ciclos.ciclosZ.size(); i++) {
                    ztotal.add(new ArrayList<Double>(ciclos.ciclosZ.get(i))); 
                }

            }
            
             //CALCULO DE LOS CICLOS
        int n = Ciclosxyz.MediaCiclos(ztotal); //Numero de puntos

        //Interpolacion
        for (int i = 0; i < ztotal.size(); i++) {
            InterpolacionCiclos interpolacionZ = new InterpolacionCiclos(ztotal.get(i), n);
            interpolacionZ.NuevoCicloD(ztotal.get(i));
        }

        //promediado 
        zprom = Ciclosxyz.Promediado(ztotal);
        
       matrizZ.add(ParametrosTiempo.parametrosTemporalesZ(zprom));
            conexion.close();
        }
        for (int i = 0; i < matrizZ.size(); i++) {
            for (int j = 0; j < matrizZ.get(i).length; j++) {
                System.out.println(matrizZ.get(i)[j]);
            }
        }
         return matrizZ;
    }

    public ArrayList<double[]> ConstruirModeloFFT(ArrayList<String> impostores) {
        ArrayList<double[]> paramFFT = new ArrayList<>();
        ArrayList<double[]> paramFFT2 = new ArrayList<>();
        ArrayList<double[]> aux = new ArrayList<>();

//        impostores.remove(nombre);
        // for (int i = 0; i < 20; i++) {
        //     aux = ObtenerMatriz(impostores.get(i), 3);
        //     for (int j = 0; j < aux.size(); j++) {
        //         paramFFT.add(aux.get(j));
        //     }
        //  }
        // for (int i = 0; i < paramFFT.size(); i++) {
        //         paramFFT2.add(new double[8]);
        //    for (int j = 0; j < 8; j++) {
        //          paramFFT2.get(i)[j] = paramFFT.get(i)[j];
        //       }
        //   }
        return paramFFT2;
    }

    private void CrearFrame(String titulo, Plot2DPanel plot) {
        JFrame frame = new JFrame(titulo);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 600);
        frame.add(plot, BorderLayout.CENTER);
        frame.setVisible(true);
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

    private void plotear(XYPlot plot) {
        plot.setBackgroundPaint(Color.white);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

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

    private double[] convertir(ArrayList<Double> a) {
        double x[] = new double[a.size()];

        for (int i = 0; i < a.size(); i++) {
            x[i] = a.get(i);

        }
        return x;
    }

    public ArrayList<double[]> Normalizador(ArrayList<double[]> a) {
        double aux2 = 0d;
        ArrayList<double[]> resp = new ArrayList<>();

        for (int i = 0; i < a.size(); i++) {
            resp.add(new double[a.get(i).length]);

        }

        for (int i = 0; i < a.get(0).length; i++) {
            aux2 = Math.abs(a.get(0)[i]);
            for (int j = 1; j < a.size(); j++) {
                if (Math.abs(a.get(j)[i]) >= aux2) {
                    aux2 = Math.abs(a.get(j)[i]);
                }
            }
            System.out.println(aux2);
            for (int j = 0; j < a.size(); j++) {
                resp.get(j)[i] = a.get(j)[i] / aux2;
            }
            aux2 = 0d;
        }

        return resp;
    }

    private void descartarciclos(double[] mpro, ArrayList<ArrayList<Double>> z, ArrayList<ArrayList<Double>> m) {
        Correlacion cor = new Correlacion();
        int j = 0;
        double[] aux;
        double[] corp = new double[m.size()];
        for (int i = 0; i < m.size(); i++) {
            aux = convertir(m.get(i));
            corp[i] = cor.CoPearson(aux, mpro);
        }
        double media = StatUtils.mean(corp);
        double desvi = Math.sqrt(StatUtils.variance(corp));
        for (int i = 0; i < corp.length; i++) {
            if (corp[i] < (media - 2 * desvi)) {
                m.remove(j);
                z.remove(j);
                j = j - 1;
            }
            j = j + 1;

        }

    }

    public void Test(String personas[], knn knn, ArrayList<double[]> entrada) {

    }

    /**
     * @return the nombres
     */
    public String[] getNombres() {
        return nombres;
    }

    /**
     * @param nombres the nombres to set
     */
    public void setNombres(String[] nombres) {
        this.nombres = nombres;
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

    /**
     * @return the pearsonM
     */
    public double[] getPearsonM() {
        return pearsonM;
    }

    /**
     * @param pearsonM the pearsonM to set
     */
    public void setPearsonM(double[] pearsonM) {
        this.pearsonM = pearsonM;
    }

    /**
     * @return the spearmanM
     */
    public double[] getSpearmanM() {
        return spearmanM;
    }

    /**
     * @param spearmanM the spearmanM to set
     */
    public void setSpearmanM(double[] spearmanM) {
        this.spearmanM = spearmanM;
    }

    /**
     * @return the kendallM
     */
    public double[] getKendallM() {
        return kendallM;
    }

    /**
     * @param kendallM the kendallM to set
     */
    public void setKendallM(double[] kendallM) {
        this.kendallM = kendallM;
    }

    /**
     * @return the pearsonZ
     */
    public double[] getPearsonZ() {
        return pearsonZ;
    }

    /**
     * @param pearsonZ the pearsonZ to set
     */
    public void setPearsonZ(double[] pearsonZ) {
        this.pearsonZ = pearsonZ;
    }

    /**
     * @return the spearmanZ
     */
    public double[] getSpearmanZ() {
        return spearmanZ;
    }

    /**
     * @param spearmanZ the spearmanZ to set
     */
    public void setSpearmanZ(double[] spearmanZ) {
        this.spearmanZ = spearmanZ;
    }

    /**
     * @return the kendallZ
     */
    public double[] getKendallZ() {
        return kendallZ;
    }

    /**
     * @param kendallZ the kendallZ to set
     */
    public void setKendallZ(double[] kendallZ) {
        this.kendallZ = kendallZ;
    }

    /**
     * @return the matrizZ
     */
    public ArrayList<double[]> getMatrizZ() {
        return matrizZ;
    }
    
     public ArrayList<double[]> getMatrizZ2() {
        return matrizZ2;
    }

    /**
     * @param matrizZ the matrizZ to set
     */
    public void setMatrizZ(ArrayList<double[]> matrizZ) {
        this.matrizZ = matrizZ;
    }

    /**
     * @return the matrizM
     */
    public ArrayList<double[]> getMatrizM() {
        return matrizM;
    }

    /**
     * @param matrizM the matrizM to set
     */
    public void setMatrizM(ArrayList<double[]> matrizM) {
        this.matrizM = matrizM;
    }

    /**
     * @return the zfft
     */
    public double[] getZfft() {
        return zfft;
    }

    /**
     * @param zfft the zfft to set
     */
    public void setZfft(double[] zfft) {
        this.zfft = zfft;
    }

    /**
     * @return the mfft
     */
    public double[] getMfft() {
        return mfft;
    }

    /**
     * @param mfft the mfft to set
     */
    public void setMfft(double[] mfft) {
        this.mfft = mfft;
    }

    /**
     * @return the mciclosListSI
     */
    public ArrayList<double[]> getMciclosListSI() {
        return mciclosListSI;
    }

    /**
     * @param mciclosListSI the mciclosListSI to set
     */
    public void setMciclosListSI(ArrayList<double[]> mciclosListSI) {
        this.mciclosListSI = mciclosListSI;
    }

    /**
     * @return the zciclosListSI
     */
    public ArrayList<double[]> getZciclosListSI() {
        return zciclosListSI;
    }

    /**
     * @param zciclosListSI the zciclosListSI to set
     */
    public void setZciclosListSI(ArrayList<double[]> zciclosListSI) {
        this.zciclosListSI = zciclosListSI;
    }

}
