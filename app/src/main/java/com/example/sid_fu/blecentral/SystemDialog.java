package com.example.sid_fu.blecentral;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.sid_fu.blecentral.ui.activity.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/5/27.
 */
public class SystemDialog extends BaseActivity {
    @Bind(R.id.button2)
    Button button2;
    @Bind(R.id.button3)
    Button button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_system_dialog);
        ButterKnife.bind(this);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
