import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.ArrayList;

public class Chats
{
    private String Message;
    private String Sender;

    public Chats(String Message,String Sender)
    {
        this.Message = Message;
        this.Sender = Sender;
    }

    public String getMessage()
    {
        return this.Message;
    }
    public String getSender()
    {
        return this.Sender;
    }
}