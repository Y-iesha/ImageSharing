package com.example.imagesharing.activity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.imagesharing.R;
import com.example.imagesharing.entity.User;
import com.example.imagesharing.util.StringUtils;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends BaseActivity {

    private EditText etName;        //用户名
    private EditText etPwd;         //密码
    private Button btnLogin;        //登录按钮
    User user = new User();


    @Override
    protected int initLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        etName = findViewById(R.id.et_account);
        etPwd = findViewById(R.id.et_pwd);
        btnLogin = findViewById(R.id.btn_login);
    }

    @Override
    protected void initData() {
        btnLogin.setOnClickListener(new View.OnClickListener() {          //登录按钮监听
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString().trim();
                String pwd = etPwd.getText().toString().trim();
                login(name, pwd);          //调用类内登录方法


            }
        });

    }


    //登录
    private void login(String name, String pwd) {
        //输入为空时
        if (StringUtils.isEmpty(name)) {
            showToast("请输入账号");
            return;
        }
        if (StringUtils.isEmpty(pwd)) {
            showToast("请输入密码");
            return;
        }

        //输入非空时
        user.setUsername(name);
        user.setPassword(pwd);
        //BmobUser提供的用户登录方法
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
