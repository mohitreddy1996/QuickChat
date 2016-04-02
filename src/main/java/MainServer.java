import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import com.sun.xml.internal.ws.util.QNameMap;

/*
 * a simple static http server
*/
public class MainServer {


    static ArrayList<ChatRoom> Rooms = new ArrayList<ChatRoom>();

    public static void main(String[] args) throws Exception
    {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/Home", new Home());
        server.createContext("/UserName",new UserName());
        server.createContext("/Room", new Room());
        server.createContext("/Chat", new Chat());
        server.setExecutor(null);
        server.start();
    }


    static class Chat implements HttpHandler
    {
        public void handle(HttpExchange data) throws IOException
        {
            Map<String,String> M = queryMap(data.getRequestURI().getQuery());
            String userName = M.get("UserName");
            String RoomNumber = M.get("RoomNumber");
            String Query = M.get("ChatSubmit");
            String S = "";
            if(Query.equals("Chat"))
            {
                S+=" <h1> Chat </h1>";
            }
            else if(Query.equals("Exit"))
            {
                System.out.print("Hi!");
                for(int i=0;i<Rooms.size();i++)
                {
                    if(Rooms.get(i).name.equals(RoomNumber))
                    {
                        Rooms.get(i).Members.remove(userName);
                    }
                }
            }
            ChatBox(data,S,RoomNumber,userName);

        }
    }

    static class UserName implements HttpHandler
    {
        public void handle(HttpExchange data) throws IOException
        {
            Map<String,String> M = queryMap(data.getRequestURI().getQuery());
            String Name = "Welcome " + M.get("userName") + " To QuickChat !! ";
            String F = " <form name=\"hidform\"> <input type=\"hidden\" id=\"username\" name=\"handle\" value='" + M.get("userName") + "'> </form> ";
            Headers responseHeaders = data.getResponseHeaders();
            responseHeaders.set("Content-Type","text/html");
            BufferedInputStream Stream = new BufferedInputStream(new FileInputStream("/home/mohit/IdeaProjects/QuickChat/src/test/Chat.html"));
            int StreamLen = Stream.available();
            byte[] ByteStream = new byte[StreamLen];
            Stream.read(ByteStream,0,StreamLen);
            OutputStream OutStream = data.getResponseBody();
            data.sendResponseHeaders(200,StreamLen + Name.length()+F.length());

            OutStream.write(ByteStream);

            OutStream.write(Name.getBytes());
            OutStream.write(F.getBytes());
            OutStream.close();
        }
    }

    public static void ChatBox(HttpExchange data,String S,String RoomNumber,String SENDER) throws IOException
    {
        String UsersGroup="";
        UsersGroup+="<h3> Members :</h3>";
        for(int i=0;i<Rooms.size();i++)
        {
            if(Rooms.get(i).name.equals(RoomNumber))
            {
                for(int j=0;j<Rooms.get(i).Members.size();j++)
                {
                    UsersGroup+="<br> "+Rooms.get(i).Members.get(j);
                }
            }
        }
        Map<String,String> M = queryMap(data.getRequestURI().getQuery());
        Headers responseHeaders = data.getResponseHeaders();
        responseHeaders.set("Content-Type","text/html");
        String HiddenData1 = " <form name=\"hidform\"> <input type=\"hidden\" id=\"username\" name=\"UserName\" value='" + SENDER + "'> </form> ";
        String HiddenData2 = " <form name=\"hidform2\"> <input type=\"hidden\" id=\"roomnumber\" name=\"RoomNumber\" value='" + RoomNumber + "'> </form> ";
        BufferedInputStream Stream = new BufferedInputStream(new FileInputStream("/home/mohit/IdeaProjects/QuickChat/src/test/ChatBox.html"));
        int StreamLen = Stream.available();
        byte[] ByteStream = new byte[StreamLen];
        Stream.read(ByteStream,0,StreamLen);
        OutputStream OutStream = data.getResponseBody();
        data.sendResponseHeaders(200,StreamLen + S.length() + UsersGroup.length() + HiddenData1.length() + HiddenData2.length());

        OutStream.write(ByteStream);
        OutStream.write(S.getBytes());
        OutStream.write(UsersGroup.getBytes());
        OutStream.write(HiddenData1.getBytes());
        OutStream.write(HiddenData2.getBytes());

        OutStream.close();
    }

