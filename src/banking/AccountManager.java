/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package banking;


import java.sql.*;
import java.util.Scanner;

/**
 *
 * @author ADITYA
 */
public class AccountManager {
    private Connection connection;
    private Scanner scanner;
    AccountManager(Connection connection,Scanner scanner){
        this.connection=connection;
        this.scanner=scanner;
    }
    
    public void debit_money(long account_number) throws SQLException{
        scanner.nextLine();
        System.out.print("Enter Amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = scanner.nextLine();
        try{
              connection.setAutoCommit(false);
              PreparedStatement preparedStatement=connection.prepareStatement("SELECT * FROM Accounts WHERE account_number = ? and security_pin = ?");
              preparedStatement.setLong(1,account_number);
              preparedStatement.setString(2,security_pin);
               ResultSet resultSet = preparedStatement.executeQuery();
               if(resultSet.next()){
                   double current_balance=resultSet.getDouble("balance");
                   if(amount<=current_balance){
                       String debit_query="update accounts set balance=balance-? where account_number=?";
                        PreparedStatement preparedStatement1=connection.prepareStatement(debit_query);
                        preparedStatement1.setDouble(1, amount);
                        preparedStatement1.setLong(2, account_number);
                        int rowsAffected=preparedStatement1.executeUpdate();
                        if(rowsAffected>0){
                             System.out.println("Rs."+amount+" debited Successfully");
                             connection.commit();
                             connection.setAutoCommit(true);
                                     
                       } else {
                           System.out.println("Transaction Failed!");
                           connection.rollback();
                           connection.setAutoCommit(true);
                       }
                   }
                   else {
                       System.out.println("Insufficient Balance!");
                   }
               }
               else {
                    System.out.println("Invalid Pin!");
               }
        }catch(SQLException e){
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
       

}//debit close
    
    
   public void credit_money(long account_number) throws SQLException{
       scanner.nextLine();
        System.out.print("Enter Amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = scanner.nextLine();
        try{
             connection.setAutoCommit(false);
            if(account_number!=0){
            String query="select * from accounts where account_number=? and security_pin=?";
            PreparedStatement preparedStatement=connection.prepareStatement(query);
            preparedStatement.setLong(1, account_number);
            
            preparedStatement.setString(2,security_pin);
            ResultSet rs=preparedStatement.executeQuery();
            if(rs.next()){
                String credit_query="update accounts set balance=balance + ? where account_number=?";
                 PreparedStatement preparedStatement1 = connection.prepareStatement(credit_query);
                    preparedStatement1.setDouble(1, amount);
                    preparedStatement1.setLong(2, account_number);
                    int rowsAffected = preparedStatement1.executeUpdate();
                    if(rowsAffected>0){
                        System.out.println("Rs."+amount+" credited Successfully");
                        connection.commit();
                        connection.setAutoCommit(true);
                        return;
                    }
                    else{
                        System.out.println("Transaction Failed!");
                        connection.rollback();
                        connection.setAutoCommit(true);
                    }
            }else{
                    System.out.println("Invalid Security Pin!");
                }
             }
           
            
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
                
   }
   
   
   public void transfer_money(long sender_account_number)throws SQLException{
       
         scanner.nextLine();
         System.out.println("Enter reciever account number");
         long receiver_account_number=scanner.nextLong();
         // 🚨 Prevent self-transfer
    if (sender_account_number == receiver_account_number) {
        System.out.println("❌ You cannot transfer money to your own account!");
        return;
    }
        System.out.print("Enter Amount: ");
        double amount = scanner.nextDouble();
        
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin=scanner.nextLine();
        try {
           connection.setAutoCommit(false);
           if(sender_account_number!=0 && receiver_account_number !=0){
               PreparedStatement preparedStatement=connection.prepareStatement("Select balance from accounts where account_number=? and security_pin=?");
               preparedStatement.setLong(1, sender_account_number);
               preparedStatement.setString(2, security_pin);
               ResultSet rs=preparedStatement.executeQuery();
               if (rs.next()) {
                double current_balance=rs.getDouble("balance");
                if(amount<=current_balance){
                    //check if receivers account exists 
                    PreparedStatement receiverStatement=connection.prepareStatement("Select account_number from accounts where account_number= ?");
                    receiverStatement.setLong(1, receiver_account_number);
                    ResultSet receiverResult=receiverStatement.executeQuery();
                    
                    if(!receiverResult.next()){
                         System.out.println("Receiver's account does not exist!");
                         connection.rollback();
                         return;
                    }
                    String debit_query="update accounts set balance=balance-? where account_number=?";
                    String credit_query="update accounts set balance=balance+? where account_number=?";
                    PreparedStatement creditpreparedStatement=connection.prepareStatement(credit_query);
                    PreparedStatement debitpreparedStatement=connection.prepareStatement(debit_query);
                    
                    creditpreparedStatement.setDouble(1, amount);
                    creditpreparedStatement.setLong(2, receiver_account_number);
                    
                    debitpreparedStatement.setDouble(1, amount);
                    debitpreparedStatement.setLong(2, sender_account_number);
                    
                    int rowsAffected1 = debitpreparedStatement.executeUpdate();
                    int rowsAffected2 =creditpreparedStatement.executeUpdate();
                    if (rowsAffected1 >0 && rowsAffected2 >0) {
                        System.out.println("Transfer success");
                          System.out.println("Rs."+amount+" Transferred Successfully");
                          connection.commit();
                          connection.setAutoCommit(true);
                    } else {
                        System.out.println("Transaction failed");
                        connection.rollback();
                        connection.setAutoCommit(true);
                    }                                              
                }else{
                    System.out.println("insufficient balance");
                }
            } else {
                   System.out.println("Invalid Security Pin!");
            }                  
           }
           else{
                  System.out.println("invalid account number"); 
                   }
       } catch (Exception e) {
            System.out.println(e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
       }
        finally{
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
   }
   
   public void getBalance(long account_number){
       scanner.nextLine();
       System.out.println("enter security pin");
       String security_pin=scanner.next();
       try {
           PreparedStatement preparedStatement=connection.prepareStatement("select balance from accounts where account_number=? and security_pin=?");
           preparedStatement.setLong(1,account_number);
           preparedStatement.setString(2,security_pin);
           ResultSet rs=preparedStatement.executeQuery();
           if(rs.next()){
               double balance =rs.getDouble("balance");
                System.out.println("Balance: "+balance);
           }else{
               System.out.println("Invalid Pin!");
           }
                     
           
       } catch (SQLException e) {
            e.printStackTrace();
       }
       
       
   }
    
}
