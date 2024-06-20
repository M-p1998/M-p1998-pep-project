package Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.swing.text.html.Option;

import DAO.AccountDAO;
import Model.Account;
import io.javalin.http.Context;

public class AccountService {

    private AccountDAO accountDAO;

    public AccountService(){
        this.accountDAO = new AccountDAO();
    }

    public AccountService (AccountDAO accDAO){
        this.accountDAO = accDAO;
    }

    // register validation 
    public void registerValidation(Account account) throws Exception{
        // validate input
        String username = account.getUsername().trim();
        String password = account.getPassword().trim();
        try {
            if(username.isEmpty()){
                // System.out.println("Username cannot be blank.");
                throw new Exception("Username cannot be blank.");
            }
            if(password.isEmpty()){
                // System.out.println("Username cannot be blank.");
                throw new Exception("Password cannot be blank.");
            }
            if(password.length() < 4){
                // System.out.println("Username cannot be blank.");
                throw new Exception("Password must be at least 4 characters long");
            }
            if(accountDAO.checkIfUsernameExist(account.getUsername())){
                throw new Exception("This " + account.getUsername() + " already exist.");
            }
        } catch (SQLException e) {
            throw new SQLException("Error occurred while register validation", e);
        }

        try {
            Account registeredAccount = accountDAO.insertUser(account);
            if (registeredAccount != null) {
                System.out.println("Account registered successfully");
            } else {
                throw new Exception("Registration failed.");
            }
        } catch (SQLException e) {
            throw new Exception("Error occurred while inserting account", e);
        }
        
        // else if(account.getPassword() == null || account.getPassword().trim().isEmpty()){
        //     throw new Exception("Password cannot be null.");
        // }else if(account.getPassword().length() < 4){
        //     throw new Exception("Password must be at least 4 characters long.");
        // }
        // // check if account exist with username
        // Account existingAccount = accountDAO.findAccountByUserName(account.getUsername());
        // if(existingAccount != null){
        //     System.out.println("An account with " + account.getUsername() + " already exists.");

        // }
        // register account
        // Account registeredAccount = accountDAO.insertUser(account);
        // if(registeredAccount != null){
        //     System.out.println("Account registered successfully");
        // }else{
        //     System.out.println("Registration failed.");
        // }
    }   

    // public void login(Context ctx){
    //     String username = ctx.formParam("username");
    //     String password = ctx.formParam("password");

    //     // Validate login
    //     if (username == null || username.trim().isEmpty() ||
    //             password == null || password.isEmpty()) {
    //         ctx.status(400); // Bad request
    //         ctx.json("Username and password must be provided.");
    //         return;
    //     }

    //     Account loggedInAccount = accountDAO.loginValidation(username, password);
    //     if (loggedInAccount != null) {
    //         ctx.status(200); // OK
    //         ctx.json(loggedInAccount);
    //     } else {
    //         ctx.status(401); // Unauthorized
    //         ctx.json("Invalid username or password.");
    //     }
    // }

    // public Account getAccountByUsername(String username) {
    //     try {
    //         return accountDAO.getAccountByUserName(username);
    //     } catch (Exception e) {
    //         // Handle any exceptions or log them
    //         System.out.println("Error fetching account by username: " + e.getMessage());
    //         return null; // Or throw a custom exception
    //     }
    // }

    public Account loginValidation(Account acc) throws SQLException{
        String username = acc.getUsername().trim();
        String password = acc.getPassword().trim();
        // try {
            // Retrieve account from database
            // Account account = accountDAO.login(username, password);
            // return account;
            // if (account == null) {
            //     System.out.println("Account not found for username: " + username);
            //     return null;
            // }

            // Validate password
            // if (account.getPassword().equals(password)) {
            //     System.out.println("Login successful for username: " + username);
            //     return account;
            // } else {
            //     System.out.println("Invalid password for username: " + username);
            //     return null;
            // }
        // } catch (SQLException e) {
        //     throw new SQLException("Validation login failed: " + e.getMessage());
        // }
        try {
            Account account = accountDAO.login(username, password);
            return account;
        } catch (Exception e) {
            throw new SQLException("Validation login failed: " + e.getMessage());
        }
    }

    // help to show json response body when registeration
    public Account createAccount(Account acc) throws Exception{
        String username = acc.getUsername().trim();
        // try {
        //     // registerValidation(acc);
        //     Account accInDatabase = accountDAO.getAccountByUserName(username);
        //     if(accInDatabase != null){
        //         throw new SQLException("Account with username '" + username + "' already exists.");
        //     }
        //     Account createdAccount = accountDAO.insertUser(acc);
        //     return createdAccount;
        // } catch (SQLException e) {
        //     throw new SQLException("Exception occurred while creating account", e);
        // } 
        try {
            // Validate if account with the same username already exists
            Account accInDatabase = accountDAO.getAccountByUserName(username);
            if (accInDatabase != null) {
                throw new SQLException("Account with username '" + username + "' already exists.");
            }
            
            // Proceed with account insertion if validation passes
            Account createdAccount = accountDAO.insertUser(acc);
            System.out.println("Account created successfully: " + createdAccount.getUsername());
            return createdAccount;
        } catch (SQLException e) {
            System.out.println("SQL Exception occurred: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("Exception occurred: " + e.getMessage());
            throw e;
        }    
    }




}
