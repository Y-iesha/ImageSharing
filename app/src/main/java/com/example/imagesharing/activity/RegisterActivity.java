package com.example.imagesharing.activity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.imagesharing.R;
import com.example.imagesharing.entity.User;
import com.example.imagesharing.util.StringUtils;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class RegisterActivity extends BaseActivity {

    private EditText etName;         //用户名
    private EditText etPwd;          //密码
    private Button btnRegister;      //注册按钮



    @Override
    protected int initLayout() {
        return R.layout.activity_register;
    }

    @Override
    protected void initView() {
        etName = findViewById(R.id.et_account);
        etPwd = findViewById(R.id.et_pwd);
        btnRegister = findViewById(R.id.btn_register);

    }

    @Override
    protected void initData() {
        btnRegister.setOnClickListener(new View.OnClickListener() {         //注册按钮监听
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString().trim();
                String pwd = etPwd.getText().toString().trim();
                register(name, pwd);        //调用类内注册方法


            }
        });
    }

    //注册
    private void register(String name, String pwd) {
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
        User user = new User();
        user.setUsername(name);
        user.setPassword(pwd);
        user.signUp(new SaveListener<User>() {
            @Override
            public void done(User myUser, BmobException e) {
                if (e == null) {
                    showToast("注册成功"+myUser.getObjectId());
                    BmobQuery<User> query=new BmobQuery<User>();
                    query.addWhereEqualTo("objectId","7NbI2225");
                    query.findObjects(new FindListener<User>() {

                        public void done(List<User> list, BmobException e) {
                            if(e==null){
                                BmobFile pic= list.get(0).getAvatar();
                                myUser.setAvatar(pic);
                                myUser.update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            //showToast("头像成功");
                                        } else {
                                            Log.e("BMOB", e.toString());
                                                                                   }
                                    }
                                });

                            }else{
                                Log.i("bmob","失败："+e.getMessage());
                            }
                        }
                    });

                    finish();
                } else {
                    showToast("注册失败" + e.toString());
                }
            }
        });


    }
}