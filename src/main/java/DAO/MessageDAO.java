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

    // get a message by its id
    public Message getMessageByItsId(int id) throws SQLException{
        Connection connect = ConnectionUtil.getConnection();
        Message message = null;
        String sql = "SELECT * FROM message WHERE message_id = ?";
        PreparedStatement preparedStatement = connect.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        ResultSet rs = preparedStatement.executeQuery();    
        try {
            if (rs.next()) {
                int messageId = rs.getInt("message_id");
                int postedBy = rs.getInt("posted_by");
                String textMessage = rs.getString("message_text");
                long timePostedEpoch = rs.getLong("time_posted_epoch");
                message = new Message(messageId, postedBy, textMessage, timePostedEpoch);
            }
        } catch (Exception e) {
            throw new SQLException("Error occured while retrieving message by its id.");
        }
        return message;
    }

    // delete a messag by its id
    public void deleteMessageByItsId(Message msg) throws Exception{
        Connection connect = ConnectionUtil.getConnection();
        // Message message = null;
        String sql = "DELETE FROM message WHERE message_id = ?";
        PreparedStatement preparedStatement = connect.prepareStatement(sql);
        try {
            preparedStatement.setInt(1, msg.getMessage_id());
            preparedStatement.executeUpdate();  
        } catch (Exception e) {
            throw new Exception("Error occured while deleting a message by its id", e);
        }
        
    }

    // @Override
    // public boolean delete(Message message) {
    //     String sql = "DELETE FROM message WHERE message_id = ?";
    //     int rowsUpdated = 0;
    //     Connection conn = ConnectionUtil.getConnection();
    //     try (PreparedStatement ps = conn.prepareStatement(sql)) {
    //         ps.setInt(1, message.getMessage_id());
    //         rowsUpdated = ps.executeUpdate();
    //     } catch (SQLException e) {
    //         handleSQLException(e, sql, "Error while deleting the message with id: " + message.getMessage_id());
    //     }
    //     return rowsUpdated > 0;
    // }
    
}

