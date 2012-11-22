package com.example.createrequestlist;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;
import android.content.Intent;

public class MainMenu extends Activity implements OnClickListener{
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        //ActivityのOnCreate実行
    	super.onCreate(savedInstanceState);
        
    	//レイアウト設定ファイルの指定
        this.setContentView(R.layout.main);
        
        //データベース事前設定
        this.setDataBase();
     
        //Button オブジェクト取得
        Button[] buttons = {
        	(Button)this.findViewById(R.id.order),
        	(Button)this.findViewById(R.id.editList),
        	(Button)this.findViewById(R.id.orderHistory)
        };
        
        //Button オブジェクトにクリックリスナーを設定
        for(Button button:buttons){
        	button.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view){
    	
    	//Intent変数初期化
    	Intent nextActivity = null;
    	
    	//押下別にインテント生成
    	switch(view.getId()){
    	case R.id.order:
    		nextActivity = new Intent(this,Category.class);
    		break;
    	case R.id.editList:
    		nextActivity = new Intent(this,EditList.class);
    		break;
    	case R.id.orderHistory:
    		nextActivity = new Intent(this,OrderedHistory.class);
    		break;
    	}
    	
    	//アクティビティ起動
    	startActivity(nextActivity);
    }
    
    
    private void setDataBase(){
    	//DatabaseHelperクラスのインスタンス生成
    	ProductDBHelper mDbHelper = new ProductDBHelper(this);
    	
    	//空のデータベースを作成する
    	mDbHelper.createEmptyDataBase();
    }
}
