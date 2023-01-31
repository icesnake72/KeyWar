import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainFrame extends JFrame implements ComponentListener, ActionListener, Runnable {

    long score = 0;
    JTextField tf;
    AttackLabel label;
    JPanel panel;
    LinkedList<AttackLabel> labelList = new LinkedList<>();

    public void Create(String strTitle, int width, int height) {
        setTitle( strTitle );
        setSize( width, height);

//        JButton btn1 = new JButton("여길 눌러!!!");
//        JCheckBox chkBox = new JCheckBox("Check Me Please~~~");
//        JSlider slider = new JSlider();
        tf = new JTextField("여기에 입력!!!");
        label = new AttackLabel("Hi! My name is Attack Label.", this);

//         this.setLayout( new FlowLayout());
         setLayout( new BorderLayout());
//         this.setLayout( new GridLayout());
//        this.setLayout( new CardLayout());
//        this.setLayout(null);
//        this.add(btn1);
//        this.add(chkBox);
//        this.add(slider);


        panel = new JPanel();
        panel.setLayout(null);

        Container contentPane = getContentPane();
        contentPane.add(panel, BorderLayout.CENTER);

        panel.add(tf);
        panel.add(label);
        labelList.add(label);

        getContentPane().addComponentListener(this);
        tf.addActionListener(this);

        // setResizable(false);
        //pack();
        setLocationRelativeTo(null);
        setVisible( true );
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    }

    @Override
    public void run() {
        boolean running = true;
        while ( running ) {
            try {
                Thread.sleep(1000 );
//                AttackLabel newLabel = new AttackLabel(String.format("%d", System.currentTimeMillis()), this);
//                panel.add(newLabel);
//                panel.add(new JLabel("Test"));
//                newLabel.revalidate();
//                newLabel.repaint();
//                newLabel.update(newLabel.getGraphics());
//                labelList.add(newLabel);
//                panel.validate();
//                panel.repaint();

            } catch (NullPointerException e)
            {
                System.out.println("label = " + e.getMessage() + "입니다.");
                running = false;
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }

    @Override
    public void componentResized(ComponentEvent e){
        // int h = panel.getHeight();
        int w = panel.getWidth();
        // int x = panel.getX();
        // int y = panel.getY();
        Rectangle rect = panel.getBounds();
        rect.y = rect.height - 30;
        rect.height = 30;
        tf.setBounds(rect);

//        String str = String.format("x:%d, y:%d, Width:%d, Height:%d", x, y, w, h);
//        tf.setText(str);

        rect.x = label.getX();
        rect.y = 0;
        rect.height = 30;
        rect.width = w;
        label.setBounds(rect);
    }

    public void KillMe(AttackLabel label) {
        labelList.remove(label);
        panel.remove(label);
        label = null;
    }

    @Override
    public void componentMoved(ComponentEvent e){}

    @Override
    public void componentShown(ComponentEvent e){}

    @Override
    public void componentHidden(ComponentEvent e){}

    @Override
    public void actionPerformed(ActionEvent e) {
        // String actionCommand = e.getActionCommand();
        label.setText( e.getActionCommand() );
        tf.setText("");

        label.StopThread();
        KillMe(label);
//        panel.remove(label);
//        label = null;

        AttackLabel newLabel = new AttackLabel("tst", this);
        panel.add(newLabel);
        panel.validate();
        panel.repaint();

        validate();
        repaint();

        score +=10;
        String str = getTitle();
        str = String.format("Key War : %d", score);
        setTitle(str);
        // requestFocus();
    }

}

class AttackLabel extends  JLabel implements Runnable {
    private int newY;
    private int x;
    private final AtomicBoolean running = new AtomicBoolean(false);

    MainFrame mainFrame;

    public AttackLabel(String text, MainFrame mainFrame) {
        super(text);
        this.mainFrame = mainFrame;

        Random rnd = new Random();
        this.x = ( Math.abs(rnd.nextInt()) % 500) - 100;
        this.x = Math.max(this.x, 0);
        this.newY = 0;
        setLocation(x, newY);

        //running = true;
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        running.set(true);
        while( running.get() ) { //&& !Thread.currentThread().isInterrupted()
            try {
                Thread.sleep( 10 );

                newY++;
                setLocation(x, newY);
            } catch (InterruptedException e)
            {
                System.out.println(e);
            } catch (Exception e)
            {
                System.out.println(e);
            }
            finally {
                if ( running.get() )
                    running.set(CheckLive());
            }
        }

        System.out.println("Label Thread 종료됨");
        mainFrame.KillMe(this);
    }

    private boolean CheckLive() {
        int parentHeight = 0;
        try {
            parentHeight = 500;
        } catch (NullPointerException e){
            parentHeight = 500;
            System.out.println( e );
        }

        return ( parentHeight >= newY );
    }

    public void StopThread() {
        running.set(false);
    }
}