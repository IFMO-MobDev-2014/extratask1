package com.example.kirill.topyandexphoto;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.example.kirill.topyandexphoto.fragment.ImageGridFragment;

public class MainActivity extends FragmentActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		String tag = ImageGridFragment.class.getSimpleName();
        Fragment fr = getSupportFragmentManager().findFragmentByTag(tag);
        if (fr == null) {
            fr = new ImageGridFragment();
        }

        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fr, tag).commit();
	}


}