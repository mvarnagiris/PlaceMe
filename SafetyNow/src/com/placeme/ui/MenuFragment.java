package com.placeme.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.placeme.R;

public class MenuFragment extends Fragment {

	private TextView	mMenuItemLive;
	private TextView	mMenuItemWork;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setRetainInstance(true); if we need it
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_menu, container, false);
		mMenuItemLive = (TextView) view.findViewById(R.id.menu_live_here);
		mMenuItemWork = (TextView) view.findViewById(R.id.menu_work_here);
		return view;
	}

}
