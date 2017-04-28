package com.example.sid_fu.blecentral.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.text.TextUtils;

import com.example.sid_fu.blecentral.R;
import com.example.sid_fu.blecentral.db.entity.CarBrand;
import com.example.sid_fu.blecentral.db.entity.RecordData;
import com.example.sid_fu.blecentral.utils.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Created by Administrator on 2016/6/28.
 */
public class DbHelper {
    public static final String DB_DIR1 = Environment.getExternalStorageDirectory().getAbsolutePath()!=null
            ? Environment.getExternalStorageDirectory().getAbsolutePath()+"/survey/":"/sdcard/xiaoan/survey/";

    public static final String DB_NAME = "survey.db";
    private final SQLiteDatabase database;
    private final String DB_DIR;

    public static DbHelper getInstance(Context context) {
        return new DbHelper(context);
    }

    private DbHelper(Context context,int rawId) {
        database = openDatabase(context,rawId);
        DB_DIR = null;
    }
    private DbHelper(Context context) {
        DB_DIR = context.getFilesDir()+"/databases/";
        database = openDatabase(context, R.raw.xianoan);
        Logger.i("db:"+DB_DIR);
    }
    public SQLiteDatabase openDatabase(Context context,int rawId) {
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
                InputStream is = context.getResources().openRawResource(rawId);
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
            SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);
            return database;
        } catch (Exception e) {}
        return null;
    }

    /**
     * 根据品牌id获取车辆系列
     * @param id
     * @return
     */
    public List<CarBrand> getCarSeries(int id) {
        // 查找单词的SQL语句
        String sql = "select * from car_series  where brand =?";
        if(database==null) return new ArrayList<>();
        Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(id)});
        String cname = "未找到该记录.";
        String ename = "未找到该记录.";
        List<CarBrand> carBrands = new ArrayList<>();
        // 必须使用moveToFirst方法将记录指针移动到第1条记录的位置
//        cursor.moveToFirst();
        //  如果查找单词，显示其中文信息
        while (cursor.moveToNext()) {
            if (cursor.getCount() > 0) {
                cname = cursor.getString(cursor.getColumnIndex("name"));
                int idS = cursor.getInt(cursor.getColumnIndex("id"));
//				ename = cursor.getString(cursor.getColumnIndex("ename"));
//				String initial = cursor.getString(cursor.getColumnIndex("initial"));
                Logger.e( "success"+cname+ename);
                CarBrand carBrand = new CarBrand();
                carBrand.setCname(cname);
//				carBrand.setEname(ename);
//				carBrand.setInitial(initial);
                carBrand.setId(idS);
                carBrands.add(carBrand);
//                cursor.moveToNext();
            }
        }
        cursor.close();
        return carBrands;
    }

    /**
     * 根据id和seriesId 获取详细车辆信息
     * @param id
     * @return
     */
    public CarBrand getCarInfo(int id) {
        // 查找单词的SQL语句
        String sql = "select t.*,d.cname as brandName,d.ename as logoName,s.name as seriesName from car_type t left join car_series s on t.series = s.id left join car_brand d on s.brand = d.id " +
                "where t.id = ?";
        if(database==null) return new CarBrand();
        Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(id)});
        String cname = "未找到该记录.";
        String ename = "未找到该记录.";
        // 必须使用moveToFirst方法将记录指针移动到第1条记录的位置
        cursor.moveToFirst();
        //  如果查找单词，显示其中文信息
        CarBrand carBrand = null;
        if (cursor.getCount() > 0) {
            cname = cursor.getString(cursor.getColumnIndex("name"));
            String releaseTime = cursor.getString(cursor.getColumnIndex("releaseTime"));
            String displacement = cursor.getString(cursor.getColumnIndex("displacement"));
            String weight = cursor.getString(cursor.getColumnIndex("weight"));
            String derailleur = cursor.getString(cursor.getColumnIndex("derailleur"));
            String gears = cursor.getString(cursor.getColumnIndex("gears"));
            String seriesName = cursor.getString(cursor.getColumnIndex("seriesName"));
            String logoName = cursor.getString(cursor.getColumnIndex("logoName"));
            String brandName = cursor.getString(cursor.getColumnIndex("brandName"));
            int ids = cursor.getInt(cursor.getColumnIndex("id"));
            Logger.e( "success"+cname+ename+seriesName);
            carBrand = new CarBrand();
            carBrand.setCname(cname);
            carBrand.setDerailleur(derailleur);
            carBrand.setDisplacement(displacement);
            carBrand.setReleaseTime(releaseTime);
            carBrand.setWeight(weight);
            carBrand.setGears(gears);
            carBrand.setSeriesName(seriesName);
            carBrand.setEname(logoName);
            carBrand.setId(ids);
            carBrand.setBrandName(brandName);
        }
        cursor.close();
        return carBrand;
    }

    public List<CarBrand> getCarType(int id) {
        // 查找单词的SQL语句
        String sql = "select * from car_type where series =?";
        if(database==null) return new ArrayList<>();
        Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(id)});
        String cname = "未找到该单词.";
        String ename = "未找到该单词.";
        // 必须使用moveToFirst方法将记录指针移动到第1条记录的位置
