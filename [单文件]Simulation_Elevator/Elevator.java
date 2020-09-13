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
	private static int speed = 1;					// ��������
	private static int height = 40;					// ���ݸ߶�
	int TOP = 20;					// ����¥��
	int num = 0;						// ������������
	int state = 0;						// �������з���
	int curPostion = 1; 				// ��������¥��
	int dest_floor = curPostion;		// ����Ŀ��¥��
	
	int[] btnInside = new int[TOP+1];	// *�����ڰ�ť�б�
	int[] btnUp = new int[TOP+1];		// *���������а�ť�б�
	int[] btnDown = new int[TOP+1];		// *�����������б�
	
	// ����
	JPanel jpl;
	JLabel door1, door2;
	// ����
	JTextArea[] jtas = new JTextArea[TOP];
	JLabel[] jlbs = new JLabel[TOP];
	// ��Ϣ
	CopyOnWriteArrayList<int[]> msgQueen = new CopyOnWriteArrayList<int[]>();
	
	Color[] colors = new Color[] {new Color(190,231,233), new Color(214,213,183), new Color(244,96,108), new Color(0,0,0)};
	
	public Elevator() {
		setBounds(10, 10, 500, TOP*height+60+47);
		setLayout(null);
		setTitle("����ģ��");
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy��MM��dd��");
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
		
		// ���õ���
		jpl = new JPanel();
		jpl.setBounds(100, 60+TOP*height-height, 200, height);
		jpl.setOpaque(true);
		jpl.setBackground(Color.DARK_GRAY);
		jpl.setLayout(new GridLayout(1,2));
		door1 = new JLabel("��ʼ״̬", JLabel.CENTER);
		door1.setSize(100, height);
		door1.setOpaque(true);
		door1.setFont(new Font("MicroSoft Yahei", 1, 20));
		door1.setBackground(new Color(230,206,172));
		door2 = new JLabel("����:"+this.num+"", JLabel.CENTER);
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
		door1.setText("Ŀ��:"+this.dest_floor+"F");
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
		door1.setText("Ŀ��:"+this.dest_floor+"F");
	}
	public void openDoor() throws InterruptedException {
		Thread.sleep(50);
	}
	public void closeDoor() throws InterruptedException {
		Thread.sleep(50);
		door2.setText("����:"+this.num+"");
	}
	public static void main(String[] args) throws InterruptedException {
		// ����ͼ�λ�����
		Elevator e = new Elevator();
		writeMsg wm = new writeMsg(e.TOP, e.msgQueen, e.jtas);
		wm.start();
		Thread.sleep(1000);
		elevatorMonitor em = new elevatorMonitor(e);
		em.start();
	}
}

