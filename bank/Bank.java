package bank;


import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Bank {

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bank_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";
    private static final String TABLE_NAME = "accounts";
    private Connection c;

    private ArrayList Accounts;

    public Bank() {
        initDb();
        Accounts = new ArrayList<>();

    }

    private void initDb() {
        try {
            Class.forName(JDBC_DRIVER);
            c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("Opened database successfully");

            try (Statement s = c.createStatement()) {
                s.executeUpdate(
                        "CREATE TABLE " + TABLE_NAME +" (" +
                                "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,"+
                                "name VARCHAR(64),"+
                                "balance INT,"+
                                "threshold INT,"+
                                "banned VARCHAR(1) DEFAULT 'f'"+
                                ");");

            } catch (Exception e) {
                System.out.println(e.toString());
            }

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }


    }

    public void closeDb() {
        try {
            c.close();
        } catch (SQLException e) {
            System.out.println("Could not close the database : " + e);
        }
    }

    public void dropAllTables() {


        try (Statement s = c.createStatement()) {
            s.executeUpdate(
                       "DROP TABLE " + TABLE_NAME + ";"
                            );


        } catch (Exception e) {
            System.out.println(e.toString());
        }

        closeDb();
    }

    private Boolean checkName( String name ){

        Boolean nameExist = false;

        String sql = "SELECT name FROM " + TABLE_NAME + " WHERE name=?";

        try (PreparedStatement preparedStatement = c.prepareStatement(sql)) {

            preparedStatement.setString(1, name);
            preparedStatement.executeQuery();
            ResultSet result = preparedStatement.executeQuery();

            nameExist = result.next();

        } catch (Exception e) {

            System.out.println(e.toString());
        }

        return nameExist;

    }


    public void createNewAccount(String name, int balance, int threshold) {

        if( threshold <= 0 && !checkName(name) ) {

            String sql = "INSERT INTO " + TABLE_NAME + "(name, balance, threshold, banned) VALUES (?,?,?,?)";

            try (PreparedStatement preparedStatement = c.prepareStatement(sql)) {

                preparedStatement.setString(1, name);
                preparedStatement.setInt(2, balance);
                preparedStatement.setInt(3, threshold);
                preparedStatement.setString(4, "f");
                preparedStatement.execute();

            } catch (Exception e) {

                System.out.println(e.toString());
            }

        }

    }

    public String printAllAccounts() {

        String result = getTableDump();

        result = result.replace(']' , '\n');
        result = result.replace("[" , "");
        result = result.replace("," , " |");
        result = result.replace("f" , "false");
        result = result.replace("t" , "true");

        return result;
    }

    private Boolean checkBannedAndBalance(String name , int balanceModifier){

        String valueBanned = "";
        int valueBalance = 0, valueThreshold = 0;

        String sql = "SELECT balance,threshold,banned FROM " + TABLE_NAME + " WHERE name=?";

        try (PreparedStatement preparedStatement = c.prepareStatement(sql)) {

            preparedStatement.setString(1, name);
            preparedStatement.executeQuery();
            ResultSet result = preparedStatement.executeQuery();

            while (result.next()){
                valueBanned = result.getString("banned");
                valueBalance = result.getInt("balance");
                valueThreshold = result.getInt("threshold");
            }

        } catch (Exception e) {

            System.out.println(e.toString());
        }

        return valueBanned.equals("f") && ( valueBalance + balanceModifier > valueThreshold);
    }

    public void changeBalanceByName(String name, int balanceModifier) {



        if( checkBannedAndBalance(name , balanceModifier) && checkName( name )) {
            String sql = "UPDATE " + TABLE_NAME + " SET balance = balance + ? WHERE name=?";

            try (PreparedStatement preparedStatement = c.prepareStatement(sql)) {

                preparedStatement.setInt(1, balanceModifier);
                preparedStatement.setString(2, name);
                preparedStatement.executeUpdate();

            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }else System.out.println("change not possible: account blocked, balance under threshold or name doesn't exist");

    }

    public void blockAccount(String name) {

        String sql = "UPDATE " + TABLE_NAME + " SET banned='t' WHERE name=?";

        if(checkName( name )) {
            try (PreparedStatement preparedStatement = c.prepareStatement(sql)) {

                preparedStatement.setString(1, name);

                preparedStatement.executeUpdate();

            } catch (Exception e) {

                System.out.println(e.toString());
            }
        }else System.out.println("name doesn't exist");

    }

    // For testing purpose
    String getTableDump() {


        String query = "select name,balance,threshold,banned from " + TABLE_NAME;
        String res = "";

        try (PreparedStatement s = c.prepareStatement(query)) {
            ResultSet r = s.executeQuery();

            // Getting nb column from meta data
            int nbColumns = r.getMetaData().getColumnCount();

            // while there is a next row
            while (r.next()){
                String[] currentRow = new String[nbColumns];

                // For each column in the row
                for (int i = 1 ; i <= nbColumns ; i++) {

                    currentRow[i - 1] = r.getString(i);
                }
                res += Arrays.toString(currentRow);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return res;
    }
}
