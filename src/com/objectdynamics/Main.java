package com.objectdynamics;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.io.*;
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

class TranslucentWindowMin extends JFrame {
    public TranslucentWindowMin() {
        setTitle("Translucent Window Example");
        setSize(300,300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        // Set the window to 45% opaque = 55% translucent
        setOpacity(0.45f);
    }
    public void displayWindow(){
        JFrame.setDefaultLookAndFeelDecorated(true);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TranslucentWindowMin().setVisible(true);
            }
        });
    }
}

class WindowsDrawingFrame{
    Robot rob;
    int x,y,width,height,tabletWidth,tabletHeight;
    double xScaleFactor,yScaleFactor;
    Rectangle captureRegion;
    BufferedImage capturedImage;
    WindowsDrawingFrame(Robot robot,int x,int y,int tWidth,int tHeight){
        this.rob=robot;
        this.tabletWidth=tWidth;
        this.tabletHeight=tHeight;
        this.x=x;
        this.y=y;
        this.width=640;
        this.height=480;
        this.xScaleFactor = tWidth/this.width;
        this.yScaleFactor=tHeight/this.height;
        this.captureRegion = new Rectangle(this.x,this.y,this.width,this.height);
    }
    public int solveX(int x){
        return ((int)xScaleFactor*x);
    }
    public int solveY(int y){
        return ((int)yScaleFactor*y);
    }
    public void updateWindowFactor(int x,int y,int width,int height){
        this.width=width;this.height=height;this.x=x;this.y=y;
        this.xScaleFactor=tabletWidth/(this.width-this.x);
        this.yScaleFactor=tabletHeight/(this.height-this.y);
        this.captureRegion = new Rectangle(this.x,this.y,this.width,this.height);
    }
    public DataBuffer getScreen() throws IOException {
        capturedImage = rob.createScreenCapture(this.captureRegion);
        //byte[] imageBytes = ((DataBufferByte) bufferedImage.getData().getDataBuffer()).getData();
        return capturedImage.getRaster().getDataBuffer();
    }
}

class MouseController{
    private double x_bound;
    private double x_range;
    private double y_bound;
    private double y_range;
    private static Robot rob;
    private MouseFunctionType mtype;
    private WindowsDrawingFrame windowsDrawingFrame;

    private int[] gestures_automaton;
    //estados: click  | drag | click-tap | click-drag  | zoom in  |  zoom out  | drag
    //actions: select | move |   rclick  | lclick-move | scrollUp | scrolldown | setProperties(boundaries and range)

    MouseController(WindowsDrawingFrame drawingFrame,Robot rob) throws AWTException {
        windowsDrawingFrame=drawingFrame;this.rob=rob;x_bound=0;x_range=0;y_bound=0;y_range=0;mtype=MouseFunctionType.PEN;
        for(int i=0;i < dataList.length;i++)
            dataList[i]=1;
    }
    public boolean warningFlag=false;
    public int[] dataList = new int[5];
    public int[][] coordList = new int[dataList.length][2];

    private boolean ld_flag=false;
    private boolean rd_flag=false;
    private boolean[] dlMoving = new boolean[dataList.length];

