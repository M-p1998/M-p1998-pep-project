package Controller;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */

    private MessageService messageService;
    private AccountService accountService;

    public SocialMediaController(){
        this.messageService = new MessageService();
        this.accountService = new AccountService();
    }
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("/register", this::registerAccController);
        app.post("/login", this::loginController);
        app.get("/messages", this::getAllMessagesController);
        app.post("/messages", this::postMessageController);
        // app.get("/messages/{message_id}", this::messageById);
        // app.delete("/messages/{message_id}", this::messageById);
        // app.patch("/messages/{message_id}", this::messageById);
        // app.get("/accounts/{account_id}/messages", this::messageByUserId);
        // app.start(8080);
        System.out.println("javalin app started successfully on port 8080");
        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void exampleHandler(Context context) {
        context.json("sample text");
    }

    private void registerAccController(Context ctx) {
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");
           
        try {
            ObjectMapper mapper = new ObjectMapper();
            Account acc = mapper.readValue(ctx.body(),Account.class);
            // Account newAcc = new Account(username,password);
            accountService.registerValidation(acc);
            // Account registeredAccount = accountService.createAccount(acc);
            ctx.status(200).json(acc);
            ctx.json("Account registered successfully.");
        } catch (Exception e) {
            ctx.status(400).json(e.getMessage());
        }
    }

    private void loginController(Context ctx) throws JsonProcessingException{
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");
        ObjectMapper mapper = new ObjectMapper();
        Account acc = mapper.readValue(ctx.body(),Account.class);
        // validate input
        try {
            Account loggedInAccount = accountService.loginValidation(acc);
            if(loggedInAccount != null){
                ctx.status(200).json(loggedInAccount);
            } else {
                ctx.status(401).json("Invalid username or password.");
            }
        } catch (Exception e) {
            ctx.status(401).json("Login failed" + e.getMessage());
        }
        // if(username == null || username.trim().isEmpty() || password == null || password.isEmpty()){
        //     ctx.status(400);
        //     ctx.json("Username and password must be provided.");
        // }
    }



    // private void registerAccount(Context ctx) throws JsonProcessingException {
    //     ObjectMapper mapper = new ObjectMapper();
    //     Account account = mapper.readValue(ctx.body(), Account.class);
    //     try {
    //         Account registeredAccount = accountService.createAccount(account);

    //         // Send the registered account as a JSON response
    //         ctx.json(mapper.writeValueAsString(registeredAccount));
    //     } catch (ServiceException e) {
    //         // Set the response status to 400 (Bad Request) in case of exception
    //         ctx.status(400);
    //     }
    // }

    //  create a message
    private void postMessageController(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(ctx.body(),Message.class);
        Message addMsg = messageService.createMessage(message);
        if(addMsg != null){
            ctx.json(mapper.writeValueAsString(addMsg));
        }else{
            ctx.status(400);
        }
    }

    private void getAllMessagesController(Context ctx){
         ctx.json(MessageService.ListMessages());
    }

    


}