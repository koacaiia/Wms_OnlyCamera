package org.techtown.wms_onlycamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Picture_FineAdapter extends RecyclerView.Adapter<Picture_FineAdapter.ViewHolder> {


    private List<Picture_Fine> items = new ArrayList<>();
    public Map<Picture_Fine, Boolean> mCheckedMap = new HashMap<>();
    public List mCheckedList=new ArrayList<>();
    private Object Picture_Fine;
    public SparseArray<Boolean> mSelectedItems = new SparseArray<>();

    public void setData(List<Picture_Fine> data) {
        items = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.picture_fine_gallery, viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Picture_Fine images = items.get(position);
        viewHolder.setItem(images);
        if (mSelectedItems.get(position, false)) {
            viewHolder.itemView.setBackgroundColor(Color.BLUE);
            mCheckedList.add(images);
        } else {
            viewHolder.itemView.setBackgroundColor(Color.WHITE);
            mCheckedList.remove(images);
        }
    }

    private boolean isItemSelected(int position) {
        return mSelectedItems.get(position, false);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Picture_Fine item) {
        items.add(item);
    }

    public void setItems(ArrayList<Picture_Fine> items) {
        this.items = items;
    }

    public Picture_Fine getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, Picture_Fine item) {
        items.set(position, item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView27;
        ImageView imageView;
        BitmapFactory.Options options = new BitmapFactory.Options();

        public ViewHolder(View itemView) {
            super(itemView);
            textView27 = itemView.findViewById(R.id.text_disName);
            imageView = itemView.findViewById(R.id.imageView);
            options.inSampleSize = 12;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    List<Picture_Fine> images = new ArrayList<>();
                    if (mSelectedItems.get(position, false)) {
                        mSelectedItems.put(position, false);
                        v.setBackgroundColor(Color.WHITE);
                    } else {
                        mSelectedItems.put(position, true);
                        v.setBackgroundColor(Color.BLUE);
                    }
                    notifyItemChanged(position);
                } }); }

        public void setItem(Picture_Fine item) {
            textView27.setText(item.getDisplayName());
            Bitmap bitmap = BitmapFactory.decodeFile(item.getPath(), options);
            imageView.setImageBitmap(bitmap);
        }
    }

}



