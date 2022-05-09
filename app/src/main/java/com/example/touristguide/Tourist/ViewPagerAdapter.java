package com.example.touristguide.Tourist;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.touristguide.R;

import java.util.Objects;

public class ViewPagerAdapter extends PagerAdapter {

    Context context;
    Uri[] images;
    LayoutInflater mLayoutInflater;
    String[] title_txt;
    String[] description_txt;
    int layout;

    public ViewPagerAdapter(Context context, Uri[] images, int layout, String[] title, String[] description) {
        this.context = context;
        this.images = images;
        this.layout = layout;
        this.title_txt = title;
        this.description_txt = description;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {

        return images.length;
    }
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((RelativeLayout) object);
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        // inflating the item.xml
        View itemView = mLayoutInflater.inflate(layout, container, false);
        // referencing the image view from the item.xml file
        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageViewMain);
        TextView title = itemView.findViewById(R.id.title);
        title.setText(title_txt[position]);

        TextView description = itemView.findViewById(R.id.description);
        title.setText(title_txt[position]);
        description.setText(description_txt[position]);
        // setting the image in the imageView
        Glide.with(context)
                .load(images[position])
                .into(imageView);


        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        // Adding the View
        Objects.requireNonNull(container).addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((RelativeLayout) object);
    }

}
