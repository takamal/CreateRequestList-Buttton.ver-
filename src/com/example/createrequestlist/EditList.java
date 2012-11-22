package com.example.createrequestlist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class EditList extends Activity implements OnClickListener{

	//ヘルパークラス
	private ProductDBHelper pHelper;
	
	//データベース
	private SQLiteDatabase productDB;
	
	//データベースの情報
	private static final String[] COLUMNS = {
		"id","product_name","product_category"
	};
	
	//処理モード
	private static String modeType;
	
	//ラジオボタン表示領域
	private LinearLayout lLayout;
	
	//品物情報(追加用)
	private String item_name;
	private String category_name;
	
	//品物情報(変更・削除用)
	private String itemTxt;
	private String id;
	
	//選択ダイアログ位置情報
	private int categoryWhich = 0;
	private int modeWhich = 0;

<<<<<<< HEAD
	//＊＊＊＊＊＊＊＊＊＊＊＊＊＊初期処理メソッド　ここから＊＊＊＊＊＊＊＊＊＊＊＊＊＊
	@Override
	public void onCreate(Bundle savedInstanceState){
		//ActivityクラスのonCreateを実行
		super.onCreate(savedInstanceState);
		
		//レイアウト設定ファイルの指定
		this.setContentView(R.layout.edit_list);
		
		//データ取得
		this.getData();
		
		//Buttonオブジェクト取得
		Button[] btns = {
			(Button)this.findViewById(R.id.ItemAdd),
			(Button)this.findViewById(R.id.ItemUpdate),
			(Button)this.findViewById(R.id.ItemDelete),
		};
		
		//ButtonオブジェクトにOnClickListenerを設定
		for(Button btn:btns){
			btn.setOnClickListener(this);
		}
	}
	
	private void getData(){
		//データベースを開く
		pHelper = new ProductDBHelper(this);
		productDB = pHelper.openDataBase();
		
		//ラジオボタン生成
		this.createRadio();
		
		//データベースを閉じる
		productDB.close();
	}
	
	private void createRadio(){
		//データ取得
		Cursor cursor = productDB.query("product_info", COLUMNS, null, null, null, null, "product_category");
		
		//LinearLayout取得
		if(lLayout == null){
			lLayout = (LinearLayout)this.findViewById(R.id.itemInfo);
		}
		
		//レコード数チェック
		//0件の場合:TextView生成
		//1件以上の場合:RadioGroup生成
		if(cursor.moveToFirst()){
			//RadioGroup生成
			RadioGroup rGroup = new RadioGroup(this);
			rGroup.setId(0);

			//登録内容分繰り返す
			do{
				//キーワード設定(　品物名　（種別名）)
				String itemName = "　品物名：　" + cursor.getString(1) + "\n　種別：　　" + cursor.getString(2);
				
				//RadioButton生成
				RadioButton rButton = new RadioButton(this);
				rButton.setId(cursor.getInt(0));
				rButton.setText(itemName);
				
				//RadioGroupにRadioButtonを追加
				rGroup.addView(rButton);
			}while(cursor.moveToNext());			
		
			//LinearLayoutにRadioGroupを追加
			lLayout.addView(rGroup);
			
		}else{
			//TextView生成
			TextView nothing = new TextView(this);
			nothing.setText("品物は現在登録されていません。");
			
			//LinearLayoutにTextViewを追加
			lLayout.addView(nothing);
		}
				
		//検索結果クリア
		cursor.close();
	}
	//＊＊＊＊＊＊＊＊＊＊＊＊＊＊初期処理メソッド　ここまで＊＊＊＊＊＊＊＊＊＊＊＊＊＊

	
	
	
	
	//品物名入力ダイアログ表示
	public void inputItemName(){
		//初期化
		item_name = "";
		
		//EditText生成
		final EditText itemName = new EditText(this);
		itemName.setHint("(例) トマト");
		itemName.setInputType(InputType.TYPE_CLASS_TEXT);
				
		//ダイアログ生成
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("品物入力ダイアログ");
		dialog.setMessage("品物名を入力してください。");
		dialog.setView(itemName);
		dialog.setPositiveButton("次へ", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				item_name = itemName.getText().toString();
				
				//カテゴリ選択ダイアログ画面へ
				selectCategory();
			}
		});
		dialog.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//追加処理キャンセル表示
				showCancel();
			}
		});
		//ダイアログ表示
		dialog.show();
	}
		
	
	
	//確認ダイアログ
	private void confirmDialog(){
		//ダイアログ用メッセージ設定
		String confirmTxt;
		
		if("追加".equals(modeType) || "変更".equals(modeType)){
			confirmTxt = "以下を" + modeType + "しますか？" + 
					"\n　品物名：　" + item_name +
					"\n　種別：　　" + category_name;
		}else{
			confirmTxt = "以下を" + modeType + "しますか？\n" + 
					itemTxt;
		}
		
		//ダイアログ生成
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("確認ダイアログ");
		dialog.setMessage(confirmTxt);
		dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//各処理メソッドへ
				if("追加".equals(modeType)){
					addData();			//追加処理
				}else if("変更".equals(modeType)){
					//変更処理
					Toast.makeText(EditList.this, "変更するって", Toast.LENGTH_LONG).show();
				}else if("削除".equals(modeType)){
					deleteData();		//削除処理
				}
			}
		});
		dialog.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//処理キャンセル
				showCancel();
			}
		});
		//ダイアログ表示
		dialog.show();
	}
	
	
	//＊＊＊＊＊＊＊＊＊＊＊＊＊＊追加処理メソッド ここから＊＊＊＊＊＊＊＊＊＊＊＊＊＊
	//データベース追加処理
	private void addData(){
		//データベースを開く
		if(productDB != null){
			productDB.close();
		}
		
		productDB = pHelper.openDataBase();
		
		try{
			//トランザクション制御開始
			productDB.beginTransaction();
			
			//登録データ設定
			ContentValues val = new ContentValues();
			val.put("product_name", item_name);
			val.put("product_category", category_name);
			
			//データ登録
			productDB.insert("product_info", null, val);
			
			//コミット
			productDB.setTransactionSuccessful();
			
			//トランザクション制御終了
			productDB.endTransaction();
			
		}catch(Exception e){
			Log.e("InsertError",e.toString());
		}finally{
			//データベースを閉じる
			productDB.close();
		}
		
		Toast.makeText(this, "データを追加しました。", Toast.LENGTH_LONG).show();
		
		//LinearLayoutを初期化
		lLayout.removeAllViews();
		
		//ラジオボタン更新
		this.getData();
	}
	//＊＊＊＊＊＊＊＊＊＊＊＊＊＊追加処理メソッド　ここまで＊＊＊＊＊＊＊＊＊＊＊＊＊＊
	
	//＊＊＊＊＊＊＊＊＊＊＊＊＊＊変更処理メソッド　ここから＊＊＊＊＊＊＊＊＊＊＊＊＊＊
	//変更モード選択ダイアログ生成
	private void selectEditMode(){
		//変更箇所
		String[] EDIT_MODES = {
			"品物名と種別を変更する",
			"品物名のみ変更する",
			"種別のみ変更する"
		};
		
		//ダイアログ生成
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("変更箇所選択ダイアログ");
		dialog.setSingleChoiceItems(EDIT_MODES, 0, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				modeWhich = whichButton;							//選択位置保持
			}
		});
		dialog.setPositiveButton("次へ", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				//モード選択
				//Toast.makeText(EditList.this, String.valueOf(modeWhich), Toast.LENGTH_SHORT).show();
				switch(modeWhich){
				case 0:
					inputItemName();
					break;
				case 1:
					break;
				case 2:
					break;
				}
			}
		});
		dialog.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				showCancel();									//追加処理キャンセル表示
			}
		});
		dialog.show();											//ダイアログ表示
	}
	
	//＊＊＊＊＊＊＊＊＊＊＊＊＊＊変更処理メソッド　ここまで＊＊＊＊＊＊＊＊＊＊＊＊＊＊
	
	//＊＊＊＊＊＊＊＊＊＊＊＊＊＊削除処理メソッド　ここから＊＊＊＊＊＊＊＊＊＊＊＊＊＊
	private void deleteData(){
		//データベースを閉じる
		if(productDB != null){
			productDB.close();
		}
		
		//データベースを開く
		productDB = pHelper.openDataBase();
				
		try{
			//トランザクション制御開始
			productDB.beginTransaction();
			
			//削除条件設定
			String condition = "id = '" + id + "'";
					
			//データ削除
			productDB.delete("product_info", condition, null);
			
			//コミット
			productDB.setTransactionSuccessful();
					
			//トランザクション制御終了
			productDB.endTransaction();
					
		}catch(Exception e){
			Log.e("DeleteError",e.toString());
		}finally{
			//データベースを閉じる
			productDB.close();
		}
				
		Toast.makeText(this, "データを削除しました。", Toast.LENGTH_LONG).show();
				
		//LinearLayoutを初期化
		lLayout.removeAllViews();
				
		//ラジオボタン更新
		this.getData();
	}		
	
	//＊＊＊＊＊＊＊＊＊＊＊＊＊＊削除処理メソッド　ここまで＊＊＊＊＊＊＊＊＊＊＊＊＊＊
	
	//＊＊＊＊＊＊＊＊＊＊＊＊＊＊共通処理メソッド　ここから＊＊＊＊＊＊＊＊＊＊＊＊＊＊
	//追加・変更・削除押下時
	@Override
	public void onClick(View view) {
		//初期化
		modeType = "";					
		int modeId = view.getId();			//オブジェクトID取得
		
		if(modeId == R.id.ItemAdd){
			modeType = "追加";
			this.inputItemName();			//品物入力ダイアログ表示へ
		}else{
			//必須チェック
			RadioButton rButton = this.checkRadioButton();
			if(rButton == null){
				this.showCheck();
				return ;
			}else{
				id = String.valueOf(rButton.getId());			//チェックしたIDを選択 
			}
			
			//処理モード設定
			if(modeId == R.id.ItemUpdate){
				modeType = "変更";
				this.selectEditMode();							//変更モード選択
			}else if(modeId == R.id.ItemDelete){
				modeType = "削除";
				itemTxt = rButton.getText().toString();			//品物情報を取得
				this.confirmDialog();							//確認ダイアログ
			}
		}	
	}
	
	//カテゴリ選択画面表示
	private void selectCategory(){
		//初期化
		category_name = "";
			
		//品物未入力チェック
		if("".equals(item_name.trim())){
			Toast cancel = Toast.makeText(this, "品物名が未入力です。" + "\n追加処理を終了します。" , Toast.LENGTH_LONG);
			cancel.setGravity(Gravity.CENTER, 0, 0);
			cancel.show();
		}else{
			//ダイアログ生成
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("種別選択ダイアログ");
			dialog.setSingleChoiceItems(Category.CATEGORIES, 0, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					categoryWhich = whichButton;							//選択位置保持
				}
			});
			dialog.setPositiveButton("次へ", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					category_name = Category.CATEGORIES[categoryWhich];		//種別情報取得
					confirmDialog();								//確認ダイアログ表示
				}
			});
			dialog.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					showCancel();									//追加処理キャンセル表示
				}
			});
			dialog.show();											//ダイアログ表示
		}
	}
	
	
	//選択チェック
	private void showCheck(){
		Toast checkErr = Toast.makeText(this, "【品物リスト】から品物をひとつ選択してください。", Toast.LENGTH_SHORT);
		checkErr.setGravity(Gravity.CENTER, 0, 0);
		checkErr.show();
	}
	
	//処理キャンセル表示
	private void showCancel(){
		Toast cancel = Toast.makeText(EditList.this, "キャンセルが押されました\n" + modeType + "処理を終了します。" ,  Toast.LENGTH_LONG);
		cancel.setGravity(Gravity.CENTER, 0, 0);
		cancel.show();
	}
	
	//トップページへ押下した場合
	public void returnTopPage(View view){
		//アクティビティ終了
		this.finish();
	}
	//＊＊＊＊＊＊＊＊＊＊＊＊＊＊共通処理メソッド　ここまで＊＊＊＊＊＊＊＊＊＊＊＊＊＊

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//変更・削除共通ロジック
	public RadioButton checkRadioButton(){
		//生成したRadioGroupを取得
		RadioGroup rGroup = (RadioGroup)this.findViewById(0);
								
		//チェックされているRadioButtonを戻り値として返す
		RadioButton rButton = (RadioButton)this.findViewById(rGroup.getCheckedRadioButtonId());
		return rButton;
		//Toast.makeText(this, Integer.toString(rButton.getId()), Toast.LENGTH_SHORT).show();
	}
	
	
	/*
	//要改善
	//変更する押下した場合
	public void itemUpdate(View view){
		//生成したRadioGroupを取得
		RadioGroup rGroup = (RadioGroup)this.findViewById(0);
				
		//チェックされているRadioButtonを取得する
		RadioButton rButton = (RadioButton)this.findViewById(rGroup.getCheckedRadioButtonId());
				
		//チェック有無確認
		if(rButton == null){
			Toast.makeText(this, "【品物リスト】から変更する品物をひとつ選択してください。", Toast.LENGTH_SHORT).show();
		}else{
			//String confirm = rButton.getText().toString() + "\n　の内容を変更しますか？";
			//Toast.makeText(this, confirm, Toast.LENGTH_SHORT).show();
			Toast.makeText(this, Integer.toString(rButton.getId()), Toast.LENGTH_SHORT).show();
		}
	}
	
	//要改善
	//削除する押下した場合
	public void itemDelete(View view){
		//生成したRadioGroupを取得
		RadioGroup rGroup = (RadioGroup)this.findViewById(0);
						
		//チェックされているRadioButtonを取得する
		RadioButton rButton = (RadioButton)this.findViewById(rGroup.getCheckedRadioButtonId());
						
		//チェック有無確認
		if(rButton == null){
			Toast.makeText(this, "【品物リスト】から削除する品物をひとつ選択してください。", Toast.LENGTH_SHORT).show();
		}else{
			//String confirm = rButton.getText().toString() + "\n　を削除しますか？";
			//Toast.makeText(this, confirm, Toast.LENGTH_SHORT).show();
			Toast.makeText(this, Integer.toString(rButton.getId()), Toast.LENGTH_SHORT).show();
		}
	}
	*/
	
