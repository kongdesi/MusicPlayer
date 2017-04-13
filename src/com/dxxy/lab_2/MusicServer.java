package com.dxxy.lab_2;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

public class MusicServer extends Service {
	
	//好，刚刚结束了对MainActivity的一个全解，那么不做停留，终于要开始解释Service了，一鼓作气，杀过去！！！
	//这个部分看样子主要是对这个文件里面可能会用到的一些变量做声明
	// 3 初始化变量
	int mediaDuration;// 音频的持续时间
	int mediaPercent;// 音频的播放进度
	//这里还声明了很多的广播的一些对象
	//那么自然，一个广播的对象就证明，就肯定会有一个对应的广播处理事件
	//这里我们看到有五个，显然，在MainActivity里面有几个暗号这里就会有几个广播处理，闲言少叙，直接看代码
	public static MediaPlayer mediaPlayer = null;// 音频播放组件变量
	MusicBroadcastReciver musicBroadcastReciver;// 开始广播接收器
	MusicPauseBroadcastReciver musicPauseBroadcastReciver;// 暂停广播接收器
	MusicContinueBroadcastReciver musicContinueBroadcastReciver;// 继续广播接收器
	MusicSeekBarBroadcastReciver musicSeekBarBroadcastReciver;// 进度条控制广播接收器

	
	//因为这个MusicService继承了Service，那么就必须要全部重新写Service全部的构造方法，但是这里我们看到，onDestroy（服务被销毁）
	//onBind（绑定服务）方法都没有什么特别的地方，只用了onCreate（服务被创建：只是简单的继承了超类的那个onCreate方法，因为并没有什么特别的操作）、
	//onStart（）是在服务开始的时候的方法，想想，自然是如此的，那么就直接看onStart()方法是什么！
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	// 4 Service的启动服务中完成广播注册
	@Override
	//先是习以为常的继承了父类的onStart()方法
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		// 注册播放广播"receiver_music"
		//这里它的解释是说，注册播放广播，这里我们看到有一句代码写道intentFilter.addAction("receiver_music");
		//那么，八成是对上了第一个暗号，也就是，第一个暗号所对应的要做的事由这个服务（名为musicBroadcastReciver）帮忙后台给做，
		//后面的代码就可以来看看做什么
		//动态的注册广播时间
		IntentFilter intentFilter = new IntentFilter();
		musicBroadcastReciver = new MusicBroadcastReciver();
		intentFilter.addAction("receiver_music");
		registerReceiver(musicBroadcastReciver, intentFilter);
		
		// 注册暂停广播"pause_music"
		IntentFilter intentFilter2 = new IntentFilter();
		musicPauseBroadcastReciver = new MusicPauseBroadcastReciver();
		intentFilter2.addAction("pause_music");
		registerReceiver(musicPauseBroadcastReciver, intentFilter2);
		Log.i("调用", "调用pause");
		
		// 注册继续播放广播"continue_music"
		IntentFilter intentFilter3 = new IntentFilter();
		musicContinueBroadcastReciver = new MusicContinueBroadcastReciver();
		intentFilter3.addAction("continue_music");
		registerReceiver(musicContinueBroadcastReciver, intentFilter3);
		
		// 注册进度条控制播放广播"seekbar"
		IntentFilter intentFilter4 = new IntentFilter();
		musicSeekBarBroadcastReciver = new MusicSeekBarBroadcastReciver();
		intentFilter4.addAction("seekbar");
		registerReceiver(musicSeekBarBroadcastReciver, intentFilter4);
		
