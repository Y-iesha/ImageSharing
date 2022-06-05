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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.imagesharing.R;
import com.example.imagesharing.entity.Image;
import com.example.imagesharing.util.PhotoUtils;
import com.example.imagesharing.util.RealPathFromUriUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CollectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CollectFragment extends Fragment {
    private static final String TAG = CollectFragment.class.getName();
    private static final int USE_PHOTO = 1001;
    private static final int START_CAMERA = 1002;
    private String camera_image_path;
    private String image_path;
    private ImageView show_image;
    private TextView title;
    private Button btn_camera;
    private Button btn_album;
    private Button upload_img;
    View view;


    public CollectFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static CollectFragment newInstance() {
        CollectFragment fragment = new CollectFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_collect, container, false);
        title = (TextView) view.findViewById(R.id.EditTitle);
        btn_camera = (Button) view.findViewById(R.id.photograph);
        btn_album = (Button) view.findViewById(R.id.album);
        show_image = (ImageView) view.findViewById(R.id.img_cover);
        upload_img = (Button) view.findViewById(R.id.upload);

        btn_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoUtils.use_photo(getActivity(), USE_PHOTO);
            }
        });
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera_image_path = PhotoUtils.start_camera(getActivity(), START_CAMERA);
            }
        });
        upload_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (image_path == null) {
                    Toast.makeText(getActivity(), "请选择图片", Toast.LENGTH_SHORT).show();
                } else {
                    upload();
                }
            }
        });

        return view;
    }

    private void upload() {
        BmobUser user = new BmobUser();
        Image image = new Image();
        BmobFile pic;

        image.setTitle(title.getText().toString().trim());
        BmobPointer pointer = new BmobPointer(user);
        image.setAuthor(BmobUser.getCurrentUser(BmobUser.class));

        String picPath = image_path;
        pic = new BmobFile(new File(picPath));
        image.setPic(pic);
        pic.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(getActivity(), "上传文件成功:" + pic.getFileUrl(), Toast.LENGTH_SHORT).show();
                    image.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                Toast.makeText(getActivity(), "插入记录成功:" + s, Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getActivity(), "插入记录失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                //text.setText(e.getMessage());
                            }
                        }
                    });

                } else {
                    Toast.makeText(getActivity(), "上传文件失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        RequestOptions options = new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case USE_PHOTO:
                    if (data == null || options == null) {
                        Log.w(TAG, "user photo data is null");
                        return;
                    }
                    Log.e("data", data.getData().getPath());
                    Uri image_uri = data.getData();
                    image_path = RealPathFromUriUtils.getRealPathFromUri(getActivity(), data.getData());
                    //image_path=image_uri.getPath()+".jpg";
                    Glide.with(getActivity())
                            .load(image_uri)
                            //.apply(options)
                            .override(360, 200)
                            .into(show_image);
                    // get image path from uri
                    //image_path = PhotoUtil.get_path_from_URI(MainActivity.this, image_uri);
                    // predict image
                    //predict_image(image_path);
                    break;
                case START_CAMERA:
                    // show photo
                    Log.e("path", camera_image_path);
                    Log.e("options", options + "[]");
                    image_path = camera_image_path;
                    Glide.with(getActivity())
                            .load(new File(camera_image_path))
                            .override(360, 200)
                            //.apply(options)
                            .into(show_image);
                    // predict image
                    //predict_image(camera_image_path);
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {

                        int grantResult = grantResults[i];
                        if (grantResult == PackageManager.PERMISSION_DENIED) {
                            String s = permissions[i];
                            Toast.makeText(getActivity(), s + " permission was denied", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
        }


    }
}