//        cursor.moveToFirst();
        List<CarBrand> SourceDateList = new ArrayList<>();
        //  如果查找单词，显示其中文信息
        while (cursor.moveToNext()) {
            if (cursor.getCount() > 0) {
                cname = cursor.getString(cursor.getColumnIndex("name"));
                String releaseTime = cursor.getString(cursor.getColumnIndex("releaseTime"));
                String displacement = cursor.getString(cursor.getColumnIndex("displacement"));
                String weight = cursor.getString(cursor.getColumnIndex("weight"));
                String derailleur = cursor.getString(cursor.getColumnIndex("derailleur"));
                String gears = cursor.getString(cursor.getColumnIndex("gears"));
                int ids = cursor.getInt(cursor.getColumnIndex("id"));
                Logger.e( "success"+cname+ename);
                CarBrand carBrand = new CarBrand();
                carBrand.setCname(cname);
                carBrand.setDerailleur(derailleur);
                carBrand.setDisplacement(displacement);
                carBrand.setReleaseTime(releaseTime);
                carBrand.setWeight(weight);
                carBrand.setGears(gears);
                carBrand.setId(ids);
                SourceDateList.add(carBrand);
//                cursor.moveToNext();
            }
        }
        cursor.close();
        return SourceDateList;
    }
    public List<CarBrand> getCarList() throws NullPointerException {
        // 查找单词的SQL语句
        String sql = "select * from car_brand ";
        if(database==null) return new ArrayList<>();
        Cursor cursor = database.rawQuery(sql, null);
        String cname = "未找到该单词.";
        String ename = "未找到该单词.";
        List<CarBrand> carBrands = new ArrayList<>();
        // 必须使用moveToFirst方法将记录指针移动到第1条记录的位置
//        cursor.moveToFirst();
        //  如果查找单词，显示其中文信息
        while (cursor.moveToNext()) {
            if (cursor.getCount() > 0) {
                cname = cursor.getString(cursor.getColumnIndex("cname"));
                ename = cursor.getString(cursor.getColumnIndex("ename"));
                String initial = cursor.getString(cursor.getColumnIndex("initial"));
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                Logger.e( "success"+cname+ename);
                CarBrand carBrand = new CarBrand();
                carBrand.setCname(cname);
                carBrand.setEname(ename);
                carBrand.setInitial(initial);
                carBrand.setId(id);
                carBrands.add(carBrand);
//                cursor.moveToNext();
            }
        }
        cursor.close();
        return carBrands;
    }

    public List<CarBrand> getCarLunTaiTypeList() {
        // 查找单词的SQL语句
        String sql = "select * from car_tire ";
        if(database==null) return new ArrayList<>();
        Cursor cursor = database.rawQuery(sql, new String[]{});
        String cname = "未找到该记录.";
        String ename = "未找到该记录.";
        List<CarBrand> carBrands = new ArrayList<>();
        // 必须使用moveToFirst方法将记录指针移动到第1条记录的位置
//        cursor.moveToFirst();
        //  如果查找单词，显示其中文信息
        while (cursor.moveToNext()) {
            if (cursor.getCount() > 0) {
                cname = cursor.getString(cursor.getColumnIndex("name"));
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                Logger.e( "success"+cname+ename);
                CarBrand carBrand = new CarBrand();
                carBrand.setCname(cname);
                carBrand.setId(id);
                carBrands.add(carBrand);
//                cursor.moveToNext();
            }
        }
        cursor.close();
        return carBrands;
    }

    public int saveCarInfo(CarBrand carBrand) {
        // 查找单词的SQL语句
        if(database==null) return 0;
        String sql = "insert into  car_info (brandCname,brandEname,carTypeName,carSeriesName,releaseTime,userCode,weight,displacement,derailleur,gears) values (?,?,?,?,?,?,?,?,?,?)";
        ContentValues cv = new ContentValues();
        cv.put("releaseTime", carBrand.getReleaseTime());
        cv.put("displacement", carBrand.getDisplacement());
        cv.put("weight", carBrand.getWeight());
        cv.put("derailleur", carBrand.getDerailleur());
        cv.put("gears", carBrand.getGears());
        cv.put("ftyre", carBrand.getFtyre());
        cv.put("btyre", carBrand.getBtyre());
        cv.put("brandCname", carBrand.getBrandName());
        cv.put("brandEname", carBrand.getEname());
        cv.put("carSeriesName", carBrand.getSeriesName());
        cv.put("carTypeName", carBrand.getCname());
        cv.put("userCode", carBrand.getSeriesName());
        database.insert("car_info", null,cv);
//        database.execSQL(sql,new String[]{"good","good","good","good","good","good","good","good","good","good",});
        //getCarInfo();
        Cursor cursor = database.rawQuery("select max(id) as uid from car_info", null);
        cursor.moveToFirst();
        int count = cursor.getInt(cursor.getColumnIndex("uid"));
        cursor.close();
        return count;
    }
    public void updateCarInfo(CarBrand carBrand, int id) {
        // 查找单词的SQL语句
        String sql = "update into  car_info (brandCname,brandEname,carTypeName,carSeriesName,releaseTime,userCode,weight,displacement,derailleur,gears) values (?,?,?,?,?,?,?,?,?,?)";
        ContentValues cv = new ContentValues();
        cv.put("ftyre", carBrand.getFtyre());
        cv.put("btyre", carBrand.getBtyre());
        database.update("car_info", cv,"id = ?",new String[]{String.valueOf(id)});
        getUserCarInfo(id);
    }

    public CarBrand getUserCarInfo(int id) {
        // 查找单词的SQL语句
        String sql = "select * from car_info where id = ?";
        if(database==null) return new CarBrand();
        Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(id)});
        // 必须使用moveToFirst方法将记录指针移动到第1条记录的位置
        cursor.moveToFirst();
        Logger.e("条数："+String.valueOf(cursor.getCount()));
        //  如果查找单词，显示其中文信息
        CarBrand carBrand = null;
        if (cursor.getCount() > 0) {
            String releaseTime = cursor.getString(cursor.getColumnIndex("releaseTime"));
            String displacement = cursor.getString(cursor.getColumnIndex("displacement"));
            String weight = cursor.getString(cursor.getColumnIndex("weight"));
            String derailleur = cursor.getString(cursor.getColumnIndex("derailleur"));
            String gears = cursor.getString(cursor.getColumnIndex("gears"));
            String seriesName = cursor.getString(cursor.getColumnIndex("carSeriesName"));
            String logoName = cursor.getString(cursor.getColumnIndex("brandEname"));
            String brandName = cursor.getString(cursor.getColumnIndex("brandCname"));
            String ftyre = cursor.getString(cursor.getColumnIndex("ftyre"));
            String btyre = cursor.getString(cursor.getColumnIndex("btyre"));
            int ids = cursor.getInt(cursor.getColumnIndex("id"));
            String carTypeName = cursor.getString(cursor.getColumnIndex("carTypeName"));
            carBrand = new CarBrand();
            carBrand.setDerailleur(derailleur);
            carBrand.setDisplacement(displacement);
            carBrand.setReleaseTime(releaseTime);
            carBrand.setCname(carTypeName);
            carBrand.setWeight(weight);
            carBrand.setGears(gears);
            carBrand.setSeriesName(seriesName);
            carBrand.setEname(logoName);
            carBrand.setId(ids);
            carBrand.setFtyre(ftyre);
            carBrand.setBtyre(btyre);
            carBrand.setBrandName(brandName);
            Logger.e( "success:"+carBrand.toString());
        }
        cursor.close();
        return carBrand;
    }
    public void insertCarData(RecordData data) throws SQLiteCantOpenDatabaseException {
        // 查找单词的SQL语句
        String sql = "insert into  car_data (deviceId,name,data,createDate,state) values (?,?,?,?,?)";
        if(database==null) return;
        ContentValues cv = new ContentValues();
        cv.put("deviceId", data.getDeviceId());
        cv.put("name", data.getName());
        cv.put("data", data.getData());
        cv.put("state", data.getState());
        cv.put("createDate", data.getCreateDate());
        database.insert("car_data", null,cv);
//        Cursor cursor = database.rawQuery("select max(id) as uid from car_info", null);
//        cursor.moveToFirst();
//        return cursor.getInt(cursor.getColumnIndex("uid"));
    }
    public void updateCarData(int id,String name,RecordData data) throws SQLiteCantOpenDatabaseException {
        // 查找单词的SQL语句
        String sql = "update into  car_info (brandCname,brandEname,carTypeName,carSeriesName,releaseTime,userCode,weight,displacement,derailleur,gears) values (?,?,?,?,?,?,?,?,?,?)";
        if(database==null) return ;
        ContentValues cv = new ContentValues();
        cv.put("data", data.getData());
        cv.put("state", data.getState());
        cv.put("createDate", data.getCreateDate());
        database.update("car_data", cv,"deviceId = ? and name =?",new String[]{String.valueOf(id),name});
//        getUserCarInfo(id);
    }
    public boolean update(int deviceId, String name, RecordData data) {
        if(TextUtils.isEmpty(name)) return false;
        if(database==null) return false;
        try {
            String sql = "select * from car_data where deviceId = ? and name = ?";
            Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(deviceId),name});
            // 必须使用moveToFirst方法将记录指针移动到第1条记录的位置
