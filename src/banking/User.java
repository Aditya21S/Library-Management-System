/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package banking;

/**
 *
 * @author ADITYA
 */


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;


public class User {
    private final Connection connection;
    private final Scanner scanner;
    User(Connection connection,Scanner scanner){
        this.connection=connection;
        this.scanner=scanner;
    }
        public void register(){
        scanner.nextLine();
        System.out.println("full name");                
        String full_name=scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        if(user_exists(email)){
            System.out.println("User Already Exists for this Email Address!!");
            return;
        }
        String register_query="insert into user(full_name,email,password)values(?,?,?)";
         try{
             PreparedStatement preparedStatement=connection.prepareStatement(register_query);
             preparedStatement.setString(1, full_name);
             preparedStatement.setString(2, email);
             preparedStatement.setString(3, password);
             
            int rowsAffected= preparedStatement.executeUpdate();
            if(rowsAffected>0){
               System.out.println("Registration Successfull!");
            }else{
                System.out.println("Registration fail");
            }             
         }
        catch(SQLException e){
             System.out.println(e.getMessage());
        }
        
    }
    public String login(){
        scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        String login_query="select * from user where email=? and password=?";
        
        try{
            PreparedStatement preparedStatement=connection.prepareStatement(login_query);
            preparedStatement.setString(1,email);
            preparedStatement.setString(2,password);
            ResultSet rs=preparedStatement.executeQuery();
            if(rs.next()){
               return email;
               
            }else{
              return null;
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
       return null;
    }
    public boolean user_exists(String email){
        String query="Select * from user where email=?";
        try{
            PreparedStatement preparedStatement=connection.prepareStatement(query);
            preparedStatement.setString(1,email);
            ResultSet rs=preparedStatement.executeQuery();
              return rs.next();
         
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        
        return false;
    }
}
