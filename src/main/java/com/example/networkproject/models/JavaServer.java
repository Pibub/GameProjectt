package com.example.networkproject.models;
import java.io.*;
import java.net.*;


public class JavaServer {
    private ServerSocket ss;
    private int numPlayer;
    private int turnsMade;
    private int maxTurns;
    private int[] values;
    private int playerOneButtonNum;
    private int playerTwoButtonNum;

    private ServerSideConnection player1;
    private ServerSideConnection player2;
    public JavaServer() throws IOException {
        System.out.println("----------Server is now started.----------");
        numPlayer = 0;
        turnsMade = 0;
        maxTurns = 4;
        values = new int[4];
        for(int i=0; i < values.length;i++){
            values[i] = (int) Math.ceil(Math.random() * 100);
            System.out.println("Value " + (i + 1) + "is" + values[i]);
        }
        ss = new ServerSocket(50001);
    }
    public void acceptConnection() throws IOException {
        System.out.println("----------Waiting for connection.----------");
        while(numPlayer < 2){
            Socket s = ss.accept();
            numPlayer++;
            System.out.println("Player " + numPlayer + " has connected.");
            ServerSideConnection ssc = new ServerSideConnection(s , numPlayer);
            if (numPlayer == 1){
                player1 = ssc;
            }else {
                player2 = ssc;
                System.out.println("Match is now started.");
            }
            Thread t = new Thread(ssc);
            t.start();
        }
    }


    private class ServerSideConnection implements Runnable {
        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        private int playerID;
        public  ServerSideConnection(Socket s , int id){
            socket = s;
            playerID = id;
            try {
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
            }catch (IOException exception){
                System.out.println("IOException From SSC");
            }

        }
        public void run(){
            try {
                dataOut.writeInt(playerID);
                dataOut.writeInt(maxTurns);
                dataOut.writeInt(values[0]);
                dataOut.writeInt(values[1]);
                dataOut.writeInt(values[2]);
                dataOut.writeInt(values[3]);
                dataOut.flush();
                while (true){
                    if(playerID == 1){
                        playerOneButtonNum = dataIn.readInt();
                        System.out.println("Player 1 clicked button " + playerOneButtonNum);
                    }else{
                        playerTwoButtonNum = dataIn.readInt();
                        System.out.println("Player 2 clicked button " + playerTwoButtonNum);
                    }
                    turnsMade++;
                    if(turnsMade == maxTurns){
                        System.out.println("Max turn has been reached.");
                        break;
                    }
                    player1.closeConnection();
                    player2.closeConnection();
                }
            }catch (IOException exception){
                System.out.println("IOException from run()");
            }
        }    public void sendButtonNum(int n){
            try {
                dataOut.writeInt(n);
                dataOut.flush();
            }catch (IOException exception){
                System.out.println("IOException for sendButtonNum.");
            }
        }
        public void closeConnection(){
            try {
                socket.close();
                System.out.println("Connection closed.");
            }catch (IOException exception){
                System.out.println("IOException for closeConnection.");
            }
        }
    }
    public static void main(String[] arg) throws IOException {
        JavaServer js = new JavaServer();
        js.acceptConnection();
    }
}
