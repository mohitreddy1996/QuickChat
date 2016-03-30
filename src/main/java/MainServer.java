import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

/*
 * a simple static http server
*/
public class MainServer {

    public static void main(String[] args) throws Exception
    {
        HttpServer server = HttpServer.create(new InetSocketAddress(8117), 0);
        server.createContext("/Home", new Home());
        server.createContext("/UserName",new UserName());
        server.setExecutor(null);
        server.start();
    }
    static class UserName implements HttpHandler
    {
        public void handle(HttpExchange data) throws IOException
        {
            Map<String,String> M = queryMap(data.getRequestURI().getQuery());
            String Name = "Hi " + M.get("userName") + "<br> ";

            Headers responseHeaders = data.getResponseHeaders();
            responseHeaders.set("Content-Type","text/html");
            BufferedInputStream Stream = new BufferedInputStream(new FileInputStream("/home/mohit/IdeaProjects/QuickChat/src/test/Chat.html"));
            int StreamLen = Stream.available();
            byte[] ByteStream = new byte[StreamLen];
            Stream.read(ByteStream,0,StreamLen);
            OutputStream OutStream = data.getResponseBody();
            data.sendResponseHeaders(200,StreamLen + Name.length());

            OutStream.write(ByteStream);
            OutStream.write(Name.getBytes());
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
        System.out.print("Hurray");
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