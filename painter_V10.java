
/*
todo replace shapecount with Shape.count
*/

//deaw rect but with no moves
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JFileChooser;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
/*
TODO make a textbox to change the size of every thin you draw
 rodo make a variabble to the prevent blink part try 10 as initial
*/
public class Main extends Applet
{

    Button BRed,BGreen,BBlue,BYellow,BPink,BOrange,BWhite,BBlack,Clear;
    static int shapecount = 0;
    Shape [] shapes = new Shape[10000];
    int lx = -1;int ly = -1;int tx = -1;int ty = -1;
    int mode = 0;
    boolean dotted = false;
    boolean fill = false;
    boolean clearsignal = false;
    boolean freeHandpinter = false;
    boolean openimg = true;
    String imgname= "img.jpg";
    String name_drc = "pk\\";
    int color = 0; //default black
    int eraserwidth = 20;
    int eraserhight = 20;
    Frame frame;
    Font font;
    public void init ()
    {
        setLayout(null);
        frame = new Frame("Painter");
        font = new Font("TimeNewRoman",1,10);
        setFont(font);
        Color();
        Mousehandling mouse_on_background = new Mousehandling();
        addMouseMotionListener(mouse_on_background);
        addMouseListener(mouse_on_background);

        //eraser btn source and listner
        Button ersrBt = new Button("eraser");
        add(ersrBt);
        ersrBt.setBounds(40,5,60,30);
        setLayout(null);
        ersrBt.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e) {mode = 2;}
        });

        //free pen button source and listner
        Button freeHandBt = new Button("frHnd");
        add(freeHandBt);
        freeHandBt.setBounds(135,5,60,30);
        freeHandBt.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e) {mode = 1;}
        });

        //dot button
        Button dot = new Button("dot");
        add(dot);
        dot.setBounds(1045,10,50,40);
        dot.addMouseListener(new MouseAdapter()
        {public void mousePressed(MouseEvent e) {
                dotted = !dotted;
                repaint();
            }
        });


        Checkbox fillBox = new Checkbox("filld");
        add(fillBox);
        fillBox.setBounds(1000,55,50,40);
        fillBox.setFocusable(false);
        fillBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange()==1)
                    fill = true;
                else
                    fill = false;
            }
        });

        //open text
        TextField imgText = new TextField("Enter image name");
        imgText.setBounds(800,65,150,30);
        add(imgText);

        //openBt
        Button openBt = new Button("open");
        add(openBt);
        openBt.setBounds(950,55,50,40);
        openBt.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {disp();}

            private void disp (){openimg = !openimg;
                imgname = imgText.getText();
                repaint();}
        });



        /*Button button = new Button("select");
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                test t = new test();

                t.setVisible(true);
                System.out.println("selest");
            }
        });
        add(button);
        button.setBounds(500,500,100,100);*/

        //button undo
        Button undoBt = new Button("undo");
        add(undoBt);
        undoBt.setBounds(40,35,60,30);
        undoBt.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (shapecount >= 1){
                    shapecount -= 1;
                    mode = -1;
                    //undo the hole line of pin and eraser
                    if (shapes[shapecount].w == eraserwidth) {
                        for (; shapecount >= 0 && shapes[shapecount].w ==eraserwidth; shapecount--);
                        shapecount++;
                    }

                    System.out.println(shapecount);
                    repaint();
                }
            }
        });
        //clear Button
        ClearButton();

        ///shapees buttons
        //staight line button
        Button strLineBt = new Button("stLn");
        add(strLineBt);
        strLineBt.setBounds(200,5,60,30);
        strLineBt.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mode = 3;
            }
        });
        //rect button
        Button rectBt = new Button("rect");
        add(rectBt);
        rectBt.setBounds(135,35,60,30);
        rectBt.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {mode = 4;}
        });
        //oval button
        Button ovalBt = new Button("oval");
        add(ovalBt);
        ovalBt.setBounds(200,35,60,30);
        ovalBt.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {mode = 5;}
        });

    }


    public void paint (Graphics g)
    {
        if (openimg)
        {
            name_drc = "pk\\"+ imgname;
            Image img = getImage(getCodeBase(), name_drc);
            g.drawImage(img, 0, 105, 1100, 900 - 105, this);
        }


        g.drawString("MODE:"+ mode,1150,900);

        //drawing what inside the drawing data structure.
        for (int i = 0; i < shapecount; i++)
        {
            //colorSetter(7);
            Shape shape = new Shape();
            shape = shapes[i];
            //color check
            g.setColor(colorSetter(shape.color));

            if (shape.type == 3){//strln
                g.drawLine(shape.cx, shape.cy, shape.w, shape.h);
            }
            if (shape.type == 4) {//rect
                if (shape.fillen)
                    g.fillRect(shape.cx, shape.cy, shape.w, shape.h);
                else
                    g.drawRect(shape.cx, shape.cy, shape.w, shape.h);
            }
            else if (shape.type == 5) {//oval
                if (shape.fillen)
                    g.fillOval(shape.cx, shape.cy, shape.w, shape.h);
                else
                    g.drawOval(shape.cx, shape.cy, shape.w, shape.h);
            }

        }
        if (clearsignal)
        {
            clearsignal = false;
            return;
        }


        g.setColor(colorSetter(color));
        int shapewidth = Math.abs(lx - tx);
        int shapeheight = Math.abs(ly - ty);
        if (mode == 3) {
            g.drawLine(lx,ly,tx, ty);
        }
        else if (mode == 4) {//rect
            if (fill)
                g.fillRect(Math.min(lx,tx),Math.min(ly,ty),shapewidth, shapeheight);
            else
                g.drawRect(Math.min(lx,tx),Math.min(ly,ty),shapewidth, shapeheight);
        }
        else if(mode == 5) {//oval
            if (fill || freeHandpinter){
                g.fillOval(Math.min(lx, tx), Math.min(ly, ty), shapewidth, shapeheight);
                freeHandpinter = false;
            }
            else
                g.drawOval(Math.min(lx, tx), Math.min(ly, ty), shapewidth, shapeheight);
        }


    }

    public class Mousehandling implements  MouseMotionListener,MouseListener {//Mousehandling
        Graphics g = getGraphics();
        public void mouseDragged(MouseEvent e)
        {


            if (mode == 1)
            {//freehand
                freeHandpinter = true;
                Shape sh = new Shape();
                if (Math.abs(e.getX() - lx) > 5 || Math.abs(e.getY() - ly)  > 5 ){
                    sh.push(e.getX(),e.getY(),eraserwidth, eraserhight,5,color,true);
                    shapes[shapecount] = sh;
                    shapecount+=1;
                    sh.push(lx,ly,eraserwidth, eraserhight,5,color,true);
                    shapes[shapecount] = sh;
                    shapecount+=1;

                    //the dotted part
                    if (dotted) {
                        try
                        {Thread.sleep(100);}
                        catch (InterruptedException ex)
                        {throw new RuntimeException(ex);}
                    }
                    lx = e.getX();
                    ly = e.getY();
                    repaint();
                }
            }

            else if(mode == 2)
            {//eraser
                //g.setColor(Color.RED);
                Shape sh = new Shape();
                if (Math.abs(e.getX() - lx) > 10 || Math.abs(e.getY() - ly)  > 10 ) {
                    sh.push(e.getX(), e.getY(), eraserwidth, eraserhight, 5, -1, true);
                    shapes[shapecount] = sh;
                    shapecount += 1;
                    repaint();
                    lx = e.getX();
                    ly = e.getY();
                    //the dotted part
                    if (dotted) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }

            else if (mode == 3||mode == 4 || mode == 5){//stln rect oval
                tx = e.getX();
                ty = e.getY();
                //to get rid of the tedious blinking.
                //change
                int framerate = 4;
                if (openimg)
                    framerate = 8;
                else
                    framerate = 4;
                try
                {Thread.sleep(framerate);}
                catch (InterruptedException ex)
                {throw new RuntimeException(ex);}
                repaint();

            }

        }

        public void mouseMoved(MouseEvent e) {}

        public void mouseClicked(MouseEvent e) {
            if (mode == 1){
                Shape sh = new Shape();
                sh.push(e.getX(), e.getY(), eraserwidth, eraserhight, 5, color, true);
                shapes[shapecount] = sh;
                shapecount += 1;
                repaint();
            }
            else if (mode == 2){
                Shape sh = new Shape();
                sh.push(e.getX(), e.getY(), eraserwidth, eraserhight, 5, -1, true);
                shapes[shapecount] = sh;
                shapecount += 1;
                repaint();
            }


        }

        public void mousePressed(MouseEvent e) {
            lx = e.getX();
            ly = e.getY();
        }

        public void mouseReleased(MouseEvent e) {
            tx = e.getX();
            ty = e.getY();
            int shapewidth = Math.abs(lx - tx);
            int shapeheight = Math.abs(ly - ty);
            if (mode == 3) {//straight line
                Shape sh = new Shape();
                sh.push(lx,ly,tx, ty,mode,color,fill);
                shapes[shapecount] = sh;
                shapecount+=1;

            }
            else if (mode == 4) {//rect
                Shape sh = new Shape();
                sh.push(Math.min(lx,tx),Math.min(ly,ty),shapewidth, shapeheight,mode, color,fill);
                shapes[shapecount] = sh;
                shapecount+=1;
            }
            else if(mode == 5){//oval
                Shape sh = new Shape();
                sh.push(Math.min(lx,tx),Math.min(ly,ty),shapewidth, shapeheight,mode, color,fill);
                shapes[shapecount] = sh;
                shapecount+=1;
            }
        }

        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
    }
    public Color colorSetter (int c){
        if (c == -1){return getBackground();}
        else if (c == 1){return Color.RED;}
        else if (c == 2){return Color.pink;}
        else if (c == 3){return Color.orange;}
        else if (c == 4){return Color.yellow;}
        else if (c == 5){return Color.green;}
        else if (c == 6){return Color.blue;}
        else if (c == 7){return Color.white;}
        else if (c == 8){return Color.black;}
        else {return Color.black;}

    }
    public void mouseButtonColor(Button Bcolor , int x){
        Bcolor.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e) {color = x;}
        });
    }
    public void Color(){
        BRed = new Button(" ");
        BRed.setBounds(1100,1,100,100);
        add(BRed);
        BRed.setBackground(Color.RED);
        mouseButtonColor(BRed,1);

        BPink = new Button(" ");
        BPink.setBounds(1100,200,100,100);
        add(BPink);
        BPink.setBackground(Color.PINK);
        mouseButtonColor(BPink,2);

        BOrange = new Button(" ");
        BOrange.setBounds(1100,100,100,100);
        add(BOrange);
        BOrange.setBackground(Color.ORANGE);
        mouseButtonColor(BOrange,3);

        BYellow = new Button(" ");
        BYellow.setBounds(1100,500,100,100);
        add(BYellow);
        BYellow.setBackground(Color.YELLOW);
        mouseButtonColor(BYellow,4);

        BGreen = new Button(" ");
        BGreen.setBounds(1100,300,100,100);
        add(BGreen);
        BGreen.setBackground(Color.GREEN);
        mouseButtonColor(BGreen,5);

        BBlue = new Button(" ");
        BBlue.setBounds(1100,400,100,100);
        add(BBlue);
        BBlue.setBackground(Color.BLUE);
        mouseButtonColor(BBlue,6);

        BWhite = new Button(" ");
        BWhite.setBounds(1100,600,100,100);
        add(BWhite);
        BWhite.setBackground(Color.WHITE);
        mouseButtonColor(BWhite,7);

        BBlack = new Button(" ");
        BBlack.setBounds(1100,700,100,100);
        add(BBlack);
        BBlack.setBackground(Color.BLACK);
        mouseButtonColor(BBlack,8);
    }
    public void ClearButton(){
        Clear = new Button("Clear");
        Clear.setFont(font);
        Clear.setBounds(1100,800,100,100);
        add(Clear);
        Clear.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Dimension d = getSize();
                clearsignal = true;
                shapecount = 0;
                repaint();
            }
        });
    }
}
class Shape
{
    static public int count = 0;
    int cx, cy, w, h, type, color;
    boolean fillen;
    public Shape() {
    }
    public void push(int lx, int ly, int x, int y,int mode, int colorin, boolean fillcase)
    {
        count +=1;
        System.out.println("shape count is: "+(Main.shapecount+1) );
        cx = lx;
        cy = ly;
        w = x;
        h = y;
        type = mode;
        color = colorin;
        fillen = fillcase;
        /*if (fillen)
            System.out.println("dotcase in Shape\n");
        else
            System.out.println("false dotcase in Shape\n");*/
    }
}

