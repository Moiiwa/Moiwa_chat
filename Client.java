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
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            chatWindow=new ChatWindow();
            chatWindow.setVisible(true);
            chatWindow.readArea.append(in.readLine()+"\n");
            new ClientFunctions(in).start();
            while (!message.equalsIgnoreCase("Stop")){
                message=chatWindow.writeArea.getText();
                //out.write(message+"\n");
                //out.flush();
            }
            in.close();
            out.close();
            console.close();
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
                Client.chatWindow.readArea.append(reader.readLine() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ChatWindow extends JFrame{
    public TextArea readArea = new TextArea(9,64);
    public TextField writeArea = new TextField(64);
    public ChatWindow(){
        super("Moiwa-chat");
        this.setBounds(500,500,500,250);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e){
                try {
                    Client.out.write("stop\n");
                    Client.out.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        readArea.setEditable(false);
        Container container=this.getContentPane();
        container.setLayout(new FlowLayout());
        container.add(readArea);
        container.add(writeArea);
        writeArea.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Client.out.write(Client.message+"\n");
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
