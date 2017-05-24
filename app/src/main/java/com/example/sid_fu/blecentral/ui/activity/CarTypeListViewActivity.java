package com.example.sid_fu.blecentral.ui.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sid_fu.blecentral.App;
import com.example.sid_fu.blecentral.R;
import com.example.sid_fu.blecentral.adapter.CarListAdapter;
import com.example.sid_fu.blecentral.db.DbHelper;
import com.example.sid_fu.blecentral.db.entity.CarBrand;
import com.example.sid_fu.blecentral.utils.pinyin.CharacterParser;
import com.example.sid_fu.blecentral.utils.pinyin.ClearEditText;
import com.example.sid_fu.blecentral.utils.pinyin.PinyinComparator;
import com.example.sid_fu.blecentral.utils.pinyin.SideBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CarTypeListViewActivity extends BaseActionBarActivity {
	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private CarListAdapter adapter;
	private ClearEditText mClearEditText;
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<CarBrand> SourceDateList = new ArrayList<>();

	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;
	private SQLiteDatabase database;
	private int id;
	private CarTypeListViewActivity mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		if(SharedPreferences.getInstance().getString(Constants.LANDORPORT,Constants.DEFIED).equals("横屏"))
//		{
//			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式
//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
//		}else{
//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
//		}
		setContentView(R.layout.activity_list);
		 /*显示App icon左侧的back键*/
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		mContext = CarTypeListViewActivity.this;
		App.getInstance().addActivity(this);
		intitDatas();
		//initViews();
//		App.getInstance().speak("请选择车型");
	}
	private void intitDatas() {
		id = getIntent().getExtras().getInt("id");
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 打开数据库，database是在Main类中定义的一个SQLiteDatabase类型的变量
				Message msg = new Message();
				msg.what = 0;
				msg.obj = DbHelper.getInstance(mContext).getCarType(id);
				handler.sendMessage(msg);
			}
		}).start();
		//SourceDateList = DbHelper.getInstance(mContext).getCarType(id);
	}
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 0) {
				SourceDateList = (List<CarBrand>) msg.obj;
				initViews();
			}
		}
	};
	private void initViews() {
		sortListView = (ListView) findViewById(R.id.listView);
		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long idL) {
				//这里要利用adapter.getItem(position)来获取当前position所对应的对象
//				App.getInstance().speak("您选择的车型是"+((CarBrand)adapter.getItem(position)).getCname());
//				Toast.makeText(getApplication(), ((CarBrand)adapter.getItem(position)).getCname(), Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(CarTypeListViewActivity.this,CarInfoDetailActivity.class);
				intent.putExtra("id",((CarBrand) adapter.getItem(position)).getId());
				startActivity(intent);
			}
		});
		adapter = new CarListAdapter(this, SourceDateList);
		sortListView.setAdapter(adapter);
	}


	public static final String DB_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/survey/";
	public static final String DB_NAME = "survey.db";
	private SQLiteDatabase openDatabase(int rawId) {
		try {
			// 获得dictionary.db文件的绝对路径
			String databaseFilename = DB_DIR  + DB_NAME;
			File dir = new File(DB_DIR);
			// 如果/sdcard/dictionary目录中存在，创建这个目录
			if (!dir.exists())
				dir.mkdir();
			// 如果在/sdcard/dictionary目录中不存在
			// dictionary.db文件，则从res\raw目录中复制这个文件到
			// SD卡的目录（/sdcard/dictionary）
			if (!(new File(databaseFilename)).exists()) {
				// 获得封装dictionary.db文件的InputStream对象
				InputStream is = getResources().openRawResource(rawId);
				FileOutputStream fos = new FileOutputStream(databaseFilename);
				byte[] buffer = new byte[8192];
				int count = 0;
				// 开始复制dictionary.db文件
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}

				fos.close();
				is.close();
			}
			// 打开/sdcard/dictionary目录中的dictionary.db文件
			SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(
					databaseFilename, null);
			return database;
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				break;
		}
		return true;
	}
}
