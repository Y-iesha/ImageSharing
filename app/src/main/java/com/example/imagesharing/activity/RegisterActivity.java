package com.example.imagesharing.activity;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.imagesharing.R;
import com.example.imagesharing.entity.Avatar;
import com.example.imagesharing.util.StringUtils;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
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
                    BmobQuery<Avatar> query=new BmobQuery<Avatar>();
                    query.addWhereEqualTo("objectId","0czC777G");
                    query.findObjects(new FindListener<Avatar>() {

                        public void done(List<Avatar> list, BmobException e) {
                            if(e==null){
                                Avatar avatar = new Avatar();
                                BmobFile pic= list.get(0).getAvatar();
                                avatar.setAuthor(myUser);
                                avatar.setAvatar(pic);
                                avatar.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        if (e == null) {
                                            Toast.makeText(RegisterActivity.this, "插入记录成功:" + s, Toast.LENGTH_SHORT).show();

                                        } else {
                                            Toast.makeText(RegisterActivity.this, "插入记录失败:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }else{
                                Log.i("bmob","失败："+e.getMessage());
                            }
                        }
                    });

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