package com.example.createrequestlist;

import java.io.Serializable;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;
import android.view.WindowManager.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Category extends Activity implements Serializable{
	
	//データベース
	private SQLiteDatabase productDB;
	
	//データベースの情報
	private static final String[] COLUMNS = {
		"id","product_name","product_category"
	};
	
	//カテゴリの種類
	private static final String[] CATEGORIES ={
		"卵・乳製品・飲料","加工品","魚介","肉類","野菜","果物","その他"
	};
	
	//Buttonオブジェクトに設定するタグ用カウント
	private Integer tagCount;
	
	//カテゴリカウンター
	private Integer categoryCount;
	
	//生成したEditTextをArrayListに格納
	private ArrayList<EditText> edit = new ArrayList<EditText>();
	
	//生成したTableRowをArrayListに格納
	private ArrayList<TableRow> tRow = new ArrayList<TableRow>();
	
	//リソースを持つ。
	private static Drawable pushBackminus; // 押された時の画像
	private static Drawable normalBackminus; // 通常時の画像
	private static Drawable pushBackplus; // 押された時の画像
	private static Drawable normalBackplus; // 通常時の画像
	private Resources res;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		//ActivityのOnCreateを実行
		super.onCreate(savedInstanceState);
		
		//画面遷移直後にIMEが自動的に起動するのを防ぐ
		this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		//レイアウト設定ファイルの指定
		this.setContentView(R.layout.category);
		
		//DB取得
		this.getDB();
		
		//画像ID取得
		res = getResources();
	}

	//カテゴリのボタン押下した場合
	public void orderItemInfo(View view){
		//Button型にキャスト
		Button categoryBtn = (Button)view;
		
		//表示するカテゴリ名称取得
		String categoryName = categoryBtn.getText().toString();
		
		//表示・非表示の制御(ArrayListに格納したTableRow分繰り返す)
		String categoryTag;
		for(int i = 0; i < tRow.size(); i++){
			//TableRowに設定したタグを取得
			categoryTag = (tRow.get(i)).getTag().toString();
			
			//タグと押下されたカテゴリの一致確認
			if(categoryTag.equals(categoryName)){
				tRow.get(i).setVisibility(View.VISIBLE); //表示
			}else{
				tRow.get(i).setVisibility(View.GONE);	//非表示
			}
		}
	}
	
	//トップページへ押下した場合
	public void returnTopPage(View view){
		//アクティビティ終了
		this.finish();
	}
	
	//注文確認を押下した場合
	public void toConfirmPage(View view){
		
		//インテント生成
		Intent nextActivity = new Intent(this, OrderedConfirm.class);
		
		//遷移後に渡す値 EditText , TableRow
		nextActivity.putExtra("ITEM_COUNT", edit);		//EditText
//		nextActivity.putExtra("ITEM", tRow);		//EditText

		//アクティビティ起動
		this.startActivity(nextActivity);
	}
	
	//データベース取得
	private void getDB(){
		//データベースを開く
		ProductDBHelper pHelper = new ProductDBHelper(this);
		productDB = pHelper.openDataBase();
		
		//(EditText)タグ用カウント初期化
		tagCount = 0;
		categoryCount = 0;
		
		//カテゴリ数分繰り返す
		for(String categoryName:CATEGORIES){
			//TableLayout生成
			this.createTable(categoryName);
			categoryCount = categoryCount + 1;
		}
		
		//データベース閉じる
		productDB.close();
	}
	
	//TableLayout生成
	private void createTable(String categoryName){
		//データ取得
		Cursor cursor = this.getProductData(categoryName);
		
		//TableLayout取得
		TableLayout tLayout = (TableLayout)this.findViewById(R.id.itemInfo);
		
		//レコード数チェック
		//0件の場合:TextView生成
		//1件以上の場合:TableLayout生成
		if(cursor.moveToFirst()){
			//レコード数分作成する
			do{
				//TableRow生成
				TableRow row = new TableRow(this);
				row.setTag(categoryName);
				
				//初期表示用
				if(categoryCount != 0){
					row.setVisibility(View.GONE);
				}
				
				//CheckBox
				//CheckBox chk = new CheckBox(this);
				//chk.setWidth(100);
				//chk.setHighlightColor(60);
				//row.addView(chk);
				
				
				//TextView
				TextView tv = new TextView(this);
				tv.setText(cursor.getString(1));
				tv.setWidth(230);
				row.addView(tv);
				
				//マイナスするButtonオブジェクトを追加
				row.addView(this.createMinusView());
				
				//EditText
				EditText ed = new EditText(this);
				ed.setInputType(InputType.TYPE_CLASS_NUMBER);
				ed.setText("0");
				ed.setWidth(80);
				row.addView(ed);
				
				//EditTextをArrayListに格納
				edit.add(ed);
				
				//プラスするButtonオブジェクトを追加
				row.addView(this.createPlusView());
				
				//TableRowをArrayListに追加
				tRow.add(row);
				
				//生成したTableRowをTableLayoutに追加
				tLayout.addView(row);
				
				//Buttonオブジェクトに設定するタグ用カウントアップ
				tagCount = tagCount + 1;
				
			}while(cursor.moveToNext());
		}else{
			//TableRow生成
			TableRow row = new TableRow(this);
			row.setTag(categoryName);
			
			//初期表示用
			if(categoryCount != 0){
				row.setVisibility(View.GONE);
			}
			
			//TextView生成
			TextView nothing = new TextView(this);
			nothing.setText("選択されたカテゴリに品物は登録されていません。");
			row.addView(nothing);
			
			//TableRowをArrayListに格納
			tRow.add(row);
			
			//生成したTableRowをTableLayoutに追加
			tLayout.addView(row);
		}
		//検索結果クリア
		cursor.close();
	}
	
	//データ取得
	private Cursor getProductData(String categoryName){
		return productDB.query("product_info", COLUMNS, "product_category='" + categoryName + "'", null, null, null, "id");
	}
	
	
	
	
	//マイナス用Buttonオブジェクト生成
