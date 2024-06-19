package Service;

import java.util.List;

import DAO.MessageDAO;
import Model.Message;

public class MessageService {

    public static MessageDAO msgDAO;

    // constructor
    public MessageService(){
        msgDAO = new MessageDAO();
    }

    // assigning all available methods from MessageDAO to message service
    public MessageService(MessageDAO messageDAO){
        this.msgDAO = messageDAO;
    }

    // retrieve all messages
    public static List<Message> ListMessages(){
        return msgDAO.getAllMessages();
    }

    // create a message
    public Message createMessage(Message msg){
        return msgDAO.insertMessage(msg);
    }
    
}
