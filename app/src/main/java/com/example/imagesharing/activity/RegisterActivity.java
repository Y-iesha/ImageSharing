package com.example.imagesharing.activity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.imagesharing.R;
import com.example.imagesharing.util.StringUtils;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends BaseActivity {

    private EditText etAccount;
    private EditText etPwd;
    private Button btnRegister;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_register);
//    }

    @Override
    protected int initLayout() {
        return R.layout.activity_register;
    }

    @Override
    protected void initView() {
        etAccount = findViewById(R.id.et_account);
        etPwd = findViewById(R.id.et_pwd);
        btnRegister = findViewById(R.id.btn_register);

    }

    @Override
    protected void initData() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String account = etAccount.getText().toString().trim();
                String pwd = etPwd.getText().toString().trim();
                register(account, pwd);


            }
        });
    }

    private void register(String account, String pwd) {
        if (StringUtils.isEmpty(account)) {
            showToast("请输入账号");
            return;
        }
        if (StringUtils.isEmpty(pwd)) {
            showToast("请输入密码");
            return;
        }

        BmobUser user = new BmobUser();
        user.setUsername(account);
        user.setPassword(pwd);
        user.signUp(new SaveListener<BmobUser>() {
            @Override
            public void done(BmobUser myUser, BmobException e) {
                if (e == null) {
                    showToast("注册成功"+myUser.getObjectId());
                    finish();
                    //Snackbar.make(R.layout.activity_registeractivity, "注册成功", Snackbar.LENGTH_LONG).show();
                } else {
                    showToast("注册失败" + e.toString());
                    //Snackbar.make(view, "尚未失败：" + e.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });


    }
}