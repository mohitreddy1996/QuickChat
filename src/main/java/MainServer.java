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
        HttpServer server = HttpServer.create(new InetSocketAddress(8025), 0);
        server.createContext("/Home", new Home());
        server.createContext("/UserName",new UserName());
        server.createContext("/Room", new Room());
        server.setExecutor(null);
        server.start();
    }
    static class UserName implements HttpHandler
    {
        public void handle(HttpExchange data) throws IOException
        {
            Map<String,String> M = queryMap(data.getRequestURI().getQuery());
            String Name = "Welcome " + M.get("userName") + " To QuickChat !!<br> ";
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

    public static void displayChatRoom(HttpExchange data,String S,int marker,String RoomNumber) throws IOException
    {
        if(marker==0)
        {
            int flag=1;
            for(int i=0;i<Rooms.size();i++)
            {
                if(ChatRoom.RoomCheck(RoomNumber,Rooms))
                {
                    S+=" Room Number" + RoomNumber + " Already Exists!!";
                    flag=0;
                    break;
                }
            }
            if(flag==1)
            {
                S+=" Room " + RoomNumber + " SuccessFully Created!!";
            }
        }
        else if(marker==1)
        {
            S += " Chats!!";
        }
        Map<String,String> M = queryMap(data.getRequestURI().getQuery());
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

    static class Room implements HttpHandler
    {
        public void handle(HttpExchange data) throws IOException
        {
            Map<String,String> M = queryMap(data.getRequestURI().getQuery());
            String S="";
            String QueryString = M.get("Room");
            String RoomNumber = M.get("RoomNumber");
            String UserName = M.get("userName");
            S += " Hi "+UserName+" <br>";
            S += " <form name=\"hidform\"> <input type=\"hidden\" id=\"username\" name=\"handle\" value='" + M.get("userName") + "'> </form> ";

            if(QueryString.equals("CreateNew"))
            {
                displayChatRoom(data,S,0,RoomNumber);
                return;
            }
            else if(QueryString.equals("Enter"))
            {
                displayChatRoom(data,S,1,RoomNumber);
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