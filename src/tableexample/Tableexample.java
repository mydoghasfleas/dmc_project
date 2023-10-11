/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package tableexample;

import java.util.HashMap;
import java.sql.*;

/**
 *
 * @author martin
 */
public class Tableexample {

    public void initializeDatabase() {
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
//        Person p1 = new Person("John", 44);
//        
//        Class.forName("org.sqlite.JDBC");
//        Connection c = DriverManager.getConnection("jdbc:sqlite:test.db");
//        Statement s1 = c.createStatement();
//        ResultSet rs = s1.executeQuery("select * from person;");
//        
//        
        
        
        MyUI ui = new MyUI();
        HashMap<String, Double> hm = new HashMap();
        hm.put("banana", Double.valueOf(1));
        hm.put("cherry", Double.valueOf(2));
        ui.setTableData(hm);
        ui.setVisible(true);
    }
    
}
