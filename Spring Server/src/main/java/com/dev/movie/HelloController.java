
package com.dev.movie;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.*;
import java.io.*;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.net.UnknownHostException;

@RestController
@SpringBootApplication
public class HelloController {
    static String url_temp = "C:\\Users\\luraw\\OneDrive\\Desktop\\data\\movies_metadata.csv";
    static String ip = "127.0.0.1";
    static int port = 8080;
    static Socket socket = null;
    static ServerSocket server = null;  //서버 생성을 위한 ServerSocket
    static OutputStream out = null;
    static InputStream in = null;

    static BufferedWriter bw = null;
    static BufferedReader br = null;
    static ArrayList<String> lines;
    static String html;

    @RequestMapping("/movie")
    public String index() {
        return "index";
    }

    public static void main(String[] args) {
        handshake();
        sendDataFile();

        // get search_title

        System.out.println("");
        System.out.println("=== send search title ===");
        sendMsg("Heat");

        String msg = rcvMsg();

        ArrayList<String> lines = new ArrayList<String>();

        if(msg.equals("send_result")){
            System.out.println("=== download result ===");

            receiveResult(lines);
        }

        StringBuilder sb = new StringBuilder();

        for (String s : lines) {
            sb.append(s+"\n");
        }
        html = sb.toString();



        socketClose();

        SpringApplication.run(HelloController.class, args);
    }

    public static boolean handshake(){
        try {
            server = new ServerSocket(port);
            System.out.println("Accept : wait for client...");
            socket = server.accept();

            InetAddress inetaddr = socket.getInetAddress();
            System.out.println("Connect : " + inetaddr.getHostAddress());

            out = socket.getOutputStream();
            in = socket.getInputStream();

            bw = new BufferedWriter(new OutputStreamWriter(out));
            br = new BufferedReader(new InputStreamReader(in));

            String c_msg = br.readLine();

            System.out.println("Received : " + c_msg);

            if (c_msg.equals("client_ready")) {
                System.out.println("\n=== client ready ===");
            }

            String s_msg = "server_ready";

            bw.write(s_msg);
            bw.flush();

            System.out.println("Send : " + s_msg);

            c_msg = br.readLine();

            System.out.println("Received : " + c_msg);

            if(c_msg.equals("client_ACK")) {
                return true;
            }

        } catch(IOException e){
            e.printStackTrace();
        }

        return false;
    }

    public static boolean sendDataFile(){
        try{
            String c_msg;
            c_msg = br.readLine();

            System.out.println("Received : " + c_msg);

            if(c_msg.equals("down_start")){
                System.out.println("\n=== download start ===");

                fileSender(url_temp, bw);

                System.out.println("\n=== download end ===");
            }


            return true;

        } catch(IOException e){
            e.printStackTrace();
        }
        return false;
    }

    public static void receiveResult(ArrayList<String> lines){
        String msg ="";

        try {
            while (!((msg = br.readLine()).equals("!download_end"))) {
                lines.add(msg);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void fileSender(String url, BufferedWriter bw ){
        BufferedReader reader = null;

        try{
            reader = new BufferedReader(new FileReader(url));

            String line = "";

            while((line = reader.readLine())!=null){

                bw.write(line+"\n");
                bw.flush();
            }

            bw.write("\n");
            bw.flush();
            bw.write("!download_end");
            bw.flush();

        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean sendMsg(String msg){
        try{
            bw.write(msg);
            bw.flush();

            System.out.println("Send : " + msg);
        }catch(IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String rcvMsg(){
        String c_msg= "";
        try {
             c_msg = br.readLine();
        } catch(IOException e){
            e.printStackTrace();
        }
        return c_msg;
    }

    public static void socketClose(){
        try{
            socket.close();
            server.close();

            System.out.println("=== close ===");
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFile(){
        String message = "This is a sample message.\n";

        File file = new File("test1.txt");
        FileWriter writer = null;

        try {
            // 기존 파일의 내용에 이어서 쓰려면 true를, 기존 내용을 없애고 새로 쓰려면 false를 지정한다.
            writer = new FileWriter("C:\\Users\\luraw\\git_Repository\\movie_recommend\\movieRecommendation\\Spring Server\\src\\main\\resources\\templates\\index.html", true);

            for ( String s: lines) {
                writer.write(s+'\n');
                writer.flush();
            }

            System.out.println("DONE");
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(writer != null) writer.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