//        cursor.moveToFirst();
//            while (cursor.moveToNext()) {
//                String name1 = cursor.getString(cursor.getColumnIndex("name"));
//                Logger.e(name1);
//            }
            if(cursor.getCount()>0) {
                Logger.e("更新数据");
                updateCarData(deviceId,name,data);
            }else {
                Logger.e("插入新数据");
                insertCarData(data);
            }
            cursor.close();
            return true;
        }catch (IllegalArgumentException e) {
        }catch (NullPointerException e1) {
        }catch (SQLiteCantOpenDatabaseException e2) {
        }catch (Exception e){

        }
        return false;
    }
    public Observable<Boolean> updateRecord(int deviceId,String name,RecordData data) {
        return Observable.just(update(deviceId,name,data));
    }
    public List<RecordData> getCarDataList(int deviceId) throws SQLiteException {
        // 查找单词的SQL语句
        String sql = "select * from car_data where deviceId = ?";
        if(database==null) return new ArrayList<>();
        Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(deviceId)});
        String name = "未找到该单词.";
        String data = "未找到该单词.";
        List<RecordData> carBrands = new ArrayList<>();
        // 必须使用moveToFirst方法将记录指针移动到第1条记录的位置
//        cursor.moveToFirst();
        if(cursor.getCount()<=0) return carBrands;
        //  如果查找单词，显示其中文信息
        while (cursor.moveToNext()) {
            if (cursor.getCount() > 0) {
                name = cursor.getString(cursor.getColumnIndex("name"));
                data = cursor.getString(cursor.getColumnIndex("data"));
                String state = cursor.getString(cursor.getColumnIndex("state"));
                String createDate = cursor.getString(cursor.getColumnIndex("createDate"));
//                String initial = cursor.getString(cursor.getColumnIndex("initial"));
//                int id = cursor.getInt(cursor.getColumnIndex("id"));
                Logger.e( "success"+name+data);
                RecordData carBrand = new RecordData();
                carBrand.setName(name);
                carBrand.setData(data);
                carBrand.setCreateDate(createDate);
                carBrand.setState(state);
                carBrands.add(carBrand);
//                cursor.moveToNext();
            }
        }
        cursor.close();
        return carBrands;
    }
}
