package com.hv.ottosample;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
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


	private TextView label;
	private TestService mService;
	private boolean mBound = false;
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.d("tests", "onServiceConnected, startStop=" + startStop);

			if (!startStop) {
				getActivity().unbindService(mConnection);
				mBound = false;
				return;
			}

			TestService.ServiceBinder binder = (TestService.ServiceBinder) service;
			mService = binder.getService();
			mBound = true;
			mService.setOnDataEvent(new TestService.OnDataEvent() {
				@Override
				public void onData(int data) {
					Log.d("tests", "ON EVENT DATA: " + data);
					BusProvider.getInstance().post(new TestEventData(data));
				}
			});
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			Log.d("tests", "onServiceDisconnected");
			mService = null;
			mBound = false;
		}
	};

	public MainActivityFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View res = inflater.inflate(R.layout.fragment_main, container, false);

		label = (TextView) res.findViewById(R.id.label);

		res.findViewById(R.id.startStopBtn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d("tests", "mBound=" + mBound);
				if (mBound) {
					mService.runStopService();
				}
			}
		});

		return res;
	}

	private boolean startStop = false;

	@Override
	public void onStart() {
		super.onStart();
		startStop = true;
		Log.i("tests", "onStart");
		Intent intent = new Intent(getActivity(), TestService.class);
		getActivity().startService(intent);
		boolean b = getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		Log.i("tests", "BIND=" + b);
	}

	@Override
	public void onStop() {

		startStop = false;
		Log.i("tests", "onStop, mBound=" + mBound);

		if (mService != null) {
			mService.setOnDataEvent(null);
			mService = null;
		}

		if (mBound) {
			//mService.setOnDataEvent(null);
			getActivity().unbindService(mConnection);
			mBound = false;
			Log.i("tests", "mBound[onStop]=" + mBound);
		}

		super.onStop();
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
