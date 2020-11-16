//...
//La seguente classe si occupa dell'avvio del Client client.start()
//...
package ClientChat;
import java.io.*;
public class ProgChatClient 
{
    public static void main(String[] args){
        ChatClient client = new ChatClient("localhost", 8888);
        client.start();
    }
}
