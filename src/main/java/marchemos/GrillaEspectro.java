/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marchemos;

/**
 *
 * @author Victor Gil
 */
import java.awt.*;
import javax.swing.*;

public class GrillaEspectro extends JFrame {

    private static final long serialVersionUID = 6632092242560855625L;
    static JPanel gridPane;
    static JViewport view;

    public GrillaEspectro(double[][] fft,String nombre) {
        setTitle(nombre);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 850);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane();
        setContentPane(scrollPane);
        view = scrollPane.getViewport();
        gridPane = new JPanel() {
            private static final long serialVersionUID = 2900962087641689502L;

            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGrid(g, fft);
            }
        };
        Dimension paneSize = new Dimension(1000, 4500);
        gridPane.setPreferredSize(paneSize);
        gridPane.setBackground(Color.WHITE);
        scrollPane.setViewportView(gridPane);
    }

    static void drawGrid(Graphics g, double[][] fft) {
        int control = 0;
        double aux=0;
        Color color1;
        for (int i = 0; i < fft.length; i++) {

            for (int j = 0; j < fft[0].length; j++) {
                if(fft[i][j]<1.5){
                    aux =-3*fft[i][j]*50 + 230;
                control = (int) Math.round(aux);
                }else{
                    control = 5;
                }
                int f=(int) Math.round(control*0.3+10);
                color1 = new Color(control, control, control);
                //color1 = new Color(control, control, control);
                g.setColor(color1);
                g.fillRect(10+3*j, 10 + i * 15, 3, 15);
                System.out.println(Math.round(control*0.1+20));
            }

        }
    }

}
