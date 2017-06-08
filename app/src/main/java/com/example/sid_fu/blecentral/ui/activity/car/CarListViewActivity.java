package com.example.sid_fu.blecentral.ui.activity.car;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sid_fu.blecentral.App;
import com.example.sid_fu.blecentral.R;
import com.example.sid_fu.blecentral.db.entity.CarBrand;
import com.example.sid_fu.blecentral.ui.activity.base.BaseActionBarActivity;
import com.example.sid_fu.blecentral.utils.pinyin.CharacterParser;
import com.example.sid_fu.blecentral.utils.pinyin.ClearEditText;
import com.example.sid_fu.blecentral.utils.pinyin.PinyinComparator;
import com.example.sid_fu.blecentral.utils.pinyin.SideBar;
import com.example.sid_fu.blecentral.utils.pinyin.SortAdapter;
import com.example.sid_fu.blecentral.utils.pinyin.SortModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CarListViewActivity extends BaseActionBarActivity {
	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private SortAdapter adapter;
	private ClearEditText mClearEditText;
	private List<CarBrand> carBrands = new ArrayList<>();
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<SortModel> SourceDateList;

	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;
	private SQLiteDatabase database;

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
		setContentView(R.layout.activity_main);
		 /*显示App icon左侧的back键*/
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		App.getInstance().addActivity(this);
		intitDatas();
//		App.getInstance().speak("请选择品牌");
	}
	private void intitDatas() {
		//carBrands = DbHelper.getInstance(this).getCarList();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 打开数据库，database是在Main类中定义的一个SQLiteDatabase类型的变量
				Message msg = new Message();
				msg.what = 0;
				msg.obj = App.getInstance().dbHelper.getCarList();
				handler.sendMessage(msg);
			}
		}).start();
	}
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 0) {
				carBrands = (List<CarBrand>) msg.obj;
				initViews();
			}
		}
	};
	private void initViews() {
		//实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();

		pinyinComparator = new PinyinComparator();

		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialog);

		//设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				//该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if(position != -1){
					sortListView.setSelection(position);
				}

			}
		});

		sortListView = (ListView) findViewById(R.id.country_lvcountry);
		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				//这里要利用adapter.getItem(position)来获取当前position所对应的对象
//				App.getInstance().speak("您选择的品牌是"+((SortModel)adapter.getItem(position)).getName());
//				Toast.makeText(getApplication(), ((SortModel)adapter.getItem(position)).getName(), Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(CarListViewActivity.this,CarSeriesListViewActivity.class);
				intent.putExtra("id",((SortModel) adapter.getItem(position)).getId());
				startActivity(intent);
				//finish();
			}
		});

		//SourceDateList = filledData(getResources().getStringArray(R.array.date));
		SourceDateList = filledData(carBrands);

		// 根据a-z进行排序源数据
		Collections.sort(SourceDateList, pinyinComparator);
		adapter = new SortAdapter(this, SourceDateList);
		sortListView.setAdapter(adapter);


		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);

		//根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

	}


	/**
	 * 为ListView填充数据
	 * @param date
	 * @return
	 */
	private List<SortModel> filledData(String [] date){
		List<SortModel> mSortList = new ArrayList<SortModel>();

		for(int i=0; i<date.length; i++){
			SortModel sortModel = new SortModel();
			sortModel.setName(date[i]);
			//汉字转换成拼音
			String pinyin = characterParser.getSelling(date[i]);
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if(sortString.matches("[A-Z]")){
				sortModel.setSortLetters(sortString.toUpperCase());
			}else{
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;

	}
	/**
	 * 为ListView填充数据
	 * @param date
	 * @return
	 */
	private List<SortModel> filledData(List<CarBrand> date){
		List<SortModel> mSortList = new ArrayList<SortModel>();

		for(int i=0; i<date.size(); i++){
			SortModel sortModel = new SortModel();
			sortModel.setName(date.get(i).getCname());
			sortModel.setEname(date.get(i).getEname());
			sortModel.setId(date.get(i).getId());
			//汉字转换成拼音
			String pinyin = characterParser.getSelling(date.get(i).getCname());
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if(sortString.matches("[A-Z]")){
				sortModel.setSortLetters(sortString.toUpperCase());
			}else{
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;

	}
	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * @param filterStr
	 */
	private void filterData(String filterStr){
		List<SortModel> filterDateList = new ArrayList<SortModel>();

		if(TextUtils.isEmpty(filterStr)){
			filterDateList = SourceDateList;
		}else{
			filterDateList.clear();
			for(SortModel sortModel : SourceDateList){
				String name = sortModel.getName();
				if(name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())){
					filterDateList.add(sortModel);
				}
			}
		}

		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
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
	public void imporDatabase(String dbName,int id) {
		//存放数据库的目录
		String dirPath="/data/data/com.hkx.wan/databases";
		File dir = new File(dirPath);
		if(!dir.exists()) {
			dir.mkdir();
		}
		//数据库文件
		File file = new File(dir, dbName);
		try {
			if(!file.exists()) {
				file.createNewFile();
			}
			//加载需要导入的数据库
			InputStream is = this.getApplicationContext().getResources().openRawResource(id);
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffere=new byte[is.available()];
			is.read(buffere);
			fos.write(buffere);
			is.close();
			fos.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}
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
