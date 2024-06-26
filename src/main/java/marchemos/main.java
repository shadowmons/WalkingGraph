/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marchemos;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Marinany Guzman
 */
public class main {

    public static void main(String[] args) throws IOException{
          // GraficadorPrueba X = new GraficadorPrueba();
       //   X.Su3();
        File bd = new File("Files/basedatos.txt");
        File proc = new File("Files/proc.txt");
        File fileKnn = new File("Files/pruebaknn.txt");
        File filedataTest = new File("Files/datatest.txt");
     //   File filedataTest = new File("Files/basedatos.txt");

        String[] tablas = null;
        String[] bases = null;
        ArrayList<String> pruebaKnn = new ArrayList<String>();
        ArrayList<String> dataTest = new ArrayList<String>();

        try {
        	bases = Leer.leer_lista(bd);
            tablas = Leer.leer_lista(proc);
            pruebaKnn = Leer.fileToList(fileKnn);
            dataTest = Leer.fileToList(filedataTest);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        Frame frame = new Frame(new VentanaInicio(bases, tablas, pruebaKnn, dataTest));
        
        

    }

}