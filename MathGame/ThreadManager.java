package MathGame;

import java.util.ArrayList;

/**
 * Created by Emma on 10/8/18.
 */
public class ThreadManager {
    ArrayList<ServerThread> ThreadedSockets;
    private int actualSize = 0;

    public ThreadManager()
    {
        ThreadedSockets = new ArrayList<>();
    }

    public int getSize()
    {
        return actualSize;
    }

    public void addPlayer(ServerThread st, int playerNu, String message)
    {
        actualSize++;
        if (ThreadedSockets.size() == 4)
        {
            ThreadedSockets.set(playerNu, st);
        } else {
            ThreadedSockets.add(st);
        }
        sendSingleMessage(playerNu, message);
    }


    public void sendMessage(String message)
    {
        for (int i = 0; i < ThreadedSockets.size(); i++)
        {
            ServerThread t = ThreadedSockets.get(i);
            if (t != null) {
                t.printToUser(message);
            }
        }
    }

    public void sendSingleMessage(int player, String message)
    {
        System.out.println("got to Send Single Message");
        ServerThread t = ThreadedSockets.get(player);
        t.printToUser(message);
    }

    public String[] receiveMessage()
    {
        int size = ThreadedSockets.size();
        String[] answers = new String[size];

        for (int i = 0; i < size; i++)
        {
            ServerThread t = ThreadedSockets.get(i);
            answers[i] = t.getAnswer();
        }
        System.out.println("Got all answers.");
        return answers;
    }

    public void closeConnections()
    {
        for (int i = 0; i < ThreadedSockets.size(); i++)
        {
            ServerThread t = ThreadedSockets.get(i);
            t.close();
            ThreadedSockets.remove(i);
        }
    }

    public void remove(int playerNu)
    {
        ServerThread t = ThreadedSockets.get(playerNu);
        t.close();
        ThreadedSockets.set(playerNu, null);
        actualSize--;
    }

    public int replacing()
    {
        int newPlayerNumb = -1;
        for (int i = 0; i < ThreadedSockets.size(); i++)
        {
            ServerThread t = ThreadedSockets.get(i);
            if (t == null) {
                newPlayerNumb = i;
                break;
            }

        }
        return newPlayerNumb;
    }

}
