package com.example.imagesharing.adapter;

import android.content.Context;
import android.net.Uri;
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
import com.example.imagesharing.entity.ImageEntity;
import com.example.imagesharing.fragment.HomeFragment;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Image> datas;

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
        private ImageView tvAvatar;
        //private TextView tvDz;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.title);
            tvAuthor = itemView.findViewById(R.id.author);
            tvImage=itemView.findViewById(R.id.img_cover);
            tvAvatar=itemView.findViewById(R.id.img_avatar);
          //  tvDz= itemView.findViewById(R.id.dzcount);

        }
    }
}
