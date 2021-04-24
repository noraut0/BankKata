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

    private ArrayList<Account> accounts = new ArrayList<Account>();


    public Bank() {
        initDb();

        // we select accounts which exist in database
        String query = "select name,balance,threshold,banned from " + TABLE_NAME;

        try (PreparedStatement s = c.prepareStatement(query)) {
            ResultSet r = s.executeQuery();

            // we add every account in a array list of account's objet
            while (r.next()){
                accounts.add( new Account( r.getString("name") , r.getInt("balance") ,r.getInt("threshold") , r.getBoolean("banned") ) );
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    private void initDb() {
        try {
            Class.forName(JDBC_DRIVER);
            c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("Opened database successfully");

            // we create the account table if it not exist
            try (Statement s = c.createStatement()) {
                s.executeUpdate(
                        "CREATE TABLE " + TABLE_NAME +" (" +
                                "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,"+
                                "name VARCHAR(64),"+
                                "balance INT,"+
                                "threshold INT,"+
                                "banned BOOLEAN DEFAULT false"+
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

        // close database
        try {
            c.close();
        } catch (SQLException e) {
            System.out.println("Could not close the database : " + e);
        }
    }

    public void dropAllTables() {

        // we delete the database account for the test
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

        // we check if the name doesn't exist in the array list
        for( int i = 0 ; i < accounts.size()  ; i++){

            if( accounts.get(i).getName().equals(name) ){
                return true;

            }
        }
        return false;

    }


    public void createNewAccount(String name, int balance, int threshold) {

        // we check value of threshold and name
        if( threshold <= 0 && !checkName(name) ) {

            // we add account to the array list of account
            accounts.add( new Account(name , balance , threshold , false) );

            // and to  mysql database
            String sql = "INSERT INTO " + TABLE_NAME + "(name, balance, threshold, banned) VALUES (?,?,?,?)";

            try (PreparedStatement preparedStatement = c.prepareStatement(sql)) {

                preparedStatement.setString(1, name);
                preparedStatement.setInt(2, balance);
                preparedStatement.setInt(3, threshold);
                preparedStatement.setBoolean(4, false);
                preparedStatement.execute();

            } catch (Exception e) {

                System.out.println(e.toString());
            }
        }else System.out.println("this name is already used or threshold is not under 0");
    }

    public String printAllAccounts() {

        String stringAccounts = "";

        // we display every account
        for( int i = 0 ; i < accounts.size() ; i++ ){

            stringAccounts += accounts.get(i).toString();
        }

        return stringAccounts;
    }

    private Boolean checkBanned(String name ){

        // we check if the account is not blocked

        for( int i = 0 ; i < accounts.size()  ; i++){

            if( accounts.get(i).getName().equals(name) && accounts.get(i).getBanned() ) {
                System.out.println("the account is currently blocked !");
                return false;
            }

        }
        return true;
    }

    private Boolean checkBalance(String name , int modifier ){

        // check if the balance doesn't go under threshold after the update
        for( int i = 0 ; i < accounts.size()  ; i++){

            if( accounts.get(i).getName().equals(name) && accounts.get(i).getBalance() + modifier < accounts.get(i).getThreshold() ){
                System.out.println("balance will be under threshold !");
                return false;

            }
        }
        return true;
    }



    public void changeBalanceByName(String name, int balanceModifier) {

        if( checkBanned( name ) && checkBalance(name , balanceModifier) && checkName( name )) {

            // update the balance account
            for( int i = 0 ; i < accounts.size()  ; i++){

                if( accounts.get(i).getName().equals(name) )  accounts.get(i).updateBalance( balanceModifier );

            }

            // then update balance in database
            String sql = "UPDATE " + TABLE_NAME + " SET balance = balance + ? WHERE name=?";

            try (PreparedStatement preparedStatement = c.prepareStatement(sql)) {

                preparedStatement.setInt(1, balanceModifier);
                preparedStatement.setString(2, name);
                preparedStatement.executeUpdate();

            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }

    }

    public void blockAccount(String name) {

        // if the name exist block the account
        if(checkName( name )) {

            for( int i = 0 ; i < accounts.size()  ; i++){

                if( accounts.get(i).getName().equals(name) ){
                    accounts.get(i).setBanned();

                }
            }


            String sql = "UPDATE " + TABLE_NAME + " SET banned=? WHERE name=?";
            try (PreparedStatement preparedStatement = c.prepareStatement(sql)) {

                preparedStatement.setBoolean(1, true);
                preparedStatement.setString(2, name);

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
