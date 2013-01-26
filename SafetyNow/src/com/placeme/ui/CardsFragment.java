package com.placeme.ui;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.placeme.R;
import com.placeme.model.CardInfo;
import com.placeme.model.Place;
import com.placeme.views.CardView;

public class CardsFragment extends Fragment
{
	private LinearLayout	cardsContainer_V;
	private RelativeLayout	locationContainer;
	private TextView		locationTitle;
	private TextView		locationSubtitle;

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
		locationContainer = (RelativeLayout) view.findViewById(R.id.location_container);
		locationTitle = (TextView) locationContainer.findViewById(R.id.location_title);
		locationSubtitle = (TextView) locationContainer.findViewById(R.id.location_subtitle);
	}

	// Public methods
	// --------------------------------------------------------------------------------------------------------------------------------

	public void bind(Place place, ArrayList<CardInfo> cardInfoArray)
	{
		cardsContainer_V.removeViewsInLayout(1, cardsContainer_V.getChildCount() - 1);
		int position = 0;
		for (CardInfo cardInfo : cardInfoArray)
		{
			cardsContainer_V.addView(CardView.newInstance(getActivity(), cardInfo, position));
			position++;
		}

		locationTitle.setText(place.getName());
		locationSubtitle.setText(place.getConnurbation() + ", " + place.getPostcode());
	}
}