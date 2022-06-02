package com.example.imagesharing.activity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.imagesharing.R;
import com.example.imagesharing.util.StringUtils;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends BaseActivity {

    private EditText etAccount;
    private EditText etPwd;
    private Button btnLogin;
    BmobUser user = new BmobUser();


    @Override
    protected int initLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        etAccount = findViewById(R.id.et_account);
        etPwd = findViewById(R.id.et_pwd);
        btnLogin = findViewById(R.id.btn_login);
    }

    @Override
    protected void initData() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String account = etAccount.getText().toString().trim();
                String pwd = etPwd.getText().toString().trim();
                login(account, pwd);


            }
        });

    }

    private void login(String account, String pwd) {
        if (StringUtils.isEmpty(account)) {
            showToast("请输入账号");
            return;
        }
        if (StringUtils.isEmpty(pwd)) {
            showToast("请输入密码");
            return;
        }



        user.setUsername(account);
        user.setPassword(pwd);
        user.login(new SaveListener<BmobUser>() {
            @Override
            public void done(BmobUser user, BmobException e) {
                if(e==null){
                    navigateTo(HomeActivity.class);
                    showToast("登录成功"+user.getObjectId());
                    finish();
                }else{
                    showToast("登录失败" + e.toString());
                }
            }
        });
//        user.login(new SaveListener<_User>() {
//            @Override
//            public void done(_User user, BmobException e) {
//                if (e == null) {
//                    navigateTo(HomeActivity.class);
//                    showToast("登录成功");
//                    finish();
//                    //Snackbar.make(R.layout.activity_registeractivity, "注册成功", Snackbar.LENGTH_LONG).show();
//                } else {
//                    showToast("登录失败" + e.toString());
//                    //Snackbar.make(view, "尚未失败：" + e.getMessage(), Snackbar.LENGTH_LONG).show();
//                }
//            }
//            @Override
//            public void done(BmobUser myUser, BmobException e) {
//                if (e == null) {
//                    navigateTo(HomeActivity.class);
//                    showToast("登录成功");
//                    finish();
//                    //Snackbar.make(R.layout.activity_registeractivity, "注册成功", Snackbar.LENGTH_LONG).show();
//                } else {
//                    showToast("登录失败" + e.toString());
//                    //Snackbar.make(view, "尚未失败：" + e.getMessage(), Snackbar.LENGTH_LONG).show();
//                }
//            }
//        });


    }
}
