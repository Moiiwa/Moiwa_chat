import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.*;
import java.io.*;
import java.awt.*;
public class Client {
    public static Socket socket;
    public static String message="";
    public static ChatWindow chatWindow;
    public static BufferedWriter out;
    public static void main(String[] args){
        try {
            byte adr[]=new byte[]{(byte)192,(byte)168,0,(byte)191};         //put your adress instead
            socket = new Socket(InetAddress.getByAddress(adr),1337);        //and port, if needed
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            chatWindow=new ChatWindow();                                    //creation of gui
            chatWindow.setVisible(true);                                    //making it visible
            chatWindow.readArea.append(in.readLine()+"\n");                 //write to readArea
            new ClientFunctions(in).start();                                //start new thread
            while (!message.equalsIgnoreCase("Stop")){                      //while keyword "Stop" was not sent
                message=chatWindow.writeArea.getText();                     //read text from writeArea
            }
            in.close();
            out.close();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
class ClientFunctions extends Thread{
    BufferedReader reader;
    public ClientFunctions(BufferedReader in){
        reader=in;
    }

    @Override
    public void run() {
        try {

            while (!Client.message.equalsIgnoreCase("stop"))
                Client.chatWindow.readArea.append(reader.readLine() + "\n");    //write message sent from server to readArea
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ChatWindow extends JFrame{                                //description of gui
    public TextArea readArea = new TextArea(9,64);              //readArea consists of 9 rows and 64 columns
    public TextField writeArea = new TextField(64);             //textArea of only 1 row and 64 columns
    public ChatWindow(){
        super("Moiwa-chat");                                    //title of the window
        this.setBounds(500,500,500,250);                        //description of size and coordinates of window
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e){
                try {
                    Client.out.write("stop\n");                 //when window is closed client send keyword to stop chatting
                    Client.out.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        readArea.setEditable(false);                           //we make readArea non-editable, to make users write only in writeArea
        Container container=this.getContentPane();      
        container.setLayout(new FlowLayout());                 //flowlayout is used because it looks more flexible than other layouts
        container.add(readArea);                               //add readArea and writeArea
        container.add(writeArea);
        writeArea.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Client.out.write(Client.message+"\n");      //when "Enter" button was pressed, send message to server and clean writeArea
                    Client.out.flush();
                    Client.chatWindow.writeArea.setText("");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        this.setVisible(true);
    }
}
