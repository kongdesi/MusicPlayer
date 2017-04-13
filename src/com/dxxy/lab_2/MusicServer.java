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
	
	//�ã��ոս����˶�MainActivity��һ��ȫ�⣬��ô����ͣ��������Ҫ��ʼ����Service�ˣ�һ��������ɱ��ȥ������
	//������ֿ�������Ҫ�Ƕ�����ļ�������ܻ��õ���һЩ����������
	// 3 ��ʼ������
	int mediaDuration;// ��Ƶ�ĳ���ʱ��
	int mediaPercent;// ��Ƶ�Ĳ��Ž���
	//���ﻹ�����˺ܶ�Ĺ㲥��һЩ����
	//��ô��Ȼ��һ���㲥�Ķ����֤�����Ϳ϶�����һ����Ӧ�Ĺ㲥�����¼�
	//�������ǿ������������Ȼ����MainActivity�����м�����������ͻ��м����㲥������������ֱ�ӿ�����
	public static MediaPlayer mediaPlayer = null;// ��Ƶ�����������
	MusicBroadcastReciver musicBroadcastReciver;// ��ʼ�㲥������
	MusicPauseBroadcastReciver musicPauseBroadcastReciver;// ��ͣ�㲥������
	MusicContinueBroadcastReciver musicContinueBroadcastReciver;// �����㲥������
	MusicSeekBarBroadcastReciver musicSeekBarBroadcastReciver;// ���������ƹ㲥������

	
	//��Ϊ���MusicService�̳���Service����ô�ͱ���Ҫȫ������дServiceȫ���Ĺ��췽���������������ǿ�����onDestroy���������٣�
	//onBind���󶨷��񣩷�����û��ʲô�ر�ĵط���ֻ����onCreate�����񱻴�����ֻ�Ǽ򵥵ļ̳��˳�����Ǹ�onCreate��������Ϊ��û��ʲô�ر�Ĳ�������
	//onStart�������ڷ���ʼ��ʱ��ķ��������룬��Ȼ����˵ģ���ô��ֱ�ӿ�onStart()������ʲô��
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

	// 4 Service��������������ɹ㲥ע��
	@Override
	//����ϰ��Ϊ���ļ̳��˸����onStart()����
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		// ע�Ქ�Ź㲥"receiver_music"
		//�������Ľ�����˵��ע�Ქ�Ź㲥���������ǿ�����һ�����д��intentFilter.addAction("receiver_music");
		//��ô���˳��Ƕ����˵�һ�����ţ�Ҳ���ǣ���һ����������Ӧ��Ҫ�����������������ΪmusicBroadcastReciver����æ��̨������
		//����Ĵ���Ϳ�����������ʲô
		//��̬��ע��㲥ʱ��
		IntentFilter intentFilter = new IntentFilter();
		musicBroadcastReciver = new MusicBroadcastReciver();
		intentFilter.addAction("receiver_music");
		registerReceiver(musicBroadcastReciver, intentFilter);
		
		// ע����ͣ�㲥"pause_music"
		IntentFilter intentFilter2 = new IntentFilter();
		musicPauseBroadcastReciver = new MusicPauseBroadcastReciver();
		intentFilter2.addAction("pause_music");
		registerReceiver(musicPauseBroadcastReciver, intentFilter2);
		Log.i("����", "����pause");
		
		// ע��������Ź㲥"continue_music"
		IntentFilter intentFilter3 = new IntentFilter();
		musicContinueBroadcastReciver = new MusicContinueBroadcastReciver();
		intentFilter3.addAction("continue_music");
		registerReceiver(musicContinueBroadcastReciver, intentFilter3);
		
		// ע����������Ʋ��Ź㲥"seekbar"
		IntentFilter intentFilter4 = new IntentFilter();
		musicSeekBarBroadcastReciver = new MusicSeekBarBroadcastReciver();
		intentFilter4.addAction("seekbar");
		registerReceiver(musicSeekBarBroadcastReciver, intentFilter4);
		
		// 5�Լ���ɽ�������ʾ�㲥�ķ���
		//�������ǿ��Կ����Ƕ�����һ����ʱ��Timer
