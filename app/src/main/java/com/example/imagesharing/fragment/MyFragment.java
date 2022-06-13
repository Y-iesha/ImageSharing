package com.example.imagesharing.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.imagesharing.MainActivity;
import com.example.imagesharing.R;
import com.example.imagesharing.activity.LoginActivity;
import com.example.imagesharing.activity.RegisterActivity;
import com.example.imagesharing.entity.Avatar;
import com.example.imagesharing.entity.Image;
import com.example.imagesharing.entity.User;
import com.example.imagesharing.util.PhotoUtils;
import com.example.imagesharing.util.RealPathFromUriUtils;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyFragment extends Fragment {
    private static final String TAG = CollectFragment.class.getName();
    private static final int USE_PHOTO = 1003;
    private String image_path;
    private ImageView avatar;
    private TextView username;
    private RelativeLayout logout;

    static User user = new User();
    View view;


    public static MyFragment newInstance() {
        MyFragment fragment = new MyFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my, container, false);
        avatar = (ImageView) view.findViewById(R.id.img_avatar);
        username = (TextView)view.findViewById(R.id.text_username);
        logout = (RelativeLayout) view.findViewById(R.id.rl_logout);

        user = User.getCurrentUser(User.class);
        username.setText(user.getUsername());

        //加载头像
        BmobQuery<User> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("objectId",user.getObjectId());
        bmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> object, BmobException e) {
                if (e == null) {
                    User B=new User();
                    B=object.get(0);
                    Glide.with(getActivity())
                            .load(B.getAvatar().getFileUrl())
                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                            .into(avatar);
                    //Snackbar.make(view, "查询成功", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(view, "查询失败：" + e.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });


        //点击头像可以更换头像
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoUtils.use_photo(getActivity(), USE_PHOTO);
            }
        });

        //退出登录
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.logOut();
                Toast.makeText(getActivity(), "退出成功:" , Toast.LENGTH_SHORT).show();
                Intent intent=new Intent();
                intent.setClass(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        RequestOptions options = new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE);
        if (resultCode == Activity.RESULT_OK) {
            if (data == null || options == null) {
                Log.w(TAG, "user photo data is null");
                return;
            }
        Log.e("data", data.getData().getPath());
        Uri image_uri = data.getData();
        image_path = RealPathFromUriUtils.getRealPathFromUri(getActivity(), data.getData());

        //上传头像到数据库，并显示
        upload();
        Glide.with(getActivity())
                .load(image_uri)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .override(360,200)
                .into(avatar);

            }
        }

    //上传头像到数据库
    private void upload() {

        BmobFile avatar;
        String picPath = image_path;
        avatar = new BmobFile(new File(picPath));
        avatar.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(getActivity(), "上传文件成功:" + avatar.getFileUrl(), Toast.LENGTH_SHORT).show();

                    user.setAvatar(avatar);
                    user.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(getActivity(), "修改成功:", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("BMOB", e.toString());
                                Toast.makeText(getActivity(), "修改失败:", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    Toast.makeText(getActivity(), "上传文件失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}