package com.example.createrequestlist;


import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class OrderedHistory extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
		TextView text = (TextView)findViewById(R.id.text1);
		text.setText("OrderedHistory.java");
	}
}
