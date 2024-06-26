/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marchemos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

/**
 *
 * @author Victor Gil
 */
public class Leer {

    public static String[] leer_lista(File archivo) throws IOException {
        Scanner entrada = new Scanner(archivo);
        int lineas = 0;
        while (entrada.hasNext()) {
            lineas++;
            entrada.nextLine();
        }

        String m_leida[] = new String[lineas];
        entrada = new Scanner(archivo);
        for (int i = 0; i < m_leida.length; i++) {
            m_leida[i] = entrada.next();
        }

        entrada.close();
        return m_leida;
    }
	
	public static ArrayList<String> fileToList(File archivo) throws IOException {
        Scanner entrada = new Scanner(archivo);
        int lineas = 0;
        while (entrada.hasNext()) {
            lineas++;
            entrada.nextLine();
        }
        
        ArrayList<String> m_leida = new ArrayList<String>();
        entrada = new Scanner(archivo);
        for (int i = 0; i < lineas; i++) {
            m_leida.add(entrada.next());
        }

        entrada.close();
        return m_leida;
    }

    public static ArrayList<String> ListaClasificador() throws IOException {
        File archivo = new File("Files/clasificador.txt");
        Scanner entrada = new Scanner(archivo);
        int lineas = 0;
        while (entrada.hasNext()) {
            lineas++;
            entrada.nextLine();
        }

        ArrayList<String> leido = new ArrayList<>();
        entrada = new Scanner(archivo);
        for (int i = 0; i < lineas; i++) {
            leido.add(entrada.next());
        }

        entrada.close();
        return leido;
    }

    public static ArrayList<double[]> extraerMatrizImpostores() {
        File archivo = new File("Files/Matriz Impostores.txt");
        Scanner entrada;
        try {
            entrada = new Scanner(archivo);
            int lineas = 0;
            while (entrada.hasNext()) {
                lineas++;
                entrada.nextLine();
            }
    
            ArrayList<double[]> matriz = new ArrayList<>();
        entrada = new Scanner(archivo).useLocale(Locale.US);

        for (int i = 0; i < lineas / 9; i++) {
            matriz.add(new double[9]);
            for (int j = 0; j < 9; j++) {
                    matriz.get(i)[j] = entrada.nextDouble();
                    
            }
        }
        entrada.close();
        return matriz;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }

    public static boolean ComprobarNombre(String nombre1, String nombre2) {
        boolean com = true;
        char[] nom1 = nombre1.toCharArray();
        char[] nom2 = nombre2.toCharArray();
        if (nom2.length >= nom1.length - 2) {
            for (int i = 0; i < nom1.length - 12; i++) {
                if (nom1[i] != nom2[i]) {
                    com = false;
                }
            }
        } else {
            com = false;
        }
        return com;
    }

}
