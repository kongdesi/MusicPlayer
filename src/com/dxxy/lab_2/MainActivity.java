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
	// 1变量定义
	public static List<HashMap<String, String>> list;// 歌曲信息集合变量
	ListView lv;
	ImageButton play, next, last;// 控制按钮
	Button onlythis, cycle, texton, textoff;// 单曲循环、随机播放按钮
	SeekBar seekBar;// 进度条
	TextView text, songname;// 音乐播放时间和总时间
	MediaPlayer mediaPlayer = null;// 音乐播放控制器
	boolean playSwitch = false;// 判断是否处于播放
	boolean onlythisSwitch = false;// 判断是否处于单曲循环模式
	boolean cycleSwitch = false;// 判断是否处于全部循环模式
	int currentPosition; // 音乐当前位置
	MusicBroadcastReciver musicBroadcastReciver;// 进度条广播变量

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
		// 1 变量初始化
		initview();

		// 2 读取音频文件信息并在列表中显示
		if (MainActivity.this != null) {
			// 从媒体库中读取音频信息并存入游标
			Cursor cursor = MainActivity.this.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
					null, null, null, null);
			if (cursor != null) {
				// 轮询游标并把音频信息加载到集合变量list中
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
		// 将ListView与Adapter绑定
		MusicAdapter musicAdapter = new MusicAdapter(list, MainActivity.this, R.layout.item);
		lv = (ListView) findViewById(R.id.lv);
		lv.setAdapter(musicAdapter);

		// 以上部分是把扫描到的手机上的音乐信存储到HashMap中，并且变成列表信息给list，这时，list里面的内容就是受手机上所有的下载的音乐列表信息

		// 3 开启播放器服务
		startService(new Intent(MainActivity.this, MusicServer.class));
		// 单击ListView中的条项后发送播放消息给广播接收器

		// 当某一个Item的音乐被点击的时候，需要响应一个触发事件
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				// 我这里补充了一个逻辑，就是当点击了某一首需要播放音乐的时候，自然这个时候其实就已经开始了播放，那么，图标就会
				// 从横三角变成了双竖线
				// songname.setTextColor(ContextCompat.getColor(getApplicationContext(),
				// R.color.myyelow));
				play.setBackgroundResource(R.drawable.pause);
				String str_musicpathString = list.get(arg2).get("musicpath");
				Intent intent = new Intent();// new了一个Intent
				// 这里设置一个“暗号”，名为：receiver_music，能够对上这个暗号的某个逻辑代码就可以得到musicPath，
				// 音乐的路径：cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
				intent.setAction("receiver_music");
				intent.putExtra("musicPath", str_musicpathString);
				sendBroadcast(intent);// 发送广播！！！
				playSwitch = true;// 这里把布尔型变量:palySwitch变为true，播放器开始了播放
				currentPosition = arg2;// 当前的位置为agr2（可以先不了解，暂且记住）
			}
		});
		// 4 控制按钮添加单击响应函数
		// 播放按钮添加单击响应函数
		// 这里写的是播放按钮所要进行的一系列的点击响应事件
		play.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (playSwitch) {// 如果现在播放器是正在播放音乐的状态
					// 就先把play那个ImageButton所对应的背景图片由两条竖线变为横三角
					play.setBackgroundResource(R.drawable.play);
					// 然后new了一个Intent
					Intent intent = new Intent();
					// 设置了第二个暗号：pause_music
					intent.setAction("pause_music");
					// 发送广播！！！
					sendBroadcast(intent);
					// 这个时候，播放器的状态因为由播放状态变成了暂停状态，所以，自然那个布尔型变量就要变成faluse，就是播放器没有正在播放
					playSwitch = false;
				} else {
					// 如果播放器现在没有正在播放，那么执行另外一个点击事件
					// 首先把播放器的背景图片由横三角形变成双竖线
					play.setBackgroundResource(R.drawable.pause);
					// new一个Intent
					Intent intent = new Intent();
					// 设置第三个暗号：continue_music
					intent.setAction("continue_music");
					// 发送广播！！！
					sendBroadcast(intent);
					// 这时，播放器就会变成正在播放状态
					playSwitch = true;
				}
			}
		});
		// 下一首按钮添加单击响应函数
		// 这里是要写当用户点击下一曲的点击世间监听
		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 下一首这个按键其实是可以写一个判断的，因为毕竟下一首下一首总会有到头的那一刻，这里的话其实是有必要加一个判断的
				// 如果当前的位置即当前播放的歌曲，也就是这一首的下一首小于扫描到的本地的所有的音乐列表的数值的大小，那么这时我们是可以认为
				// 这个时候是可以点击下一首按钮的
				if (currentPosition + 1 < list.size()) {
					// 这里的话，就把当前音乐的下一首的音乐的播放路径给str_musicpathString赋上
					String str_musicpathString = list.get(currentPosition + 1).get("musicpath");
					// new了一个Intent
					Intent intent = new Intent();
					// 设置第四个暗号：receiver_music
					intent.setAction("receiver_music");
					// 【putExtra("A",B)中，AB为键值对，第一个参数为键名，第二个参数为键对应的值。
					// 顺便提一下，如果想取出Intent对象中的这些值，需要在你的另一个Activity中用getXXXXXExtra方法，
					// 注意需要使用对应类型的方法，参数为键名】
					// 把tr_musicpathString的值给键：musicPath，注意，这里的是musicPath，而不是上面的musicpath
					// 这里主要是因为str_musicpathString的值不确定，因为不确定用户会这时点击哪一首歌作为心仪的播放音乐
					intent.putExtra("musicPath", str_musicpathString);
					// 发送广播！！！
					sendBroadcast(intent);
					// 这时，把播放器的状态设置为播放状态
					playSwitch = true;
					// 此时的音乐播放的位置会发生变化，因为是下一首，那么就会执行这个自加运算：currentPosition=currentPosition+1
					// 就这样，音乐会随着每一次点击下一首，下一首，一直下去，然后相应的位置也会随之变化，相当的严谨！
					currentPosition += 1;
				} else {
					// 我这里补充了一个逻辑，就是到了底之后图标是要发生一个变化的吧。
					next.setBackgroundResource(R.drawable.nextend);
					// 如果这时的歌曲的下一曲的位置比整个歌曲的列表的大小还大，也就是说到了底，没有办法进行下一首
					Toast.makeText(MainActivity.this, "已经是最后一首了", Toast.LENGTH_SHORT).show();
				}
			}
		});
		// 我这里修改了一个用语，我感觉这里用上一首比用后一首要更加好理解一些
		// 上一首按钮添加单击响应函数
		// 这里的是说点击上一首的点击，那么从理论上和点击下一首的理解会完全一样的，所以，我这里也就不再说了。
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
					Toast.makeText(MainActivity.this, "已经是第一首了", Toast.LENGTH_SHORT).show();
				}

			}
		});

		// 单曲循环按钮添加响应函数
		// 我在这里会添加点击单曲循环按钮之后的点击事件，我这里先是把按钮的颜色做了一个变化，由红色变为了黄色
		// 然后我自己认为的逻辑，就是点击了这个之后，那么这里的话会由音乐的位置，下一个音乐的位置等于当前的位置，那么也就相当于
		// 进入了一个死循环一样，下一个歌还是自己，那么自然就是单曲循环,不过这里因为还没有完全看完代码，所以有很多的参数不是知道
		// 是该怎样用，那么这里我先搁置这个和下一个的随机循环的逻辑编写

		onlythis.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onlythis.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.myred));
				cycle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.mywhite));
				onlythisSwitch = true;
				/*
				 * if(onlythisSwitch){ //我这里得到下一个音乐的位置 String
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

		// 此部分为自己自定义显示姓名学号的部分
		texton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.mywhite));
			}
		});
		// 此部分为自己自定义隐藏姓名学号的部分
		textoff.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.myred2));
			}
		});
		// 进度条控制响应功能
		// 差点忘记了这个部分的代码，好吧，乍一看这是写的是当用户点击了进度条时的一个点击事件
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			// 进度条停止拖动
			// 这里看形势应该是系统自带了三个情况的点击，这第一个就是当停止拖动进度条的时候的那个事件
			// 也就是用户拖动完了进度条
			// 来看代码写了什么
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				// 这里用一个progress对象获取了用户拖动到的位置所对应的那个进度条的进度
				int progress = seekBar.getProgress();
				// 又是new了一个Intent
				Intent intent = new Intent();
				// 又是发了一个广播，暗号为seekbar
				intent.setAction("seekbar");
				// 并且这里把获取到的用户拖动的进度条对应的进度数据打成一个名为Progress的数据包，向Service丢了过去
				intent.putExtra("Progress", progress);
				// 发送广播！！！
				sendBroadcast(intent);
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub

			}
		});
		// 5 进度条进度广播注册
		// 这里是动态的添加了一个进度条的广播注册
		// 这里因为在代码的最后部分定义了一个名为MusicBroadcastReciver的函数

		/*
		 * IntentFilter intentFilter = new IntentFilter();
		musicBroadcastReciver = new MusicBroadcastReciver();
		intentFilter.addAction("currentProgress");
		registerReceiver(musicBroadcastReciver, intentFilter);
		 */
		IntentFilter intentFilter = new IntentFilter();
		musicBroadcastReciver = new MusicBroadcastReciver();
		// 注册完了之后就是发出了一个广播暗号：currentProgress
		intentFilter.addAction("currentProgress");
		registerReceiver(musicBroadcastReciver, intentFilter);
	}

	// 1 变量初始化
	// 这里只是初始化的一些方法
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

	// 5 设置进度条显示进度
	// 这个显示的进度是最终停下来的那个进度（主要是用户还可能会拖动进度条，如果不拖动，那么就是歌曲播放到的那个位置）
	void setSeekbarProgerss(int int_musicProgress) {
		seekBar.setProgress(int_musicProgress);
	}

	// 5 进度条进度广播
	// 好，这里我先对这个方法进行一下尝试性的理解
	class MusicBroadcastReciver extends BroadcastReceiver {
		@Override
		// 这里因为MusicBroadcastReciver是一个继承了BroadcastReceiver的一个方法，
		// 那么就需要重写BroadcastReceiver的构造函数
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			// 函数里面主要是接受了Service丢出去的一个名为“currentProgress”的数据包，并且获取了其中的数据
			// 也就是当前音乐播放的百分比，并且显示到进度条中
			int int_musicProgress = intent.getIntExtra("currentProgress", 0);
			setSeekbarProgerss(int_musicProgress);
		}

	}

}
