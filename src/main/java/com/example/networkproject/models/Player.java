package com.example.networkproject.models;

import java.awt.*;
import java.net.Socket;
import java.io.*;
import javax.swing.*;
import java.awt.event.*;

public class Player extends JFrame {
    private int width;
    private int height;
    private Container pane;
    private JTextArea message;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton button4;

    private int playerID;

    private int otherPlayer;
    private int[] values;
    private int maxTurns;
    private int myDamage;
    private int enemyDamage;
    private int turnsMade;
    private boolean buttonEnabled;

    private ClientSideConnection csc;
    public Player(int w , int h) {
        width = w;
        height = h;
        pane = this.getContentPane();
        message = new JTextArea();
        button1 = new JButton("1");
        button2 = new JButton("2");
        button3 = new JButton("3");
        button4 = new JButton("4");
        values = new int[4];
        turnsMade = 0;
        myDamage = 0;
        enemyDamage = 0;
    }

    public void setUpUI(){
        this.setSize(width,height);
        this.setTitle("Gladiator-Fight [Player " + playerID + "]");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        pane.setLayout(new GridLayout(1,5));
        pane.add(message);
        message.setText("Simple UDP Game.");
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
        message.setEditable(false);
        pane.add(button1);
        pane.add(button2);
        pane.add(button3);
        pane.add(button4);
        if(playerID == 1){
            message.setText("You go first.");
            otherPlayer = 2;
            buttonEnabled = true;
        }else {
            message.setText("You go second , wait for your turn.");
            otherPlayer = 1;
            buttonEnabled = false;
            Thread t = new Thread(new Runnable(){
                public void run(){
                    updateTurn();
                }
            });
            t.start();
        }
        toggleButton();
        this.setVisible(true);
    }
    public void connectToServer() throws IOException {
        csc = new ClientSideConnection();
    }
    private void winner(){
        buttonEnabled = false;
        if(myDamage > enemyDamage){
            message.setText("You won!\n" + "Your damage: " + myDamage + "\n" + "Enemy damage: " + enemyDamage);
        } else if (myDamage < enemyDamage) {
            message.setText("You Lose\n"+ "Your damage: " + myDamage + "\n" + "Enemy damage: " + enemyDamage);
        }else{
            message.setText("Tie" +"\n" + "Your damage: " + myDamage + "\n" + "Enemy damage: " + enemyDamage);
        }
        csc.closedConnection();
    }
    public void setUpButtons(){
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent ae){
                JButton b = (JButton) ae.getSource();
                int bNum = Integer.parseInt(b.getText());
                message.setText("You clicked " + bNum + " Now wait for Player " + otherPlayer);
                turnsMade++;
                System.out.println("Turns made: " + turnsMade);

                buttonEnabled = false;
                toggleButton();

                myDamage += values[bNum-1];
                System.out.println("Damage dealt: " + myDamage);
                csc.sendButtonNum(bNum);

                if (playerID == 2 && turnsMade == maxTurns){
                    winner();
                }else {
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            updateTurn();
                        }
                    });
                    t.start();
                }
            }
        };
        button1.addActionListener(al);
        button2.addActionListener(al);
        button3.addActionListener(al);
        button4.addActionListener(al);
    }
    public void toggleButton(){
        button1.setEnabled(buttonEnabled);
        button2.setEnabled(buttonEnabled);
        button3.setEnabled(buttonEnabled);
        button4.setEnabled(buttonEnabled);
    }

    public void updateTurn(){
        int n = csc.receiveButtonNum();
        message.setText("Your enemy clicked button "+ n + "Now your turn.");
        enemyDamage += values[n + 1];
        System.out.println("Your enemy dealt "+ enemyDamage + "in total.");
        buttonEnabled = true;
        if(playerID == 1 && turnsMade == maxTurns){
            winner();
        }else {
            buttonEnabled = true;
        }
        toggleButton();
    }



    private class ClientSideConnection{
        private Socket socket;
        private DataInputStream dataIn;
        private  DataOutputStream dataOut;

        public ClientSideConnection() throws IOException {
            System.out.println("Client started.");
            try {
                socket = new Socket("localhost" , 50001);
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
                playerID = dataIn.readInt();
                System.out.println("Connected to server as Player " + playerID + ".");
                maxTurns = dataIn.readInt() / 2;
                values[0] = dataIn.readInt();
                values[1] = dataIn.readInt();
                values[2] = dataIn.readInt();
                values[3] = dataIn.readInt();
                System.out.println("maxTurns is " + maxTurns);
                System.out.println("Value 1 is " + values[0]);
                System.out.println("Value 2 is " + values[1]);
                System.out.println("Value 3 is " + values[2]);
                System.out.println("Value 4 is " + values[3]);
            }catch (IOException ex){
                System.out.println("IOException for CSC");
            }


        }
        public  void sendButtonNum(int n){
            try{
                dataOut.writeInt(n);
                dataOut.flush();
            }catch(IOException exception){
                System.out.println("IOException for sendButtonNum.");
            }
        }
        public int receiveButtonNum(){
            int n = -1;
            try{
                n = dataIn.readInt();
                System.out.println("Player " + otherPlayer + "clicked button " + n);
            }catch (IOException exception){
                System.out.println("IOException for receiveButtonNum");
            }
            return n;
        }
        public void closedConnection(){
            try {
                socket.close();
                System.out.println("Connection closed.");
            }catch (IOException exception){
                System.out.println("IOException for closedConnection()");
            }
        }

    }





    public  static void main(String[] arg) throws IOException {
        Player a = new Player(500 , 200);
        a.connectToServer();
        a.setUpUI();
        a.setUpButtons();
    }
}
