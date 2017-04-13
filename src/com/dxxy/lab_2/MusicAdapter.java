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
	List<HashMap<String,String>>list;//������Ϣ�ļ�����
	Context context;//����������
	int layout;//Layout�����ļ�
	LayoutInflater inflater;//��̬���Layout�����ļ�
	
	//���캯��
	public MusicAdapter(List list,Context context,int layout){
		this.list=list;
		this.context=context;
		this.layout=layout;
		//��̬���ص�mInflater�ɵ�����Activity��ȡ
		inflater=inflater.from(context);
	}
	
	//��ȡ������Ϣ������
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	
	//��ȡָ��λ�ø�����Ϣ
	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	//��ȡ������Ϣָ��λ��
	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	//Adapter�����þ���ListView����������֮������������б����ÿһ����ʾ��ҳ��ʱ���������Adapter��getView��������һ��View.
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
