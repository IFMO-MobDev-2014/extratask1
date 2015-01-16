package lapics.sergeybudkov.ru.lapics;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;

public class SinglePictureShow extends Activity {
    private int index;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_picture);
        Intent intent = getIntent();
        index = intent.getIntExtra("index", 0);
        setPic(index);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.single_picture_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        if(item.getItemId() == R.id.save_file){
            try {
                String s = "laPics" + ""+index+".png";
                File sd = Environment.getExternalStorageDirectory();
                File dest = new File(sd, s);
                FileOutputStream out = new FileOutputStream(dest);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Сохранено как:"+s,
                        Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void setPic(int i){
        PicsDatabase database = new PicsDatabase(this);
        database.open();
        bitmap = database.getPicture(i);
        database.close();
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);
    }

    public void next(View v){
        index++;
        if(index > 39)
        {
            index = 39;
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Листайте в другую сторону",
                    Toast.LENGTH_LONG);
            toast.show();
        }else {
            setPic(index);
        }
    }

    public void prev(View v){
        index--;
        if(index < 0)
        {
            index = 0;
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Листайте в другую сторону",
                    Toast.LENGTH_LONG);
            toast.show();
        }
        else {
            setPic(index);
        }
    }
}