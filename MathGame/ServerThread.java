package MathGame;

/**
 * Created by Emma on 10/1/18.
 */
import java.net.*;
import java.io.*;

public class ServerThread extends Thread {
    public Socket socket = null;
    private PrintWriter out;
    private BufferedReader in;


    public ServerThread(Socket socket) {
        super("ServerThread");
        this.socket = socket;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true)
        {

        }
    }

    public void printToUser(String message)
    {
        System.out.println("got to PrintToUser");
        out.println(message);
        out.flush();
    }

    public String getAnswer()
    {
        String answer;
        try {
            answer = in.readLine();
        } catch (IOException e)
        {
            answer = "error";
            out.println("error in reading user info... no points for you this question :(");
            out.flush();
        }

        return answer;
    }


    public void close()
    {
        try {
            socket.close();
        } catch(IOException e)
        {
            out.println("error in trying to close socket connection");
            out.flush();
            e.printStackTrace();
        }
    }

}