=======
//	//＊＊＊＊＊＊＊＊＊＊＊＊＊＊初期処理メソッド　ここから＊＊＊＊＊＊＊＊＊＊＊＊＊＊
//	@Override
//	public void onCreate(Bundle savedInstanceState){
//		//ActivityクラスのonCreateを実行
//		super.onCreate(savedInstanceState);
//		
//		//レイアウト設定ファイルの指定
//		this.setContentView(R.layout.edit_list);
//		
//		//データ取得
//		this.getData();
//	}
//	
//	private void getData(){
//		//データベースを開く
//		pHelper = new ProductDBHelper(this);
//		productDB = pHelper.openDataBase();
//		
//		//ラジオボタン生成
//		this.createRadio();
//		
//		//データベースを閉じる
//		productDB.close();
//	}
//	
//	private void createRadio(){
//		//データ取得
//		Cursor cursor = productDB.query("product_info", COLUMNS, null, null, null, null, "product_category");
//		
//		//LinearLayout取得
//		LinearLayout lLayout = (LinearLayout)this.findViewById(R.id.itemInfo);
//		
//		//RadioGroup生成
//		RadioGroup rGroup = new RadioGroup(this);
//		rGroup.setId(0);
//		
//		//レコード数チェック
//		//0件の場合:TextView生成
//		//1件以上の場合:RadioGroup生成
//		if(cursor.moveToFirst()){
//
//			//登録内容分繰り返す
//			do{
//				//キーワード設定(　品物名　（種別名）)
//				String itemName = "　品物名：　" + cursor.getString(1) + "\n　種別：　　" + cursor.getString(2);
//				
//				//RadioButton生成
//				RadioButton rButton = new RadioButton(this);
//				rButton.setId(cursor.getInt(0));
//				rButton.setText(itemName);
//				
//				//RadioGroupにRadioButtonを追加
//				rGroup.addView(rButton);
//			}while(cursor.moveToNext());			
//		
//			//LinearLayoutにRadioGroupを追加
//			lLayout.addView(rGroup);
//			
//		}else{
//			//TextView生成
//			TextView nothing = new TextView(this);
//			nothing.setText("品物は現在登録されていません。");
//			
//			//LinearLayoutにTextViewを追加
//			lLayout.addView(nothing);
//		}
//				
//		//検索結果クリア
//		cursor.close();
//	}
//	//＊＊＊＊＊＊＊＊＊＊＊＊＊＊初期処理メソッド　ここまで＊＊＊＊＊＊＊＊＊＊＊＊＊＊
//
//	
//	
//	//＊＊＊＊＊＊＊＊＊＊＊＊＊＊追加処理メソッド ここから＊＊＊＊＊＊＊＊＊＊＊＊＊＊
//	//追加する押下した場合
//	public void itemAdd(View view){
//		
//		//初期化
//		item_name = "";
//		category_name = "";
//		
//		//EditText生成
//		final EditText itemName = new EditText(this);
//		itemName.setHint("(例) トマト");
//		itemName.setInputType(InputType.TYPE_CLASS_TEXT);
//				
//		//ダイアログ生成
//		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//		dialog.setTitle("品物入力ダイアログ");
//		dialog.setMessage("追加したい品物名を入力してください。");
//		dialog.setView(itemName);
//		dialog.setPositiveButton("次へ", new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				//カテゴリ選択ダイアログ画面へ
//				item_name = itemName.getText().toString();
//				selectCategory();
//			}
//		});
//		dialog.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				//追加処理キャンセル表示
//				showCancel("追加");
//			}
//		});
//				
//		//ダイアログ表示
//		dialog.show();
//	}
//		
//	//カテゴリ選択画面表示
//	private void selectCategory(){
//		
//		//品物未入力チェック
//		if("".equals(item_name.trim())){
//			Toast cancel = Toast.makeText(this, "品物名が未入力です。" + "\n追加処理を終了します。" , Toast.LENGTH_LONG);
//			cancel.setGravity(Gravity.CENTER, 0, 0);
//			cancel.show();
//		}else{
//			//ダイアログ生成
//			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//			dialog.setTitle("種別選択ダイアログ");
//			dialog.setSingleChoiceItems(Category.CATEGORIES, 0, new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int whichButton) {
//					//選択位置保持
//					which = whichButton;
//				}
//			});
//			dialog.setPositiveButton("次へ", new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int whichButton) {
//					//種別情報取得
//					category_name = Category.CATEGORIES[which];
//					
//					//確認ダイアログ表示
//					confirmDialog();
//				}
//			});
//			dialog.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int whichButton) {
//					//追加処理キャンセル表示
//					showCancel("追加");
//				}
//			});
//			
//			//ダイアログ表示
//			dialog.show();
//		}
//	}
//	
//	//確認ダイアログ
//	private void confirmDialog(){
//		//ダイアログ用メッセージ設定
//		String confirmTxt = "追加する品物は以下でよろしいでしょうか？" + 
//							"\n品物名：　" + item_name +
//							"\n種別：　　" + category_name;
//		
//		//ダイアログ生成
//		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//		dialog.setTitle("確認ダイアログ");
//		dialog.setMessage(confirmTxt);
//		dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				//データ追加処理へ
//				addData();
//			}
//		});
//		dialog.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				//追加処理キャンセル
//				showCancel("追加");
//			}
//		});
//		
//		//ダイアログ表示
//		dialog.show();
//	}
//	
//	//データベース追加処理
//	private void addData(){
//		//データベースを開く
//		productDB = pHelper.openDataBase();
//		
//		try{
//			//トランザクション制御開始
//			productDB.beginTransaction();
//			
//			//登録データ設定
//			ContentValues val = new ContentValues();
//			val.put("product_name", item_name);
//			val.put("product_category", category_name);
//			
//			//データ登録
//			productDB.insert("product_info", null, val);
//			
//			//コミット
//			productDB.setTransactionSuccessful();
//			
//			//トランザクション制御終了
//			productDB.endTransaction();
//			
//		}catch(Exception e){
//			Log.e("InsertError",e.toString());
//		}finally{
//			//データベースを閉じる
//			productDB.close();
//		}
//		
//		Toast.makeText(this, "データを追加しました。", Toast.LENGTH_LONG).show();
//		
//		//ラジオボタン更新
//		this.getData();
//	}
//	//＊＊＊＊＊＊＊＊＊＊＊＊＊＊追加処理メソッド　ここまで＊＊＊＊＊＊＊＊＊＊＊＊＊＊
//	
//	
//	//＊＊＊＊＊＊＊＊＊＊＊＊＊＊共通処理メソッド　ここから＊＊＊＊＊＊＊＊＊＊＊＊＊＊
//	//処理キャンセル表示
//	private void showCancel(String modeName){
//		Toast cancel = Toast.makeText(EditList.this, "キャンセルが押されました\n" + modeName + "処理を終了します。" ,  Toast.LENGTH_LONG);
//		cancel.setGravity(Gravity.CENTER, 0, 0);
//		cancel.show();
//	}
//	//＊＊＊＊＊＊＊＊＊＊＊＊＊＊共通処理メソッド　ここまで＊＊＊＊＊＊＊＊＊＊＊＊＊＊
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	//要改善
//	//変更する押下した場合
//	public void itemUpdate(View view){
//		//生成したRadioGroupを取得
//		RadioGroup rGroup = (RadioGroup)this.findViewById(0);
//				
//		//チェックされているRadioButtonを取得する
//		RadioButton rButton = (RadioButton)this.findViewById(rGroup.getCheckedRadioButtonId());
//				
//		//チェック有無確認
//		if(rButton == null){
//			Toast.makeText(this, "【品物リスト】から変更する品物をひとつ選択してください。", Toast.LENGTH_SHORT).show();
//		}else{
//			//String confirm = rButton.getText().toString() + "\n　の内容を変更しますか？";
//			//Toast.makeText(this, confirm, Toast.LENGTH_SHORT).show();
//			Toast.makeText(this, Integer.toString(rButton.getId()), Toast.LENGTH_SHORT).show();
//		}
//	}
//	
//	//要改善
//	//削除する押下した場合
//	public void itemDelete(View view){
//		//生成したRadioGroupを取得
//		RadioGroup rGroup = (RadioGroup)this.findViewById(0);
//						
//		//チェックされているRadioButtonを取得する
//		RadioButton rButton = (RadioButton)this.findViewById(rGroup.getCheckedRadioButtonId());
//						
//		//チェック有無確認
//		if(rButton == null){
//			Toast.makeText(this, "【品物リスト】から削除する品物をひとつ選択してください。", Toast.LENGTH_SHORT).show();
//		}else{
//			//String confirm = rButton.getText().toString() + "\n　を削除しますか？";
//			//Toast.makeText(this, confirm, Toast.LENGTH_SHORT).show();
//			Toast.makeText(this, Integer.toString(rButton.getId()), Toast.LENGTH_SHORT).show();
//		}
//	}
//	
//	//トップページへ押下した場合
//	public void returnTopPage(View view){
//		//アクティビティ終了
//		this.finish();
//	}
>>>>>>> Imada
}
