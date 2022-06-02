package com.example.imagesharing.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.imagesharing.R;
import com.example.imagesharing.adapter.ImageAdapter;
import com.example.imagesharing.entity.Image;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String title;
    private String mParam2;
    Image image=new Image();

    public HomeFragment() {
        // Required empty public constructor
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment HomeFragment.
//     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String title) {
        HomeFragment fragment = new HomeFragment();
        fragment.title = title;
        return fragment;
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        //获取所有的Image信息
        BmobQuery<Image> bq=new BmobQuery<>();
        bq.include("author");
        bq.findObjects(new FindListener<Image>() {
            @Override
            public void done(List<Image> list, BmobException e) {
                if(e==null){
                    ImageAdapter imageAdapter=new ImageAdapter(getActivity(),list);
                    recyclerView.setAdapter(imageAdapter);

                }else{

                }
            }
        });

//        for(int i=0;i<8;i++){
//            Image ve=new Image();
//            ve.setTitle("jjjjjjj");
//            //ve.setAuthor(user);
//            ve.setAuthor(BmobUser.getCurrentUser(BmobUser.class));
//            datas.add(ve);
//        }
//        ImageAdapter imageAdapter=new ImageAdapter(getActivity(),datas);
//        recyclerView.setAdapter(imageAdapter);
        return v;
    }
}