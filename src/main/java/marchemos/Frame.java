package marchemos;

import java.awt.BorderLayout;

import javax.swing.*;

public class Frame extends JFrame {
	
	public Frame(JPanel panel) {
	 JFrame frame = new JFrame("Inicio");
     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     frame.setSize(800, 600);
     frame.add(panel,BorderLayout.CENTER);
     frame.setVisible(true); 
     }
	
}