    public void proccessData(){
        if(warningFlag){warningFlag=false;System.err.println("WARNINGFLAG!!!");return;}

        System.out.println("===========");
        for(int ii=0;ii < dataList.length;ii++){
            System.out.println("Touch: "+ii+" | action: "+dataList[ii]+" | X:"+coordList[ii][0]+" Y:"+coordList[ii][1]);
        }

        if(mtype == MouseFunctionType.PEN){
            if(dataList[0] == 0){
                if(dataList[1] == 0){
                    if(dataList[2] == 0){

                    }
                    if(dataList[2] == 1){
                        automaton(MouseAutomaton.READY_RIGHT);
                    }
                }
                if(dataList[1] == 1){
                    if(dataList[2] == 0){

                    }
                    if(dataList[2] == 1){
                        automaton(MouseAutomaton.READY_LEFT);
                    }
                }
            }
            if(dataList[0] == 1){
                if(dataList[1] == 0){
                    if(dataList[2] == 0){
                        automaton(MouseAutomaton.RIGHT_UP);
                    }
                    if(dataList[2] == 1){

                    }
                }
                if(dataList[1] == 1){
                    if(dataList[2] == 0){

                    }
                    if(dataList[2] == 1){
                        automaton(MouseAutomaton.LEFT_UP);
                        automaton(MouseAutomaton.RIGHT_UP);
                        automaton(MouseAutomaton.LEFT_CLICK);
                        automaton(MouseAutomaton.RIGHT_CLICK);
                    }
                }
            }
            if(dataList[0] == 2){
                if(dataList[1] == 0){   //start calculating vectors
                    if(dataList[2] == 0){

                    }
                    if(dataList[2] == 1){
                        automaton(MouseAutomaton.MOVE);
                    }
                }
                if(dataList[1] == 1){
                    if(dataList[2] == 0){

                    }
                    if(dataList[2] == 1){
                        automaton(MouseAutomaton.MOVE);
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
        switch(mouseAutomaton){
            case READY_LEFT:
                if(!ld_flag){
                    ld_flag=true;rd_flag=false;}
                break;
            case READY_RIGHT:
                if(!rd_flag){
                    rd_flag=true;ld_flag=false;}
                break;
            case MOVE:
                if(ld_flag)
                    automaton(MouseAutomaton.LEFT_DOWN);
                if(rd_flag)
                    automaton(MouseAutomaton.RIGHT_DOWN);
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
                if(!ld_flag)break;
                ld_flag=false;
                rob.mousePress(InputEvent.BUTTON1_MASK);
                rob.mouseRelease(InputEvent.BUTTON1_MASK);
                break;
            case RIGHT_DOWN:
                if(rd_flag)
                    rob.mousePress(InputEvent.BUTTON3_MASK);
                rd_flag=false;
                break;
            case RIGHT_UP:
                rob.mouseRelease(InputEvent.BUTTON3_MASK);
                break;
            case RIGHT_CLICK:
                if(!rd_flag) break;
                rd_flag=false;
                rob.mousePress(InputEvent.BUTTON3_MASK);
                rob.mouseRelease(InputEvent.BUTTON3_MASK);
                break;
            case SCROLL_DOWN:
                rob.mouseWheel(5);
                break;
            case SCROLL_UP:
                rob.mouseWheel(-5);
        }
        rob.mouseMove(this.windowsDrawingFrame.solveX(coordList[0][0]), this.windowsDrawingFrame.solveY(coordList[0][1]));//index hasn't to be based on history
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
    static WindowsDrawingFrame windowsDrawingFrame;
    static Robot rob;

    private static int clipStringStep=0;
    private static String clipString="";
    private static int tempID=-1;
    private static boolean workData=false;

    private static void bindScreenServer(){
        Thread t = new Thread(new Runnable() {
            Socket client;
            DataOutputStream dOs;
            @Override
            public void run() {
                try {
                    client = new Socket("127.0.0.1",5546);
                    if(client==null){ System.out.println("Screen server error");return; }
                    dOs = new DataOutputStream(client.getOutputStream());
                    while (client.isConnected()){
                        dOs.writeBytes(windowsDrawingFrame.getScreen().toString());
                        dOs.flush();
                        Thread.sleep(66);
                    }
                    dOs.close();
                    client.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }
    private static void bindTouchServer(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Socket client;
                try{
                    client = new Socket("127.0.0.1",5545);
                    if(client==null) {
                        System.out.println("error connecting, closing now");
                        return;
                    }

                    System.out.println("Connected! "+client.isConnected()+"\n"+client.toString());
                    //Scanner ston = new Scanner(client.getInputStream());
                    DataInputStream ston = new DataInputStream(client.getInputStream());
                    while(client.isConnected()){
                        //System.out.println("Stonks "+ (char)ston.readByte() );
                        setClipString(ston.readByte());
                        //mouseController.proccessData(ston.readUTF());
                    }
                    if(client != null)
                        try{
                            client.close();
                        }catch (Exception ex){
                            System.err.println(ex);
                        }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    private static void setClipString(int v){
        if(v==122){//pairing data
            workData=true;
            clipStringStep=0;clipString="";return;
        }
        if(v==120){//end pairing
            workData=false;
            clipStringStep=0;clipString="";return;
        }
        if(v==99||v==124){
            clipStringStep=0;clipString="";return;
        }else if(v==100){
            clipStringStep=0;clipString="";
            mouseController.proccessData();
            return;
        }else if((v > 47 && v < 58) || v==45){
            clipString+=(char)v; //from 48 to 57
        }else if(v==10){/*idk actually*/
        }else if(v==59){//change variable
            if(workData){
                switch (clipStringStep){
                    case 0: windowsDrawingFrame.tabletWidth=Integer.parseInt(clipString);break;
                    case 1: windowsDrawingFrame.tabletHeight=Integer.parseInt(clipString);break;
                }
            }else{
            switch (clipStringStep){
                case 0:
                    tempID = Integer.parseInt(clipString);
                    if(tempID>3||tempID<0){clipStringStep=-4;}break;
                case 1:
                    mouseController.dataList[tempID] = Integer.parseInt(clipString);break;
                case 2:
                    mouseController.coordList[tempID][0] = Integer.parseInt(clipString);break;
                case 3:
                    mouseController.coordList[tempID][1] = Integer.parseInt(clipString);break;
                default:
                    mouseController.warningFlag=true;
            }}
            clipStringStep++;
            clipString="";
        }else{
            System.err.println("UNKNOWN SYMBOL!! decimal: "+v);
        }//System.out.println(clipString+"=="+clipStringStep);
    }

    public static void main(String[] args) throws IOException {
        try  {
            rob= new Robot();
            windowsDrawingFrame=new WindowsDrawingFrame(rob,0,0,640,480);
            mouseController = new MouseController(windowsDrawingFrame,rob);
            System.out.println("CONNECTING...");
            bindTouchServer();
            bindScreenServer();
        }catch (Exception e) {
            System.out.println("Erro!: "+e.getMessage());
        }

    }
}