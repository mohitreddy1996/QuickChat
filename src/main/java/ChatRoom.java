import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.ArrayList;

public class ChatRoom
{
    ArrayList<String> Members = new ArrayList<String>();

    String name = "";

    ArrayList<Chats> chats = new ArrayList<Chats>();

    ChatRoom(String name)
    {
        this.name = name;
    }

    static boolean RoomCheck(String roomName , ArrayList<ChatRoom> chatRooms)
    {
        for(int i=0;i<chatRooms.size();i++)
        {
            if(chatRooms.get(i).name.equals(roomName))
            {
                return true;
            }
        }

        return false;
    }

}