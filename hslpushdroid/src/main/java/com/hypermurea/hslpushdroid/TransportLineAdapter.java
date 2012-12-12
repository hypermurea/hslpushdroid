package com.hypermurea.hslpushdroid;

import java.util.List;

import com.hypermurea.hslpushdroid.reittiopas.TransportLine;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TransportLineAdapter extends ArrayAdapter<TransportLine> {

	private LayoutInflater inflater;
	private LinesOfInterestChangeListener clickListener;

	private static SparseIntArray transportImageMap; 
	private static int[][] transportToImage = {
		{1,R.drawable.bussi},
		{2,R.drawable.ratikka},
		{3,R.drawable.bussi},
		{4,R.drawable.bussi},
		{5,R.drawable.bussi},
		{6,R.drawable.metro},	
		{7,R.drawable.lautta},
		{8,R.drawable.bussi},
		{12,R.drawable.juna},
		{21,R.drawable.bussi},
		{22,R.drawable.bussi},
		{23,R.drawable.bussi},
		{24,R.drawable.bussi},
		{25,R.drawable.bussi},
		{36,R.drawable.bussi},
		{39,R.drawable.bussi},
	};
	
	static {
		transportImageMap = new SparseIntArray();
		for(int i = 0; i < transportToImage.length; i ++) {
			transportImageMap.put(transportToImage[i][0], transportToImage[i][1]);
		}
	}

	/**
	 * From reittiopas API description (http://developer.reittiopas.fi/pages/fi/http-get-interface-version-2.php#lines)
	1 = Helsinki internal bus lines
	2 = trams
	3 = Espoo internal bus lines
	4 = Vantaa internal bus lines
	5 = regional bus lines
	6 = metro
	7 = ferry
	8 = U-lines
	12 = commuter trains
	21 = Helsinki service lines
	22 = Helsinki night buses
	23 = Espoo service lines
	24 = Vantaa service lines
	25 = region night buses
	36 = Kirkkonummi internal bus lines
	39 = Kerava internal bus lines	 
	 */


	public TransportLineAdapter(Context context, int textViewResourceId,
			List<TransportLine> objects, LinesOfInterestChangeListener listener) {
		super(context, textViewResourceId, objects);
		inflater = LayoutInflater.from(context);
		this.clickListener = listener;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = convertView;
		if(view == null) {
			view = inflater.inflate(R.layout.line_list_row, null);
		}
		view.setClickable(false);
		
		TransportLine line = this.getItem(position);

		TextView lineCodeTextView = (TextView) view.findViewById(R.id.lineCode);
		TextView lineNameTextView = (TextView) view.findViewById(R.id.lineName);
		ImageView transportImageView = (ImageView) view.findViewById(R.id.transportImage);
		ImageView trackLineButton = (ImageView) view.findViewById(R.id.trackLineImageButton);

		lineCodeTextView.setText(line.shortCode);
		lineNameTextView.setText(line.name);
		transportImageView.setImageResource(getDrawableId(line));
		trackLineButton.setOnClickListener(new TransportLineClickProxy(line));

		return view;
	}

	class TransportLineClickProxy implements OnClickListener {

		private TransportLine line;
		public TransportLineClickProxy(TransportLine line) {
			this.line = line;
		}
		
		@Override
		public void onClick(View v) {
			TransportLineAdapter.this.clickListener.addTransportLine(line);
		}
		
	}
	
	public static int getDrawableId(TransportLine line) {
		return TransportLineAdapter.transportImageMap.get(line.transportType);
	}


}
