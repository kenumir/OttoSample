package com.hv.ottosample;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

	private Thread mThread;
	private TextView label;

	public MainActivityFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View res = inflater.inflate(R.layout.fragment_main, container, false);

		label = (TextView) res.findViewById(R.id.label);

		res.findViewById(R.id.startStopBtn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mThread == null) {
					mThread = new Thread() {
						@Override
						public void run() {
							int i = 0;
							while (!Thread.currentThread().isInterrupted()) {
								Log.v("tests", "POST: " + i);
								BusProvider.getInstance().post(new TestEventData(i));
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
		});

		return res;
	}

	@Subscribe
	public void onEventData(TestEventData event) {
		label.setText("Data: " + event.value);
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.w("tests", "BusProvider.getInstance().register");
		BusProvider.getInstance().register(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.w("tests", "BusProvider.getInstance().unregister --- ");
		BusProvider.getInstance().unregister(this);
	}

}
