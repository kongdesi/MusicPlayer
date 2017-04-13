package com.dxxy.lab_2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.musicplayer.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends Activity {
	// 1��������
	public static List<HashMap<String, String>> list;// ������Ϣ���ϱ���
	ListView lv;
	ImageButton play, next, last;// ���ư�ť
	Button onlythis, cycle, texton, textoff;// ����ѭ����������Ű�ť
	SeekBar seekBar;// ������
	TextView text, songname;// ���ֲ���ʱ�����ʱ��
	MediaPlayer mediaPlayer = null;// ���ֲ��ſ�����
	boolean playSwitch = false;// �ж��Ƿ��ڲ���
	boolean onlythisSwitch = false;// �ж��Ƿ��ڵ���ѭ��ģʽ
	boolean cycleSwitch = false;// �ж��Ƿ���ȫ��ѭ��ģʽ
	int currentPosition; // ���ֵ�ǰλ��
	MusicBroadcastReciver musicBroadcastReciver;// �������㲥����

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// 1 ������ʼ��
		initview();

		// 2 ��ȡ��Ƶ�ļ���Ϣ�����б�����ʾ
		if (MainActivity.this != null) {
			// ��ý����ж�ȡ��Ƶ��Ϣ�������α�
			Cursor cursor = MainActivity.this.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
					null, null, null, null);
			if (cursor != null) {
				// ��ѯ�α겢����Ƶ��Ϣ���ص����ϱ���list��
				while (cursor.moveToNext()) {
					HashMap<String, String> hashMap = new HashMap<String, String>();
					hashMap.put("music",
							cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
					hashMap.put("musicpath",
							cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
					list.add(hashMap);
				}
			}
		}
		// ��ListView��Adapter��
		MusicAdapter musicAdapter = new MusicAdapter(list, MainActivity.this, R.layout.item);
		lv = (ListView) findViewById(R.id.lv);
		lv.setAdapter(musicAdapter);

		// ���ϲ����ǰ�ɨ�赽���ֻ��ϵ������Ŵ洢��HashMap�У����ұ���б���Ϣ��list����ʱ��list��������ݾ������ֻ������е����ص������б���Ϣ

		// 3 ��������������
		startService(new Intent(MainActivity.this, MusicServer.class));
		// ����ListView�е�������Ͳ�����Ϣ���㲥������

		// ��ĳһ��Item�����ֱ������ʱ����Ҫ��Ӧһ�������¼�
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				// �����ﲹ����һ���߼������ǵ������ĳһ����Ҫ�������ֵ�ʱ����Ȼ���ʱ����ʵ���Ѿ���ʼ�˲��ţ���ô��ͼ��ͻ�
				// �Ӻ����Ǳ����˫����
				// songname.setTextColor(ContextCompat.getColor(getApplicationContext(),
				// R.color.myyelow));
				play.setBackgroundResource(R.drawable.pause);
				String str_musicpathString = list.get(arg2).get("musicpath");
				Intent intent = new Intent();// new��һ��Intent
				// ��������һ�������š�����Ϊ��receiver_music���ܹ�����������ŵ�ĳ���߼�����Ϳ��Եõ�musicPath��
				// ���ֵ�·����cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
				intent.setAction("receiver_music");
				intent.putExtra("musicPath", str_musicpathString);
				sendBroadcast(intent);// ���͹㲥������
				playSwitch = true;// ����Ѳ����ͱ���:palySwitch��Ϊtrue����������ʼ�˲���
				currentPosition = arg2;// ��ǰ��λ��Ϊagr2�������Ȳ��˽⣬���Ҽ�ס��
			}
		});
		// 4 ���ư�ť��ӵ�����Ӧ����
		// ���Ű�ť��ӵ�����Ӧ����
		// ����д���ǲ��Ű�ť��Ҫ���е�һϵ�еĵ����Ӧ�¼�
		play.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (playSwitch) {// ������ڲ����������ڲ������ֵ�״̬
					// ���Ȱ�play�Ǹ�ImageButton����Ӧ�ı���ͼƬ���������߱�Ϊ������
					play.setBackgroundResource(R.drawable.play);
					// Ȼ��new��һ��Intent
					Intent intent = new Intent();
					// �����˵ڶ������ţ�pause_music
					intent.setAction("pause_music");
					// ���͹㲥������
					sendBroadcast(intent);
					// ���ʱ�򣬲�������״̬��Ϊ�ɲ���״̬�������ͣ״̬�����ԣ���Ȼ�Ǹ������ͱ�����Ҫ���faluse�����ǲ�����û�����ڲ���
					playSwitch = false;
				} else {
					// �������������û�����ڲ��ţ���ôִ������һ������¼�
					// ���ȰѲ������ı���ͼƬ�ɺ������α��˫����
					play.setBackgroundResource(R.drawable.pause);
					// newһ��Intent
					Intent intent = new Intent();
					// ���õ��������ţ�continue_music
					intent.setAction("continue_music");
					// ���͹㲥������
					sendBroadcast(intent);
					// ��ʱ���������ͻ������ڲ���״̬
					playSwitch = true;
				}
			}
		});
		// ��һ�װ�ť��ӵ�����Ӧ����
		// ������Ҫд���û������һ���ĵ���������
		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// ��һ�����������ʵ�ǿ���дһ���жϵģ���Ϊ�Ͼ���һ����һ���ܻ��е�ͷ����һ�̣�����Ļ���ʵ���б�Ҫ��һ���жϵ�
				// �����ǰ��λ�ü���ǰ���ŵĸ�����Ҳ������һ�׵���һ��С��ɨ�赽�ı��ص����е������б����ֵ�Ĵ�С����ô��ʱ�����ǿ�����Ϊ
				// ���ʱ���ǿ��Ե����һ�װ�ť��
				if (currentPosition + 1 < list.size()) {
					// ����Ļ����Ͱѵ�ǰ���ֵ���һ�׵����ֵĲ���·����str_musicpathString����
					String str_musicpathString = list.get(currentPosition + 1).get("musicpath");
					// new��һ��Intent
					Intent intent = new Intent();
					// ���õ��ĸ����ţ�receiver_music
					intent.setAction("receiver_music");
					// ��putExtra("A",B)�У�ABΪ��ֵ�ԣ���һ������Ϊ�������ڶ�������Ϊ����Ӧ��ֵ��
					// ˳����һ�£������ȡ��Intent�����е���Щֵ����Ҫ�������һ��Activity����getXXXXXExtra������
					// ע����Ҫʹ�ö�Ӧ���͵ķ���������Ϊ������
					// ��tr_musicpathString��ֵ������musicPath��ע�⣬�������musicPath�������������musicpath
					// ������Ҫ����Ϊstr_musicpathString��ֵ��ȷ������Ϊ��ȷ���û�����ʱ�����һ�׸���Ϊ���ǵĲ�������
					intent.putExtra("musicPath", str_musicpathString);
					// ���͹㲥������
					sendBroadcast(intent);
					// ��ʱ���Ѳ�������״̬����Ϊ����״̬
					playSwitch = true;
					// ��ʱ�����ֲ��ŵ�λ�ûᷢ���仯����Ϊ����һ�ף���ô�ͻ�ִ������Լ����㣺currentPosition=currentPosition+1
					// �����������ֻ�����ÿһ�ε����һ�ף���һ�ף�һֱ��ȥ��Ȼ����Ӧ��λ��Ҳ����֮�仯���൱���Ͻ���
					currentPosition += 1;
				} else {
					// �����ﲹ����һ���߼������ǵ��˵�֮��ͼ����Ҫ����һ���仯�İɡ�
					next.setBackgroundResource(R.drawable.nextend);
					// �����ʱ�ĸ�������һ����λ�ñ������������б�Ĵ�С����Ҳ����˵���˵ף�û�а취������һ��
					Toast.makeText(MainActivity.this, "�Ѿ������һ����", Toast.LENGTH_SHORT).show();
				}
			}
		});
		// �������޸���һ������Ҹо���������һ�ױ��ú�һ��Ҫ���Ӻ����һЩ
		// ��һ�װ�ť��ӵ�����Ӧ����
		// �������˵�����һ�׵ĵ������ô�������Ϻ͵����һ�׵�������ȫһ���ģ����ԣ�������Ҳ�Ͳ���˵�ˡ�
		last.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (currentPosition - 1 > 0) {
					String str_musicpathString = list.get(currentPosition - 1).get("musicpath");
					Intent intent = new Intent();
					intent.setAction("receiver_music");
					intent.putExtra("musicPath", str_musicpathString);
					sendBroadcast(intent);
					playSwitch = true;
					currentPosition -= 1;
				} else {
					Toast.makeText(MainActivity.this, "�Ѿ��ǵ�һ����", Toast.LENGTH_SHORT).show();
				}

			}
		});

		// ����ѭ����ť�����Ӧ����
		// �����������ӵ������ѭ����ť֮��ĵ���¼������������ǰѰ�ť����ɫ����һ���仯���ɺ�ɫ��Ϊ�˻�ɫ
		// Ȼ�����Լ���Ϊ���߼������ǵ�������֮����ô����Ļ��������ֵ�λ�ã���һ�����ֵ�λ�õ��ڵ�ǰ��λ�ã���ôҲ���൱��
		// ������һ����ѭ��һ������һ���軹���Լ�����ô��Ȼ���ǵ���ѭ��,����������Ϊ��û����ȫ������룬�����кܶ�Ĳ�������֪��
		// �Ǹ������ã���ô�������ȸ����������һ�������ѭ�����߼���д

		onlythis.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onlythis.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.myred));
				cycle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.mywhite));
				onlythisSwitch = true;
				/*
				 * if(onlythisSwitch){ //������õ���һ�����ֵ�λ�� String
				 * str_musicpathString = list.get(currentPosition +
				 * 1).get("musicpath");
				 * 
				 * }*/
				 
			}
		});

		cycle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				cycle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.myred));
				onlythis.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.mywhite));
				cycleSwitch = true;
			}
		});

		// �˲���Ϊ�Լ��Զ�����ʾ����ѧ�ŵĲ���
		texton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.mywhite));
			}
		});
		// �˲���Ϊ�Լ��Զ�����������ѧ�ŵĲ���
		textoff.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.myred2));
			}
		});
		// ������������Ӧ����
		// ���������������ֵĴ��룬�ðɣ�էһ������д���ǵ��û�����˽�����ʱ��һ������¼�
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			// ������ֹͣ�϶�
			// ���￴����Ӧ����ϵͳ�Դ�����������ĵ�������һ�����ǵ�ֹͣ�϶���������ʱ����Ǹ��¼�
			// Ҳ�����û��϶����˽�����
			// ��������д��ʲô
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				// ������һ��progress�����ȡ���û��϶�����λ������Ӧ���Ǹ��������Ľ���
				int progress = seekBar.getProgress();
				// ����new��һ��Intent
				Intent intent = new Intent();
				// ���Ƿ���һ���㲥������Ϊseekbar
				intent.setAction("seekbar");
				// ��������ѻ�ȡ�����û��϶��Ľ�������Ӧ�Ľ������ݴ��һ����ΪProgress�����ݰ�����Service���˹�ȥ
				intent.putExtra("Progress", progress);
				// ���͹㲥������
				sendBroadcast(intent);
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub

			}
		});
		// 5 ���������ȹ㲥ע��
		// �����Ƕ�̬�������һ���������Ĺ㲥ע��
		// ������Ϊ�ڴ������󲿷ֶ�����һ����ΪMusicBroadcastReciver�ĺ���

		/*
		 * IntentFilter intentFilter = new IntentFilter();
		musicBroadcastReciver = new MusicBroadcastReciver();
		intentFilter.addAction("currentProgress");
		registerReceiver(musicBroadcastReciver, intentFilter);
		 */
		IntentFilter intentFilter = new IntentFilter();
		musicBroadcastReciver = new MusicBroadcastReciver();
		// ע������֮����Ƿ�����һ���㲥���ţ�currentProgress
		intentFilter.addAction("currentProgress");
		registerReceiver(musicBroadcastReciver, intentFilter);
	}

	// 1 ������ʼ��
	// ����ֻ�ǳ�ʼ����һЩ����
	void initview() {
		list = new ArrayList<HashMap<String, String>>();
		play = (ImageButton) findViewById(R.id.btn_play);
		last = (ImageButton) findViewById(R.id.btn_last);
		next = (ImageButton) findViewById(R.id.btn_next);
		seekBar = (SeekBar) findViewById(R.id.seekBar);
		onlythis = (Button) findViewById(R.id.btn_onlythis);
		cycle = (Button) findViewById(R.id.btn_cycle);
		text = (TextView) findViewById(R.id.text);
		songname = (TextView) findViewById(R.id.songname);
		texton = (Button) findViewById(R.id.btn_texton);
		textoff = (Button) findViewById(R.id.btn_textoff);

	}

	// 5 ���ý�������ʾ����
	// �����ʾ�Ľ���������ͣ�������Ǹ����ȣ���Ҫ���û������ܻ��϶���������������϶�����ô���Ǹ������ŵ����Ǹ�λ�ã�
	void setSeekbarProgerss(int int_musicProgress) {
		seekBar.setProgress(int_musicProgress);
	}

	// 5 ���������ȹ㲥
	// �ã��������ȶ������������һ�³����Ե����
	class MusicBroadcastReciver extends BroadcastReceiver {
		@Override
		// ������ΪMusicBroadcastReciver��һ���̳���BroadcastReceiver��һ��������
		// ��ô����Ҫ��дBroadcastReceiver�Ĺ��캯��
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			// ����������Ҫ�ǽ�����Service����ȥ��һ����Ϊ��currentProgress�������ݰ������һ�ȡ�����е�����
			// Ҳ���ǵ�ǰ���ֲ��ŵİٷֱȣ�������ʾ����������
			int int_musicProgress = intent.getIntExtra("currentProgress", 0);
			setSeekbarProgerss(int_musicProgress);
		}

	}

}
