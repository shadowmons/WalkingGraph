/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marchemos;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author DEHernandez
 */
public class VentanaInicio extends javax.swing.JPanel {

    private knn knnClasificador;
    private JButton btn_graficar;
    private JButton btn_autenticar;
    private JButton btn_autenticarT;
    private JButton btn_param;
    private JButton btn_reiniciar_clasificador;
    private JButton btn_knn_todos;
    private JButton btn_auth;
    private JList lista_personas;
    private JTextArea lista_parametros;
    JRadioButton tbtn_tablas;
    JRadioButton rbtn_tablas;
    JRadioButton rbtn_tabla;
    private String[] nombres;
    private String[] tablas;
    private ArrayList<String> pruebaKnn;
    private ArrayList<String> dataTest;
    private JCheckBox checkBox[];
    JScrollPane scroll2;
    String personaAutentica = "";
    ArrayList<double[]> iZ;
    ArrayList<double[]> aZ;

    public VentanaInicio(String[] nombres, String[] tablas, ArrayList<String> pruebaKnn, ArrayList<String> dataTest) {
        setLayout(new BorderLayout());
        this.nombres = new String[nombres.length];
        this.tablas = new String[tablas.length];
        System.arraycopy(nombres, 0, this.nombres, 0, nombres.length);
        System.arraycopy(tablas, 0, this.tablas, 0, tablas.length);

        this.pruebaKnn = new ArrayList<String>();
        this.dataTest = new ArrayList<String>();
        for (int i = 0; i < pruebaKnn.size(); i++) {
            this.pruebaKnn.add(pruebaKnn.get(i));
        }
        for (int i = 0; i < dataTest.size(); i++) {
            this.dataTest.add(dataTest.get(i));
        }

        JPanel auxiliar = new JPanel(new BorderLayout());
        JPanel panel_center = new JPanel(new GridLayout(1, 2));

        rbtn_tablas = new JRadioButton("Todas las tablas", false);
        rbtn_tabla = new JRadioButton("Por cada tabla", true);
        tbtn_tablas = new JRadioButton("Por varias personas", false);
        JPanel panel_rbtn = new JPanel();
        panel_rbtn.add(rbtn_tabla);
        panel_rbtn.add(rbtn_tablas);
        panel_rbtn.add(tbtn_tablas);

        btn_graficar = new JButton("Graficar");
        btn_autenticar = new JButton("Autenticar estadística");
        btn_autenticarT = new JButton("Autenticar estadística total");
        btn_param = new JButton("Parámetros");
        btn_auth = new JButton("Generar clasificador");
        btn_knn_todos = new JButton("Autenticar todos por K-NN");
        btn_reiniciar_clasificador = new JButton("Reiniciar clasificador");
        //Oyentes
        btn_graficar.addActionListener(new OyenteBoton());
        btn_param.addActionListener(new OyenteBoton());
        btn_autenticar.addActionListener(new OyenteBoton2());
        btn_autenticarT.addActionListener(new OyenteBoton2());
        btn_auth.addActionListener(new OyenteBoton());
        tbtn_tablas.addActionListener(new OyenteRBTN());
        rbtn_tablas.addActionListener(new OyenteRBTN());
        rbtn_tabla.addActionListener(new OyenteRBTN());
        btn_knn_todos.addActionListener(new OyenteBoton2());

        JScrollPane scroll = new JScrollPane();
        lista_personas = new JList(nombres);
        lista_personas.setLayoutOrientation(JList.VERTICAL);
        lista_personas.setSize(120, 500);
        lista_personas.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        scroll.setViewportView(lista_personas);
        panel_center.add(scroll);
        String[] graficas = {"Patrón de caminar", "Ciclos","Ciclos sin Interpolación" , "Correlación",
            "Espectro de frecuencias Z promedio", "Espectro de frecuencias magnitud promedio", "Espectrograma de señales Z", "Espectrograma de magnitud de aceleración"};

        JPanel auxiliar2 = new JPanel(new GridLayout(graficas.length, 1));
        auxiliar2.setBorder(BorderFactory.createTitledBorder("Graficar:"));
        checkBox = new JCheckBox[graficas.length];
        for (int i = 0; i < graficas.length; i++) {
            checkBox[i] = new JCheckBox(graficas[i]);
            if (i == 0) {
                checkBox[i].setSelected(true);
            }
            auxiliar2.add(checkBox[i]);
        }
        checkBox[6].setEnabled(false);
        checkBox[3].setEnabled(false);
        checkBox[7].setEnabled(false);
        

        auxiliar.add(auxiliar2, BorderLayout.NORTH);
        auxiliar.add(panel_rbtn, BorderLayout.CENTER);
        JPanel auxiliar3 = new JPanel(new GridLayout(0, 2));
        auxiliar3.add(btn_graficar);
        auxiliar3.add(btn_autenticar);
        auxiliar3.add(btn_autenticarT);
        auxiliar3.add(btn_param);
        auxiliar3.add(btn_auth);
        auxiliar3.add(btn_reiniciar_clasificador);
        auxiliar3.add(btn_knn_todos);
        auxiliar.add(auxiliar3, BorderLayout.SOUTH);
        panel_center.add(auxiliar);

        add(panel_center, BorderLayout.CENTER);

    }

