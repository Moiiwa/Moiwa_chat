import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.lang.Thread;
import java.io.*;
import java.util.ArrayList;
public class Serv extends Thread {
    public static ServerSocket serverSocket;
    public static ArrayList<Socket> listOfSockets;
    public static ArrayList<BufferedReader> readers;
    public static ArrayList<BufferedWriter> writers;
    public static void main(String[] args){
        try {
            byte adr[]=new byte[]{(byte)192,(byte)168,0,(byte)191};                 //change adress and port if needed
            serverSocket = new ServerSocket(1337,40,InetAddress.getByAddress(adr)); // serverSocket is initialised
            listOfSockets = new ArrayList();                                        // list of connected socket initialised as new arraylist
            readers=new ArrayList<>();                                              //intialised list of reading streams
            writers=new ArrayList<>();                                              //initialised list of writing streams
            while(true){                                                            //infinite loop in main daemon thread
                Socket clientSocket = serverSocket.accept();                        //waiting for connection
                listOfSockets.add(clientSocket);                
                new ServerFunctions(clientSocket).start();                          //starts new thread for work with concrete socket
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
class ServerFunctions extends Thread {
    private Socket socket;
    private  BufferedReader in;                                                     //create socket's reader and writer
    private BufferedWriter out;                                                     
    public ServerFunctions(Socket socket) throws IOException{
        this.socket=socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        Serv.readers.add(in);
        Serv.writers.add(out);
    }
    String message="";
    @Override
    public void run(){
        int number=Serv.listOfSockets.size()-1;
        try {

            out.write("Welcome to server!\n");
            out.flush();
            while(true){
                if (message.equalsIgnoreCase("Stop")) {
                    in.close();
                    out.close();
                    Serv.writers.remove(number);
                    Serv.readers.remove(number);
                    Serv.listOfSockets.get(number).close();
                    Serv.listOfSockets.remove(number);
                    break;
                }else {
                    message = in.readLine();
                    for (int i = 0; i < Serv.listOfSockets.size(); i++) {
                        Serv.writers.get(i).write("User" + number + ": " + message + "\n");
                        Serv.writers.get(i).flush();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
