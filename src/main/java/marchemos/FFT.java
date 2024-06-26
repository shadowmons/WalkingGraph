/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marchemos;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

/**
 *
 * @author DEHernandez
 */
public class FFT {

    private double xs[];
    private TransformType t;
    int n;
    private int tamaño;

    public FFT(double[] xs, int n, int i) {
        this.xs = xs;
        this.n = n;
        if (i == 1) {
            t = TransformType.FORWARD;
        } else {
            t = TransformType.INVERSE;
        }
    }

    public Complex[] Transformada(int length) {
        tamaño = length;
        Complex zpromComplex[] = new Complex[n];
        for (int i = 0; i < length; i++) {
                zpromComplex[i] = new Complex(xs[i]);
            }
            //zero padding
            for (int i = length; i < n; i++) {
                 zpromComplex[i] = new Complex(0); 
            }

        return new FastFourierTransformer(DftNormalization.STANDARD).transform(zpromComplex, t);
    }
    
    public double[] FFTShift(double[] fft_original){
        //Desplazamiento de la FFT
            double fft_desplazada[] = new double[205];
//            for (int i = n/2; i < (n-1); i++) {
//                //Frecuencias negativas
//                fft_desplazada[i] = fft_original[i];
//            }
            for (int i = 0; i < 205; i++) {
                 //Frecuencias positiva
                fft_desplazada[i] = fft_original[i]/(tamaño/2); 
            }
           
            return fft_desplazada;
    }
    
    public double[] EjeNormalizado(){
        double ejeX[] = new double[205];
//            for (int i = 0; i < n/2; i++) {
//                ejeX[i] =  ((-1*n/2) + i)/1024f;
//            }
            ejeX[0] = 0;
            double k = (double)50/n;//Resolucion espectral
            for (int i = 1; i < 205; i++) {
                ejeX[i] = k*i;
                
            }
            return ejeX;
    }

}
