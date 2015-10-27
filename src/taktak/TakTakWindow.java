package taktak;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.swing.JFrame;

public class TakTakWindow extends JFrame implements Runnable {
    static int bombattempts;
    static final int WINDOW_WIDTH = 420;
    static final int WINDOW_HEIGHT = 445;
    final int XBORDER = 20;
    final int YBORDER = 20;
    final int YTITLE = 25;
    boolean animateFirstTime = true;
    int xsize = -1;
    int ysize = -1;
    Image image;
    Graphics2D g;

    public static Random rand = new Random();
//    static ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit().getImage("./icon.PNG"));

    public TakTakWindow() {
        
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
        setTitle("Tak-Tak");
        //setIconImage(icon.getImage());
        
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.BUTTON1 == e.getButton()) {
                   
                }
//                if (e.BUTTON3 == e.getButton()) {
//                    //right button
//                    reset();
//                }
                repaint();
            }
        });
//
//    addMouseMotionListener(new MouseMotionAdapter() {
//      public void mouseDragged(MouseEvent e) {
//        repaint();
//      }
//    });
//
//    addMouseMotionListener(new MouseMotionAdapter() {
//      public void mouseMoved(MouseEvent e) {
//
//        repaint();
//      }
//    });
//
//        addKeyListener(new KeyAdapter() {
//
//            public void keyPressed(KeyEvent e) {
//                if (e.VK_UP == e.getKeyCode() && speedY < 5) {
//                    speedY++;
//                } else if (e.VK_DOWN == e.getKeyCode() && speedY > -5) {
//                    speedY--;
//                } else if (e.VK_LEFT == e.getKeyCode() && speedX > -6) {
//                    speedX--;
//                } else if (e.VK_RIGHT == e.getKeyCode() && speedX < 6 ) {
//                    speedX++;
//                }
//                repaint();
//            }
//        });
        init();
        start();
    }
    Thread relaxer;
////////////////////////////////////////////////////////////////////////////
    public void init() {
        requestFocus();
    }
////////////////////////////////////////////////////////////////////////////
    public void destroy() {
    }
////////////////////////////////////////////////////////////////////////////
    public void paint(Graphics gOld) {
        if (image == null || xsize != getSize().width || ysize != getSize().height) {
            xsize = getSize().width;
            ysize = getSize().height;
            image = createImage(xsize, ysize);
            g = (Graphics2D) image.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
//fill background
        g.setColor(Color.black);
        g.fillRect(0, 0, xsize, ysize);

        int x[] = {getX(0), getX(getWidth2()), getX(getWidth2()), getX(0), getX(0)};
        int y[] = {getY(0), getY(0), getY(getHeight2()), getY(getHeight2()), getY(0)};
//fill border
        g.setColor(Color.black);
        g.fillPolygon(x, y, 4);
// draw border
        //g.setColor(Color.red);
        g.drawPolyline(x, y, 5);

        if (animateFirstTime) {
            gOld.drawImage(image, 0, 0, null);
            return;
        }
        
        gOld.drawImage(image, 0, 0, null);
    }

////////////////////////////////////////////////////////////////////////////
// needed for     implement runnable
    public void run() {
        while (true) {
            animate();
            repaint();
            double seconds = 0.04;    //time that 1 frame takes.
            int miliseconds = (int) (1000.0 * seconds);
            try {
                Thread.sleep(miliseconds);
            } catch (InterruptedException e) {
            }
        }
    }
/////////////////////////////////////////////////////////////////////////
    public void reset() {

    }
/////////////////////////////////////////////////////////////////////////
    public void animate() {
        if (animateFirstTime) {
            animateFirstTime = false;
            if (xsize != getSize().width || ysize != getSize().height) {
                xsize = getSize().width;
                ysize = getSize().height;
            }
            reset();
        }
    }

////////////////////////////////////////////////////////////////////////////
    public void start() {
        if (relaxer == null) {
            relaxer = new Thread(this);
            relaxer.start();
        }
    }
////////////////////////////////////////////////////////////////////////////
    public void stop() {
        if (relaxer.isAlive()) {
            relaxer.stop();
        }
        relaxer = null;
    }
/////////////////////////////////////////////////////////////////////////
    public int getX(int x) {
        return (x + XBORDER);
    }

    public int getY(int y) {
        return (y + YBORDER + YTITLE);
    }

    public int getYNormal(int y) {
        return (-y + YBORDER + YTITLE + getHeight2());
    }
    
    
    public int getWidth2() {
        return (xsize - getX(0) - XBORDER);
    }

    public int getHeight2() {
        return (ysize - getY(0) - YBORDER);
    }
}
//
//class sound implements Runnable {
//    Thread myThread;
//    File soundFile;
//    public boolean donePlaying = false;
//    sound(String _name)
//    {
//        soundFile = new File(_name);
//        myThread = new Thread(this);
//        myThread.start();
//    }
//    public void run()
//    {
//        try {
//        AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
//        AudioFormat format = ais.getFormat();
//    //    System.out.println("Format: " + format);
//        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
//        SourceDataLine source = (SourceDataLine) AudioSystem.getLine(info);
//        source.open(format);
//        source.start();
//        int read = 0;
//        byte[] audioData = new byte[16384];
//        while (read > -1){
//            read = ais.read(audioData,0,audioData.length);
//            if (read >= 0) {
//                source.write(audioData,0,read);
//            }
//        }
//        donePlaying = true;
//
//        source.drain();
//        source.close();
//        }
//        catch (Exception exc) {
//            System.out.println("error: " + exc.getMessage());
//            exc.printStackTrace();
//        }
//    }
//
//}
