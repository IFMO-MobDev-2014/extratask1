package com.example.alexey.extratask1;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Alexey on 18.01.2015.
 */

    public class BoxAdapter extends BaseAdapter {
        Context ctx;
        LayoutInflater lInflater;
        ArrayList<Bitmap> objects;
    private Context mContext;
        BoxAdapter(Context context, ArrayList<Bitmap> products) {
            ctx = context;
            objects = products;
           mContext = context;
            lInflater = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        // кол-во элементов
        @Override
        public int getCount() {
            return objects.size();
        }

        // элемент по позиции
        @Override
        public Object getItem(int position) {
            return objects.get(position);
        }

        // id по позиции
        @Override
        public long getItemId(int position) {
            return position;
        }
        // пункт списка
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // используем созданные, но не используемые view
      //      View view = convertView;
     //       if (view == null) {
     //           view = lInflater.inflate(R.layout.item, parent, false);
       //     }



            ImageView imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(180, 180));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);

            imageView.setImageBitmap(getProduct(position));
            //((ImageView) view.findViewById(R.id.imageView))

          //  CheckBox cbBuy = (CheckBox) view.findViewById(R.id.cbBox);
            // присваиваем чекбоксу обработчик
        //    cbBuy.setOnCheckedChangeListener(myCheckChangList);
            // пишем позицию
       //     cbBuy.setTag(position);
            // заполняем данными из товаров: в корзине или нет
          //  cbBuy.setChecked(p.box);
            return imageView;
        }

        // товар по позиции
        Bitmap getProduct(int position) {
            return (Bitmap) getItem(position);
        }

        // содержимое корзины
     //   ArrayList<Product> getBox() {
    //        ArrayList<Product> box = new ArrayList<Product>();
    //        for (Product p : objects) {
    //            // если в корзине
   //             if (p.box)
   //                 box.add(p);
   //         }
   //         return box;
   //     }

        // обработчик для чекбоксов
       // OnCheckedChangeListener myCheckChangList = new OnCheckedChangeListener() {
      //      public void onCheckedChanged(CompoundButton buttonView,
        //                                 boolean isChecked) {
      //          // меняем данные товара (в корзине или нет)
          //      getProduct((Integer) buttonView.getTag()).box = isChecked;
       //     }
      //  };

}
