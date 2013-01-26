package com.placeme.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.placeme.R;

public class MenuFragment extends ListFragment {

	public static final String	POSITION	= "position";

	public static final int[]	TITLES		= { R.string.menu_live_here, R.string.menu_work_here };

	private int					mPosition;
	private String[]			mTitles;
	private MenuListener		mListener;

	public static interface MenuListener {
		void onPositionChange(int position, String title);
	}

	public static MenuFragment getInstance(int position) {
		Bundle args = new Bundle();
		args.putInt(POSITION, position);
		MenuFragment fragment = new MenuFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mListener = (MenuListener) activity;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

		mTitles = new String[TITLES.length];
		for (int i = 0; i < TITLES.length; i++) {
			mTitles[i] = getString(TITLES[i]);
		}

		Bundle args = getArguments();
		if (null != args) {
			mPosition = args.getInt(POSITION, 0);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_menu, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(getActivity(), R.layout.menu_item, R.id.item, mTitles));
		ListView listView = getListView();
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setItemChecked(mPosition, true);
	}

	public int getPosition() {
		return mPosition;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		mPosition = position;
		if (null != mListener) {
			mListener.onPositionChange(mPosition, mTitles[mPosition]);
		}
	}

}