//	private Button createMinusButton(){
//		//Button生成
//		Button mBtn = new Button(this);
//		mBtn.setWidth(120);
//		mBtn.setText("マイナス");
//		mBtn.setTag(tagCount);
//		mBtn.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				//Buttonクラスにキャスト
//				Button b = (Button)v;
//						
//				//Buttonオブジェクトに設定したタグを取得(Integer変換)
//				Integer i = (Integer)b.getTag();
//						
//				//ArrayListに格納したEditTextをタグ用カウントを利用して取得する
//				EditText editNum = edit.get(i);
//						
//				//現在表示している数量からプラス１する
//				//EditText設定している数字を取得する
//				Integer num = Integer.valueOf(editNum.getText().toString());
//						
//				//プラス１する(現在設定してある数量が０以上の場合、マイナス)
//				if(num > 0){
//					num = num - 1;
//				}
//				//プラスした値をEditTextに再設定する
//				editNum.setText(String.valueOf(num));
//			}
//		});
//		return mBtn;
//	}
	
	//ImageView:マイナス
	private ImageView createMinusView(){
		ImageView mImvw = new ImageView(this);
		mImvw.setImageResource(R.drawable.miniminus_hb);
		mImvw.setScaleType(ScaleType.CENTER_CROP);
		mImvw.setTag(tagCount);
		//押下している間の画像変更
		mImvw.setOnTouchListener(new View.OnTouchListener() 
		{
			
			@Override
			public boolean onTouch(View v, MotionEvent event){
				// TODO 自動生成されたメソッド・スタブ
				ImageView iV = (ImageView)v;
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					if(pushBackminus == null){
						pushBackminus = res.getDrawable(R.drawable.miniminus);
					}
					iV.setImageDrawable(pushBackminus);
					
				}else if(event.getAction() == MotionEvent.ACTION_CANCEL){
					if(normalBackminus == null){
						normalBackminus = res.getDrawable(R.drawable.miniminus_hb);
					}
					iV.setImageDrawable(normalBackminus);
					
				}else if(event.getAction() == MotionEvent.ACTION_UP) {
					if(normalBackminus == null){
						normalBackminus = res.getDrawable(R.drawable.miniminus_hb);
					}
					iV.setImageDrawable(normalBackminus);
				}
				return false;
			}
		});
		
		mImvw.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				Integer num = null;
				ImageView iV = (ImageView)v;
				Integer i = (Integer)iV.getTag();
				EditText editNum = edit.get(i);
				
				//Log.d("おしたボタン：", String.valueOf(i-1));
				if (!((editNum.getText().toString()).equals(""))) {
					num = Integer.valueOf(editNum.getText().toString());
				
					if ((num > 0) && (num != null)) {							
						num = num - 1;
					}
					editNum.setText(String.valueOf(num));
				}
				
				//Toast.makeText(Category.this, String.valueOf(count2), Toast.LENGTH_SHORT).show();
			}
		});
		
		
		return mImvw;
	}	
	
	//プラス用Buttonオブジェクト生成
