package main;

import java.util.Random;

/**
 * 人对象（一个乘坐电梯的人），用于消息队列的消息生成
 */
public class People {
    // 所在楼层
    int pos;
    // 目标楼层
    int dest;
    // 在电梯外按下的按钮（向上、向下）
    int dir;
    // 是否在电梯内（默认不在）
    int inside = 0;

    /**
     * 构造方法，给定楼层高度，随机生成 人 所在楼层/目标楼层/，并计算出按钮（上、下）
     * @param TOP
     */
    public People(int TOP) {
        pos = new Random().nextInt(TOP) + 1;
        dest = new Random().nextInt(TOP) + 1;
        while (dest == pos) {
            dest = new Random().nextInt(TOP) + 1;
        }
        if (dest <= pos) {
            dir = -1;
        } else {
            dir = 1;
        }
    }
}
