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
import com.example.imagesharing.entity.Avatar;
import com.example.imagesharing.entity.Image;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
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
    static BmobUser user =BmobUser.getCurrentUser(BmobUser.class);




    public void checkdz(ViewHolder vh, Image imageEntity){
        BmobQuery<BmobUser> query=new BmobQuery<BmobUser>();
        query.addWhereRelatedTo("likes",new BmobPointer(imageEntity));
        query.findObjects(new FindListener<BmobUser>() {

            public void done(List<BmobUser> list, BmobException e) {
                if(e==null){
                    dzcount=list.size();
                    if(dzcount==0){
                        BmobRelation relation = new BmobRelation();
                        relation.add(user);
                        imageEntity.setLikes(relation);
                        imageEntity.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    vh.DzImage.setImageResource(R.mipmap.dianzan_select);
                                    vh.tvDz.setText(String.valueOf(++dzcount));
                                    Log.i("bmob","多对多关联添加成功");
                                }else{
                                    Log.i("bmob","失败："+e.getMessage());
                                }
                            }

                        });
                    }
                    for(int i=0;i<dzcount;i++){
                        BmobUser B=new BmobUser();
                        B=list.get(i);
                        if(B.getObjectId().toString().equals(user.getObjectId().toString())){
                            BmobRelation relation = new BmobRelation();
                            relation.remove(user);
                            imageEntity.setLikes(relation);
                            imageEntity.update(new UpdateListener() {

                                @Override
                                public void done(BmobException e) {
                                    if(e==null){
                                        vh.DzImage.setImageResource(R.mipmap.dianzan);
                                        vh.tvDz.setText(String.valueOf(--dzcount));
                                        Log.i("bmob","关联关系删除成功");
                                    }else{
                                        Log.i("bmob","失败："+e.getMessage());
                                    }
                                }

                            });
                            break;
                        }
                        if(i==dzcount-1) {
                            BmobRelation relation = new BmobRelation();
                            relation.add(user);
                            imageEntity.setLikes(relation);
                            imageEntity.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e==null){
                                        vh.DzImage.setImageResource(R.mipmap.dianzan_select);
                                        vh.tvDz.setText(String.valueOf(++dzcount));
                                        Log.i("bmob","多对多关联添加成功");
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
        vh.tvTitle.setText(imageEntity.getTitle());
        vh.tvAuthor.setText(imageEntity.getAuthor().getUsername());
        vh.tvDl.setText(String.valueOf(imageEntity.getDlCount()));

        BmobQuery<BmobUser> query=new BmobQuery<BmobUser>();
        query.addWhereRelatedTo("likes",new BmobPointer(imageEntity));
        query.findObjects(new FindListener<BmobUser>() {

            public void done(List<BmobUser> list, BmobException e) {
                if(e==null){
                    dzcount=list.size();
                    vh.tvDz.setText(String.valueOf(dzcount));
                    for(int i=0;i<dzcount;i++){
                        BmobUser B=new BmobUser();
                        B=list.get(i);

                        if(B.getObjectId().toString().equals(user.getObjectId().toString())){
                            vh.DzImage.setImageResource(R.mipmap.dianzan_select);

                        }else{

                        }
                    }
                }else{
                    Log.i("bmob","失败："+e.getMessage());
                }
            }
        });

        vh.DzImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkdz(vh, imageEntity);

            }
        });

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
                            Toast.makeText(mContext, "下载成功,保存路径:"+savePath, Toast.LENGTH_SHORT).show();
                            vh.tvDl.setText(String.valueOf(imageEntity.getDlCount()));
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
        //vh.tvDz.setText(String.valueOf(imageEntity.getLikes()));

        //加载头像
        BmobQuery<Avatar> fa=new BmobQuery<>();
        fa.addWhereEqualTo("author",imageEntity.getAuthor());

        fa.findObjects(new FindListener<Avatar>() {
            @Override
            public void done(List<Avatar> listt, BmobException e) {
                if(listt!=null){
                    //Avatar av= listt.get(0);
                    //Toast.makeText(mContext, "av:"+listt.size(), Toast.LENGTH_SHORT).show();

                    Glide.with(mContext)
                            .load(listt.get(0).getAvatar().getFileUrl())
                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                            .into(vh.tvAvatar);
                    //Toast.makeText(mContext, "listt", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(mContext, "异常"+e.getMessage(), Toast.LENGTH_SHORT).show();
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
