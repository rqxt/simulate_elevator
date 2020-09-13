package com.mainProg;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;


public class Elevator extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static int speed = 1;					// 电梯移速
	private static int height = 40;					// 电梯高度
	int TOP = 20;					// 设置楼层
	int num = 0;						// 电梯现有人数
	int state = 0;						// 电梯运行方向
	int curPostion = 1; 				// 电梯所在楼层
	int dest_floor = curPostion;		// 电梯目标楼层
	
	int[] btnInside = new int[TOP+1];	// *电梯内按钮列表
	int[] btnUp = new int[TOP+1];		// *电梯外上行按钮列表
	int[] btnDown = new int[TOP+1];		// *电梯内上行列表
	
	// 电梯
	JPanel jpl;
	JLabel door1, door2;
	// 走廊
	JTextArea[] jtas = new JTextArea[TOP];
	JLabel[] jlbs = new JLabel[TOP];
	// 消息
	CopyOnWriteArrayList<int[]> msgQueen = new CopyOnWriteArrayList<int[]>();
	
	Color[] colors = new Color[] {new Color(190,231,233), new Color(214,213,183), new Color(244,96,108), new Color(0,0,0)};
	
	public Elevator() {
		setBounds(10, 10, 500, TOP*height+60+47);
		setLayout(null);
		setTitle("电梯模拟");
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
		JLabel jlb = new JLabel(df.format(new Date()), JLabel.CENTER);
		jlb.setBounds(0, 0, 500, 60);
		jlb.setOpaque(true);
		jlb.setFont(new Font("MicroSoft Yahei",1,25));
		jlb.setBackground(new Color(209,186,116));
		add(jlb); 
		
		for(int i=0; i<TOP; i++) {
			JTextArea jta1 = new JTextArea("  ");
			jta1.setBounds(300, 60+i*height, 200, height);
			jta1.setOpaque(true);
			jta1.setBackground(colors[i%2]);
			jta1.setFont(new Font("MicroSoft Yahei", 1, 28));
			jta1.setEditable(false);
			jtas[i] = jta1;
			add(jta1);
			
			JLabel jlb1 =new JLabel(TOP-i+"F", JLabel.CENTER);
			jlb1.setBounds(0, 60+i*height, 100, height);
			jlb1.setOpaque(true);
			jlb1.setBackground(colors[i%2]);
			jlb1.setFont(new Font("MicroSoft Yahei", 1, 28));
			jlbs[i] = jlb1;
			add(jlb1);
		}
		
		// 设置电梯
		jpl = new JPanel();
		jpl.setBounds(100, 60+TOP*height-height, 200, height);
		jpl.setOpaque(true);
		jpl.setBackground(Color.DARK_GRAY);
		jpl.setLayout(new GridLayout(1,2));
		door1 = new JLabel("初始状态", JLabel.CENTER);
		door1.setSize(100, height);
		door1.setOpaque(true);
		door1.setFont(new Font("MicroSoft Yahei", 1, 20));
		door1.setBackground(new Color(230,206,172));
		door2 = new JLabel("人数:"+this.num+"", JLabel.CENTER);
		door2.setOpaque(true);
		door2.setBackground(new Color(230,206,172));
		door2.setSize(100, height);
		door2.setFont(new Font("MicroSoft Yahei", 1, 20));

		jpl.add(door1);
		jpl.add(door2);
		add(jpl);
		
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);;
	}
	
	public void moveUp() {
		int i = 0;
		while(i<height) {
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
		door1.setText("目标:"+this.dest_floor+"F");
	}
	public void moveDown() {
		int i = 0;
		while(i<height) {
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
		door1.setText("目标:"+this.dest_floor+"F");
	}
	public void openDoor() throws InterruptedException {
		Thread.sleep(50);
	}
	public void closeDoor() throws InterruptedException {
		Thread.sleep(50);
		door2.setText("人数:"+this.num+"");
	}
	public static void main(String[] args) throws InterruptedException {
		// 生成图形化界面
		Elevator e = new Elevator();
		writeMsg wm = new writeMsg(e.TOP, e.msgQueen, e.jtas);
		wm.start();
		Thread.sleep(1000);
		elevatorMonitor em = new elevatorMonitor(e);
		em.start();
	}
}

/* 人对象*/
class People{
	int pos;
	int dest;
	int dir;
	int inside = 0;
	public People(int TOP) {
		pos = new Random().nextInt(TOP)+1;
		dest = new Random().nextInt(TOP)+1;
		while(dest == pos) {
			dest = new Random().nextInt(TOP)+1;
		}
		if(dest <= pos) {
			dir = -1;
		}else {
			dir = 1;
		}
		
	}
}
/* 随机写入消息*/
class writeMsg extends Thread{
	int TOP;
	CopyOnWriteArrayList<int[]> msgQueen;
	JTextArea[] jtas;
	public writeMsg(int TOP, CopyOnWriteArrayList<int[]> msgQueen, JTextArea[] jtas) {
		this.TOP = TOP;
		this.msgQueen = msgQueen;
		this.jtas = jtas;
	}
	public void run() {
		while(true) {
			try {
				People p = new People(TOP);
				int[] msg = new int[] {p.pos, p.dir, p.dest, p.inside};
				jtas[TOP-p.pos].setText("呉"+jtas[TOP-p.pos].getText());
				msgQueen.add(msg);
				Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
			}
		}
	}
}


class elevatorMonitor extends Thread{
	Elevator e;
	int temp_state;
	public elevatorMonitor(Elevator e) {
		this.e = e;
	}
	public void run() {
		while(true) {
			try {
				Thread.sleep(50);
				// 1.分析消息序列，转换成电梯内按钮和上/下行按钮列表
				analysisMsg();
				// 3.进行移动和人员进出
				printStatus();
				move();
				// 2.根据按钮列表和电梯运行状态确定目标楼层
				setDestFloor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void move() throws InterruptedException {
		// 如果电梯不在目标楼层，移动
		if(e.curPostion != e.dest_floor) {
			if(e.curPostion>e.dest_floor) {
				e.state = -1;
			}else if(e.curPostion < e.dest_floor){
				e.state = 1;
			}
			if(e.state == 1) {
				e.curPostion += 1;
				e.moveUp();
			}
			else if(e.state == -1){
				e.curPostion -= 1;
				e.moveDown();
			}
		}
		else {
			// 电梯停在了目标楼层，人员进电梯
			temp_state = e.state;
			e.state = 0;	// 停止电梯
			Iterator<?> msg = e.msgQueen.iterator();
			CopyOnWriteArrayList<int[]> temp = new CopyOnWriteArrayList<int[]>();
			while(msg.hasNext()) {
				int[] p = (int[]) msg.next();
				e.colors[3] = e.jlbs[e.TOP-e.curPostion].getBackground();
				// 进电梯
				if(p[0] == e.curPostion && p[1] == temp_state) {
					e.openDoor();
					// 防止进两次电梯
					if(p[3] == 0) {
						p[3] = 1; 
						e.num++;
						e.jtas[e.TOP-e.curPostion].replaceRange("", 0, 1);
						e.door2.setText("进入电梯");
						Thread.sleep(180);
					}
				}
				// 出电梯
				if(p[3] == 1 && p[2] == e.curPostion) {
					e.jlbs[e.TOP-e.curPostion].setBackground(e.colors[2]);
					e.openDoor();
					temp.add(p);
					e.num--;
					e.door1.setText("离开电梯");
					Thread.sleep(180);
				}
				e.jlbs[e.TOP-e.curPostion].setBackground(e.colors[3]);
			}
			e.closeDoor();
			
			for(Object i : temp) {
				e.msgQueen.remove(i);
			}
		}
	}
	public void setDestFloor() {
		// 消息序列不为空的时候
		if(e.msgQueen.isEmpty()==false) {
			// （初始）如果电梯是停止状态且电梯没有人，则消息序列中的第一个为目标楼层
			if(e.state == 0 && e.num == 0) {
				if(temp_state == 0) {
					int[] msg = (int[])e.msgQueen.iterator().next();
					e.dest_floor = msg[0];
					if(e.curPostion == msg[0]) {
						e.dest_floor = msg[2];
					}	
				}else if(temp_state == 1) {
					findA();
				}else if(temp_state == -1) {
					findB();
				}
	
				if(e.dest_floor > e.curPostion) {
					e.state = 1;
				}else if(e.dest_floor < e.curPostion) {
					e.state = -1;
				}
			}		
			// 电梯运行过程中，分析电梯内，电梯外按钮列表，确定目标楼层
			if(e.state == 1) {
				// 内部按钮：向上最近
				findUpInBtn(0);
				// 外部按钮：向上最近
				findUpOutBtn();
			}
			else if(e.state == -1) {
				// 内部按钮：向下最近
				findDownInBtn(0);
				// 外部按钮：向下最近
				findDownOutBtn();
			}
			// 电梯停靠某一楼层，并且电梯内还有人
			else if(e.state == 0 && e.num != 0) {
				e.state = temp_state;	// 恢复停靠前的状态
				if(e.state == 1) {
					// 参数 1 表示电梯内一定有人，因此会遍历所有的内部按钮
					// 当前层向上没找到，就会往当前层下找最近按钮
					this.findUpInBtn(1);
				}
				else if(e.state == -1) {
					this.findDownInBtn(1);
				}
			}
		}
	}
	// 向上运行时候
	public void findA() {
		int temp_dest = e.dest_floor;
		// 1.找到向上最近的外部按钮
		for(int i = 1; i<e.btnUp.length; i++) {
			if(e.btnUp[i] == 1 && i >= e.curPostion){
				e.dest_floor = i;
				break;
			}
		}
		if(temp_dest != e.dest_floor) {
			System.out.println("1.找到向上最近的外部按钮");
			return;
		}
		// 2.如果没有1没有找到，则找向下的最高层的按钮
		findHighestDown();
		if(temp_dest != e.dest_floor) {
			System.out.println("2.如果没有1没有找到，则找向下的最高层的按钮");
			return;
		}
		// 3.如果12都没有改变dest_floor的值，那么就找向上的最低层的按钮
		findLowestUp();
		if(temp_dest != e.dest_floor) {
			System.out.println("3.如果12都没有改变dest_floor的值，那么就找向上的最低层的按钮");
			return;
		}
		System.out.println("Up: Error");
	}
	
	// 向下运行时候
	public void findB() {
		int temp_dest = e.dest_floor;
		// 1.找到向下最近的外部按钮
		for(int i = e.btnDown.length-1; i>=1; i--) {
			if(e.btnDown[i] == 1 && i <= e.curPostion) {
				e.dest_floor = i;
				break;
			}
		}
		if(temp_dest != e.dest_floor) {
			System.out.println("1.找到向下最近的外部按钮");
			return;
		}
		// 2.如果没有1没有找到，则找向上的最低层的按钮
		findLowestUp();
		if(temp_dest != e.dest_floor) {
			System.out.println("2.如果没有1没有找到，则找向上的最低层的按钮");
			return;
		}
		// 3.如果12都没有改变dest_floor的值，那么就找向下的最高层的按钮
		findHighestDown();
		if(temp_dest != e.dest_floor) {
			System.out.println("3.如果12都没有改变dest_floor的值，那么就找向下的最高层的按钮");
			return;
		}
		System.out.println("Down: Error");
	}
	
	public void findLowestUp() {
		/* 找向上的最低层的按钮 */
		for(int i = 1; i<e.btnUp.length; i++) {
			if(e.btnUp[i] == 1 && i <= e.curPostion){
				e.dest_floor = i;
				break;
			}
		}
	}
	public void findHighestDown() {
		// 找向下的最高层的按钮
		for(int i = e.btnDown.length-1; i>=1; i--) {
			if(e.btnDown[i] == 1 && i >= e.curPostion) {
				e.dest_floor = i;
				break;
			}
		}
	}
	/* 内部按钮：（往下）小于当前位置最近的按钮信号*/
	public void findDownInBtn(int changeState) {
		for(int i = e.btnInside.length-1; i>=1; i--) {
			if(e.btnInside[i] == 1 && i <= e.curPostion) {
				e.dest_floor = i;
				break;
			}
			if(i == 1 && changeState == 1) {
				findUpInBtn(0);
				e.state *= -1;
			}
		}
	}
	/* 内部按钮：（往上）大于当前位置最近的按钮信号*/
	public void findUpInBtn(int changeState) {
		for(int i = 1; i<e.btnInside.length; i++) {
			if(e.btnInside[i] == 1 && i >= e.curPostion) {
				e.dest_floor = i;
				break;
			}
			if(i == e.btnInside.length-1 && changeState == 1) {
				findDownInBtn(0);
				e.state *= -1;
			}
		}
	}
	// 外部按钮：向下最近
	public void findDownOutBtn() {
		for(int i = e.btnDown.length-1; i>=1; i--) {
			if(e.btnDown[i] == 1 && i <= e.curPostion && i>e.dest_floor) {
				e.dest_floor = i;
				break;
			}
		}
	}
	// 外部按钮：向上最近
	public void findUpOutBtn() {
		for(int i = 1; i<e.btnUp.length; i++) {
			if(e.btnUp[i] == 1 && i >= e.curPostion && i <e.dest_floor) {
				e.dest_floor = i;
				break;
			}
		}
	}
	/** 输出程序当前状态*/
	public void printStatus() {
		Iterator<?> i = e.msgQueen.iterator();
		System.out.println("\n消息序列");
		while(i.hasNext()) {
			int[] p = (int[])i.next();
			System.out.print(p[0]+","+p[1]+","+p[2]+","+p[3]+"|");
		}
		System.out.println("\n当前楼层:"+e.curPostion+" 目标楼层:"+e.dest_floor
				+" 运行状态:"+e.state+" 电梯人数:"+e.num);
	}
	/** 分析消息序列，并将其转换为电梯内，电梯外按钮信号列表*/
	public void analysisMsg() {
		// 每次运行都初始化按钮列表为空
		e.btnInside = new int[e.TOP+1];
		e.btnUp = new int[e.TOP+1];
		e.btnDown = new int[e.TOP+1];
		// 遍历消息序列
		Iterator<int[]> msg = e.msgQueen.iterator();
		int[] p;
		while(msg.hasNext()) {
			p = (int[])msg.next();
			if(p[3]==1) {
				// 电梯内按钮
				e.btnInside[p[2]] = 1;
			}
			if(p[3]==0 && p[1]==1) {
				// 电梯外上行按钮
				e.btnUp[p[0]] = 1;
			}
			else if(p[3]==0 && p[1]==-1) {
				// 电梯外下行按钮
				e.btnDown[p[0]] = 1;
			}
		}
	}
}