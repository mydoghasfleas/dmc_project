package tableexample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Vector;

/**
 *
 * @author martin
 */
public class AppLogic {
    
    public static AppLogic appLogic;
    
    protected Connection conn;
    
    protected void initializeDB() throws SQLException {
        Statement s1 = conn.createStatement();
        
        // Food groups table
        String sqlDropFoodGroupsTable = "drop table if exists food_groups;";
        String sqlCreateFoodGroupsTable = 
        """
        create table food_groups (
            name text primary key,
            description text,
            total real,
            required real,
            difference real,
            result text
            );
        """;
        s1.execute(sqlDropFoodGroupsTable);
        s1.execute(sqlCreateFoodGroupsTable);                
        
        // Foods table
        String sqlDropFoodTable = "drop table if exists foods;";
        String sqlCreateFoodTable = """
            create table foods (
                name text primary key,
                food_group text
            );
        """;
        s1.execute(sqlDropFoodTable);
        s1.execute(sqlCreateFoodTable);
        
        // Portions table
        String sqlDropPortionsTable = "drop table if exists portions;";
        String sqlCreatePortionsTable = """
            create table portions (
                food text primary key,
                portions real                                  
            );
        """;
        s1.execute(sqlDropPortionsTable);
        s1.execute(sqlCreatePortionsTable);    
        
        // Rules table
        s1.execute("drop table if exists rules;");
        s1.execute(
        """
        create table rules (
            calories int,
            food_group text,
            portions real
        );
        """
        );
        
        // Populate lookup tables
        String sqlPopulateFoodGroups = 
        """
        insert into food_groups (name, description) values
        ('Fruit&Veg', 'Good to have'),
        ('Protein', 'Strong Muscle'),
        ('Starch', 'Yummy');
        """;
        s1.execute(sqlPopulateFoodGroups);
        
        String sqlPopulateFoods = 
        """
        insert into foods values
        ('Beans', 'Fruit&Veg'),
        ('Apples', 'Fruit&Veg'),
        ('Bananas', 'Fruit&Veg'),
        ('Pears', 'Fruit&Veg'),
        ('Chicken', 'Protein'),
        ('Beef', 'Protein'),
        ('Fish', 'Protein'),
        ('Doughnuts', 'Starch'),
        ('Porridge', 'Starch');
        """;
        s1.execute(sqlPopulateFoods);
        
        // Populate rules
        s1.execute(
        """
        insert into rules values
        (1000, 'Fruit&Veg', 1.0),
        (1000, 'Starch', 1.1),
        (1000, 'Protein', 1.2),
        (2000, 'Fruit&Veg', 1.5),
        (2000, 'Starch', 1.6),
        (2000, 'Protein', 1.7),        
        (3000, 'Fruit&Veg', 2.1),
        (3000, 'Starch', 2.2),
        (3000, 'Protein', 2.3);
        """);
        
        // Populate example data
        s1.execute(
        """
        insert into portions values
        ('Beans', 2.0),
        ('Apples', 1.5),
        ('Chicken', 1.0),
        ('Beef', 1.0);
        """);
        
        
        
        s1.close();
    }

    private AppLogic() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:test.db");
        
        initializeDB();
    }
    
    public static AppLogic getInstance() throws ClassNotFoundException, SQLException {
        if (appLogic == null) {
            appLogic = new AppLogic();
        }
        return appLogic;
    }
    
    public FoodGroupPortion[] getFoodGroupPortionTotals() throws SQLException {       
        Vector<FoodGroupPortion> v1 = new Vector<>();
        
        Statement s1 = conn.createStatement();
        
        java.sql.ResultSet rs = s1.executeQuery(
        """
        select 
        foods.food_group, sum(portions.portions) as total
        from portions 
        join foods on foods.name = portions.food
        group by foods.food_group;
        """
        );
                
        while (rs.next()) {
            v1.add(new FoodGroupPortion(
                    rs.getString(1),
                    rs.getDouble(2)
            ));
        }
        
        return v1.toArray(new FoodGroupPortion[0]);
//        return (FoodGroupPortion[]) v1.toArray();
    }
    
    public void updateResultsFromEnteredPortions() throws SQLException {
        Statement s1 = conn.createStatement();
        
        // Update totals of entered portions into the food groups table
        s1.execute(
  """
        update food_groups
        set total = res1.total
        from (select foods.food_group, sum(portions.portions) as total
        from portions join foods on foods.name = portions.food
        group by foods.food_group) as res1
        where food_groups.name = res1.food_group;
        """);
    }
    
    public void updateRequiredPortionsForCalories(int calories) throws SQLException {
        Statement s1 = conn.createStatement();
        
        // TODO Check whether there are rules for the given calories
        String countRulesSQL = String.format("select count(*) from rules where calories = %s", calories);
        ResultSet rs = s1.executeQuery(countRulesSQL);
        if (rs.getFetchSize() == 0) {
            
        }
        
        // Update the food groups table with the required portions for each food
        // group based on the given calories
        String sql =
        """
        update food_groups
                set required = res1.portions
                from (select food_group, portions from rules where calories = %s) as res1
                where food_groups.name = res1.food_group;        
        """;
        s1.execute(String.format(sql, calories));
        
        s1.close();
    }
    
    public void calculateResults() throws SQLException {
        Statement s1 = conn.createStatement();
        s1.execute(
        """
        update food_groups set 
          difference = total - required,
          result = case
            when required > total then 'too little'
            when required < total then 'too much'
            when required = total then 'just right'
          end;
        """);
        s1.close();        
    }
    
    public void clearAllResults() throws SQLException {
        Statement s1 = conn.createStatement();
        s1.execute("update food_groups set total = 0, required = 0, difference = 0;");
        s1.close();
    }
    
}
