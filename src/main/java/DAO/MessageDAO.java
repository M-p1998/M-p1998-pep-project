package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Model.Message;
import Util.ConnectionUtil;

public class MessageDAO {
    // retrieve all messages.
    public List<Message> getAllMessages(){
        Connection connection = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();
        try {
            String sql = "SELECT * FROM message;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                Message msg = new Message(rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch"));
                messages.add(msg);

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return messages;
    }

    // create a message
    public Message insertMessage(Message msg){
        Connection connect = ConnectionUtil.getConnection();
        Message message = null;
        try {
            String sql = "INSERT INTO message (posted_by,message_text,time_posted_epoch) VALUES (?,?,?)";
            PreparedStatement preparedStatement = connect.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setInt(1, msg.getPosted_by());
            preparedStatement.setString(2, msg.getMessage_text());
            preparedStatement.setLong(3, msg.getTime_posted_epoch());
            int affectedRows = preparedStatement.executeUpdate();

            if( affectedRows > 0){
                try(ResultSet generatedKeys = preparedStatement.getGeneratedKeys()){
                    if (generatedKeys.next()) {
                        
                        int messageId = generatedKeys.getInt(1);
                        // msg.setMessage_id(generatedKeys.getInt(1));
                        message = new Message(messageId, msg.getPosted_by(),msg.getMessage_text(),msg.getTime_posted_epoch());
                    }
                }
            }
            

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return message;
    }

    // public List<Message> getAllMessages(){
    //     Connection connect = ConnectionUtil.getConnection();
    //     String sql = "SELECT * FROM message;";
    //     List<Message> messages = new ArrayList<>();
    //     PreparedStatement stmt = connect.prepareStatement(sql);
    //     ResultSet rs = stmt.executeQuery();
    //     try {
    //         while(rs.next()){
    //             Message msg = new Message(rs.getInt("message_id"),
    //                     rs.getInt("posted_by"),
    //                     rs.getString("message_text"),
    //                     rs.getLong("time_posted_epoch")
    //             );
    //         }
    //     } catch (Exception e) {
    //         throw new Exception("Error occured while retrieving all messages.");
    //     }
    //     return messages;
    // }


    
}

