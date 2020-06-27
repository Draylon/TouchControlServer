package com.objectdynamics;

import java.awt.*;
import java.awt.event.InputEvent;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    static Robot rob;
    boolean mouseLeft=false;
    boolean mouseRight=false;

    private boolean switchMouseLeft(){
        if(mouseLeft)mouseLeft=false;
        else mouseLeft=true;
        return mouseLeft;
    }
    private boolean switchMouseRight(){
        if(mouseLeft)mouseLeft=false;
        else mouseLeft=true;
        return mouseLeft;
    }
    static String[] s;
    static int ii=0;
    private static void printData(String ss){
        try{
            s=ss.split("-|;|,");
            if(s.length < 4) {
                System.err.println("ISSUE: "+ss);
                return;
            }
            int pointerID=Integer.parseInt(s[1]);
            if(pointerID!=0)
                return;
            int action=Integer.parseInt(s[0]);
            int x=Integer.parseInt(s[2]);
            //int y=Integer.parseInt(s[3]);
            int y=Integer.parseInt(s[3].substring(0,s[3].length()-1));
            //System.err.println(ii+"-> "+action+" "+pointerID+" ("+x+","+y+")");
            rob.mouseMove(x, y);
            switch (action){
                case 0:
                    rob.mousePress(InputEvent.BUTTON1_MASK);
                    break;
                case 3:
                case 1:
                    rob.mouseRelease(InputEvent.BUTTON1_MASK);
                    break;
            }
        ii++;
        } catch (Exception e){
            System.err.print(ss+ " ");
            System.err.println(e);

        }
    }

    public static void main(String[] args) {

        Socket client=null;
        try  {
            rob= new Robot();
            System.out.println("CONNECTING...");
            client = new Socket("127.0.0.1",5545);
            System.out.println("Connected!");
            Scanner ston = new Scanner(client.getInputStream());
            while(ston.hasNext()){
                printData(ston.next());
            }

        } catch (Exception e) {
            System.out.println("Erro!: "+e.getMessage());
        }
    }

}
