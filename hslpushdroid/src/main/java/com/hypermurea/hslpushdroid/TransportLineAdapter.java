package com.hypermurea.hslpushdroid;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class TransportLineAdapter extends ArrayAdapter<TransportLine> {

	public TransportLineAdapter(Context context, int textViewResourceId,
			List<TransportLine> objects) {
		super(context, textViewResourceId, objects);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return super.getView(position, convertView, parent);
	}
	

}
