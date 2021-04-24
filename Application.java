
import bank.Bank;
import java.util.Scanner;

public class Application {

    private static Scanner s = new Scanner(System.in);

    public static void interfaceCreate(Bank b){

        try {

            System.out.println("CREATE NEW ACCOUNT\n");
            System.out.print("enter a account's name: ");
            String name = s.nextLine();

            System.out.print("enter the initial balance: ");
            int balance = s.nextInt();
            System.out.print("enter the initial threshold: ");
            int threshold = s.nextInt();
            s.nextLine();

            b.createNewAccount( name, balance, threshold );

        }catch (Exception e){

            System.out.println("error : input value is wrong !");

        }
    }


    public static void interfaceUpdateBalance(Bank b){

        try{

            System.out.println("UPDATE BALANCE ACCOUNT\n");
            System.out.print("enter your account's name: ");
            String name = s.nextLine();
            System.out.print("enter a modifier balance: ");
            int modifierBalance = s.nextInt();
            s.nextLine();

            b.changeBalanceByName( name , modifierBalance );
        }catch (Exception e){

            System.out.println("error : input value is wrong !");

        }

    }

    public static void interfaceBlockAccount(Bank b){

        System.out.println("BLOCK AN ACCOUNT\n");
        System.out.print("enter your account's name: ");
        String name = s.nextLine();

        b.blockAccount( name );
    }



}
