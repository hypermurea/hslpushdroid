package com.hypermurea.hslpushdroid.reittiopas;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class TransportLine implements Parcelable {

	private static final String TAG = "TransportLine";

	public String shortCode;
	public String code;
	public int transportType;
	public String name;

	public TransportLine(String shortCode, String code, int transportType, String name) {
		this.shortCode = shortCode;
		this.code = cleanLineCode(code);
		this.transportType = transportType;
		this.name = name;
	}
	
	public static String cleanLineCode(String original) {
		return original.split(" ")[0];
	}
		
	public TransportLine(Parcel createFrom) {
		List<String> data = new ArrayList<String>();
		createFrom.readStringList(data);
		int i = 0;
		this.shortCode = data.get(i++);
		this.code = data.get(i++);
		this.transportType = Integer.parseInt(data.get(i++));
		this.name = data.get(i++);
	}

	public static List<TransportLine> getTransportLines(String json) {
		List<TransportLine> lines = new ArrayList<TransportLine>();		

		try {
			JSONArray array = new JSONArray(json);

			for(int i = 0; i < array.length(); i ++) {
				JSONObject object = array.getJSONObject(i);
				TransportLine line = new TransportLine(
						object.getString("short_code"), 
						object.getString("code"), 
						object.getInt("transport_type_id"), 
						object.getString("name"));
				lines.add(line);
			}
		} catch(Exception ex) {
			Log.e(TAG, "json -> list of transportlines failed", ex);
		}
		return lines;
	}

	public static JSONArray getJsonArray(List<TransportLine> lines) throws JSONException {
		JSONArray array = new JSONArray();
		for(TransportLine line : lines) {
			JSONObject object = line.getJsonRepresentation();
			array.put(object);
		}
		return array;
	}

	private JSONObject getJsonRepresentation() throws JSONException  {
		JSONObject object = new JSONObject();
		object.put("shortCode", shortCode);
		object.put("code", code);
		object.put("transportType", transportType);
		object.put("name", name);
		return object;
	}

	public String toString() {
		return this.shortCode + " " + name;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		List<String> data = new ArrayList<String>();
		data.add(shortCode);
		data.add(code);
		data.add(String.valueOf(transportType));
		data.add(name);
		parcel.writeStringList(data);
	}

	public static final Parcelable.Creator<TransportLine> CREATOR
	= new Parcelable.Creator<TransportLine>() {
		public TransportLine createFromParcel(Parcel in) {
			return new TransportLine(in);
		}

		public TransportLine[] newArray(int size) {
			return new TransportLine[size];
		}
	};

}
