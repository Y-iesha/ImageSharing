package com.example.imagesharing.adapter;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.imagesharing.R;
import com.example.imagesharing.entity.Image;
import com.example.imagesharing.entity.User;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Image> datas;
    private static int dzcount;
    private static User user =User.getCurrentUser(User.class);



    public ImageAdapter(Context context, List<Image> datas) {
        this.mContext = context;
        this.datas = datas;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_image, parent, false);
        ViewHolder viewholder = new ViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder vh= (ViewHolder) holder;
        Image imageEntity= datas.get(position);
        //一个ViewHolder vh显示一个Image的信息
        vh.tvTitle.setText(imageEntity.getTitle());
        vh.tvAuthor.setText(imageEntity.getAuthor().getUsername());
        vh.tvDl.setText(String.valueOf(imageEntity.getDlCount()));

        //显示点赞相关信息
        //利用BmobObject提供的查询方法查询Image表
        BmobQuery<User> query=new BmobQuery<User>();
        query.addWhereRelatedTo("likes",new BmobPointer(imageEntity));
        query.findObjects(new FindListener<User>() {

            public void done(List<User> list, BmobException e) {
                if(e==null){
                    dzcount=list.size();                            //喜欢该图片的用户的数量
                    vh.tvDz.setText(String.valueOf(dzcount));       //显示喜欢该图片的用户的数量
                    for(int i=0;i<dzcount;i++){                     //判断该用户是否已经喜欢过该图片
                        User B=new User();
                        B=list.get(i);
                        if(B.getObjectId().toString().equals(user.getObjectId().toString())){  //如果喜欢过图标变为红色
                            vh.DzImage.setImageResource(R.mipmap.dianzan_select);

                        }else{

                        }
                    }
                }else{
                    Log.i("bmob","错误："+e.getMessage());
                }
            }
        });

        //点赞图标监听
        vh.DzImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkdz(vh, imageEntity);            //调用类内方法

            }
        });

        //下载图标监听
        vh.DlImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File saveFile = new File(Environment.getExternalStorageDirectory(),imageEntity.getPic().getFilename());
                imageEntity.getPic().download(saveFile, new DownloadFileListener() {

                    @Override
                    public void onStart() {
                        Toast.makeText(mContext, "开始下载...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void done(String savePath,BmobException e) {
                        if(e==null){
                            int dlcount=imageEntity.getDlCount()+1;
                            imageEntity.setDlCount(dlcount);
                            imageEntity.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    Toast.makeText(mContext, "下载成功,保存路径:"+savePath, Toast.LENGTH_SHORT).show();
                                    vh.tvDl.setText(String.valueOf(imageEntity.getDlCount()));
                                }
                            });

                        }else{
                            Toast.makeText(mContext, "下载失败："+e.getErrorCode()+","+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onProgress(Integer value, long newworkSpeed) {
                        Log.i("bmob","下载进度："+value+","+newworkSpeed);
                    }

                });
            }
        });


        //加载图片
        Glide.with(mContext)
                .load(imageEntity.getPic().getFileUrl())
                .override(360,200)
                //.centerCrop()
                .into(vh.tvImage);

        //加载头像
        Glide.with(mContext)
                .load(imageEntity.getAuthor().getAvatar().getFileUrl())
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(vh.tvAvatar);

//        BmobQuery<Avatar> fa=new BmobQuery<>();
//        fa.addWhereEqualTo("author",imageEntity.getAuthor());
//
//        fa.findObjects(new FindListener<Avatar>() {
//            @Override
//            public void done(List<Avatar> listt, BmobException e) {
//                if(listt!=null){
//                    //Avatar av= listt.get(0);
//                    //Toast.makeText(mContext, "av:"+listt.size(), Toast.LENGTH_SHORT).show();
//
//                    Glide.with(mContext)
//                            .load(listt.get(0).getAvatar().getFileUrl())
//                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
//                            .into(vh.tvAvatar);
//                    //Toast.makeText(mContext, "listt", Toast.LENGTH_SHORT).show();
//                }else{
//                    Toast.makeText(mContext, "异常"+e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });



    }

    //用户点击点赞图标
    public void checkdz(ViewHolder vh, Image imageEntity){
        BmobQuery<User> query=new BmobQuery<User>();
        query.addWhereRelatedTo("likes",new BmobPointer(imageEntity));
        query.findObjects(new FindListener<User>() {

            public void done(List<User> list, BmobException e) {
                if(e==null){
                    dzcount=list.size();         //获取点赞数
                    if(dzcount==0){              //点赞数为零时，直接将该用户插入Image表中该表项的likes中，并更新显示的信息
                        BmobRelation relation = new BmobRelation();
                        relation.add(user);
                        imageEntity.setLikes(relation);
                        imageEntity.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    vh.DzImage.setImageResource(R.mipmap.dianzan_select);
                                    vh.tvDz.setText(String.valueOf(++dzcount));
                                    //Log.i("bmob","多对多关联添加成功");
                                }else{
                                    Log.i("bmob","错误："+e.getMessage());
                                }
                            }

                        });
                    }
                    for(int i=0;i<dzcount;i++){    //点赞数不为零时，检查是点赞操作还是取消点赞
                        User B=new User();
                        B=list.get(i);
                        if(B.getObjectId().toString().equals(user.getObjectId().toString())){   //取消点赞
                            BmobRelation relation = new BmobRelation();
                            relation.remove(user);
                            imageEntity.setLikes(relation);
                            imageEntity.update(new UpdateListener() {

                                @Override
                                public void done(BmobException e) {
                                    if(e==null){
                                        vh.DzImage.setImageResource(R.mipmap.dianzan);
                                        vh.tvDz.setText(String.valueOf(--dzcount));
                                        //Log.i("bmob","关联关系删除成功");
                                    }else{
                                        Log.i("bmob","错误："+e.getMessage());
                                    }
                                }

                            });
                            break;
                        }
                        if(i==dzcount-1) {                                                     //点赞
                            BmobRelation relation = new BmobRelation();
                            relation.add(user);
                            imageEntity.setLikes(relation);
                            imageEntity.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e==null){
                                        vh.DzImage.setImageResource(R.mipmap.dianzan_select);
                                        vh.tvDz.setText(String.valueOf(++dzcount));
                                        //Log.i("bmob","多对多关联添加成功");
                                    }else{
                                        Log.i("bmob","失败："+e.getMessage());
                                    }
                                }

                            });
                        }

                    }
                }else{
                    Log.i("bmob","失败："+e.getMessage());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvAuthor;
        private ImageView tvImage;
        private ImageView DzImage;
        private ImageView DlImage;
        private ImageView tvAvatar;
        private TextView tvDz;
        private TextView tvDl;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.title);
            tvAuthor = itemView.findViewById(R.id.author);
            tvImage=itemView.findViewById(R.id.img_cover);
            tvAvatar=itemView.findViewById(R.id.img_avatar);
            tvImage=itemView.findViewById(R.id.img_cover);
            tvDz= itemView.findViewById(R.id.dzcount);
            DzImage= itemView.findViewById(R.id.img_dianzan);
            tvDl= itemView.findViewById(R.id.download);
            DlImage= itemView.findViewById(R.id.img_download);






        }
    }


}
