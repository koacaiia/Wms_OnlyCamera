package org.techtown.wms_onlycamera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Gallery extends AppCompatActivity {
    RecyclerView recyclerView;
    Picture_FineAdapter rAdapter;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년MM월dd일 HH:mm:ss");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        recyclerView = findViewById(R.id.container1);
        LinearLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        rAdapter = new Picture_FineAdapter();
        recyclerView.setAdapter(rAdapter);

        ArrayList<Picture_Fine> result = queryAllPictures();
        rAdapter.setItems(result);
        rAdapter.notifyDataSetChanged();
        Intent intent1 = getIntent();
        String name=intent1.getStringExtra("des");

        Toast.makeText(this, "복사내용:"+name, Toast.LENGTH_LONG).show();

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getShared();
            }
        });



        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent mIntent = new Intent(Intent.ACTION_VIEW,Uri.parse("content://media/internal/images/media"));
                startActivity(mIntent);
                return true;
            }
        });



    }
    public ArrayList<Picture_Fine> queryAllPictures() {
        ArrayList<Picture_Fine> result = new ArrayList<>();
        String AstrSDpath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File AmyDir = new File(AstrSDpath + "/Fine");
        String fileU = AmyDir.getAbsolutePath();
        Uri uri1 = Uri.fromFile(AmyDir);
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.DATE_ADDED};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, MediaStore.MediaColumns.DATE_ADDED + " desc");
        int columnDataIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        int columnNameIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
        int columnDateIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED);
        while (cursor.moveToNext()) {
            String path = cursor.getString(columnDataIndex);
            String displayName = cursor.getString(columnNameIndex);
            String outDate = cursor.getString(columnDateIndex);
            String addedDate = dateFormat.format(new Date(new Long(outDate).longValue() * 1000L));
//            특정폴더에서 부터 추출 시작
            if (path.startsWith(fileU)) {
                Picture_Fine info = new Picture_Fine(path, displayName, addedDate);
                result.add(info);
            }
        }
        for (Picture_Fine info : result) {
            Log.d("MainActivity", info.toString());
        }
        return result;
    }
    private void getShared() {
      int i=rAdapter.mCheckedList.size();
        Intent intent1 = getIntent();
        String name;
//        name= rAdapter.mCheckedList.get(0).toString();
        name=intent1.getStringExtra("des");
        ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("화인2물류","화인2물류"+name+"("+i+")"+" 사진 전달 합니다.");
        clipboard.setPrimaryClip(clip);

        ArrayList<Uri> uris = new ArrayList<>();
        for (Object file : rAdapter.mCheckedList) {
            File fileName=new File(String.valueOf(file));
            Uri uri1= FileProvider.getUriForFile(getApplicationContext(),"org.techtown.wms_onlycamera.provider", fileName);
            uris.add(uri1);}
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE); //전송 메소드를 호출합니다. Intent.ACTION_SEND
        intent.setType("image/*"); //jpg 이미지를 공유 하기 위해 Type을 정의합니다.
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris); //사진의 Uri를 가지고 옵니다.
        intent.setPackage("com.kakao.talk");
        startActivity(intent);
    }


}
