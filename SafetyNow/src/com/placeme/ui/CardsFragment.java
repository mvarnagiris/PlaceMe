package com.placeme.ui;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.placeme.R;
import com.placeme.model.CardInfo;
import com.placeme.views.CardView;

public class CardsFragment extends Fragment
{
	private LinearLayout	cardsContainer_V;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_cards, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		// Get views
		cardsContainer_V = (LinearLayout) view.findViewById(R.id.cardsContainer_V);
	}

	// Public methods
	// --------------------------------------------------------------------------------------------------------------------------------

	public void bind(ArrayList<CardInfo> cardInfoArray)
	{
		cardsContainer_V.removeAllViews();
		for (CardInfo cardInfo : cardInfoArray)
		{
			cardsContainer_V.addView(CardView.newInstance(getActivity(), cardInfo));
		}
	}
}