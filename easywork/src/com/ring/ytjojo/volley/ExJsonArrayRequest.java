package com.ring.ytjojo.volley;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request.Method;
import com.android.volley.Request.Priority;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A request for retrieving a {@link JSONArray} response body at a given URL.
 */
public class ExJsonArrayRequest extends JsonArrayRequest {

	private Map<String, String> headers = new HashMap<String, String>();
	private Priority priority = null;
	
	public ExJsonArrayRequest( String url,
			Listener<JSONArray> listener, ErrorListener errorListener) {
		super(url,listener,errorListener);
		// TODO Auto-generated constructor stub
	}

	public void setHeader(String title, String content) {
		 headers.put(title, content);
	}
	
	public void setPriority(Priority priority) {
		this.priority = priority;
	}
	
	/*
	 * If prioirty set use it,else returned NORMAL
	 * @see com.android.volley.Request#getPriority()
	 */
    public Priority getPriority() {
    	if( this.priority != null) {
    		return priority;
    	} else {
    		return Priority.NORMAL;	
    	}
    }
}