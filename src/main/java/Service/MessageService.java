package Service;

import java.util.List;

import DAO.AccountDAO;
import DAO.MessageDAO;
import Model.Account;
import Model.Message;

public class MessageService {

    private  MessageDAO msgDAO;
    private AccountDAO accDAO; 

    // constructor
    public MessageService(){
        msgDAO = new MessageDAO();
    }

    // assigning all available methods from MessageDAO to message service
    public MessageService(MessageDAO messageDAO){
        this.msgDAO = messageDAO;
    }

    // retrieve all messages
    public  List<Message> ListMessages(){
        return msgDAO.getAllMessages();
    }

    // create a message
    public Message createMessage(Message msg, Account acc) throws Exception{
        if(acc == null){
            throw new Exception("Account not existed.");
        }
        messageValidation(msg);
        if(msg.posted_by != acc.account_id){
            throw new Exception("posted_by id is not existed/authorized.");
        }
        try {
            Message createMsg = msgDAO.insertMessage(msg);
            return createMsg;
        } catch (Exception e) {
            throw new Exception("Create message failed.");
        }
        
    }

    public void messageValidation(Message message){
        if (message.getMessage_text() == null || message.getMessage_text().trim().isEmpty()) {
            throw new IllegalArgumentException("Message text must not be blank and must be less than 255 characters.");
        }
        if(message.getMessage_text().length() > 255){
            throw new IllegalArgumentException("Message must be less than 255 characters.");
        }
        // You can add additional validation here, such as checking if posted_by exists in the database
       
    }

    // private boolean checkIfUserExists(int userId) throws SQLException {
    //     Connection connection = ConnectionUtil.getConnection();
    //     String sql = "SELECT COUNT(*) FROM Account WHERE account_id = ?";
    //     try (PreparedStatement stmt = connection.prepareStatement(sql)) {
    //         stmt.setInt(1, userId);
    //         try (ResultSet rs = stmt.executeQuery()) {
    //             if (rs.next()) {
    //                 int count = rs.getInt(1);
    //                 return count > 0;
    //             }
    //         }
    //     }
    //     return false;
    // }

   
}
