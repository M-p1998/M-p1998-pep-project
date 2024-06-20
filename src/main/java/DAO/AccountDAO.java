package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

import Model.Account;
import Util.ConnectionUtil;

// ## 1: Our API should be able to process new User registrations.

// As a user, I should be able to create a new Account on the endpoint POST localhost:8080/register. The body will contain a representation of a JSON Account, but will not contain an account_id.

// - The registration will be successful if and only if the username is not blank, the password is at least 4 characters long, and an Account with that username does not already exist. If all these conditions are met, the response body should contain a JSON of the Account, including its account_id. The response status should be 200 OK, which is the default. The new account should be persisted to the database.
// - If the registration is not successful, the response status should be 400. (Client error)



// ## 2: Our API should be able to process User logins.

// As a user, I should be able to verify my login on the endpoint POST localhost:8080/login. The request body will contain a JSON representation of an Account, not containing an account_id. In the future, this action may generate a Session token to allow the user to securely use the site. We will not worry about this for now.

// - The login will be successful if and only if the username and password provided in the request body JSON match a real account existing on the database. If successful, the response body should contain a JSON of the account in the response body, including its account_id. The response status should be 200 OK, which is the default.
// - If the login is not successful, the response status should be 401. (Unauthorized)
public class AccountDAO {

    // register user/accountf
    public Account insertUser(Account acc) throws SQLException{
        
        Connection connection = ConnectionUtil.getConnection();
        Account registeredAccount = null;
        
        try  {
            String sql = "INSERT INTO account (username, password) VALUES (?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, acc.getUsername());
            stmt.setString(2, acc.getPassword());
            int affectedRows = stmt.executeUpdate();
            // if (affectedRows == 0) {
            //     throw new SQLException("Creating account failed, no rows affected.");
            // }

            // ResultSet generatedKeys = stmt.getGeneratedKeys() ;
            //     if (generatedKeys.next()) {
            //         int accountId = generatedKeys.getInt(1);
            //         registeredAccount = new Account(accountId, acc.getUsername(),acc.getPassword());
            //     } else {
            //         throw new SQLException("Creating account failed");
            //     }

            if(affectedRows > 0){
                try(ResultSet generatedKeys = stmt.getGeneratedKeys()){
                    if (generatedKeys.next()) {
                        
                        // int accountId = generatedKeys.getInt(1);
                        acc.setAccount_id(generatedKeys.getInt(1));
                        registeredAccount = new Account(acc.getUsername(),acc.getPassword());
                    }
                }
            }
            
        } catch (SQLException e) {
            throw new SQLException("Creating account failed due to SQL error ", e);

        }
        return registeredAccount;

    }

    public boolean checkIfUsernameExist(String username) throws SQLException{
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "SELECT * FROM account WHERE username = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                int count = rs.getInt("count");
                return count > 0;
            }
        } catch (SQLException e) {
            throw new SQLException("Error occurs in sql while checking if " + username + " exists", e);
        }
        return false;
    }

    public Account getAccountByUserName(String username) throws Exception{
        Connection connection = ConnectionUtil.getConnection();
        String sql = "SELECT * FROM account WHERE username = ?";
        Account account = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                int accountId = rs.getInt("account_id");
                String userName = rs.getString("username");
                String password = rs.getString("password");
                account = new Account(accountId, userName, password);
            }
        } catch (Exception e) {
            throw new Exception("Account with this username not found.");
        }
        return account;
    }   

    // check if username and password match the account in database
    public Account login(String username, String password){
        PreparedStatement preparedStatement = null;
        Account loggedInAccount = null;
        Connection connection = ConnectionUtil.getConnection();

        // try {
        //     PreparedStatement preparedStatement = connection.prepareStatement(sql);
        //     preparedStatement.setString(1, acc.getUsername());
        //     preparedStatement.setString(2, acc.getPassword());
        //     ResultSet rs = preparedStatement.executeQuery();

        //     if (rs.next()) {
        //         int accountId = rs.getInt("account_id");
        //         String username = rs.getString("username");
        //         String password = rs.getString("password");
        //         loggedInAccount = new Account(accountId, username, password);
        //     }
            
        // } 
        try {
            String sql = "SELECT * FROM account WHERE username = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();

            // if (resultSet.next()) {
            //     String hashedPassword = resultSet.getString("password");
            //     if (BCrypt.checkpw(password, hashedPassword)) {
            //         int accountId = resultSet.getInt("account_id");
            //         String fetchedUsername = resultSet.getString("username");
            //         loggedInAccount = new Account(accountId, fetchedUsername, null); 
            //     }
            // }
            if (rs.next()) {
                // int accountId = rs.getInt("account_id");
                // username = rs.getString("username");
                // password = rs.getString("password");
                // loggedInAccount = new Account(accountId, username, password);

                int accountId = rs.getInt("account_id");
                String fetchedUsername = rs.getString("username");
                String hashedPassword = rs.getString("password"); // Assuming password is hashed in the database

                // Example of password validation using BCrypt
                // Replace this with your actual password hashing/validation mechanism
                if (BCrypt.checkpw(password, hashedPassword)) {
                    loggedInAccount = new Account(accountId, fetchedUsername, null); 
                } 
                // else {
                //     System.out.println("Password does not match.");
                // }
            }

        }
        catch (SQLException e) {
            System.out.println("Error occurs: " + e.getMessage() + " while login validation");
        }
        return loggedInAccount;
    }



}

