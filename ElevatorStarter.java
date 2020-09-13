package main;

/**
 * 电梯启动器，整个模拟程序的入口
 */
public class ElevatorStarter {
    public static void main(String[] args) throws InterruptedException {
        // 实例化 JFrame 对象（图形化界面）
        ElevatorGUI e = new ElevatorGUI();
        // 实例化 专门写入消息的对象
        WriteMsg wm = new WriteMsg(e.TOP, e.msgQueen, e.jtas);
        // 开始写入消息
        wm.start();
        Thread.sleep(1000);
        // 实例化电梯对象
        ElevatorLogic em = new ElevatorLogic(e);
        // 电梯运行
        em.start();
    }
}
