package com.example.createrequestlist;


import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;

public class OrderedConfirm extends Activity {

	//生成したEditTextをArrayListに格納
	private ArrayList<EditText> edit;
	
	//生成したTableRowをArrayListに格納
	private ArrayList<TableRow> tRow = new ArrayList<TableRow>();

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
		TextView text = (TextView)findViewById(R.id.text1);
		text.setText("OrderedConfirm.java");
		
		Intent beforeActivity = getIntent();
//		edit = (ArrayList<EditText>)beforeActivity.getSerializableExtra("ITEM_COUNT");
//		tRow = (ArrayList<TableRow>)beforeActivity.getSerializableExtra("ITEM");
		
	}
}
