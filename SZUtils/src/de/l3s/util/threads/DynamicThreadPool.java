package de.l3s.util.threads;

import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.l3s.util.file.WebUtils;





public class DynamicThreadPool {
	private static final Logger logger = LogManager.getLogger(DynamicThreadPool.class);
	public interface TaskListener {

		void taskDone(Task task);

	}

	class WThread extends Thread {
		private Task task;
		private boolean isfree;
		private boolean stopped;
		private boolean done;
		private TaskListener taskListener;

		public WThread() {
			isfree = true;
			stopped = false;
			done = false;
		}

		public void stopp(boolean stop) {
			this.stopped = stop;
		}

		public synchronized void setTask(Task t) {
			isfree = false;
			this.task = t;
		}

		@Override
		public void run() {

			while (!stopped) {
				if (task != null) {
					try {
						executetask(task);

						if (taskListener != null) {
							taskListener.taskDone(task);
						}
						isfree = true;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					this.task = null;
				}

				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			done = true;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return task.toString();
		}

		public void setTaskListener(TaskListener taskListener) {
			this.taskListener = taskListener;
		}

		private synchronized void executetask(Task task) throws Exception {
			try {
				task.execute();
				isfree = true;

			} catch (Exception e) {
				e.printStackTrace();
				isfree = true;

				throw e;
			}

		}

		boolean isFree() {
			return isfree;
		}
	}

	Vector<WThread> tasks = new Vector<WThread>();
	int taskcounter = 0;

	public DynamicThreadPool(int maxthreads, TaskListener listener) {

		for (int i = 0; i < maxthreads; i++) {
			WThread t;
			tasks.add(t = new WThread());
			if(listener!=null)
			t.setTaskListener(listener);
			else
			t.setTaskListener(new TaskListener() {

				@Override
				public void taskDone(Task task) {
					log().debug("Task done: " + (task.toString()));
					log().debug("Tasks done: " + (++taskcounter));

				}

				private Logger log() {
					return logger;

				}
			});
		
			t.start();
		}
	}

	public synchronized void exec(Task t) {

		while (true) {
			for (int i = 0; i < tasks.size(); i++) {
				if (tasks.elementAt(i).isFree()) {
					tasks.elementAt(i).setTask(t);
					return;
				}
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public void finish() {

		waitforready: while (true) {
			int cntready = 0;
			for (int i = 0; i < tasks.size(); i++) {

				if (!tasks.elementAt(i).isFree()) {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue waitforready;
				} else {
					tasks.elementAt(i).stopped = true;
					if (!tasks.elementAt(i).done) {
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						continue waitforready;
					}
					cntready++;
				}
			}
			if (cntready == tasks.size()) {
				return;
			}
		}

	}

}
