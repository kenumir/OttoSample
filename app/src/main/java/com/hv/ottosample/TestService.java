package com.hv.ottosample;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class TestService extends Service {

	public class ServiceBinder extends Binder {
		public TestService getService() {
			return TestService.this;
		}
	}

	public interface OnDataEvent {
		void onData(int data);
	}

	private final IBinder mBinder = new ServiceBinder();

	private Thread mThread;
	private OnDataEvent mOnDataEvent;

	public void setOnDataEvent(OnDataEvent e) {
		mOnDataEvent = e;
	}

	public TestService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_NOT_STICKY;
	}

	public void runStopService() {
		if (mThread == null) {
			mThread = new Thread() {
				@Override
				public void run() {
					int i = 0;
					while (!Thread.currentThread().isInterrupted()) {
						Log.v("tests", "POST: " + i);
						if (mOnDataEvent != null) mOnDataEvent.onData(i);
						//BusProvider.getInstance().post(new TestEventData(i));
						i++;
						try {
							Thread.sleep(1200);
						} catch (InterruptedException e) {
							break;
						}
					}
					mThread = null;
					Log.i("tests", "Thread finish");
				}
			};
			mThread.start();
		} else {
			mThread.interrupt();
		}
	}
}
