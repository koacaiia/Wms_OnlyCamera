package org.techtown.wms_onlycamera;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

class CustomAdapter extends PagerAdapter {

    private MainActivity mainActivity;

    public CustomAdapter(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public int getCount() {
        return mainActivity.views.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        mainActivity.viewPager.addView(mainActivity.views.get(position));
        return mainActivity.views.get(position);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        mainActivity.viewPager.removeView((View)object);
    }
}
