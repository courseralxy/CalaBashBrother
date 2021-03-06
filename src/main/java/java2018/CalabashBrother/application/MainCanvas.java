package java2018.CalabashBrother.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.scene.paint.Color;
import java2018.CalabashBrother.BattleField.BattleFields;
import java2018.CalabashBrother.Beings.CalabashBrother;
import java2018.CalabashBrother.Beings.CalabashBrothers;
import java2018.CalabashBrother.Beings.Creature;
import java2018.CalabashBrother.Director.*;
import javafx.application.Platform;
//import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
//import javafx.scene.control.TextField;
import javafx.scene.image.Image;
//import javafx.scene.input.KeyEvent;

public class MainCanvas extends Canvas{
	private int goodboyCount = 8;
	private int badboyCount = -1;
	private boolean begin = false;
	private BattleFields BFs;
	private Director director;
	private GraphicsContext gc;
	private String fileName;
	private boolean battleOver;
	int play;
	private FileWriter writer;
	private  BufferedReader reader;
	ExecutorService exec = Executors.newCachedThreadPool();
	static Image BG;// = new Image(new File("C:\\Users\\13668\\Desktop\\background.jpg").toURI().toURL().toString());
	
	static Image CB1 = null;// = new Image(new File("C:\\Users\\13668\\Desktop\\1.jpg").toURI().toURL().toString());
	static Image CB2 = null;
	static Image CB3 = null;
	static Image CB4 = null;
	static Image CB5 = null;
	static Image CB6 = null;
	static Image CB7 = null;
	static Image GP = null;
	static Image LL = null;
	static Image SC = null;
	static Image SN = null;
	static int menuBarHeight = 35;
	static {
		try {
			BG = new Image(new File("resource/background.jpg").toURI().toURL().toString());
			CB1 = new Image(new File("resource/1.jpg").toURI().toURL().toString());
			CB2 = new Image(new File("resource/2.jpg").toURI().toURL().toString());
			CB3 = new Image(new File("resource/3.jpg").toURI().toURL().toString());
			CB4 = new Image(new File("resource/4.jpg").toURI().toURL().toString());
			CB5 = new Image(new File("resource/5.jpg").toURI().toURL().toString());
			CB6 = new Image(new File("resource/6.jpg").toURI().toURL().toString());
			CB7 = new Image(new File("resource/7.jpg").toURI().toURL().toString());
			GP = new Image(new File("resource/Grandpa.jpg").toURI().toURL().toString());
			LL = new Image(new File("resource/LouLuo.jpg").toURI().toURL().toString());
			SC = new Image(new File("resource/Scopion.jpg").toURI().toURL().toString());
			SN = new Image(new File("resource/Snake.jpg").toURI().toURL().toString());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//private boolean isRunning = true;
	//private long sleep = 100;
	
	private Thread thread = new Thread(new Runnable() {
		
		@Override
		public void run(){
			while(true) {
				System.err.println("running "+play);
				//draw();
				//BFs.BFOutput();
				Platform.runLater(()->{
					if(play==1) {
						if((goodboyCount==0)||(badboyCount==0)) {
							System.err.println("goodboyCount:"+goodboyCount+" badboyCount:"+badboyCount);
						}
						else {
						saveBattleField();
						draw();
						}
					}
					else if(play==0)
						replay();

				});
				try {
					Thread.sleep(50);
					
				}catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	});
	public MainCanvas() {
		super(1000, 500+menuBarHeight);
		play = -1;
		battleOver = true;
		BFs=new BattleFields();
		director = new Director(BFs,new CalabashBrothers());
		gc = this.getGraphicsContext2D();
		gc.drawImage(BG, 0, 0+menuBarHeight,1000,500);

		
		gc.setLineWidth(2);
		
		for(int i = 0; i <= 20; i++)
			gc.strokeLine(50*i, 0+menuBarHeight, 50*i, 500+menuBarHeight);
		for(int i = 0; i <= 10;i++)
			gc.strokeLine(0, 50*i+menuBarHeight, 1000, 50*i+menuBarHeight);
		thread.start();
	}
	public void flashBegin() {
		if((!begin)&&play==1) {
			System.err.println("not begin and begin: "+begin);
			creatureThreadRun();
		}
	}
	public void drawBackground() {
		gc.drawImage(BG, 0, 0+menuBarHeight,1000,500);
		for(int i = 0; i <= 20; i++)
			gc.strokeLine(50*i, 0+menuBarHeight, 50*i, 500+menuBarHeight);
		for(int i = 0; i <= 10;i++)
			gc.strokeLine(0, 50*i+menuBarHeight, 1000, 50*i+menuBarHeight);
	}
	public void newWar() {
		battleOver=false;
		BFs=new BattleFields();
		director = new Director(BFs,new CalabashBrothers());
		director.setPos();
		draw();
	}
	public void draw() {
		System.err.println("drawing");
		int newGoodboyCount = 0;
		int newBadboyCount = 0;
		gc.clearRect(0, 0, 1000, 500+menuBarHeight);
		
		//画背景
		gc.drawImage(BG, 0, 0+menuBarHeight,1000,500);
		//画格子
		//gc.setStroke(Color.BLACK);
		for(int i = 0; i <= 20; i++)
			gc.strokeLine(50*i, 0+menuBarHeight, 50*i, 500+menuBarHeight);
		for(int i = 0; i <= 10;i++)
			gc.strokeLine(0, 50*i+menuBarHeight, 1000, 50*i+menuBarHeight);
		//画人物
		for(int i = 0; i < 10;i++) {
			for(int j = 0; j < 20;j++) {
				if(BFs.isEmpty(i, j)||(!BFs.getCreature(i, j).isLiving()))continue;
				if(((double)BFs.getCreature(i, j).getHP())/((double)BFs.getCreature(i, j).getFullHP())>0.7)
					gc.setStroke(Color.LIGHTGREEN);
				else if(((double)BFs.getCreature(i, j).getHP())/((double)BFs.getCreature(i, j).getFullHP())>0.4)
					gc.setStroke(Color.YELLOW);
				else
					gc.setStroke(Color.RED);
				gc.setLineWidth(8);
				if((50*j+5)<=50*j-3+((double)BFs.getCreature(i, j).getHP())/((double)BFs.getCreature(i, j).getFullHP())*48) {
					gc.strokeLine(50*j+5, 50*i+menuBarHeight, 50*j-3+((double)BFs.getCreature(i, j).getHP())/((double)BFs.getCreature(i, j).getFullHP())*48, 50*i+menuBarHeight);
				}
				else
					gc.strokeLine(50*j+5, 50*i+menuBarHeight, 50*j+5+((double)BFs.getCreature(i, j).getHP())/((double)BFs.getCreature(i, j).getFullHP())*48, 50*i+menuBarHeight);

				gc.setStroke(Color.BLACK);
				gc.setLineWidth(2);
				switch(BFs.creatureType(i, j)) {
				case "java2018.CalabashBrother.Beings.CalabashBrother":
					newGoodboyCount++;
					switch(BFs.CBName(i, j)) {
					case "老大":
						gc.drawImage(CB1, 50*j+1, 50*i+menuBarHeight+1,48,48);
						break;
					case "老二":
						gc.drawImage(CB2, 50*j+1, 50*i+menuBarHeight+1,48,48);
						break;
					case "老三":
						gc.drawImage(CB3, 50*j+1, 50*i+menuBarHeight+1,48,48);
						break;
					case "老四":
						gc.drawImage(CB4, 50*j+1, 50*i+menuBarHeight+1,48,48);
						break;
					case "老五":
						gc.drawImage(CB5, 50*j+1, 50*i+menuBarHeight+1,48,48);
						break;
					case "老六":
						gc.drawImage(CB6, 50*j+1, 50*i+menuBarHeight+1,48,48);
						break;
					case "老七":
						gc.drawImage(CB7, 50*j+1, 50*i+menuBarHeight+1,48,48);
						break;
					}					
					break;
				case "java2018.CalabashBrother.Beings.GrandPa":
					newGoodboyCount++;
					gc.drawImage(GP, 50*j+1, 50*i+menuBarHeight+1,48,48);
					break;
				case "java2018.CalabashBrother.Beings.LouLuo":
					if(goodboyCount==0)
						BFs.getCreature(i, j).killThread();;
					newBadboyCount++;
					gc.drawImage(LL, 50*j+1, 50*i+menuBarHeight+2,48,48);
					break;
				case "java2018.CalabashBrother.Beings.Snake":
					newBadboyCount++;
					gc.drawImage(SN, 50*j+1, 50*i+menuBarHeight+1,48,48);
					break;
				case "java2018.CalabashBrother.Beings.Scorpion":
					newBadboyCount++;
					gc.drawImage(SC, 50*j+1, 50*i+menuBarHeight+1,48,48);
					break;
				}
			}
		}
		goodboyCount = newGoodboyCount;
		badboyCount = newBadboyCount;
		if(goodboyCount==0||badboyCount==0) {
			for(int i = 0; i < 10;i++) {
				for(int j = 0; j < 20;j++) {
					if(BFs.isEmpty(i, j)||(!BFs.getCreature(i, j).isLiving()))continue;
					else
						BFs.getCreature(i, j).killThread();
				}
			}
			setPlay(-1);
			battleOver=true;
		}
			
	}
	public boolean isBattleOver() {
		return battleOver;
	}
	public void creatureThreadRun() {
		begin = true;
		battleOver=false;
		BattleFields BFs= director.getBFs();
		
		gc.clearRect(0, 0, 1000, 500+menuBarHeight);
		gc.drawImage(BG, 0, 0+menuBarHeight,1000,500);
			for(int i = 0; i <= 20; i++)
				gc.strokeLine(50*i, 0+menuBarHeight, 50*i, 500+menuBarHeight);
			for(int i = 0; i <= 10;i++)
				gc.strokeLine(0, 50*i+menuBarHeight, 1000, 50*i+menuBarHeight);
		//ExecutorService exec = Executors.newCachedThreadPool();
		for(int i = 0; i < 10;i++) {
			for(int j = 0;j<20;j++) {
				if(!BFs.isEmpty(i, j)) {
					Creature createExec = BFs.getCreature(i, j);
					exec.execute(createExec);
					//Thread th = new Thread(BFs.getCreature(i, j));
					//th.setDaemon(true);
					//th.start();
					//exec.execute(BFs.getCreature(i, j));
				}
			}
		}
		//exec.shutdown();
	}
	public void setPlay(int play) {
		System.err.println(this.play);
		if(this.play==-1) {
			System.err.println("------------begin set false-------------");
			begin = false;
		}
		this.play=play;
		System.err.println(play);
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
		try {
			reader = new BufferedReader(new FileReader(new File(fileName)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void replay() {
		System.err.println("replaying");
			gc.clearRect(0, 0, 1000, 500+menuBarHeight);
			gc.drawImage(BG, 0, 0+menuBarHeight,1000,500);
			for(int i = 0; i <= 20; i++)
				gc.strokeLine(50*i, 0+menuBarHeight, 50*i, 500+menuBarHeight);
			for(int i = 0; i <= 10;i++)
				gc.strokeLine(0, 50*i+menuBarHeight, 1000, 50*i+menuBarHeight);
			//while(true) {
				
			for(int i = 0;i<10;i++) {
				String line=null;
				try {
					if((line = reader.readLine())==null)return;
				
				System.err.println(line);
				String[]BFsLine = line.split(" ");
				System.out.println(BFsLine[0]);
				
					
				for(int j = 0; j < 20; j++) {
					switch(BFsLine[j]) {
					case "R":
						gc.drawImage(CB1, 50*j+1, 50*i+menuBarHeight+1,48,48);
						break;
					case "O":
						gc.drawImage(CB2, 50*j+1, 50*i+menuBarHeight+1,48,48);
						break;
					case "Y":
						gc.drawImage(CB3, 50*j+1, 50*i+menuBarHeight+1,48,48);
						break;
					case "G":
						gc.drawImage(CB4, 50*j+1, 50*i+menuBarHeight+1,48,48);
						break;
					case "C":
						gc.drawImage(CB5, 50*j+1, 50*i+menuBarHeight+1,48,48);
						break;
					case "B":
						gc.drawImage(CB6, 50*j+1, 50*i+menuBarHeight+1,48,48);
						break;
					case "P":
						gc.drawImage(CB7, 50*j+1, 50*i+menuBarHeight+1,48,48);
						break;
					case "g":
						gc.drawImage(GP, 50*j+1, 50*i+menuBarHeight+1,48,48);
						break;
					case "S":
						gc.drawImage(SC, 50*j+1, 50*i+menuBarHeight+1,48,48);
						break;
					case "L":
						gc.drawImage(LL, 50*j+1, 50*i+menuBarHeight+2,48,48);
						break;
					case "s":
						gc.drawImage(SN, 50*j+1, 50*i+menuBarHeight+1,48,48);
						break;
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}}
			
		//}
	}
	public void saveBattleField() {
		System.err.println(fileName);
		try {
			//打开写文件器，第二个参数表示为append
			writer = new FileWriter(fileName,true);
			for(int i = 0; i < 10;i++) {
				for(int j = 0; j < 20;j++) {
					if(BFs.isEmpty(i, j))writer.write("* ");
					else {
						switch(BFs.getCreature(i, j).getClass().getName()) {
						
						case "java2018.CalabashBrother.Beings.CalabashBrother":
							switch(((CalabashBrother)BFs.getCreature(i, j)).getNameAndColor()) {
							case RED:
								writer.write("R ");
								break;
							case ORANGE:
								writer.write("O ");
								break;
							case YELLOW:
								writer.write("Y ");
								break;
							case GREEN:
								writer.write("G ");
								break;
							case CYAN:
								writer.write("C ");
								break;
							case BLUE:
								writer.write("B ");
								break;
							case PURPLE:
								writer.write("P ");
							}
							break;
						case "java2018.CalabashBrother.Beings.GrandPa":
							writer.write("g ");
							break;
						case "java2018.CalabashBrother.Beings.Scorpion":
							writer.write("S ");
							break;
						case "java2018.CalabashBrother.Beings.LouLuo":
							writer.write("L ");
							break;
						case "java2018.CalabashBrother.Beings.Snake":
							writer.write("s ");
							break;
						}
					}
				}
				writer.write("\r\n");
			}
			//writer.write("\r\n");
			writer.close();
		}catch (IOException e) {  
            e.printStackTrace();  
        }  

	}
}
