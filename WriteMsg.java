package main;

import javax.swing.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 生成人的消息，并写入消息队列
 */
public class WriteMsg extends Thread {
    // 楼层高度
    int TOP;
    // 消息队列
    CopyOnWriteArrayList<int[]> msgQueen;
    // 右侧走廊
    JTextArea[] jtas;

    public WriteMsg(int TOP, CopyOnWriteArrayList<int[]> msgQueen, JTextArea[] jtas) {
        this.TOP = TOP;
        this.msgQueen = msgQueen;
        this.jtas = jtas;
    }

    /**
     * 生成人，并不断往消息队列中写入消息，以及在 ElevatorGUI 中画图 “呉”
     */
    public void run() {
        while (true) {
            try {
                People p = new People(TOP);
                int[] msg = new int[] { p.pos, p.dir, p.dest, p.inside };
                jtas[TOP - p.pos].setText("呉" + jtas[TOP - p.pos].getText());
                msgQueen.add(msg);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}