//	private Button createPlusButton(){
//		//Button生成
//		Button pBtn = new Button(this);
//		pBtn.setWidth(120);
//		pBtn.setText("プラス");
//		pBtn.setTag(tagCount);
//		pBtn.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				//Buttonクラスにキャスト
//				Button b = (Button)v;
//				
//				//Buttonオブジェクトに設定したタグを取得(Integer変換)
//				Integer i = (Integer)b.getTag();
//				
//				//ArrayListに格納したEditTextをタグ用カウントを利用して取得する
//				EditText editNum = edit.get(i);
//				
//				//現在表示している数量からプラス１する
//				//EditText設定している数字を取得する
//				Integer num = Integer.valueOf(editNum.getText().toString());
//				
//				//プラス１する
//				num = num + 1;
//				
//				//プラスした値をEditTextに再設定する
//				editNum.setText(String.valueOf(num));
//			}
//		});
//		return pBtn;
//	}
	
	//ImageView:プラス
	private ImageView createPlusView(){
		ImageView pImvw = new ImageView(this);
		pImvw.setImageResource(R.drawable.miniplus_hb);
		pImvw.setScaleType(ScaleType.CENTER_CROP);
		pImvw.setTag(tagCount);
		pImvw.setOnTouchListener(new View.OnTouchListener() 
		{
			
			@Override
			public boolean onTouch(View v, MotionEvent event){
				// TODO 自動生成されたメソッド・スタブ
				ImageView iV = (ImageView)v;
				
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					if(pushBackplus == null){
						pushBackplus = res.getDrawable(R.drawable.miniplus);
					}
					iV.setImageDrawable(pushBackplus);
				}else if(event.getAction() == MotionEvent.ACTION_CANCEL){
					if(normalBackplus == null){
						normalBackplus = res.getDrawable(R.drawable.miniplus_hb);
					}
					iV.setImageDrawable(normalBackplus);
				}else if(event.getAction() == MotionEvent.ACTION_UP) {
					if(normalBackplus == null){
						normalBackplus = res.getDrawable(R.drawable.miniplus_hb);
					}
					iV.setImageDrawable(normalBackplus);
				}
				return false;
			}
		});
		
		pImvw.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Integer num = null;
				//ImageViewクラスにキャスト
				ImageView iV = (ImageView)v;
				
				//ImageViewオブジェクトに設定したタグを取得(Integer変換)
				Integer i = (Integer)iV.getTag();
				
				
				//ArrayListに格納したEditTextインスタンスをカウントを利用して取得する
				EditText editNum = edit.get(i);

				if (!((editNum.getText().toString()).equals(""))) {
					num = Integer.valueOf(editNum.getText().toString());
					num = num + 1;
				}else{
					num = 1;
				}
				//プラスした値をEditTextに再設定する
				editNum.setText(String.valueOf(num));
//				iV.setImageResource(R.drawable.miniplus_hb);

			}
		});

		return pImvw;
	}	
	
	
	//Backキー無効
	@Override
	public boolean dispatchKeyEvent(KeyEvent event){
		//デバイスボタンが押下された場合
		if(event.getAction() == KeyEvent.ACTION_DOWN){
			//Backキーが押下された場合
			if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
				return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}
}
