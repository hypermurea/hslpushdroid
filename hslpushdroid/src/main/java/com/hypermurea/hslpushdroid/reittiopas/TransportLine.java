package com.hypermurea.hslpushdroid.reittiopas;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class TransportLine {
	
	private static final String TAG = "TransportLine";

	public String shortCode;
	public int transportType;
	public String name;	
	public HashSet<String> codes = new HashSet<String>();

	public TransportLine(String shortCode, int transportType, String name) {
		this.shortCode = shortCode;
		this.transportType = transportType;
		this.name = name;
	}

	public static List<TransportLine> getTransportLines(String json) {
		List<TransportLine> lines = new ArrayList<TransportLine>();		

		try {
			JSONArray array = new JSONArray(json);

			for(int i = 0; i < array.length(); i ++) {
				JSONObject object = array.getJSONObject(i);
				TransportLine line = new TransportLine(object.getString("short_code"), 
						object.getInt("transport_type_id"), object.getString("name"));

				JSONArray codeArray = object.getJSONArray("codes");
				for(int j = 0; j < codeArray.length(); j ++) {
					line.codes.add(codeArray.getString(j));
				}
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
		object.put("transportType", transportType);
		object.put("name", name);
		JSONArray codesInJson = new JSONArray();
		for(String code: codes) {
			codesInJson.put(code);
		}
		object.put("codes",codesInJson);
		return object;
	}

	public String toString() {
		return this.shortCode + " " + name;
	}

}
