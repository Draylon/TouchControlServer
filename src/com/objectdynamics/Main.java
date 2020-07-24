package com.objectdynamics;

import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Scanner;

enum MouseFunctionType{
    TRACKPAD,
    PEN;
}
enum MouseAutomaton{
    READY_LEFT,
    LEFT_DOWN,
    LEFT_UP,
    LEFT_CLICK,
    MOVE,
    READY_RIGHT,
    RIGHT_DOWN,
    RIGHT_UP,
    RIGHT_CLICK,
    SCROLL_DOWN,
    SCROLL_UP
}

class MouseController{
    private double x_bound;
    private double x_range;
    private double y_bound;
    private double y_range;
    private static Robot rob;
    private MouseFunctionType mtype;

    private int[] gestures_automaton;
    //estados: click  | drag | click-tap | click-drag | zoom in  | zoom out  | drag
    //actions: select | move |   rclick  | lclick-move| scrollUp | scrolldown| setProperties(boundaries and range)

    MouseController() throws AWTException {
        rob= new Robot();x_bound=0;x_range=0;y_bound=0;y_range=0;mtype=MouseFunctionType.PEN;
        for(int i=0;i < dataList.length;i++)
            dataList[i]=1;
    }

    private int[] dataList = new int[3];
    private int[][] coordList = new int[3][2];

    private static int ii=0;
    private boolean ld_flag=false;
    private boolean rd_flag=false;

    int pointerID;
    int action,x,y;

    public void proccessData(){

        if(pointerID < 0 || pointerID > 2)
            return;
        System.out.println(ii+"-> "+pointerID+" "+action+" ("+x+","+y+")");ii++;
        if(1==0){ // mtype == MouseFunctionType.PEN
            if(dataList[0] == 0){
                if(dataList[1] == 0){
                    if(dataList[2] == 0){

                    }
                    if(dataList[2] == 1){
                        ld_flag=false;
                        rd_flag=true;
                    }
                    if(dataList[2] == 2){

                    }
                }
                if(dataList[1] == 1){
                    if(dataList[2] == 0){

                    }
                    if(dataList[2] == 1){
                        automaton(MouseAutomaton.READY_LEFT);
                    }
                    if(dataList[2] == 2){

                    }
                }
                if(dataList[1] == 2){
                    if(dataList[2] == 0){

                    }
                    if(dataList[2] == 1){

                    }
                    if(dataList[2] == 2){

                    }
                }
            }
            if(dataList[0] == 1){
                if(dataList[1] == 0){
                    if(dataList[2] == 0){

                    }
                    if(dataList[2] == 1){

                    }
                    if(dataList[2] == 2){

                    }
                }
                if(dataList[1] == 1){
                    if(dataList[2] == 0){

                    }
                    if(dataList[2] == 1){
                        automaton(MouseAutomaton.LEFT_CLICK);
                        automaton(MouseAutomaton.RIGHT_CLICK);
                        automaton(MouseAutomaton.READY_RIGHT);
                    }
                    if(dataList[2] == 2){

                    }
                }
                if(dataList[1] == 2){
                    if(dataList[2] == 0){

                    }
                    if(dataList[2] == 1){

                    }
                    if(dataList[2] == 2){

                    }
                }
            }
            if(dataList[0] == 2){
                if(dataList[1] == 0){
                    if(dataList[2] == 0){

                    }
                    if(dataList[2] == 1){

                    }
                    if(dataList[2] == 2){

                    }
                }
                if(dataList[1] == 1){
                    if(dataList[2] == 0){

                    }
                    if(dataList[2] == 1){
                        automaton(MouseAutomaton.MOVE);
                    }
                    if(dataList[2] == 2){

                    }
                }
                if(dataList[1] == 2){
                    if(dataList[2] == 0){

                    }
                    if(dataList[2] == 1){
                        rob.mouseRelease(InputEvent.BUTTON1_MASK);
                        rob.mouseRelease(InputEvent.BUTTON2_MASK);
                    }
                    if(dataList[2] == 2){

                    }
                }
            }
        }

        /*0 1 1
        2 1 1
        2 1 1
        1 1 1*/

        //===========================================================

        //rezar pra não precisar retificar a fila de entrada
        /*rob.mousePress(InputEvent.BUTTON1_MASK);
        rob.mouseRelease(InputEvent.BUTTON1_MASK);*/
        return;
    }

