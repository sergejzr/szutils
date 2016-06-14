package de.l3s.util.threads;

import java.util.Random;
import java.util.Vector;

public class Test {
	

	public static void main(String[] args) {
		DynamicThreadPool dtp = new DynamicThreadPool(10,null);
Vector<Task> workingqueue=new Vector<Task>();
		for (int i = 0; i < 50; i++) {
			Task task = new XTask(i);
			workingqueue.add(task);
		}
		for(Task t:workingqueue){
		dtp.exec(t);
		}
		dtp.finish();
		System.out.println("Ready");
	}
}

class XTask implements Task {
private int nr;
public XTask(int nr) {
	this.nr=nr;
}
		@Override
		public Object execute() {
			Random r = new Random();
			int intnum = r.nextInt(10);
			try {
				System.out.println(nr+" works for "+intnum+" seconds");
				Thread.sleep(intnum * 1000);
				System.out.println(nr+" ready "+intnum+" seconds");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return Integer.valueOf(intnum);
		}
	}