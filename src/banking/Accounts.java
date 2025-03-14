/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package banking;

import java.sql.Connection;
import java.util.Scanner;
import java.sql.*;

/**
 *
 * @author ADITYA
 */
public class Accounts {
    private Connection connection;
    private Scanner scanner;
    Accounts(Connection connection,Scanner scanner){
        this.connection=connection;
        this.scanner=scanner;
    }
    public long getAccount_number(String email){
       
        String query="select account_number from accounts where email=?";
        try{
           PreparedStatement preparedStatement=connection.prepareStatement(query);
             preparedStatement.setString(1, email); 
             ResultSet rs=preparedStatement.executeQuery();
             if(rs.next()){
                 return rs.getLong("account_number");
             }
        }catch(SQLException e){
             e.printStackTrace();
        }
        throw new RuntimeException("Account Number Doesn't Exist!");
    }
    public long open_account(String email){
        if(!account_exists(email)){
            String open_account_query="insert into accounts (account_number,full_name,email,balance,security_pin)values(?,?,?,?,?)";
            scanner.nextLine();
            System.out.print("Enter Full Name: ");
            String full_name = scanner.nextLine();
            System.out.print("Enter Initial Amount: ");
            double balance = scanner.nextDouble();
            scanner.nextLine();
            System.out.print("Enter Security Pin: ");
            String security_pin = scanner.nextLine();
            try{
             long account_number = generateAccountNumber();
             PreparedStatement preparedStatement=connection.prepareStatement(open_account_query);
             preparedStatement.setLong(1, account_number);
             preparedStatement.setString(2, full_name);
             preparedStatement.setString(3, email);
             preparedStatement.setDouble(4, balance);
             preparedStatement.setString(5, security_pin);
             
             
             int rowsAffected=preparedStatement.executeUpdate();
             if(rowsAffected>0){
                 return account_number;
             }
             else{
                 throw new RuntimeException("Account creation failed!!");
             }
             
            }
            
            catch(SQLException e){
                e.printStackTrace();
            }
         
        }
        throw new RuntimeException("Account Already exisits");
    }
    public boolean account_exists(String email){
        String query="Select * from accounts where email=?";
        try{
            PreparedStatement preparedStatement=connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet rs=preparedStatement.executeQuery();
            
            return rs.next();
            
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return false;
    }
    
    public long generateAccountNumber(){
        try{
             Statement statement = connection.createStatement();
             ResultSet rs=statement.executeQuery("Select account_number from accounts Order by account_number desc limit 1");
             if(rs.next()){
                 long last_account_number=rs.getLong("account_number");
                 return last_account_number+1;
             }
             else{
                 return 10001010;
             }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        
        return 1001001;
    }
       
}
