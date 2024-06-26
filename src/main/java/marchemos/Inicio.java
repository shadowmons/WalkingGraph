package marchemos;

import java.awt.event.*; 
import java.awt.*; 
import javax.swing.*;

public class Inicio extends JPanel {
	
	private JButton btn_todos, btn_persona;
	
	public Inicio() {
		setLayout(new BorderLayout());
		JPanel panel_east = new JPanel();
		panel_east.setLayout(null);
		panel_east.setBounds(0,0,20,600);
		btn_todos = new JButton("Graficar todos");
		btn_todos.setBounds(50,100,100,100);
		btn_persona = new JButton("Graficar persona selecionada");
		panel_east.add(btn_todos);
		panel_east.add(btn_persona);
		
		add(panel_east, BorderLayout.CENTER);
		
		
		
	}

}
