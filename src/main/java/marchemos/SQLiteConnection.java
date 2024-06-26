/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marchemos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Marinany Guzman
 */
public class SQLiteConnection {
    
    private String url = "BD/";
    private Connection connect;
    private String nombreBD;
    
    public SQLiteConnection(String nombreBD){
        this.nombreBD = nombreBD;
        url = url + nombreBD;
    }
    
    //Establecer la conexion
    public void connect(){
         try {
     connect = DriverManager.getConnection("jdbc:sqlite:"+url);
     if (connect!=null) {
         System.out.println("Conectado a la base de datos " + nombreBD);
     }
 }catch (SQLException ex) {
     System.err.println("No se ha podido conectar a la base de datos\n"+ex.getMessage());
 }
    }
    
     //Obtener el valor de los campos de la BD
     public void selectAll(String nombre,String tabla, ArrayList<Double> x,
             ArrayList<Double>  y, ArrayList<Double> z, ArrayList<Double> t){
        
         ResultSet rs = null;
         String query = "SELECT * FROM " + tabla;
     
        try{          
            PreparedStatement st = connect.prepareStatement(query);
            rs = st.executeQuery();       
            // Ciclo que recorre el resultSet
            while (rs.next()) {
                t.add(rs.getDouble("TIEMPO"));
                x.add(rs.getDouble("x"));
                y.add(rs.getDouble("y"));
                z.add(rs.getDouble("z"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
     
     //Ver en cosola los elementos extraidos de una tabla
    public void seeAll(String tabla){
        ResultSet rs = null;
        String query = "SELECT * FROM " + tabla;
        
           try{          
            PreparedStatement st = connect.prepareStatement(query);
            rs = st.executeQuery();       
            // Ciclo que recorre el resultSet
            while (rs.next()) {
                System.out.println(rs.getInt("MUESTRA_ID") +  "\t" + 
                                  rs.getDouble("TIEMPO") + "\t" +
                         rs.getDouble("x") + "\t" +
                         rs.getDouble("y") + "\t" +
                                   rs.getDouble("z"));
            }
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    //Cerrar la conexion a la BD
    public void close(){
        try {
            connect.close();
             System.out.println("Cerrada conexion a la base de datos");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
 }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
