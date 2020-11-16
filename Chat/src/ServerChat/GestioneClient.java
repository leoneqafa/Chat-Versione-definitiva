//...
//Qafaloku Leone 5 B IA 
//Questa classe si occupa della gestione di client
//...
package ServerChat;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
public class GestioneClient implements Runnable
{
    private Socket client;
    private ChatServer chatServer;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private String name;
    public GestioneClient(String name, Socket client, ChatServer chatServer) //costruttore
    {
        this.client = client;
        this.chatServer = chatServer;
        this.name = name;
    }
    @Override
    public void run() 
    {
        try {
            initialiseStreams();
            listenForMessages();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void initialiseStreams() throws IOException 
    {
        bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        printWriter = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);
    }
    private void listenForMessages() throws IOException //metodo per la lettura del messaggio
    {
        chatServer.statusServer("server in attesa");
        while (chatServer.isActive()) 
        {
            String message = bufferedReader.readLine();

            if (message == null) 
            {
                closeAll();
                return;
            }
            //si occupa del controllo dei comandi e di un eventuale utilizzo di essi 
            switch (message.split(" ")[0]) 
            {
                case "/private":
                    whisperMessage(message);
                    break;
                case "/logout":
                    closeAll();
                    return;
                case "/list":
                    sendMessage(chatServer.activeClients(name).toString());
                    break;
                default:
                    chatServer.broadcast(name + ">> " + message, name);
                    break;
            }
        }
    }
    public void sendMessage(String message) 
    {
        printWriter.println(message);
    }
    private void closeAll() throws IOException 
    {
        try {
            chatServer.removeClient(this);
            client.close();
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        }
    }
    //in questa parte di codice rendiamo possibile la chat singola tra client e client
    private void whisperMessage(String message) 
    {
        String recipient = message.split(" ")[1];
        chatServer.statusServer("Cercando l'utente " + recipient + " per l'utente " + getName() + "...");
        for (int i = 0; i < chatServer.getClientList().size(); i++) {
            GestioneClient client = chatServer.getClientList().get(i);
            if (client.getName().equals(recipient)) {
                chatServer.statusServer("Utente trovato..." + '\n' + "Invio del messaggio all'utente " + client.getName() + "...");
                String[] messageArray = message.split(" ");
                String whisperedMessage = "";
                int messageIndex = 2;
                for (int x = messageIndex; x < messageArray.length; x++) 
                {
                    whisperedMessage += messageArray[x] + " ";
                }
                client.sendMessage(name + " ti ha scritto: " + whisperedMessage);
                sendMessage("Hai scritto a " + client.getName() + ": " + (message = whisperedMessage));
                break;
            }
            if(i == (chatServer.getClientList().size() - 1))
            {
                chatServer.statusServer("Utente non trovato...");
                sendMessage("L'utente da lei cercato non esiste");
            }
        }
    }

    public String getName() 
    {
        return name;
    }
}
