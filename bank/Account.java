package bank;

class Account {

    // Attributes
    private String name;
    private int balance;
    private int threshold;
    private Boolean banned;

    // Constructor
    public Account( String name , int balance , int threshold, Boolean banned){
        this.name = name;
        this.balance = balance;
        this.threshold = threshold;
        this.banned = banned;

    }
    // Getter

    public String getName() {
        return this.name;
    }
    public int getBalance() {
        return this.balance;
    }
    public int getThreshold() {
        return this.threshold;
    }
    public Boolean getBanned() {
        return this.banned;
    }

    // setter

    public void setBanned(){

        this.banned = true;


    }

    // method
    public void updateBalance(int balance){

        this.balance += balance;

    }
    // to display database
    public String toString() {

        String valueString = this.name + " | " + this.balance + " | " + this.threshold + " | " + this.banned + "\n";


        return valueString;
    }
}
