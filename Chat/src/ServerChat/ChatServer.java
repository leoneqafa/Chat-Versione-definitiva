//...
//Qafaloku Leone 5 B IA
//In questa classe ci occupiamo del server
//...
package ServerChat; 
import ClientChat.ChatClient; 
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
//...
//Riassunto di questo metodo: Il server si mette in attesa e si avvia, viene preso il nome e visualizzato 
//broadcast con eventuale controllo di nomi già esistenti e si visualizzano i Client che si collegano.
//...
public class ChatServer 
{
    private Scanner input = new Scanner(System.in);
    public List<GestioneClient> clients;
    private ServerSocket serverSocket;
    private String nomeClient;
    public PrintStream output = null;
    public void start(){
        try {
            int portNumber = 8888;
            serverSocket = new ServerSocket(portNumber); //creazione effettiva del socket
            ExecutorService threadPool = Executors.newCachedThreadPool();
            clients = new LinkedList<>();
            InetAddress ipAddress = InetAddress.getLocalHost();
            System.out.println("esecuzione avvenuta con successo connettere alla porta "   + portNumber + " (IP: " + ipAddress.toString() + ").\n"); //il server avvisa che in stato di pronto
            while (!serverSocket.isClosed()) 
            {
                try {
                    Socket clientSocket = serverSocket.accept();
                    BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    output = new PrintStream(clientSocket.getOutputStream());
                    output.println(String.format("nome: ")); 
                    nomeClient = input.readLine().trim();
                    for (int i = 0; i < clients.size(); i++) {                               
                        GestioneClient client = clients.get(i);
                        if (client.getName().equals(nomeClient)) {
                            statusServer("L'utente ha inserito un nome già esistente...");
                            output.println(String.format("Il nome inserito esiste già." + '\n' + "Inserire un altro nome:"));
                            statusServer("Aspettando che il client dia il suo nome...");
                            nomeClient = input.readLine().trim();
                            i = -1;
                        }
                    }                                               
                    for (GestioneClient client : clients) { //avviso delle connessioni
                        client.sendMessage("L'utente " + nomeClient + " è entrato nella chat");
                    }
                    System.out.println(nomeClient + " si è connesso alla chat.");
                    output.println(String.format("Benvenuto nella chat" + '\n'));
                    GestioneClient client = new GestioneClient(nomeClient, clientSocket, this);
                    threadPool.submit(client);
                    clients.add(client);
                }catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    //Questo metodo si occupa a spedire i messaggi e controllare eventuali comandi inseriti
    public void broadcast(String message, String nome) 
    {
        synchronized (clients) {
            statusServer("Ricevuto un messaggio da " + nome + "...");
            for (GestioneClient client : clients) 
            {
                if(clients.size() == 1)
                {
                    output.println(String.format("C'è un solo client"));
                    break;
                }
                client.sendMessage(message);
            }
            statusServer("Inoltrando il messaggio a tutti gli utenti...");
        }
    }
    public boolean isActive() 
    {
        return !serverSocket.isClosed();
    }
    public void removeClient(GestioneClient client) 
    {
        synchronized (clients) 
        {
            String nome = client.getName();
            statusServer("Rimuovendo " + client.getName() + " dalla chat...");
            statusServer("Rimosso " + client.getName() + " dalla chat...");
            clients.remove(client);
            for (GestioneClient c : clients) 
            {  
                c.sendMessage("L'utente " + nome + " è uscito dalla chat");
            }
        }
    }
    //..
    //si ha la visualizzazione dei client connessi al server
    //...
    public StringBuilder activeClients(String nome) 
    {
        statusServer("Mostrando all'utente " + nome + " gli utenti connessi...");
        StringBuilder list = new StringBuilder("Utenti loggati: \n");
        for (GestioneClient client : clients) {
            list.append(client.getName()).append("\n");
        }
        return list;
    }
    public List<GestioneClient> getClientList() 
    {
        return clients;
    }
    public void statusServer(String m)
    {
        System.out.println(m);
    }
}
