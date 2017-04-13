package com.dxxy.lab_2;

import java.util.HashMap;
import java.util.List;

import com.example.musicplayer.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MusicAdapter extends BaseAdapter {
	List<HashMap<String,String>>list;//定义信息的集合体
	Context context;//定义上下文
	int layout;//Layout布局文件
	LayoutInflater inflater;//动态添加Layout布局文件
	
	//构造函数
	public MusicAdapter(List list,Context context,int layout){
		this.list=list;
		this.context=context;
		this.layout=layout;
		//动态加载的mInflater由调用者Activity获取
		inflater=inflater.from(context);
	}
	
	//获取歌曲信息的数量
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	
	//获取指定位置歌曲信息
	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	//获取歌曲信息指定位置
	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	//Adapter的作用就是ListView界面与数据之间的桥梁，当列表里的每一项显示到页面时，都会调用Adapter的getView方法返回一个View.
	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		
		ViewCatch viewCatch=new ViewCatch();
		if(view==null){
			view=inflater.inflate(layout, null);
			viewCatch.tv1=(TextView)view.findViewById(R.id.songname);
			view.setTag(viewCatch);
		}else{
			viewCatch=(ViewCatch)view.getTag();
		}
		HashMap<String,String>hashMap=list.get(position);
		viewCatch.tv1.setText(hashMap.get("music"));
		return view;
	}
	
	class ViewCatch{
		public TextView tv1;
	}

}
