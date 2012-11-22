package com.example.createrequestlist;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SelectItems extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
		
		TextView text = (TextView)findViewById(R.id.text1);
		
		Intent getActivity = this.getIntent();
		
		Bundle category_data = getActivity.getExtras();
		
		String categoryName = "";
		
		if(category_data != null){
			categoryName = category_data.getString("CategoryName");
		}
		
		text.setText("選択されたカテゴリは" + categoryName);
	}
}