    public class OyenteBoton implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            try {
                String persona = (lista_personas.getSelectedValue().toString() != null) ? lista_personas.getSelectedValue().toString() : "";
                Procesador proceso = new Procesador(persona, tablas);
                if (e.getSource() == btn_graficar) {
                    //Por tabla
                    if (rbtn_tabla.isSelected()) {
                        for (JCheckBox check : checkBox) {
                            if (check.isSelected()) {
                                proceso.procesarPorTabla(check.getText());
                            }
                        }

                    } else if (rbtn_tablas.isSelected()) { //Todas las tablas

                        for (JCheckBox check : checkBox) {
                            if (check.isSelected()) {
                                String[] nom = {persona};
                                proceso.procesarVariasPersonas(nom, check.getText());
                            }

                        }
                    } else if (tbtn_tablas.isSelected()) { //Todas las tablas
                        Object[] lista = lista_personas.getSelectedValues();
                        String[] listanombres = new String[lista.length];
                        for (int i = 0; i < lista.length; i++) {
                            listanombres[i] = (lista[i].toString() != null) ? lista[i].toString() : "";
                            System.out.println(listanombres[i]);
                        }
                        for (JCheckBox check : checkBox) {
                            if (check.isSelected()) {
                                proceso.procesarVariasPersonas(listanombres, check.getText());
                            }

                        }

                    }
                } else if (e.getSource() == btn_param) {
                    String[] nom = {persona};
                    proceso.procesarVariasPersonas(nom, "x");
                    proceso.ObtenerParametros();
                } else if (e.getSource() == btn_auth) {
                    if (btn_auth.getText().equals("Generar clasificador")) {
                        String[] aux = {persona};
                        personaAutentica = persona;
                        proceso.procesarVariasPersonas(aux, "x");
                        iZ = Leer.extraerMatrizImpostores();
                        System.out.println(iZ.size());
                        // iZ = proceso.ConstruirModelo(Leer.ListaClasificador());
                        aZ = proceso.getMatrizZ2();
                        btn_auth.setText("Autenticar K-NN");
                        JOptionPane.showMessageDialog(null, "¡Listo para autenticar!");
                        btn_reiniciar_clasificador.addActionListener(new OyenteBoton());
                    } else {
                        int contTrueMismaPersona = 0;
                        int contFalseMismaPersona = 0;
                        int contTrueOtraPersona = 0;
                        int contFalseOtraPersona = 0;
                        int contTruePorPersona = 0;
                        int contFalsePorPersona = 0;
                        for (int j = 0; j < nombres.length; j++) {
                            int contTrue = 0;
                            int contFalse = 0;
                            String[] aux = {nombres[j]};
                            proceso.procesarVariasPersonas(aux, "x");
                            ArrayList<double[]> matrizZActual = proceso.getMatrizZ2();
                            for (int i = 0; i < matrizZActual.size(); i++) {
                                ArrayList<double[]> aux2 = new ArrayList<>();
                                aux2.add(matrizZActual.get(i));
                                knnClasificador = new knn(9, aZ, iZ);
                                Boolean resp = knnClasificador.Classificador(aux2);
                                if (resp) {
                                    contTrue++;

                                } else {
                                    contFalse++;

                                }
                            }

                            if (contTrue > contFalse) {
                                contTruePorPersona++;
                            } else {
                                contFalsePorPersona++;
                            }

                            if ((contTrue > contFalse) && (Leer.ComprobarNombre(personaAutentica, nombres[j]))) {
                                contTrueMismaPersona++;
                            } else if ((contFalse > contTrue) && (Leer.ComprobarNombre(personaAutentica, nombres[j]))) {
                                contFalseMismaPersona++;
                            } else if ((contTrue > contFalse)
                                    && (!Leer.ComprobarNombre(personaAutentica, nombres[j]))) {
                                contTrueOtraPersona++;
                            } else if ((contFalse > contTrue)
                                    && (!Leer.ComprobarNombre(personaAutentica, nombres[j]))) {
                                contFalseOtraPersona++;
                            }
                        }
                        System.out.println("------------------------------------------");
                        System.out.println("Falsos positivos: " + contTrueOtraPersona);
                        System.out.println("Falsos negativos: " + contFalseMismaPersona);
                        System.out.println("Verdadero positivo: " + contTrueMismaPersona);
                        System.out.println("Verdadero negativo: " + contFalseOtraPersona);

                    }

                } else if (e.getSource() == btn_reiniciar_clasificador) {
                    btn_auth.setText("Generar clasificador");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println(ex.getMessage());
                JOptionPane.showMessageDialog(null, "Debe seleccionar una persona");
            }
        }

    }

    public class OyenteBoton2 implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            try {
                if (e.getSource() == btn_autenticar) {
                    Object[] lista = lista_personas.getSelectedValues();
                    String[] listanombres = new String[lista.length];
                    for (int i = 0; i < lista.length; i++) {
                        listanombres[i] = (lista[i].toString() != null) ? lista[i].toString() : "";
                        System.out.println(listanombres[i]);
                    }
                    Procesador proceso = new Procesador(nombres, tablas);
                    proceso.procesarVariasPersonas(listanombres, "x");

                    System.out.println("Autenticacion de " + listanombres[0]);

                    Autenticacion auten = new Autenticacion(listanombres[0],
                            proceso.getZprom(), proceso.getMprom(), proceso.getPearsonM(),
                            proceso.getSpearmanM(), proceso.getKendallM(), proceso.getPearsonZ(),
                            proceso.getSpearmanZ(), proceso.getKendallZ(), proceso.getMatrizM(), proceso.getMatrizZ(),
                            proceso.getZfft(), proceso.getMfft());
                    auten.AutenticarLista(listanombres[0], nombres, tablas);
                } else if (e.getSource() == btn_knn_todos) {
                    DescriptiveStatistics stats = new DescriptiveStatistics();
                    DescriptiveStatistics stats2 = new DescriptiveStatistics();
                    double[] auxv = new double[100];
                    double[] auxv2 = new double[100];
                    double acumuladorFalsoPositivo = 0;
                    double acumuladorFalsoNegativo = 0;
                    double acumuladorVerdaderoPositivo = 0;
                    double acumuladorVerdaderoNegativo = 0;
                    double acumuladorContTotal = 0;
                    int cont = 0;
                    for (int i = 0; i < pruebaKnn.size(); i++) {
                        int contCiclosTrueMismaPersona = 0;
                        int contCiclosFalseMismaPersona = 0;
                        int contCiclosMismaPersona = 0;
                        int contCiclosTrueOtraPersona = 0;
                        int contCiclosFalseOtraPersona = 0;
                        int contCiclosOtraPersona = 0;
                        double contTotal = 0;
                        Procesador proceso1 = new Procesador(nombres, tablas);
                        String[] aux = {pruebaKnn.get(i)};
                        proceso1.procesarVariasPersonas(aux, "x");
                        aZ = proceso1.getMatrizZ2();
                        iZ = Leer.extraerMatrizImpostores();
                        dataTest.remove(pruebaKnn.get(i));
                        for (int j = 0; j < dataTest.size(); j++) {
                            String[] aux2 = {dataTest.get(j)};
                            Procesador proceso2 = new Procesador(nombres, tablas);
                            proceso2.procesarVariasPersonas(aux2, "x");
                            ArrayList<double[]> matrizZActual = proceso2.getMatrizZ2();
                            int contCiclosPersona = 0;
                            for (int k = 0; k < matrizZActual.size(); k++) {
                                contCiclosPersona++;
                                ArrayList<double[]> aux3 = new ArrayList<>();
                                aux3.add(matrizZActual.get(k));
                                knnClasificador = new knn(7, aZ, iZ);
                                Boolean resp = knnClasificador.Classificador(aux3);
                                if (Leer.ComprobarNombre(aux2[0], aux[0])) {

                                    contTotal++;
                                    if (resp) {
                                        contCiclosTrueMismaPersona++;
                                        contCiclosMismaPersona++;
                                    } else {
                                        contCiclosFalseMismaPersona++;
                                        contCiclosMismaPersona++;
                                    }
                                } else {
                                    if (resp) {
                                        contCiclosTrueOtraPersona++;
                                        contCiclosOtraPersona++;
                                    } else {
                                        contCiclosFalseOtraPersona++;
                                        contCiclosOtraPersona++;
                                    }
                                }

                            }
                        }
                        double porcentajeVerdaderoPositivo = 0;
                        double porcentajeFalsoNegativo = 0;
                        if (contCiclosMismaPersona != 0) {
                            porcentajeVerdaderoPositivo = ((double) contCiclosTrueMismaPersona
                                    / contCiclosMismaPersona) * 100;
                           
                            porcentajeFalsoNegativo = ((double) contCiclosFalseMismaPersona
                                    / contCiclosMismaPersona) * 100;
                        }
                        double porcentajeFalsoPositivos = ((double) contCiclosTrueOtraPersona
                                / contCiclosOtraPersona) * 100;
                        double porcentajeVerdaderoNegativo = ((double) contCiclosFalseOtraPersona
                                / contCiclosOtraPersona) * 100;
                        System.out.println("------------------------------------------------------------------");
                        System.out.println("Porcentaje de verdaderos positivos para " + aux[0] + ": "
                                + porcentajeVerdaderoPositivo);
                        System.out.println(
                                "Porcentaje de falsos negativos para " + aux[0] + ": " + porcentajeFalsoNegativo);
                        System.out.println(
                                "Porcentaje de falsos positivos para " + aux[0] + ": " + porcentajeFalsoPositivos);
                        System.out.println("Porcentaje de verdaderos negativos para " + aux[0] + ": "
                                + porcentajeVerdaderoNegativo);
                        System.out.println("-----------------------------------------------------------------");

                        acumuladorFalsoNegativo += porcentajeFalsoNegativo;
                        acumuladorVerdaderoPositivo += porcentajeVerdaderoPositivo;
                        acumuladorFalsoPositivo += porcentajeFalsoPositivos;
                        acumuladorVerdaderoNegativo += porcentajeVerdaderoNegativo;

                        if (porcentajeVerdaderoPositivo > 0.1) {
                            auxv[cont] = porcentajeVerdaderoPositivo;
                            stats.addValue(porcentajeVerdaderoPositivo);
                            cont += 1;
                        }
                        auxv2[i] = porcentajeFalsoPositivos;
                        stats2.addValue(porcentajeFalsoPositivos);
                        dataTest.add(pruebaKnn.get(i));
                    }
                    double[] histo1 = new double[cont];
                    double[] histo2 = new double[pruebaKnn.size()];
                    for (int i = 0; i < histo1.length; i++) {
                        histo1[i] = auxv[i];
                        System.out.println(auxv[i]);
                    }
                    for (int i = 0; i < histo2.length; i++) {
                        histo2[i] = auxv2[i];
                    }
                    GraficadorPrueba X = new GraficadorPrueba();
                    X.Su2(histo1, 7, "Porcentaje promedio de validaciones correctas", 0);
                    X.Su2(histo2, 9, "Porcentaje promedio de Falsos Positivos", 1);
DecimalFormat df = new DecimalFormat("0.00");
                    double desviacionVerdaderoPositivo = Math.sqrt(stats.getVariance());
                    double desviacionFalsoPositivo = Math.sqrt(stats2.getVariance());
                    System.out.println("Comparaciones entre bases de un mismo sujeto: " + cont);
                    System.out.println("Bases totales comparadas: " + pruebaKnn.size());
                    System.out.println(
                            "Porcentaje promedio de falsos positivos: " + df.format(acumuladorFalsoPositivo / pruebaKnn.size())+"%");
                    System.out.println("Desviación de falsos positivos: "
                            + df.format(desviacionFalsoPositivo));
                    System.out.println(
                            "Porcentaje promedio de falsos negativos: " + df.format(acumuladorFalsoNegativo / cont)+"%");
                    System.out.println("Porcentaje promedio de verdaderos positivos: "
                            + df.format(acumuladorVerdaderoPositivo / cont)+"%");
                    System.out.println("Desviación de verdaderos positivos: "
                            + df.format(desviacionVerdaderoPositivo)
                    );
                    System.out.println("Porcentaje promedio de verdaderos negativos: "
                            + df.format(acumuladorVerdaderoNegativo / pruebaKnn.size())+"%"); 
                } else {
                    int c1 = 0, c2 = 0, c3 = 0, c4 = 0, c5 = 0, c6 = 0, c7 = 0, c8 = 0;
                    float por1 = 0, por2 = 0, por3 = 0, por4 = 0, por5 = 0, por6 = 0;
                    int c = 0;
                    int ca2 = 0;
                    double[] auxv = new double[100];
                    double[] auxv2 = new double[100];
                    int contaux = 0, contaux2 = 0;
                    boolean nombre = false;

                    for (int i = 0; i < nombres.length; i++) {
                        c += 1;
                        ca2 += 1;
                        Procesador proceso = new Procesador(nombres, tablas);

                        while (nombre == false) {
                            nombre = true;
                            if (i + contaux + 1 == nombres.length) {
                                contaux2 += 1;
                            } else {
                                contaux += 1;
                                if (Leer.ComprobarNombre(nombres[i], nombres[i + contaux])) {
                                    nombre = false;
                                }
                            }

                        }

                        String[] aux = new String[contaux + contaux2];
                        for (int j = 0; j < aux.length; j++) {
                            aux[j] = nombres[i + j];

                        }
                        nombre = false;
                        i = i + contaux + contaux2 - 1;
                        ca2 = ca2 + contaux + contaux2 - 1;
                        contaux = 0;
                        contaux2 = 0;

                        proceso.procesarVariasPersonas(aux, "x");
                        Autenticacion auten = new Autenticacion(nombres[i],
                                proceso.getZprom(), proceso.getMprom(), proceso.getPearsonM(),
                                proceso.getSpearmanM(), proceso.getKendallM(), proceso.getPearsonZ(),
                                proceso.getSpearmanZ(), proceso.getKendallZ(), proceso.getMatrizM(), proceso.getMatrizZ(),
                                proceso.getZfft(), proceso.getMfft());
                        auten.AutenticarTodo(nombres[i], nombres, tablas);
                        por1 = por1 + auten.po1;
                        por2 = por2 + auten.po2;
                        por3 = por3 + auten.po3;
                        por4 = por4 + auten.po4;
                        por5 = por5 + auten.po5;
                        por6 = por6 + auten.po6;
                        c1 = c1 + auten.c1;
                        c2 = c2 + auten.c2;
                        c3 = c3 + auten.c3;
                        c4 = c4 + auten.c4;
                        c5 = c5 + auten.c5;
                        c6 = c6 + auten.c6;
                        c7 = c7 + auten.c7;
                        c8 = c8 + auten.c8;
                        auxv[c - 1] = auten.po3;
                        auxv2[c - 1] = auten.po6;

                    }
                    double s1 = 0, m1 = 0, s2 = 0, m2 = 0;

                    DescriptiveStatistics stats = new DescriptiveStatistics();
                    DescriptiveStatistics stats2 = new DescriptiveStatistics();

                    double[] histo1 = new double[c];
                    double[] histo2 = new double[c];
                    for (int i = 0; i < histo1.length; i++) {

                        histo1[i] = auxv[i];
                        histo2[i] = auxv2[i];
                        stats.addValue(histo1[i]);
                        stats2.addValue(histo2[i]);
                        System.out.println(histo1[i]);
                    }
                    s1 = Math.sqrt(stats.getVariance());
                    m1 = stats.getMean();
                    s2 = Math.sqrt(stats2.getVariance());
                    m2 = stats2.getMean();

                    GraficadorPrueba X = new GraficadorPrueba();
                    X.Su2(histo1, 8, "Porcentaje promedio de validaciones correctas", 0);
                    X.Su2(histo2, 8, "Porcentaje promedio de Falsos Positivos", 1);

                    por1 = por1 / c;
                    por2 = por2 / c;
                    por3 = por3 / c;
                    por4 = por4 / c;
                    por5 = por5 / c;
                    por6 = por6 / c;
                    DecimalFormat df = new DecimalFormat("0.00");
                    System.out.println("Número total de comparaciones de un mismo sujeto: " + c7);
                    System.out.println("Pasaron la primera fase: " + c1 + "  , segunda: " + c2 + "  , tercera: " + c3);
                    System.out.println("Número total de comparaciones sujeto impostor: " + c8);
                    System.out.println("Pasaron la primera fase: " + c4 + "  , segunda: " + c5 + "  , tercera: " + c6);
                    System.out.println("Sujetos de prueba: " + c + ", Bases totales comparadas: " + ca2);
                    System.out.println("Desviación de las verificaciones: " + df.format(s1) + " Media: " + df.format(m1));
                    System.out.println("Desviación de los falsos positivos: " + df.format(s2) + " Media: " + df.format(m2));
                    System.out.println("Porcentaje promedio de verificaciones por Correlación Temporal: " + df.format(por1) + "%");
                    System.out.println("Porcentaje promedio de Verificaciones por Correlación Temporal y Espectral: " + df.format(por2) + "%");
                    System.out.println("Porcentaje promedio de Verificaciones Totales: " + df.format(por3) + "%");
                    System.out.println("Porcentaje promedio de Falsos positivos por Correlación Temporal : " + df.format(por4) + "%");
                    System.out.println("Porcentaje promedio de Falsos positivos por Correlación Temporal y Espectral: " + df.format(por5) + "%");
                    System.out.println("Porcentaje promedio de Falsos positivos totales : " + df.format(por6) + "%");

                }
            } catch (NullPointerException ex) {
                System.out.println(ex.getMessage());
                JOptionPane.showMessageDialog(null, "Debe seleccionar una persona");
            }

        }

    }

    public class OyenteRBTN implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getSource() == rbtn_tablas) {
                rbtn_tablas.setSelected(true);
                rbtn_tabla.setSelected(false);
                tbtn_tablas.setSelected(false);
                checkBox[0].setEnabled(false);
                checkBox[6].setEnabled(true);
                checkBox[7].setEnabled(true);                
                checkBox[3].setEnabled(true);
                checkBox[2].setEnabled(true);
            } else if (e.getSource() == rbtn_tabla) {
                rbtn_tabla.setSelected(true);
                rbtn_tablas.setSelected(false);
                tbtn_tablas.setSelected(false);
                checkBox[6].setEnabled(false);
                checkBox[7].setEnabled(false);
                checkBox[3].setEnabled(false);
                checkBox[0].setEnabled(true);
                checkBox[2].setEnabled(true);

            } else if (e.getSource() == tbtn_tablas) {
                tbtn_tablas.setSelected(true);
                rbtn_tabla.setSelected(false);
                rbtn_tablas.setSelected(false);
                checkBox[6].setEnabled(false);
                checkBox[7].setEnabled(false);
                checkBox[2].setEnabled(false);
                checkBox[0].setEnabled(false);
                checkBox[3].setEnabled(true);
            }

        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
