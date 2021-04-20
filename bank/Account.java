package bank;

class Account {

    // Attributes
    private String name;
    private int balance;
    private int threshold;


    // Constructor
    public Account( String name , int balance , int threshold){
        this.name = name;
        this.balance = balance;
        this.threshold = threshold;

    }

    public void updateBalance(int balance){

        this.balance += balance;

    }

    public String toString() {
        // TODO
        return "";
    }
}
