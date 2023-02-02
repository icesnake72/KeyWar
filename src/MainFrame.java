import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainFrame extends JFrame implements ComponentListener, ActionListener, Runnable {

    public static final int UNTIL_DEAD = 10;
    long score = 0;
    int DeadCount;
    JTextField tf;
    AttackLabel label;
    JPanel panel;
    // LinkedList<AttackLabel> labelList = new LinkedList<>();
    Map<String, AttackLabel> lbTable = new Hashtable<>();

    KoreanWordList kw;

    public void Create(String strTitle, int width, int height) {
        kw = new KoreanWordList();

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
        // labelList.add(label);

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
        DeadCount = 0;

        while ( DeadCount < UNTIL_DEAD ) {
            try {
                Thread.sleep(1000 );
                String str = kw.GetRandomString();
                AttackLabel newLabel = new AttackLabel(str, this);
                panel.add(newLabel);

//                newLabel.revalidate();
//                newLabel.repaint();
//                newLabel.update(newLabel.getGraphics());
                // labelList.add(newLabel);
                lbTable.put(str, newLabel);
                panel.validate();
                panel.repaint();

            } catch (NullPointerException e)
            {
                System.out.println("label = " + e.getMessage() + "입니다.");
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }

        JOptionPane.showMessageDialog(this,
                                String.format("게임 종료\n당신의 점수는 %d점 입니다.", score));
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
        label.setForeground(Color.DARK_GRAY);
        label.setBackground(Color.decode("#11A458"));
        label.setBounds(rect);
    }

    public void KillMe(AttackLabel label) {
        lbTable.remove(label.getText());
        panel.remove(label);
        System.out.println(label + " 죽습니다.");
        label = null;

        DeadCount++;
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
        // label.setText( e.getActionCommand() );
        try {
            AttackLabel aLabel = lbTable.remove(e.getActionCommand());
            aLabel.StopThread();
            Component[] components = panel.getComponents();
            for (Component component : components) {
                if (aLabel== component) {
                    aLabel.setBounds(0,0,0,0);
                    Container parent = aLabel.getParent();
                    parent.remove(aLabel);
                    //panel.remove(aLabel);
                    System.out.println("Find!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!  " + e.getActionCommand());
                    aLabel = null;

                    parent.validate();
                    parent.repaint();
                }
            }



        } catch (Exception exception) {
            System.out.println( exception.getMessage());
        }
        finally {
            tf.setText("");
        }




//        panel.remove(label);
//        label = null;

//        AttackLabel newLabel = new AttackLabel("tst", this);
//        labelList.add(newLabel);
//        panel.add(newLabel);
//        panel.validate();
//        panel.repaint();

        validate();
        repaint();

        score +=10;
        String str = String.format("Key War : %d", score);
        setTitle(str);
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
        this.setBounds(x, newY, 300, 30);
//        setBounds(rect);
        // setLocation(x, newY);

        //running = true;
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public String toString() {
        return this.getText();
    }

    @Override
    public boolean equals(Object strText) {
        
        if (strText instanceof String ) {

            String str = (String)strText;
            if ( strText.equals(getText()))
                return true;
        }

        return false;
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
                System.out.println(e.getMessage());
            } catch (Exception e)
            {
                System.out.println(e.getMessage());
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
        int parentHeight;
        try {
            parentHeight = 500;
        } catch (NullPointerException e){
            parentHeight = 500;
            System.out.println( e.getMessage() );
        }

        return ( parentHeight >= newY );
    }

    public void StopThread() {
        running.set(false);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("나 죽어요!!!");
    }
}



class KoreanWordList {
    protected String basic = "Kylian 파리 생제르맹이  상대로 한 3-1 승리에서 부상당하기 전까지 100초 동안 두 번의 페널티킥과 열린 골을 놓쳤습니다.\n" +
            "\n" +
            "경기 시작 7분 만에 수비수 세르히오 라모스에게 반칙을 범해 페널티킥을 얻었지만, 7주 전 월드컵 결승전에서 세 번의 페널티킥을 전환한 음바페는 그의 슛을 벤자민 르콩트가 막아냈습니다.\n" +
            "\n" +
            "  슛을 하기 전에 자신의 라인을 벗어나 재시도를 명령한 것을 발견했을 때 얼굴을 붉히지 않았습니다. 그러나  다시 정확하게 추측하여  페널티 킥을 골대에 넣었습니다. 리바운드 공은 PSG 포워드로 곧장 갔지만 그는 6야드에서 발리슛을 했다.\n" +
            "\n" +
            "얼마 지나지 않아 햄스트링을 잡고 일어나서 교체되어야 했을 때 파리지앵들에게는 상황이 나빴습니다.\n" +
            "\n" +
            "챔피언스 리그에서 독일의 거인 바이에른 뮌헨과의 크런치 타이가 2주도 채 남지 않은 상황에서 PSG 팬들은 그들의 스타 스트라이커의 체력과 함께 부상을 당한 라모스에 대한 평결을 듣고 싶어할 것입니다.\n" +
            "\n" +
            "경기 후 Christophe  감독은 그의 최고 득점자가 제 시간에 회복되기를 더 희망했습니다.\n" +
            "\n" +
            "\n" +
            "광고 피드백\n" +
            "\"그는 무릎 뒤쪽이나 허벅지 뒤쪽에 충격을 가했습니다. 그래서 우리는 볼 것입니다.\"라고  말했습니다. “별로 걱정하지 않습니다. 타박상인지 타박상인지 아직 모릅니다.\n" +
            "\n" +
            "\"경기가 계속되는 상황에서 우리는 매우 바쁜 일정 목록으로 위험을 감수하고 싶지 않지만 그렇게 심각해 보이지는 않습니다.\"\n" +
            "\n" +
            "당혹감을 더하는 것은  주말에  상대로 골을 넣어 파리지앵의 승리를 부정한  스트라이커   의해  1의 최고 득점자 차트에서 추월당했다는 것입니다.\n" +
            "\n" +
            " 없이 파리 시민들은 부분적으로는 월드컵의 사랑 리오넬 메시 덕분에 게임에서 승리했습니다.\n" +
            "\n" +
            "   여름에 계약한   55분에 그의 새 팀에 대한 계정을 열기 전에 오프사이드로 허용되지 않는 골을 보았습니다. 15분 후 메시는 멋진 팀 이동을 마친 후 리드를 두 배로 늘렸습니다.\n" +
            "\n" +
            " 젊은 선수 Warren 92분에 경기를 끝내기 전에 Arnaud  통해 늦게 한 골을 뽑아 PSG 역사상 최연소 득점자가 되었습니다.\n" +
            "\n" +
            "결국 우승을 차지했음에도 불구하고 슈퍼스타 선수단은 월드컵 이후 클럽 축구로 복귀한 이후 어려움을 겪고 있습니다.\n" +
            "\n" +
            "프랑스 챔피언은 , Messi,  및  포함하여 월드컵에서 많은 선수들이 빛을 발하는 것을 보았지만 팀은 카타르에서 축구 보난자 이후 명백한 숙취를 겪었습니다.\n" +
            "\n" +
            "리그 1에 복귀한 이후 리그에서 두 번 패했고 주말에 무승부를 기록했으며 이제는 바이에른 뮌헨과의 두 경기, 치열한 라이벌 마르세유와 모나코 여행.\n" +
            "\n" +
            "고티에 감독은 \"우리는 월드컵 이후 일정 뒤에 숨지 않을 것\"이라고 말했다.\n" +
            "\n" +
            "“우리의 경기력이 시즌 초반과 같은 수준이 아닌 지 몇 주가 지났습니다. 우리는 일하고 해결책을 찾고 다시 연결해야 합니다.”\n" +
            "\n" +
            "챔피언스 리그 첫 우승을 노리는 데 있어 또 한 해의 실망을 피하려면 팀은 잠재적으로 없이 상황을 바꿔야 할 것입니다.";

    protected String[] arrString;

    public KoreanWordList() {
        arrString = basic.split(" ");
    }

    public String GetRandomString() {
        Random rnd = new Random();
        int i = Math.abs(rnd.nextInt()) % arrString.length;
        i = Math.min( i, arrString.length-1);
        return arrString[i];
    }
}



class LabelList extends LinkedList<JLabel> {

}