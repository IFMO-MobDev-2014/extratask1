package ru.ifmo.md.extratask1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Svet on 15.01.2015.
 */
public class MyBroadcastReceiver extends BroadcastReceiver{

    public static final String PROCESS_RESPONSE = "process_response";

    MainActivity main = null;
    public void setMainActivityHandler(MainActivity main) {
        this.main = main;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int answerType = intent.getIntExtra(MyImageLoadService.ANSWER_TYPE, -1);

        switch(answerType) {
            case 1 : {
                int index = intent.getIntExtra(MyImageLoadService.IMAGE_INDEX, -1);
                int size = intent.getIntExtra(MyImageLoadService.IMAGE_SIZE, -1);
                main.setImageSize(index, size);
                break;
            }
            case 2 : {
                int index = intent.getIntExtra(MyImageLoadService.IMAGE_INDEX, -1);
                int length = intent.getIntExtra(MyImageLoadService.DIFF, -1);
                main.increaseProgressBar(index, length);
                break;
            }
            case 3 : {
                int index = intent.getIntExtra(MyImageLoadService.IMAGE_INDEX, -1);
                String addressSmall = intent.getStringExtra(MyImageLoadService.IMAGE_ADDRESS);
                main.hideProgressBar(index, addressSmall);
                break;
            }
            case 4 : {
                int index = intent.getIntExtra(MyImageLoadService.IMAGE_INDEX, -1);
                String addressHuge = intent.getStringExtra(MyImageLoadService.IMAGE_ADDRESS);
                main.uploadHugeImage(index, addressHuge);
                break;
            }
            case 11 : {
                String answer = intent.getStringExtra(MyImageLoadService.RESPONSE_STRING);
                main.setURLs(answer);
                break;
            }
        }
    }
}
