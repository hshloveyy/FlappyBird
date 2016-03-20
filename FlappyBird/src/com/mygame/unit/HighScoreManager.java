package com.mygame.unit;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.util.Base64;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

/**
 * 分数管理器
 * @author heshaohua
 *
 */
@SuppressLint("NewApi")
public class HighScoreManager {

	//highscore related
	int highscore_number = 5;
	Screen screen;
	EditText name;

	RelativeLayout layout;

	/**
	 * 定义一个内部类，用于放分数跟姓名
	 * @author heshaohua
	 *
	 */
	public class Highscore {
		public int highscore;
		public String hiscorename;
	}

	/**
	 * 实例化一个分数管理器，参数有屏幕、数据还有布局
	 * @param screen
	 * @param savedInstanceState
	 * @param layout
	 */
	public HighScoreManager(Screen screen, Bundle savedInstanceState, RelativeLayout layout) {
		this.screen = screen;
		this.layout = layout;
		PrintDeviceSignature();
	}

	//ui
	public void AddName_Editview(final int width, final String hint, final int y) {
		screen.runOnUiThread(new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				name = new EditText(screen);
				name.setHint(hint);
				name.setLines(1);
				name.setSingleLine();
				name.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });
				name.setTextColor(Color.argb(255, 51, 51, 51));
				RelativeLayout.LayoutParams nameparams = new RelativeLayout.LayoutParams(width, LayoutParams.WRAP_CONTENT);
				nameparams.addRule(RelativeLayout.CENTER_HORIZONTAL);
				nameparams.topMargin = y;

				int sdk = android.os.Build.VERSION.SDK_INT;
				if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
					name.setBackgroundDrawable(screen.getResources().getDrawable(android.R.drawable.edit_text));
				} else {
					name.setBackground(screen.getResources().getDrawable(android.R.drawable.edit_text));
				}

				layout.addView(name, nameparams);
			}
		});
	}

	/**
	 * 根据名称删除输入框
	 */
	public synchronized void RemoveName_Editview() {
		screen.runOnUiThread(new Runnable() {
			public void run() {
				layout.removeView(name);
				//layout.removeViews(0, layout.getChildCount() - 2);
			}
		});
	}

	//Local_________________________________________________________________________________________________________________________________
	/**
	 * 获取本地的分数排名信息
	 * @return
	 */
	public Highscore[] load_localscores() {
		// load preferences
		SharedPreferences hiscores = PreferenceManager.getDefaultSharedPreferences(screen.getApplicationContext());
		Highscore highscore[] = new Highscore[highscore_number];

		for (int i = 0; i < highscore_number; i++) {
			highscore[i] = new Highscore();
			highscore[i].highscore = hiscores.getInt("score" + i, 0);
			highscore[i].hiscorename = hiscores.getString("name" + i, "---");
		}
		return highscore;

	}

	/**
	 * 保存分数信息数据到本地
	 * @param highscore
	 */
	public void save_localscores(Highscore[] highscore) {
		//load preferences
		SharedPreferences hiscores = PreferenceManager.getDefaultSharedPreferences(screen.getApplicationContext());
		SharedPreferences.Editor hiscores_editor = hiscores.edit();
		for (int i = 0; i < highscore_number; i++) {
			hiscores_editor.putInt("score" + i, highscore[i].highscore);
			hiscores_editor.putString("name" + i, highscore[i].hiscorename);
		}
		hiscores_editor.commit();
	}

	/**
	 * 新的分数添加到本地缓存数据中
	 * @param highscore
	 * @param Default_name
	 */
	public void newScore(int highscore, String Default_name) {
		try {

			Highscore highscore_list[] = load_localscores();
			String temp_highscore_name = null;
			int temp_highscore;

			String highscore_name = name.getText().toString();
			if (!(highscore_name.length() > 0)) {
				highscore_name = Default_name;
			}
			for (int i = 0; i < highscore_number; i++) {
				//if (temp_highscore_name == null) {
				if (highscore >= highscore_list[i].highscore) {
					temp_highscore = highscore_list[i].highscore;
					temp_highscore_name = highscore_list[i].hiscorename;

					highscore_list[i].highscore = highscore;
					highscore_list[i].hiscorename = highscore_name;

					highscore = temp_highscore;
					highscore_name = temp_highscore_name;
				}
			}
			save_localscores(highscore_list);
			RemoveName_Editview();

		} catch (Exception e) {
			System.err.println("You didn't set a name for highscore. Please use AddName_Editview() to add an editview for the user to input his name");
		}
	}

	//Facebook share__________________________________________________________________________________________________________________________________
	public void postToFacebook(String Title, String Link, String Description, String NoFBApp) {

	
	}

	/**
	 * 此方法没什么意思，在游戏功能上没什么用，
	 * 方法是作用是对本应用APP的报名加密
	 */
	public void PrintDeviceSignature() {
		try {
			PackageInfo info = screen.getPackageManager().getPackageInfo(screen.getPackageName(),
					PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				System.out.println("Package name: " + screen.getPackageName() + "  Key Hash for facebook:" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {

		} catch (NoSuchAlgorithmException e) {

		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

	
	}

	public void onResume() {
	}

	public void onSaveInstanceState(Bundle outState) {
	}

	public void onPause() {
	}

	public void onDestroy() {
	}

}
