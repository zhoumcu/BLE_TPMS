package com.example.sid_fu.blecentral.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.sid_fu.blecentral.R;
import com.example.sid_fu.blecentral.utils.Constants;
import com.example.sid_fu.blecentral.utils.SharedPreferences;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tiansj on 15/7/29.
 */
public class ChoiseModelActivity extends BaseFragmentActivity {

    @Bind(R.id.radioButton4)
    RadioButton radioButton4;
    @Bind(R.id.radioButton5)
    RadioButton radioButton5;
    @Bind(R.id.btn_save)
    Button btnSave;
    private String models = "竖屏";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean firstTimeUse = SharedPreferences.getInstance().getBoolean(Constants.FIRST_CONFIG, false);
        if (firstTimeUse) {
            Intent intent = new Intent();
            intent.setClass(ChoiseModelActivity.this, SplashActivity.class);
            startActivity(intent);
            finish();
        }else
        {
            setContentView(R.layout.aty_config_land);
            ButterKnife.bind(this);
            //根据ID找到RadioGroup实例
             RadioGroup group = (RadioGroup)this.findViewById(R.id.radioGroup);
             //绑定一个匿名监听器
             group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                 public void onCheckedChanged(RadioGroup arg0, int arg1) {
                        // TODO Auto-generated method stub
                     //获取变更后的选中项的ID
                      int radioButtonId = arg0.getCheckedRadioButtonId();
                        //根据ID获取RadioButton的实例
                        RadioButton rb = (RadioButton)ChoiseModelActivity.this.findViewById(radioButtonId);
                        //更新文本内容，以符合选中项
                        models = rb.getText().toString();
                    }
                 });
             }

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.getInstance().putString(Constants.LANDORPORT, models);
                    Intent intent = new Intent();
                    intent.setClass(ChoiseModelActivity.this, SplashActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
    }

    private void initActivity() {
        boolean firstTimeUse = SharedPreferences.getInstance().getBoolean(Constants.FIRST_CONFIG, false);
        if (firstTimeUse) {
            //initGuideGallery();
            Intent intent = new Intent();
            intent.setClass(ChoiseModelActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            //initLaunchLogo();
            Intent intent = new Intent();
            intent.setClass(ChoiseModelActivity.this, ViewPagerDemoActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
