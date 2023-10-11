package tableexample;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author martin
 */
public class TestAppLogic {
    public static void main(String[] args) {
        try {
            AppLogic al = AppLogic.getInstance();
            
            al.clearAllResults();
            
            FoodPortion[] portions = al.getFoodsForInput("Starch");
            for (FoodPortion portion : portions) {
                System.out.println(portion.food() + " " + String.valueOf(portion.portions()));
            }
            
            al.updateResultsFromEnteredPortions();            
            al.updateRequiredPortionsForCalories(2000);
            al.calculateResults();
            
            
//            FoodGroupPortion[] fgp = al.getFoodGroupPortionTotals();
//            System.out.println(fgp.length);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(TestAppLogic.class.getName()).log(Level.SEVERE, null, ex);
        }       
    }
    
}