    public static void displayChatRoom(HttpExchange data,String S,int marker,String RoomNumber,String SENDER) throws IOException
    {
        String UsersGroup="";
        if(marker==0)
        {
            int flag=1;
            for(int i=0;i<Rooms.size();i++)
            {
                if(ChatRoom.RoomCheck(RoomNumber,Rooms))
                {
                    S+=" <h1>Room Number" + RoomNumber + " Already Exists!! </h1> ";
                    flag=0;
                    break;
                }
            }
            if(flag==1)
            {
                S+=" <h1> Room " + RoomNumber + " SuccessFully Created!! </h1>";
                Rooms.add(new ChatRoom(RoomNumber));
            }
        }
        else if(marker==1)
        {
            if(ChatRoom.RoomCheck(RoomNumber,Rooms)==false)
            {
                S+=" <h1> Room " + RoomNumber + " Doesn't Exist !! </h1>";
            }
            else
            {
                for(int i=0;i<Rooms.size();i++)
                {
                    if(Rooms.get(i).name.equals(RoomNumber))
                    {
                        Rooms.get(i).Members.add(SENDER);
                    }
                }

                ChatBox(data,S,RoomNumber,SENDER);
                return;
            }
        }
        Map<String,String> M = queryMap(data.getRequestURI().getQuery());
        Headers responseHeaders = data.getResponseHeaders();
        responseHeaders.set("Content-Type","text/html");
        BufferedInputStream Stream = new BufferedInputStream(new FileInputStream("/home/mohit/IdeaProjects/QuickChat/src/test/Room.html"));
        int StreamLen = Stream.available();
        byte[] ByteStream = new byte[StreamLen];
        Stream.read(ByteStream,0,StreamLen);
        OutputStream OutStream = data.getResponseBody();
        data.sendResponseHeaders(200,StreamLen + S.length() + UsersGroup.length());

        OutStream.write(ByteStream);
        OutStream.write(S.getBytes());
        OutStream.write(UsersGroup.getBytes());

        OutStream.close();

    }

    static class Room implements HttpHandler
    {
        public void handle(HttpExchange data) throws IOException
        {
            Map<String,String> M = queryMap(data.getRequestURI().getQuery());
            String S="";
            String QueryString = M.get("Room");
            String RoomNumber = M.get("RoomNumber");
            String UserName = M.get("userName");
            S += " <h2> Hi "+UserName+" </h2> <h2> Welcome To QuickChat </h2>";


            if(QueryString.equals("CreateNew"))
            {
                displayChatRoom(data,S,0,RoomNumber,UserName);
                return;
            }
            else if(QueryString.equals("Enter"))
            {
                displayChatRoom(data,S,1,RoomNumber,UserName);
                return;
            }
            else
            {
                S += " Error!!";
            }
            Headers responseHeaders = data.getResponseHeaders();
            responseHeaders.set("Content-Type","text/html");
            BufferedInputStream Stream = new BufferedInputStream(new FileInputStream("/home/mohit/IdeaProjects/QuickChat/src/test/Room.html"));
            int StreamLen = Stream.available();
            byte[] ByteStream = new byte[StreamLen];
            Stream.read(ByteStream,0,StreamLen);
            OutputStream OutStream = data.getResponseBody();
            data.sendResponseHeaders(200,StreamLen + S.length());

            OutStream.write(ByteStream);
            OutStream.write(S.getBytes());

            OutStream.close();
        }
    }


    public static void ViewHome(HttpExchange data) throws IOException
    {

        Headers responseHeaders = data.getResponseHeaders();
        responseHeaders.set("Content-Type","text/html");
        BufferedInputStream Stream = new BufferedInputStream(new FileInputStream("/home/mohit/IdeaProjects/QuickChat/src/test/Home.html"));
        int StreamLen = Stream.available();
        byte[] ByteStream = new byte[StreamLen];
        Stream.read(ByteStream,0,StreamLen);
        OutputStream OutStream = data.getResponseBody();
        data.sendResponseHeaders(200,StreamLen);
        OutStream.write(ByteStream);
        OutStream.close();

    }

    static class Home implements HttpHandler
    {
        public void handle(HttpExchange data) throws IOException
        {
            ViewHome(data);
        }
    }

    public static Map<String, String> queryMap(String query){
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length>1) {
                result.put(pair[0], pair[1]);
            }else{
                result.put(pair[0], "");
            }
        }
        return result;
    }




}