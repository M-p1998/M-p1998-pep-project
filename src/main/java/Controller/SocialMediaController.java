package Controller;

import java.sql.SQLException;
import java.util.List;

import org.junit.rules.ExpectedException;

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
        app.post("/messages", this::createMessageController);
        app.get("/messages/{message_id}", this::getMessageByMessageIdController);
        app.delete("/messages/{message_id}", this::deleteMessageController);
        app.patch("/messages/{message_id}", this::updateMessageController);
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
            // Deserialize JSON body to Account object
            ObjectMapper mapper = new ObjectMapper();
            Account acc = mapper.readValue(ctx.body(), Account.class);

            accountService.registerValidation(acc);
            // Account registeredAccount = accountService.createAccount(acc);

            ctx.status(200).json(acc);
        } catch (SQLException e) {
            // ctx.status(400).json("SQL Exception occurred: " + e.getMessage());
            ctx.status(400);
        } catch (Exception e) {
            // ctx.status(400).json("Exception occurred: " + e.getMessage());
            ctx.status(400);
        }
    }

    private void loginController(Context ctx) throws JsonProcessingException{

        try {
            ObjectMapper mapper = new ObjectMapper();
            Account acc = mapper.readValue(ctx.body(), Account.class);

            // Validate input
            if (acc.getUsername() == null || acc.getUsername().trim().isEmpty() ||
                acc.getPassword() == null || acc.getPassword().isEmpty()) {
                // ctx.status(400).json("Username and password must be provided.");
                ctx.status(400);
                return;
            }

            // Perform login validation
            Account loggedInAccount = accountService.loginValidation(acc);
            if (loggedInAccount != null) {
                ctx.status(200).json(loggedInAccount);
            } else {
                // ctx.status(401).json("Invalid username or password.");
                ctx.status(401);
            }
        } catch (Exception e) {
            ctx.status(401).json("Login failed: " + e.getMessage());
        }

    }

    private void createMessageController(Context ctx) throws Exception  {
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(ctx.body(),Message.class);
        
        try {
            Account accountId = accountService.getAccountByUserId(message.getPosted_by());
            Message addMsg = messageService.createMessage(message, accountId);
            if(addMsg != null){
                ctx.json(mapper.writeValueAsString(addMsg));
            }
            else{
                ctx.status(400);
            }
        } catch (Exception e) {
            ctx.status(400);
        }
    }

    private void getAllMessagesController(Context ctx){
        List<Message> messages = messageService.ListMessages();
        ctx.json(messages);
    }

    // retrieve message by its id
    public void getMessageByMessageIdController(Context ctx){

        try {
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));
            Message msg = messageService.getMsgByMsgId(messageId);
            if(msg != null){

                ctx.json(msg);
                
            }else{
                ctx.status(200);
                ctx.result("");
            }
            
            } catch (Exception e) {
                ctx.status(200);
                ctx.result("");
            }
            // catch (Exception e){
            //     ctx.status(200);
            //     ctx.result("");
            // }

    }

    public void deleteMessageController(Context ctx){
        try {
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));
            Message message = messageService.getMsgByMsgId(messageId);
            if(message != null){
                messageService.deleteMessage(message);
                ctx.status(200);
                ctx.json(message);
            }else{
                ctx.status(200);
            }
            
        } catch (Exception e) {
            ctx.status(200);
        }
    }

    public void updateMessageController(Context ctx) throws JsonMappingException, JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(ctx.body(),Message.class);
        
        try {
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));
            message.setMessage_id(messageId);
            Message updatedMsg = messageService.updateMessage(message);
            ctx.json(updatedMsg);

        } catch (Exception e) {
            ctx.status(400);
        }
    }
}
// public void updateMessageTextController(Context ctx) {
//     try {
//         // Parse message_id from path parameter
//         int messageId = Integer.parseInt(ctx.pathParam("message_id"));

//         // Deserialize JSON body to a map or use directly if body contains just the new message text
//         String newMessageText = ctx.body();

//         // Update the message text
//         messageService.updateMessageText(messageId, newMessageText);

//         // Set HTTP response status 204 (No Content) for successful update
//         ctx.status(204);
//     } catch (NumberFormatException e) {
//         // Handle invalid message_id format
//         ctx.status(400).result("Invalid message_id");
//     } catch (Exception e) {
//         // Handle other exceptions
//         ctx.status(500).result("Internal server error");
//     }
// }
