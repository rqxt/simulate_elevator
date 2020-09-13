package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * 电梯模拟程序的 GUI 界面
 */
public class ElevatorGUI extends JFrame implements Serializable {

    private static final long serialVersionUID = 1L;
    private static int speed = 1; // 电梯移速
    private static int height = 40; // 电梯高度
    int TOP = 20; // 设置楼层
    int num = 0; // 电梯现有人数
    int state = 0; // 电梯运行方向
    int curPostion = 1; // 电梯所在楼层
    int dest_floor = curPostion; // 电梯目标楼层

    int[] btnInside = new int[TOP + 1]; // *电梯内按钮列表
    int[] btnUp = new int[TOP + 1]; // *电梯外上行按钮列表
    int[] btnDown = new int[TOP + 1]; // *电梯内上行列表

    // 电梯
    JPanel jpl;
    JLabel door1, door2;
    // 右边走廊
    JTextArea[] jtas = new JTextArea[TOP];
    JLabel[] jlbs = new JLabel[TOP];
    // 消息许燎
    CopyOnWriteArrayList<int[]> msgQueen = new CopyOnWriteArrayList<int[]>();
    // 好看的颜色
    Color[] colors = new Color[]{new Color(190, 231, 233), new Color(214, 213, 183), new Color(244, 96, 108),
            new Color(0, 0, 0)};

    // 在构造方法中 初始化界面
    public ElevatorGUI() {
        setBounds(10, 10, 500, TOP * height + 60 + 47);
        setLayout(null);
        setTitle("电梯模拟");

        // 在头部显示时间
        SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
        JLabel jlb = new JLabel(df.format(new Date()), JLabel.CENTER);
        jlb.setBounds(0, 0, 500, 60);
        jlb.setOpaque(true);
        jlb.setFont(new Font("MicroSoft Yahei", 1, 25));
        jlb.setBackground(new Color(209, 186, 116));
        add(jlb);

        // 设置右侧走廊
        for (int i = 0; i < TOP; i++) {
            // 右侧走廊
            JTextArea jta1 = new JTextArea("  ");
            jta1.setBounds(300, 60 + i * height, 200, height);
            jta1.setOpaque(true);
            jta1.setBackground(colors[i % 2]);
            jta1.setFont(new Font("MicroSoft Yahei", 1, 28));
            jta1.setEditable(false);
            jtas[i] = jta1;
            add(jta1);
            // 左侧楼层号
            JLabel jlb1 = new JLabel(TOP - i + "F", JLabel.CENTER);
            jlb1.setBounds(0, 60 + i * height, 100, height);
            jlb1.setOpaque(true);
            jlb1.setBackground(colors[i % 2]);
            jlb1.setFont(new Font("MicroSoft Yahei", 1, 28));
            jlbs[i] = jlb1;
            add(jlb1);
        }

        // 设置电梯门
        jpl = new JPanel();
        jpl.setBounds(100, 60 + TOP * height - height, 200, height);
        jpl.setOpaque(true);
        jpl.setBackground(Color.DARK_GRAY);
        jpl.setLayout(new GridLayout(1, 2));
        door1 = new JLabel("初始状态", JLabel.CENTER);
        door1.setSize(100, height);
        door1.setOpaque(true);
        door1.setFont(new Font("MicroSoft Yahei", 1, 20));
        door1.setBackground(new Color(230, 206, 172));
        door2 = new JLabel("人数:" + this.num + "", JLabel.CENTER);
        door2.setOpaque(true);
        door2.setBackground(new Color(230, 206, 172));
        door2.setSize(100, height);
        door2.setFont(new Font("MicroSoft Yahei", 1, 20));

        jpl.add(door1);
        jpl.add(door2);
        add(jpl);

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ;
    }

    /**
     * 向上移动
     */
    public void moveUp() {
        int i = 0;
        while (i < height) {
            try {
                Thread.sleep(speed);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Point pos = jpl.getLocation();
            pos.translate(0, -1);
            jpl.setLocation(pos);
            i++;
        }
        door1.setText("目标:" + this.dest_floor + "F");
    }

    /**
     * 向下移动
     */
    public void moveDown() {
        int i = 0;
        while (i < height) {
            try {
                Thread.sleep(speed);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Point pos = jpl.getLocation();
            pos.translate(0, 1);
            jpl.setLocation(pos);
            i++;
        }
        door1.setText("目标:" + this.dest_floor + "F");
    }

    /**
     * 开门
     */
    public void openDoor() throws InterruptedException {
        Thread.sleep(50);
    }

    /**
     * 关门
     */
    public void closeDoor() throws InterruptedException {
        Thread.sleep(50);
        door2.setText("人数:" + this.num + "");
    }
}