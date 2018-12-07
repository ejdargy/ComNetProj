package MathGame;

/**
 * Created by Emma on 11/5/18.
 */
import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException {

        // getting localhost ip
        InetAddress ip = InetAddress.getByName("localhost");

        // set up server port 5056
        final int portNumber = 5056;

        try {
            Socket socket = new Socket(ip, portNumber);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String fromServer;
            String fromUser;

            //welcome message and then Ready question
            for (int i = 0; i < 2; i++) {
                fromServer = in.readLine();
                System.out.println(fromServer);
            }
            //responds if ready

            fromUser = stdIn.readLine();
            out.println(fromUser);
            out.flush();


            //states 1-12 (all 10 questions)
            while (true) {
                //receiving question or game is over
                fromServer = in.readLine();
                System.out.println(fromServer);
                if (fromServer.equals("Thank you for playing the Math Challenge! Come back soon!"))
                    break;
                if (fromServer.equals("One or more of our players has decided not play, so we will wait for more users to join.")) {
                    //get if ready question again
                    fromServer = in.readLine();
                    System.out.println(fromServer);
                    //say if ready
                    fromUser = stdIn.readLine();
                    out.println(fromUser);
                    out.flush();
                } else {

                    //sending answer
                    fromUser = stdIn.readLine();
                    out.println(fromUser);
                    out.flush();

                    fromUser.toLowerCase();
                    if (fromUser.equals("yes"))
                        continue;

                    //receiving if right or wrong & score standings
                    for (int i = 0; i < 2; i++) {
                        fromServer = in.readLine();
                        System.out.println(fromServer);
                    }
                }
            }
            //game is over, leave
            socket.close();
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + ip);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    ip);
            System.exit(1);
        }
    }
}
