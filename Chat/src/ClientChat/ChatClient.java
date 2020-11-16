//...
//Qafaloku Leone
//In questa classe si hanno tutti i metodi che successivamente verranno richiamati da: GestioneClient
//...
package ClientChat;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
public class ChatClient{
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader chiave;
    private String nome;
    public ChatClient(String indirizzo, int numporta) //metodo che si occupa della creazione del Socket
    {
        try 
        {
            clientSocket = new Socket(indirizzo, numporta);
            setupStreams();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    public void setupStreams() throws IOException 
    {
        out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        chiave = new BufferedReader(new InputStreamReader(System.in));
    }
    public void sendMessage() throws IOException 
    {
        while (!clientSocket.isClosed()) {
            String message = chiave.readLine();
            out.println(message);
        }
    }
    public void receiveMessage() throws IOException 
    {
        while (!clientSocket.isClosed()) 
        {
            String message = in.readLine();
            if (message == null) 
            {
                System.exit(0);
            }
            System.out.println(message);
        }
    }
    public String getNome()
    {
        return this.nome;
    }
    public void start() 
    {
        Thread thread = new Thread(new Runnable() 
        {
            @Override
            public void run() 
            {
                try 
                {
                    receiveMessage();
                } catch (IOException e) 
                {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try 
        {
            sendMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
