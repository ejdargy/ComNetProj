package MathGame;

import java.net.*;
import java.io.*;
import java.util.*;

public class Server {

    public static void main(String[] args) throws IOException {
        int portNumber = 5056;
        ThreadManager tm = new ThreadManager();
        final int PLAYERMAX = 4;
        MathGameProtocol mgp = new MathGameProtocol(tm);

        boolean listening = true;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (listening) {
                if (tm.getSize() != PLAYERMAX)
                {
                    ServerThread st = new ServerThread(serverSocket.accept());
                    st.start();
                    mgp.newPlayer(st);
                } else
                {
                    System.out.println("full but player tried to join.");
                }
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }
}