    public void automaton(MouseAutomaton mouseAutomaton){
        rob.mouseMove(coordList[0][0], coordList[0][1]);//index has to be based on history
        switch(mouseAutomaton){
            case READY_LEFT:
                if(!ld_flag)
                    ld_flag=true;
                break;
            case READY_RIGHT:
                if(!rd_flag)
                    rd_flag=true;
                break;
            case MOVE:
                if(ld_flag)
                    automaton(MouseAutomaton.LEFT_DOWN);
                //if(rd_flag) rd_flag=false;
                break;
            case LEFT_DOWN:
                if(ld_flag)
                    rob.mousePress(InputEvent.BUTTON1_MASK);
                ld_flag=false;
                break;
            case LEFT_UP:
                rob.mouseRelease(InputEvent.BUTTON1_MASK);
                break;
            case LEFT_CLICK:
                if(rd_flag)break;
                rob.mousePress(InputEvent.BUTTON1_MASK);
                rob.mouseRelease(InputEvent.BUTTON1_MASK);
                break;
            case RIGHT_DOWN:
                rob.mousePress(InputEvent.BUTTON2_MASK);
                break;
            case RIGHT_UP:
                rob.mouseRelease(InputEvent.BUTTON2_MASK);
                break;
            case RIGHT_CLICK:
                if(!rd_flag)
                    break;
                rd_flag=false;
                rob.mousePress(InputEvent.BUTTON2_MASK);
                rob.mouseRelease(InputEvent.BUTTON2_MASK);
                break;
            case SCROLL_DOWN:
                rob.mouseWheel(5);
                break;
            case SCROLL_UP:
                rob.mouseWheel(-5);
        }
    }
}

public class Main {
    private void displayScreen(){
        /*Window w=new Window(null)
        {
            @Override
            public void paint(Graphics g)
            {
                final Font font = getFont().deriveFont(48f);
                g.setFont(font);
                g.setColor(Color.RED);
                final String message = "Hello";
                FontMetrics metrics = g.getFontMetrics();
                g.drawString(message,
                        (getWidth()-metrics.stringWidth(message))/2,
                        (getHeight()-metrics.getHeight())/2);
            }
            @Override
            public void update(Graphics g)
            {
                paint(g);
            }
        };
        w.setAlwaysOnTop(true);
        w.setBounds(w.getGraphicsConfiguration().getBounds());
        w.setBackground(new Color(0, true));
        w.setVisible(true);*/
    }
    static MouseController mouseController;

    private static int clipStringStep=0;
    private static String clipString="";
    private static void setClipString(int v){
        if(v==99){
            clipStringStep=0;clipString="";return;
        }else if(v==100){
            clipStringStep=0;clipString="";
            mouseController.proccessData();
            return;
        }else if(v > 47 && v < 58 ){
            clipString+=(char)v; //from 48 to 57
        }else if(v==10){/*idk actually*/
        }else if(v==59){//change variable
            switch (clipStringStep){
                case 0: mouseController.pointerID = Integer.parseInt(clipString);break;
                case 1: mouseController.action = Integer.parseInt(clipString);break;
                case 2: mouseController.x = Integer.parseInt(clipString);break;
                case 3: mouseController.y = Integer.parseInt(clipString);break;
            }
            clipStringStep++;
            clipString="";
        }else{
            System.err.println("UNKNOWN SYMBOL!! decimal: "+v);
        }
        //System.out.println(clipString+"=="+clipStringStep);
    }
    /*dataList[pointerID] = action;
        coordList[pointerID][0] = x;
        coordList[pointerID][1] = y;*/

    public static void main(String[] args) throws IOException {
        Socket client=null;
        try  {
            mouseController = new MouseController();
            System.out.println("CONNECTING...");
            client = new Socket("127.0.0.1",5545);
            if(client==null) {
                System.out.println("error connecting, closing now");
                return;
            }
            System.out.println("Connected! "+client.isConnected()+"\n"+client.toString());
            //Scanner ston = new Scanner(client.getInputStream());
            DataInputStream ston = new DataInputStream(client.getInputStream());
            while(true){
                //System.out.println("Stonks "+ (char)ston.readByte() );
                setClipString(ston.readByte());
                //mouseController.proccessData(ston.readUTF());
            }
        }catch (Exception e) {
            System.out.println("Erro!: "+e.getMessage());
        }
        if(client != null)
            try{
                client.close();
            }catch (Exception ex){
                System.err.println(ex);
            }
    }
}



/*boolean mouseLeft=false;
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
}*/