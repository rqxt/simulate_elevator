package main;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 电梯的主要逻辑
 */
public class ElevatorLogic extends Thread {
    // GUI电梯对象
    ElevatorGUI e;
    // 电梯状态中介
    int temp_state;

    public ElevatorLogic(ElevatorGUI e) {
        this.e = e;
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(50);
                // 1.分析消息序列，转换成电梯内按钮和上/下行按钮列表
                analysisMsg();
                // 2.打印当前电梯状态
                printStatus();
                // 3.进行移动和人员进出
                move();
                // 4.根据按钮列表和电梯运行状态确定目标楼层（重要）
                setDestFloor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 移动电梯，人员进出电梯
     */
    public void move() throws InterruptedException {
        // 如果电梯不在目标楼层，往电梯运行方向上移动一层
        if (e.curPostion != e.dest_floor) {
            if (e.curPostion > e.dest_floor) {
                e.state = -1;
            } else if (e.curPostion < e.dest_floor) {
                e.state = 1;
            }
            if (e.state == 1) {
                e.curPostion += 1;
                e.moveUp();
            } else if (e.state == -1) {
                e.curPostion -= 1;
                e.moveDown();
            }
        } else {
            // 电梯停在了目标楼层，人员进电梯
            temp_state = e.state;
            e.state = 0; // 停止电梯
            Iterator<?> msg = e.msgQueen.iterator();
            // temp用于存储已经出电梯的人，之后再在消息队列这删除这些人的孽畜
            CopyOnWriteArrayList<int[]> temp = new CopyOnWriteArrayList<int[]>();
            // 遍历消息队列
            while (msg.hasNext()) {
                int[] p = (int[]) msg.next();
                e.colors[3] = e.jlbs[e.TOP - e.curPostion].getBackground();
                // 进电梯
                if (p[0] == e.curPostion && p[1] == temp_state) {
                    e.openDoor();
                    // 防止进两次电梯
                    if (p[3] == 0) {
                        p[3] = 1;
                        e.num++;
                        e.jtas[e.TOP - e.curPostion].replaceRange("", 0, 1);
                        e.door2.setText("进入电梯");
                        Thread.sleep(180);
                    }
                }
                // 出电梯
                if (p[3] == 1 && p[2] == e.curPostion) {
                    e.jlbs[e.TOP - e.curPostion].setBackground(e.colors[2]);
                    e.openDoor();
                    temp.add(p);
                    e.num--;
                    e.door1.setText("离开电梯");
                    Thread.sleep(180);
                }
                e.jlbs[e.TOP - e.curPostion].setBackground(e.colors[3]);
            }
            // 关门
            e.closeDoor();
            // 删除已经出去的人的信息
            for (Object i : temp) {
                e.msgQueen.remove(i);
            }
        }
    }

    /**
     * 设置目标楼层
     */
    public void setDestFloor() {
        // 消息序列不为空的时候
        if (e.msgQueen.isEmpty() == false) {
            // （初始）如果电梯是停止状态且电梯没有人，则消息序列中的第一个为目标楼层
            if (e.state == 0 && e.num == 0) {
                if (temp_state == 0) {
                    int[] msg = (int[]) e.msgQueen.iterator().next();
                    e.dest_floor = msg[0];
                    if (e.curPostion == msg[0]) {
                        e.dest_floor = msg[2];
                    }
                } else if (temp_state == 1) {
                    findA();
                } else if (temp_state == -1) {
                    findB();
                }

                if (e.dest_floor > e.curPostion) {
                    e.state = 1;
                } else if (e.dest_floor < e.curPostion) {
                    e.state = -1;
                }
            }
            // 电梯运行过程中，分析电梯内，电梯外按钮列表，确定目标楼层
            if (e.state == 1) {
                // 内部按钮：向上最近
                findUpInBtn(0);
                // 外部按钮：向上最近
                findUpOutBtn();
            } else if (e.state == -1) {
                // 内部按钮：向下最近
                findDownInBtn(0);
                // 外部按钮：向下最近
                findDownOutBtn();
            }
            // 电梯停靠某一楼层，并且电梯内还有人
            else if (e.state == 0 && e.num != 0) {
                e.state = temp_state; // 恢复停靠前的状态
                if (e.state == 1) {
                    // 参数 1 表示电梯内一定有人，因此会遍历所有的内部按钮
                    // 当前层向上没找到，就会往当前层下找最近按钮
                    this.findUpInBtn(1);
                } else if (e.state == -1) {
                    this.findDownInBtn(1);
                }
            }
        }
    }

    // 向上运行时候
    public void findA() {
        int temp_dest = e.dest_floor;
        // 1.找到向上最近的外部按钮
        for (int i = 1; i < e.btnUp.length; i++) {
            if (e.btnUp[i] == 1 && i >= e.curPostion) {
                e.dest_floor = i;
                break;
            }
        }
        if (temp_dest != e.dest_floor) {
            System.out.println("1.找到向上最近的外部按钮");
            return;
        }
        // 2.如果没有1没有找到，则找向下的最高层的按钮
        findHighestDown();
        if (temp_dest != e.dest_floor) {
            System.out.println("2.如果没有1没有找到，则找向下的最高层的按钮");
            return;
        }
        // 3.如果12都没有改变dest_floor的值，那么就找向上的最低层的按钮
        findLowestUp();
        if (temp_dest != e.dest_floor) {
            System.out.println("3.如果12都没有改变dest_floor的值，那么就找向上的最低层的按钮");
            return;
        }
        System.out.println("Up: Error");
    }

    // 向下运行时候
    public void findB() {
        int temp_dest = e.dest_floor;
        // 1.找到向下最近的外部按钮
        for (int i = e.btnDown.length - 1; i >= 1; i--) {
            if (e.btnDown[i] == 1 && i <= e.curPostion) {
                e.dest_floor = i;
                break;
            }
        }
        if (temp_dest != e.dest_floor) {
            System.out.println("1.找到向下最近的外部按钮");
            return;
        }
        // 2.如果没有1没有找到，则找向上的最低层的按钮
        findLowestUp();
        if (temp_dest != e.dest_floor) {
            System.out.println("2.如果没有1没有找到，则找向上的最低层的按钮");
            return;
        }
        // 3.如果12都没有改变dest_floor的值，那么就找向下的最高层的按钮
        findHighestDown();
        if (temp_dest != e.dest_floor) {
            System.out.println("3.如果12都没有改变dest_floor的值，那么就找向下的最高层的按钮");
            return;
        }
        System.out.println("Down: Error");
    }

    public void findLowestUp() {
        // 找向上的最低层的按钮
        for (int i = 1; i < e.btnUp.length; i++) {
            if (e.btnUp[i] == 1 && i <= e.curPostion) {
                e.dest_floor = i;
                break;
            }
        }
    }

    public void findHighestDown() {
        // 找向下的最高层的按钮
        for (int i = e.btnDown.length - 1; i >= 1; i--) {
            if (e.btnDown[i] == 1 && i >= e.curPostion) {
                e.dest_floor = i;
                break;
            }
        }
    }

    /* 内部按钮：（往下）小于当前位置最近的按钮信号 */
    public void findDownInBtn(int changeState) {
        for (int i = e.btnInside.length - 1; i >= 1; i--) {
            if (e.btnInside[i] == 1 && i <= e.curPostion) {
                e.dest_floor = i;
                break;
            }
            if (i == 1 && changeState == 1) {
                findUpInBtn(0);
                e.state *= -1;
            }
        }
    }

    /* 内部按钮：（往上）大于当前位置最近的按钮信号 */
    public void findUpInBtn(int changeState) {
        for (int i = 1; i < e.btnInside.length; i++) {
            if (e.btnInside[i] == 1 && i >= e.curPostion) {
                e.dest_floor = i;
                break;
            }
            if (i == e.btnInside.length - 1 && changeState == 1) {
                findDownInBtn(0);
                e.state *= -1;
            }
        }
    }

    // 外部按钮：向下最近
    public void findDownOutBtn() {
        for (int i = e.btnDown.length - 1; i >= 1; i--) {
            if (e.btnDown[i] == 1 && i <= e.curPostion && i > e.dest_floor) {
                e.dest_floor = i;
                break;
            }
        }
    }

    // 外部按钮：向上最近
    public void findUpOutBtn() {
        for (int i = 1; i < e.btnUp.length; i++) {
            if (e.btnUp[i] == 1 && i >= e.curPostion && i < e.dest_floor) {
                e.dest_floor = i;
                break;
            }
        }
    }

    /**
     * 输出程序当前状态
     */
    public void printStatus() {
        Iterator<?> i = e.msgQueen.iterator();
        System.out.println("\n消息序列");
        while (i.hasNext()) {
            int[] p = (int[]) i.next();
            System.out.print(p[0] + "," + p[1] + "," + p[2] + "," + p[3] + "|");
        }
        System.out.println("\n当前楼层:" + e.curPostion + " 目标楼层:" + e.dest_floor + " 运行状态:" + e.state + " 电梯人数:" + e.num);
    }

    /**
     * 分析消息序列，并将其转换为电梯内，电梯外按钮信号列表
     */
    public void analysisMsg() {
        // 每次运行都初始化按钮列表为空
        e.btnInside = new int[e.TOP + 1];
        e.btnUp = new int[e.TOP + 1];
        e.btnDown = new int[e.TOP + 1];
        // 遍历消息序列
        Iterator<int[]> msg = e.msgQueen.iterator();
        int[] p;
        while (msg.hasNext()) {
            p = (int[]) msg.next();
            if (p[3] == 1) {
                // 电梯内按钮
                e.btnInside[p[2]] = 1;
            }
            if (p[3] == 0 && p[1] == 1) {
                // 电梯外上行按钮
                e.btnUp[p[0]] = 1;
            } else if (p[3] == 0 && p[1] == -1) {
                // 电梯外下行按钮
                e.btnDown[p[0]] = 1;
            }
        }
    }
}