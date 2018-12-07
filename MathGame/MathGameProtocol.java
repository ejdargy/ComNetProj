package MathGame;


import java.net.*;
import java.io.*;
import java.util.*;

public class MathGameProtocol {

    //variables
    private static final int BEGINGAME = 1;

    private int state = 0;
    private int numbOfPlayers = 0;
    private int answer;
    private ThreadManager tm;
    private int[] points = {0, 0, 0, 0};
    private boolean replace = false;

    public MathGameProtocol(ThreadManager tm) {
        this.tm = tm;
    }

    public void newPlayer(ServerThread st)
    {
        if (replace) {
            System.out.println("replacing old player");
            replaceOldPlayer(st);
        } else
        {
            System.out.println("about to add player");
            addPlayer(st);
        }
    }


    public void replaceOldPlayer(ServerThread st)
    {
        numbOfPlayers++;
        int newNumb = tm.replacing();
        System.out.println(newNumb);
        String message = "WELCOME TO THE MATH CHALLENGE. We are waiting until there are 4 players to begin. You are player number: " + (newNumb + 1);
        tm.addPlayer(st, newNumb, message);
            if (numbOfPlayers == 4)
            {
                state = BEGINGAME;
                processInput(state);
            }
    }

    public void addPlayer(ServerThread st)
    {
        System.out.println("adding another player in addPlayer");
        numbOfPlayers++;
        String message = "WELCOME TO THE MATH CHALLENGE. We are waiting until there are 4 players to begin. You are player number: " + numbOfPlayers;
        tm.addPlayer(st, numbOfPlayers - 1, message);
        if (numbOfPlayers == 4) {
            state = BEGINGAME;
            processInput(state);
        }
    }

    public String[] generateOperators(int[] ops)
    {
        int size = ops.length;
        String[] operators = new String[size];

        for (int i = 0; i < size; i++) {
            if (ops[i] == 1)
                operators[i] = "+";
            else if (ops[i] == 2)
                operators[i] = "-";
        }

        return operators;
    }

    public String[] createString(int[] numbs, int[] ops)
    {
        int size = (numbs.length + ops.length) * 2;
        String[] question = new String[size];
        String[] operators;

        operators = generateOperators(ops);

        for (int i = 0; i < size; i++)
        {
            if (i % 4 == 0) {
                int k = i/4;
                question[i] = Integer.toString(numbs[k]);
            } else if (i % 2 == 0)
            {
                int k = i/4;
                question[i] = operators[k];
            } else
            {
                question[i] = " ";
            }
        }

        return question;
    }

    public int findAnswer(int[] numbs, int[] ops)
    {
        int answer = numbs[0];

        for (int i = 0; i < ops.length; i++)
        {
            if (ops[i] == 1)
            {
                answer += numbs[i+1];
            } else if (ops[i] == 2)
            {
                answer -= numbs[i+1];
            }
        }

        return answer;
    }

    public String generateQuestion()
    {
        Random rand = new Random();
        String[] question;
        String q = "";
        //10 possible numbers in question
        int numbOfnumbs = (int) (Math.random()*9) + 2;
        System.out.println("Number of numbers: " + numbOfnumbs);
        //one less number of operators
        int numbOfOps = numbOfnumbs - 1;
        System.out.println("Number of operators: " + numbOfOps);

        //create array to hold random numbers
        int[] randNumbs = new int[numbOfnumbs];
        int[] randOps = new int[numbOfOps];

        System.out.println("numbers in array: ");
        //fill arrays
        for (int i = 0; i < numbOfnumbs; i++)
        {
            randNumbs[i] = rand.nextInt(101) - 50;
            System.out.print(randNumbs[i] + " ");
        }

        System.out.println();
        System.out.println("Operator numbers (1 = +)(2 = -):");
        for (int i = 0; i < numbOfOps; i++)
        {
            randOps[i] = rand.nextInt(2) + 1;
            System.out.print(randOps[i]);
        }

        answer = findAnswer(randNumbs, randOps);
        System.out.println(answer);
        question = (createString(randNumbs, randOps));

        q += "Question " + (state -1) + ": ";

        for (int i = 0; i < question.length; i++)
        {
            q += question[i];
        }
        return q;
    }

    public void checkAnswers(String[] answers)
    {
        int userAnswer;
        String message;
        for (int i = 0; i < answers.length; i++)
        {
            try {
                userAnswer = Integer.parseInt(answers[i]);
                if (userAnswer == answer) {
                    points[i]++;
                    message = "That is correct! One point added.";
                } else {
                    message = "That is not correct! No points have been added.";
                }
            } catch (NumberFormatException e) {
                message = "That is not correct. You tried inputting a blank or non-integer.";
            }
            tm.sendSingleMessage(i, message);
        }
    }

    public boolean readyToBegin(String[] answers)
    {
        int[] notReady = {0, 0, 0, 0};
        boolean begin = true;
        boolean nullPoint = false;

        for (int i = 0; i < answers.length; i++) {
            try {
                answers[i].toLowerCase();
            } catch (NullPointerException e)
            {
                nullPoint = true;
            }
            if (nullPoint || !answers[i].equals("yes")) {
                replace = true;
                begin = false;
                notReady[i] = 1;
                state = 0;
                numbOfPlayers--;
            }
        }


        if (!begin)
        {
            for (int i = 0; i < notReady.length; i++)
            {
                if (notReady[i] == 1)
                {
                    tm.remove(i);
                }
            }
            tm.sendMessage("One or more of our players has decided not play, so we will wait for more users to join.");
        }

        return begin;
    }

    public void changeState()
    {
        state++;
        processInput(state);
    }

    public void scoreStandings()
    {
        String message;
        if (state != 11) {
            message = "Current Player Standings:";
        } else
        {
            message = "Final Player Standings:";
        }
        for (int i = 0; i < 4; i++)
        {
            message += "Player " + (i+1) + ": " + points[i] + " ";
        }
        tm.sendMessage(message);
    }

    public void processInput(int state) {

        String theOutput;
        String[] answers;
        boolean change = true;

        switch (state) {

            case 0:
                System.out.println("in state 0");
                change = false;
                break;

            case 1:
                theOutput = "LET'S BEGIN! Here's how it works... In this game you are going to be asked 10 algebra questions. Each player is given the same question. If you submit the correct answer, you receive one point. After the 10th question, the player with the highest number of points will be the winner. Are you ready for question one? Once all players reply with yes, we will begin. If you do not want to play anymore, please send no";
                tm.sendMessage(theOutput);
                answers = tm.receiveMessage();
                change = readyToBegin(answers);
                break;

            case 2:case 3:case 4:case 5:case 6:case 7:case 8:case 9:case 10:case 11:
                theOutput = generateQuestion();
                tm.sendMessage(theOutput);
                answers = tm.receiveMessage();
                checkAnswers(answers);
                scoreStandings();
                break;

            case 12:
                theOutput = "Thank you for playing the Math Challenge! Come back soon!";
                tm.sendMessage(theOutput);
                tm.closeConnections();
                break;

            default:
                break;
        }

        if (state < 12 && change) {
            changeState();
        }
        if (state == 2)
        {
            processInput(state);
        }
    }
}