		// 5以及完成进度条显示广播的发送
		//这里我们可以看到是定义了一个定时器Timer
//		 * 在开发中我们有时会有这样的需求，即在固定的每隔一段时间执行某一个任务。比如UI上的控件需要随着时间改变，
//		 * 我们可以使用Java为我们提供的计时器的工具类，即Timer和TimerTask。 
//			Timer是一个普通的类，其中有几个重要的方法；而TimerTask则是一个抽象类，其中有一个抽象方法run()，
//			类似线程中的run()方法，我们使用Timer创建一个他的对象，然后使用这对象的schedule方法来完成这种间隔的操作。
//			schedule方法有三个参数
//			第一个参数就是TimerTask类型的对象，我们实现TimerTask的run()方法就是要周期执行的一个任务；
//			第二个参数有两种类型，第一种是long类型，表示多长时间后开始执行，另一种是Date类型，表示从那个时间后开始执行；
//			第三个参数就是执行的周期，为long类型。
//			schedule方法还有一种两个参数的执行重载，第一个参数仍然是TimerTask，第二个表示为long的形式表示多长时间后执行一次，
//			为Date就表示某个时间后执行一次。 
//			Timer就是一个线程，使用schedule方法完成对TimerTask的调度，多个TimerTask可以共用一个Timer，
//			也就是说Timer对象调用一次schedule方法就是创建了一个线程，并且调用一次schedule后TimerTask是无限制的循环下去的，
//			使用Timer的cancel()停止操作。当然同一个Timer执行一次cancel()方法后，所有Timer线程都被终止。
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				sendProgress();//这里显然就是第一个参数，声明了一个函数：sendProgress，就是要周期执行的任务，
				               //我的理解就是，这里的定时器，就是
			}                  //每一首音乐的时长，因为每首歌的时间长度不尽相同
		}, 0, 1000);//这里的0，就是多长时间之后执行一次，也就是两首歌结束之间是没有等待间隔的。计数完了一首歌，马上就计数另外一首歌
		            //总共的表达就是，经历过0秒的时间间隔之后，每隔一秒执行一次

	}

	//这里就是要写那个sendProgress的函数定义
	// 5 发送广播信息给MusicActivity视图中的进度条广播接收器
	private void sendProgress() {
		if (mediaPlayer != null) {
			//这句话的意思，我认为的就是说，当媒体播放器正在播放的时候，那么就把目前已经播放的部分除以总共的长度做一个百分比
			//把最后的百分比赋值给currentProgress
			int currentProgress = (int) ((float) mediaPlayer
					.getCurrentPosition() / (float) mediaDuration * 100f);
			//又new了一个Intent
			Intent intent = new Intent();
			//发送广播并且设置了一个暗号：currentProgress
			intent.setAction("currentProgress");
			//这里把获得的那个百分比的进度打包成了一个名为currentProgress的一个数据包，往外丢
			//最后就会由那个正确对上暗号的那个函数得到这里的数据（MainActivity.MusicBroadcastReciver.int_musicProgress）,
			//也就是MainActivity里面的那个函数得到了，并且把进度显示到了进度条里面
			intent.putExtra("currentProgress", currentProgress);
			sendBroadcast(intent);
		}
	}

	//接下来，就是非常重要的能够完成上一曲、下一曲。。。等等功能的代码部分了
	/*
	 * 1 完成音乐播放器的五大功能 a开始播放 b停止播放c暂停播放d继续播放e进度条播放
	 */
	// a开始播放
	//播放方法，简单直接并且暴力
	public void play(String path) {
		stop();//我认为这里是要先把后台的所有可能会和播放这首音乐会产生阻塞的进程全部停止
		File file = new File(path);
		Uri uri = Uri.fromFile(file);//获得音乐播放的路径，就要开始播放了
		mediaPlayer = null;//这里把媒体播放器设置成了空，就是把那些和这首歌无关的进程全部清除，排除障碍
		Log.i("shoudao", "shodao");//打印一发~~
		if (mediaPlayer == null) {
			//好的，阻碍全部清除，那么就要开始播放我自己点的那首歌了
			//先把媒体播放器先给创建一下，就相当于把媒体播放器拿出来一般
			mediaPlayer = MediaPlayer.create(MusicServer.this, uri);
			//！！！！！！！这个好像是可以禁止循环播放，那么我想要让音乐循环播放的那个初衷可能在这里就得到了答案
			//好，不过我还是先不动它，先解释全部代码，然后等会再测试；；；；；；事实上在测试完了之后发现，并没有什么卵用。。
			mediaPlayer.setLooping(false);
			mediaPlayer.seekTo(0);//这个也是可以想象到的，就是不管我们本来正在听的那首歌播到了多久，我们
			//再次点击另外一首歌，那么那首歌肯定会从最开始的地方播放，这个0指的是position是0
			mediaPlayer.start();//然后，准备工作完成之后，那么就要开始播放了
			// 获取长度
			mediaDuration = mediaPlayer.getDuration();//在这里获取音乐的长度
			mediaPercent = mediaDuration / 100;//将获取的音乐的长度做一个百分比的操作，得到百分之一的长度当作
			//这首歌的单位长度，便于进度条对它的操作
		}
	}

	// b停止播放
	//这里就是停止操作了，可见，没有了那么多的准备工作要做，所以，就很简洁
	public void stop() {
		if (mediaPlayer != null) {//这个就是说如果播放器正在播放的时候才可以有停止播放操作
			mediaPlayer.stop();
			mediaPlayer.release();//停止播放了之后，自然那么就要把占用的所有的资源全部释放，毕竟，系统资源也是很宝贵的
			mediaPlayer = null;//这个地方的话就要把媒体播放器设置为没有播放的状态了，一方面却是因为它真的没有在工作
			//另一方面，是为了下一次开始播放做准备，因为开始播放的操作最初是要对媒体播放器的状态做一个判断的。
			System.gc();//这个代码，不是给人看的，因为java存在一个机制，就是可以提醒java虚拟机程序员需要在执行到这里的
			//时候进行一次资源释放的操作
		}
	}

	// c暂停播放
	//暂停和停止的区别，就在于会不会把这个音乐播放的进程删掉并且释放资源，可见，并不用。所以这里只执行了一次媒体播放器的自带的暂停方法
	public void Musicpause() {
		Log.i("调用pause", "调用pause");
		if (mediaPlayer != null) {
			mediaPlayer.pause();
		}
	}

	// d继续播放
	//那么继续播放就是结束暂停的操作，直接执行媒体播放器自带的开始方法
	public void MusicContinue() {
		if (mediaPlayer != null) {
			mediaPlayer.start();
		}
	}

	// e进度条播放
	//当用户点击进度条的时候，就会从当前进度条对应的进度开始进行播放（这一部分的功能是没有问题的）
	//这里我们可以往上看一下，上面有一个语句mediaPlayer.seekTo(0);意思是，把进度设置为0，那么，
	//下面的部分的代码就是把进度条显示为当前的progress乘以当前这首歌的单位长度，就是现在的总进度
	void musicProgress(int progress) {
		if (mediaPlayer != null) {
			mediaPlayer.seekTo(progress * mediaPercent);
		}

	}

	/*
	 * 2 完成音乐播放器的五大广播接收器功能 a开始播放 b停止播放c暂停播放d继续播放e进度条播放
	 */

	//这下面的部分就是执行四个收到暗号的操作，因为在MainActivity里面总共发了五个暗号，上面有个部分已经处理了一个，那么
	//剩下的四个就要在这里处理了
	class MusicBroadcastReciver extends BroadcastReceiver {

		@Override
		//第一个就是播放，那么可见在对上接头暗号之后，就执行了上面已经定义好了的play方法，传入的参数就是str_musicpathString
		//也就是在上面的那个play函数里面的path就是str_musicpathString，我们又已知str_musicpathString指的是
		//String str_musicpathString = list.get(currentPosition + 1).get("musicpath")和currentPosition += 1;
		//String str_musicpathString = list.get(currentPosition - 1).get("musicpath");
		//String str_musicpathString = list.get(arg2).get("musicpath")
		//等等这几条语句的最后的那些参数，大概看一下，也就是执行完操作之后 的那个路径，也就是所有的情况。可见代码是很严谨的..
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String str_musicpathString = intent.getStringExtra("musicPath");
			play(str_musicpathString);
		}

	}

	//这里是对上了暂停操作发出广播的暗号的对应操作
	class MusicPauseBroadcastReciver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

			//也就是执行上面定义好了的Musicpause（）方法
			Musicpause();
		}

	}

	//继续操作，同理也是只执行上面定义好的那个MusicContinue（）方法即可
	class MusicContinueBroadcastReciver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			MusicContinue();
		}

	}

	//这个方法看来是相比复杂一些的，也是最后一个代码块，开始解读
	class MusicSeekBarBroadcastReciver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			//这里就是得到了intent.setAction("seekbar");
			//并且这里把获取到的用户拖动的进度条对应的进度数据打成一个名为Progress的数据包，向Service丢了过去
			//intent.putExtra("Progress", progress);
			//就是得到了那个暗号所对应的那个广播丢出去的数据包，然后把值就给了progress，然后执行了已经
			//定义好了的那个musicProgress方法
			int progress = intent.getIntExtra("Progress", 0);
			musicProgress(progress);
		}
	}
	//到此，代码全部解读完毕
	
}
