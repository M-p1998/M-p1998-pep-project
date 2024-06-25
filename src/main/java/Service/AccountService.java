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

    }   


    public Account loginValidation(Account acc) throws SQLException{
        String username = acc.getUsername().trim();
        String password = acc.getPassword().trim();

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

    public Account getAccountByUserId(int id) throws Exception {
        try {
            Account account = accountDAO.getByUserId(id);
            return account;
        } catch (Exception e) {
            throw new Exception("Error occurred while fetching account", e);
        }
    }



}