/* �˶���*/
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
/* ���д����Ϣ*/
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
				jtas[TOP-p.pos].setText("��"+jtas[TOP-p.pos].getText());
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
				// 1.������Ϣ���У�ת���ɵ����ڰ�ť����/���а�ť�б�
				analysisMsg();
				// 3.�����ƶ�����Ա����
				printStatus();
				move();
				// 2.���ݰ�ť�б�͵�������״̬ȷ��Ŀ��¥��
				setDestFloor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void move() throws InterruptedException {
		// ������ݲ���Ŀ��¥�㣬�ƶ�
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
			// ����ͣ����Ŀ��¥�㣬��Ա������
			temp_state = e.state;
			e.state = 0;	// ֹͣ����
			Iterator<?> msg = e.msgQueen.iterator();
			CopyOnWriteArrayList<int[]> temp = new CopyOnWriteArrayList<int[]>();
			while(msg.hasNext()) {
				int[] p = (int[]) msg.next();
				e.colors[3] = e.jlbs[e.TOP-e.curPostion].getBackground();
				// ������
				if(p[0] == e.curPostion && p[1] == temp_state) {
					e.openDoor();
					// ��ֹ�����ε���
					if(p[3] == 0) {
						p[3] = 1; 
						e.num++;
						e.jtas[e.TOP-e.curPostion].replaceRange("", 0, 1);
						e.door2.setText("�������");
						Thread.sleep(180);
					}
				}
				// ������
				if(p[3] == 1 && p[2] == e.curPostion) {
					e.jlbs[e.TOP-e.curPostion].setBackground(e.colors[2]);
					e.openDoor();
					temp.add(p);
					e.num--;
					e.door1.setText("�뿪����");
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
		// ��Ϣ���в�Ϊ�յ�ʱ��
		if(e.msgQueen.isEmpty()==false) {
			// ����ʼ�����������ֹͣ״̬�ҵ���û���ˣ�����Ϣ�����еĵ�һ��ΪĿ��¥��
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
			// �������й����У����������ڣ������ⰴť�б�ȷ��Ŀ��¥��
			if(e.state == 1) {
				// �ڲ���ť���������
				findUpInBtn(0);
				// �ⲿ��ť���������
				findUpOutBtn();
			}
			else if(e.state == -1) {
				// �ڲ���ť���������
				findDownInBtn(0);
				// �ⲿ��ť���������
				findDownOutBtn();
			}
			// ����ͣ��ĳһ¥�㣬���ҵ����ڻ�����
			else if(e.state == 0 && e.num != 0) {
				e.state = temp_state;	// �ָ�ͣ��ǰ��״̬
				if(e.state == 1) {
					// ���� 1 ��ʾ������һ�����ˣ���˻�������е��ڲ���ť
					// ��ǰ������û�ҵ����ͻ�����ǰ�����������ť
					this.findUpInBtn(1);
				}
				else if(e.state == -1) {
					this.findDownInBtn(1);
				}
			}
		}
	}
	// ��������ʱ��
	public void findA() {
		int temp_dest = e.dest_floor;
		// 1.�ҵ�����������ⲿ��ť
		for(int i = 1; i<e.btnUp.length; i++) {
			if(e.btnUp[i] == 1 && i >= e.curPostion){
				e.dest_floor = i;
				break;
			}
		}
		if(temp_dest != e.dest_floor) {
			System.out.println("1.�ҵ�����������ⲿ��ť");
			return;
		}
		// 2.���û��1û���ҵ����������µ���߲�İ�ť
		findHighestDown();
		if(temp_dest != e.dest_floor) {
			System.out.println("2.���û��1û���ҵ����������µ���߲�İ�ť");
			return;
		}
		// 3.���12��û�иı�dest_floor��ֵ����ô�������ϵ���Ͳ�İ�ť
		findLowestUp();
		if(temp_dest != e.dest_floor) {
			System.out.println("3.���12��û�иı�dest_floor��ֵ����ô�������ϵ���Ͳ�İ�ť");
			return;
		}
		System.out.println("Up: Error");
	}
	
	// ��������ʱ��
	public void findB() {
		int temp_dest = e.dest_floor;
		// 1.�ҵ�����������ⲿ��ť
		for(int i = e.btnDown.length-1; i>=1; i--) {
			if(e.btnDown[i] == 1 && i <= e.curPostion) {
				e.dest_floor = i;
				break;
			}
		}
		if(temp_dest != e.dest_floor) {
			System.out.println("1.�ҵ�����������ⲿ��ť");
			return;
		}
		// 2.���û��1û���ҵ����������ϵ���Ͳ�İ�ť
		findLowestUp();
		if(temp_dest != e.dest_floor) {
			System.out.println("2.���û��1û���ҵ����������ϵ���Ͳ�İ�ť");
			return;
		}
		// 3.���12��û�иı�dest_floor��ֵ����ô�������µ���߲�İ�ť
		findHighestDown();
		if(temp_dest != e.dest_floor) {
			System.out.println("3.���12��û�иı�dest_floor��ֵ����ô�������µ���߲�İ�ť");
			return;
		}
		System.out.println("Down: Error");
	}
	
	public void findLowestUp() {
		/* �����ϵ���Ͳ�İ�ť */
		for(int i = 1; i<e.btnUp.length; i++) {
			if(e.btnUp[i] == 1 && i <= e.curPostion){
				e.dest_floor = i;
				break;
			}
		}
	}
	public void findHighestDown() {
		// �����µ���߲�İ�ť
		for(int i = e.btnDown.length-1; i>=1; i--) {
			if(e.btnDown[i] == 1 && i >= e.curPostion) {
				e.dest_floor = i;
				break;
			}
		}
	}
	/* �ڲ���ť�������£�С�ڵ�ǰλ������İ�ť�ź�*/
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
	/* �ڲ���ť�������ϣ����ڵ�ǰλ������İ�ť�ź�*/
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
	// �ⲿ��ť���������
	public void findDownOutBtn() {
		for(int i = e.btnDown.length-1; i>=1; i--) {
			if(e.btnDown[i] == 1 && i <= e.curPostion && i>e.dest_floor) {
				e.dest_floor = i;
				break;
			}
		}
	}
	// �ⲿ��ť���������
	public void findUpOutBtn() {
		for(int i = 1; i<e.btnUp.length; i++) {
			if(e.btnUp[i] == 1 && i >= e.curPostion && i <e.dest_floor) {
				e.dest_floor = i;
				break;
			}
		}
	}
	/** �������ǰ״̬*/
	public void printStatus() {
		Iterator<?> i = e.msgQueen.iterator();
		System.out.println("\n��Ϣ����");
		while(i.hasNext()) {
			int[] p = (int[])i.next();
			System.out.print(p[0]+","+p[1]+","+p[2]+","+p[3]+"|");
		}
		System.out.println("\n��ǰ¥��:"+e.curPostion+" Ŀ��¥��:"+e.dest_floor
				+" ����״̬:"+e.state+" ��������:"+e.num);
	}
	/** ������Ϣ���У�������ת��Ϊ�����ڣ������ⰴť�ź��б�*/
	public void analysisMsg() {
		// ÿ�����ж���ʼ����ť�б�Ϊ��
		e.btnInside = new int[e.TOP+1];
		e.btnUp = new int[e.TOP+1];
		e.btnDown = new int[e.TOP+1];
		// ������Ϣ����
		Iterator<int[]> msg = e.msgQueen.iterator();
		int[] p;
		while(msg.hasNext()) {
			p = (int[])msg.next();
			if(p[3]==1) {
				// �����ڰ�ť
				e.btnInside[p[2]] = 1;
			}
			if(p[3]==0 && p[1]==1) {
				// ���������а�ť
				e.btnUp[p[0]] = 1;
			}
			else if(p[3]==0 && p[1]==-1) {
				// ���������а�ť
				e.btnDown[p[0]] = 1;
			}
		}
	}
}