//		 * �ڿ�����������ʱ�������������󣬼��ڹ̶���ÿ��һ��ʱ��ִ��ĳһ�����񡣱���UI�ϵĿؼ���Ҫ����ʱ��ı䣬
//		 * ���ǿ���ʹ��JavaΪ�����ṩ�ļ�ʱ���Ĺ����࣬��Timer��TimerTask�� 
//			Timer��һ����ͨ���࣬�����м�����Ҫ�ķ�������TimerTask����һ�������࣬������һ�����󷽷�run()��
//			�����߳��е�run()����������ʹ��Timer����һ�����Ķ���Ȼ��ʹ��������schedule������������ּ���Ĳ�����
//			schedule��������������
//			��һ����������TimerTask���͵Ķ�������ʵ��TimerTask��run()��������Ҫ����ִ�е�һ������
//			�ڶ����������������ͣ���һ����long���ͣ���ʾ�೤ʱ���ʼִ�У���һ����Date���ͣ���ʾ���Ǹ�ʱ���ʼִ�У�
//			��������������ִ�е����ڣ�Ϊlong���͡�
//			schedule��������һ������������ִ�����أ���һ��������Ȼ��TimerTask���ڶ�����ʾΪlong����ʽ��ʾ�೤ʱ���ִ��һ�Σ�
//			ΪDate�ͱ�ʾĳ��ʱ���ִ��һ�Ρ� 
//			Timer����һ���̣߳�ʹ��schedule������ɶ�TimerTask�ĵ��ȣ����TimerTask���Թ���һ��Timer��
//			Ҳ����˵Timer�������һ��schedule�������Ǵ�����һ���̣߳����ҵ���һ��schedule��TimerTask�������Ƶ�ѭ����ȥ�ģ�
//			ʹ��Timer��cancel()ֹͣ��������Ȼͬһ��Timerִ��һ��cancel()����������Timer�̶߳�����ֹ��
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				sendProgress();//������Ȼ���ǵ�һ��������������һ��������sendProgress������Ҫ����ִ�е�����
				               //�ҵ������ǣ�����Ķ�ʱ��������
			}                  //ÿһ�����ֵ�ʱ������Ϊÿ�׸��ʱ�䳤�Ȳ�����ͬ
		}, 0, 1000);//�����0�����Ƕ೤ʱ��֮��ִ��һ�Σ�Ҳ�������׸����֮����û�еȴ�����ġ���������һ�׸裬���Ͼͼ�������һ�׸�
		            //�ܹ��ı����ǣ�������0���ʱ����֮��ÿ��һ��ִ��һ��

	}

	//�������Ҫд�Ǹ�sendProgress�ĺ�������
	// 5 ���͹㲥��Ϣ��MusicActivity��ͼ�еĽ������㲥������
	private void sendProgress() {
		if (mediaPlayer != null) {
			//��仰����˼������Ϊ�ľ���˵����ý�岥�������ڲ��ŵ�ʱ����ô�Ͱ�Ŀǰ�Ѿ����ŵĲ��ֳ����ܹ��ĳ�����һ���ٷֱ�
			//�����İٷֱȸ�ֵ��currentProgress
			int currentProgress = (int) ((float) mediaPlayer
					.getCurrentPosition() / (float) mediaDuration * 100f);
			//��new��һ��Intent
			Intent intent = new Intent();
			//���͹㲥����������һ�����ţ�currentProgress
			intent.setAction("currentProgress");
			//����ѻ�õ��Ǹ��ٷֱȵĽ��ȴ������һ����ΪcurrentProgress��һ�����ݰ������ⶪ
			//���ͻ����Ǹ���ȷ���ϰ��ŵ��Ǹ������õ���������ݣ�MainActivity.MusicBroadcastReciver.int_musicProgress��,
			//Ҳ����MainActivity������Ǹ������õ��ˣ����Ұѽ�����ʾ���˽���������
			intent.putExtra("currentProgress", currentProgress);
			sendBroadcast(intent);
		}
	}

	//�����������Ƿǳ���Ҫ���ܹ������һ������һ���������ȵȹ��ܵĴ��벿����
	/*
	 * 1 ������ֲ������������ a��ʼ���� bֹͣ����c��ͣ����d��������e����������
	 */
	// a��ʼ����
	//���ŷ�������ֱ�Ӳ��ұ���
	public void play(String path) {
		stop();//����Ϊ������Ҫ�ȰѺ�̨�����п��ܻ�Ͳ����������ֻ���������Ľ���ȫ��ֹͣ
		File file = new File(path);
		Uri uri = Uri.fromFile(file);//������ֲ��ŵ�·������Ҫ��ʼ������
		mediaPlayer = null;//�����ý�岥�������ó��˿գ����ǰ���Щ�����׸��޹صĽ���ȫ��������ų��ϰ�
		Log.i("shoudao", "shodao");//��ӡһ��~~
		if (mediaPlayer == null) {
			//�õģ��谭ȫ���������ô��Ҫ��ʼ�������Լ�������׸���
			//�Ȱ�ý�岥�����ȸ�����һ�£����൱�ڰ�ý�岥�����ó���һ��
			mediaPlayer = MediaPlayer.create(MusicServer.this, uri);
			//����������������������ǿ��Խ�ֹѭ�����ţ���ô����Ҫ������ѭ�����ŵ��Ǹ����Կ���������͵õ��˴�
			//�ã������һ����Ȳ��������Ƚ���ȫ�����룬Ȼ��Ȼ��ٲ��ԣ�������������ʵ���ڲ�������֮���֣���û��ʲô���á���
			mediaPlayer.setLooping(false);
			mediaPlayer.seekTo(0);//���Ҳ�ǿ������󵽵ģ����ǲ������Ǳ��������������׸貥���˶�ã�����
			//�ٴε������һ�׸裬��ô���׸�϶�����ʼ�ĵط����ţ����0ָ����position��0
			mediaPlayer.start();//Ȼ��׼���������֮����ô��Ҫ��ʼ������
			// ��ȡ����
			mediaDuration = mediaPlayer.getDuration();//�������ȡ���ֵĳ���
			mediaPercent = mediaDuration / 100;//����ȡ�����ֵĳ�����һ���ٷֱȵĲ������õ��ٷ�֮һ�ĳ��ȵ���
			//���׸�ĵ�λ���ȣ����ڽ����������Ĳ���
		}
	}

	// bֹͣ����
	//�������ֹͣ�����ˣ��ɼ���û������ô���׼������Ҫ�������ԣ��ͺܼ��
	public void stop() {
		if (mediaPlayer != null) {//�������˵������������ڲ��ŵ�ʱ��ſ�����ֹͣ���Ų���
			mediaPlayer.stop();
			mediaPlayer.release();//ֹͣ������֮����Ȼ��ô��Ҫ��ռ�õ����е���Դȫ���ͷţ��Ͼ���ϵͳ��ԴҲ�Ǻܱ����
			mediaPlayer = null;//����ط��Ļ���Ҫ��ý�岥��������Ϊû�в��ŵ�״̬�ˣ�һ����ȴ����Ϊ�����û���ڹ���
			//��һ���棬��Ϊ����һ�ο�ʼ������׼������Ϊ��ʼ���ŵĲ��������Ҫ��ý�岥������״̬��һ���жϵġ�
			System.gc();//������룬���Ǹ��˿��ģ���Ϊjava����һ�����ƣ����ǿ�������java���������Ա��Ҫ��ִ�е������
			//ʱ�����һ����Դ�ͷŵĲ���
		}
	}

	// c��ͣ����
	//��ͣ��ֹͣ�����𣬾����ڻ᲻���������ֲ��ŵĽ���ɾ�������ͷ���Դ���ɼ��������á���������ִֻ����һ��ý�岥�������Դ�����ͣ����
	public void Musicpause() {
		Log.i("����pause", "����pause");
		if (mediaPlayer != null) {
			mediaPlayer.pause();
		}
	}

	// d��������
	//��ô�������ž��ǽ�����ͣ�Ĳ�����ֱ��ִ��ý�岥�����Դ��Ŀ�ʼ����
	public void MusicContinue() {
		if (mediaPlayer != null) {
			mediaPlayer.start();
		}
	}

	// e����������
	//���û������������ʱ�򣬾ͻ�ӵ�ǰ��������Ӧ�Ľ��ȿ�ʼ���в��ţ���һ���ֵĹ�����û������ģ�
	//�������ǿ������Ͽ�һ�£�������һ�����mediaPlayer.seekTo(0);��˼�ǣ��ѽ�������Ϊ0����ô��
	//����Ĳ��ֵĴ�����ǰѽ�������ʾΪ��ǰ��progress���Ե�ǰ���׸�ĵ�λ���ȣ��������ڵ��ܽ���
	void musicProgress(int progress) {
		if (mediaPlayer != null) {
			mediaPlayer.seekTo(progress * mediaPercent);
		}

	}

	/*
	 * 2 ������ֲ����������㲥���������� a��ʼ���� bֹͣ����c��ͣ����d��������e����������
	 */

	//������Ĳ��־���ִ���ĸ��յ����ŵĲ�������Ϊ��MainActivity�����ܹ�����������ţ������и������Ѿ�������һ������ô
	//ʣ�µ��ĸ���Ҫ�����ﴦ����
	class MusicBroadcastReciver extends BroadcastReceiver {

		@Override
		//��һ�����ǲ��ţ���ô�ɼ��ڶ��Ͻ�ͷ����֮�󣬾�ִ���������Ѿ�������˵�play����������Ĳ�������str_musicpathString
		//Ҳ������������Ǹ�play���������path����str_musicpathString����������֪str_musicpathStringָ����
		//String str_musicpathString = list.get(currentPosition + 1).get("musicpath")��currentPosition += 1;
		//String str_musicpathString = list.get(currentPosition - 1).get("musicpath");
		//String str_musicpathString = list.get(arg2).get("musicpath")
		//�ȵ��⼸������������Щ��������ſ�һ�£�Ҳ����ִ�������֮�� ���Ǹ�·����Ҳ�������е�������ɼ������Ǻ��Ͻ���..
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String str_musicpathString = intent.getStringExtra("musicPath");
			play(str_musicpathString);
		}

	}

	//�����Ƕ�������ͣ���������㲥�İ��ŵĶ�Ӧ����
	class MusicPauseBroadcastReciver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

			//Ҳ����ִ�����涨����˵�Musicpause��������
			Musicpause();
		}

	}

	//����������ͬ��Ҳ��ִֻ�����涨��õ��Ǹ�MusicContinue������������
	class MusicContinueBroadcastReciver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			MusicContinue();
		}

	}

	//���������������ȸ���һЩ�ģ�Ҳ�����һ������飬��ʼ���
	class MusicSeekBarBroadcastReciver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			//������ǵõ���intent.setAction("seekbar");
			//��������ѻ�ȡ�����û��϶��Ľ�������Ӧ�Ľ������ݴ��һ����ΪProgress�����ݰ�����Service���˹�ȥ
			//intent.putExtra("Progress", progress);
			//���ǵõ����Ǹ���������Ӧ���Ǹ��㲥����ȥ�����ݰ���Ȼ���ֵ�͸���progress��Ȼ��ִ�����Ѿ�
			//������˵��Ǹ�musicProgress����
			int progress = intent.getIntExtra("Progress", 0);
			musicProgress(progress);
		}
	}
	//���ˣ�����ȫ��������
	
}
