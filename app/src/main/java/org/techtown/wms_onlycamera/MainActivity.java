                                                                  package org.techtown.wms_onlycamera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;


import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AutoPermissionsListener, SurfaceHolder.Callback{
    ArrayList<View> views=new ArrayList<View>();
    ViewPager viewPager;
    TextView textView,textView2;
    boolean previewing = false;;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Camera camera;
    private String imageFilePath;
    String filePath;
    File file;
    private Camera.CameraInfo mCameraInfo;

    private int mDisplayOrientation;


    String[] items = {"코만푸드", "M&F", "SPC", "공차", "케이비켐", "BNI","기타","스위치코리아","서강비철", "제임스포워딩","스위치코리아"};
    Spinner spinner;
    RadioButton radioButton, radioButton2,radioButton_equip, radioButton2_equip;
    String sp_des_text = "";

    Integer[] items_outsourcing={0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
    Spinner spinner1;
    Spinner spinner2;
    Spinner spinner3;
    CheckBox checkBox1;
    CheckBox checkBox2;
    EditText editText;

    String[] items_equip = {"", "FF02 (인천04마1068)", "FF04 인천04사4252", "FF20 (BR18S-00095)", "FF21 (BR18S-2-00016)", "FF25 (FBA03-1910-04218)",
            "FK11 (FBRW25-R75C-600M)"};
    Spinner spinner_equip;

    String[] items_etc={"","구매발주내역 입고","폐기물 수거","시설물 파손,수리","식약처,견품반출","세관검사","SPC 작업용 팔렛트 입고",
            "공차 작업용 팔렛트 입고","기타화주팔렛트 입고"};
    Spinner spinner_etc;

    EditText editText2_editable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager=findViewById(R.id.viewPager);
        textView=findViewById(R.id.textView);
        textView2=findViewById(R.id.textView2);

        LayoutInflater inflater=getLayoutInflater();
        View v1=inflater.inflate(R.layout.cargo,null);
        View v2=inflater.inflate(R.layout.outsourcing,null);
        View v3=inflater.inflate(R.layout.equipments,null);
        View v4=inflater.inflate(R.layout.etc,null);
        View v5=inflater.inflate(R.layout.editable,null);
        views.add(v1);
        views.add(v2);
        views.add(v3);
        views.add(v4);
        views.add(v5);
        CustomAdapter customAdapter=new CustomAdapter(this);
        viewPager.setAdapter(customAdapter);

        getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceView = findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        String timeStamp = new SimpleDateFormat("yyyy년MM월dd일E요일HH시mm분").format(new Date());
        textView2.setText(timeStamp);
        ConstraintLayout layoutBackground = findViewById(R.id.background);
        layoutBackground.setOnClickListener(new ConstraintLayout.OnClickListener(){
            @Override
            public void onClick(View view) {
                camera.autoFocus(myAutoFocusCallback);
                String timeStamp = new SimpleDateFormat("yyyy년MM월dd일E요일HH시mm분").format(new Date());
                textView2.setText(timeStamp);
            }});

        FloatingActionButton fab = findViewById(R.id.floatingActionButton2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(myShutterCallback,myPictureCallback_RAW, myPictureCallback_JPG);
                camera.autoFocus(myAutoFocusCallback);
               camera.autoFocus (new Camera.AutoFocusCallback() {
                    public void onAutoFocus(boolean success, Camera camera) {
                        if(success){
                            camera.takePicture(myShutterCallback,myPictureCallback_RAW, myPictureCallback_JPG);
                        }}});
                String timeStamp = new SimpleDateFormat("yyyy년MM월dd일E요일HH시mm분").format(new Date());
                textView2.setText(timeStamp);

            }});
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String des=textView.getText().toString();
                Intent intent = new Intent(MainActivity.this ,Gallery.class);
                intent.putExtra("des",des);
                startActivity(intent); // 액티비티 이동.
                return true;
            }
        });

        //cargo
        ArrayAdapter<String> cargoradapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, items);
        cargoradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        radioButton = v1.findViewById(R.id.radioButton);
        radioButton2 = v1.findViewById(R.id.radioButton2);
        spinner = v1.findViewById(R.id.spinner);
        spinner.setAdapter(cargoradapter);
        sp_des_text = spinner.getSelectedItem().toString();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str_radio = "";
                if (radioButton.isChecked()) {
                    str_radio += radioButton.getText().toString();
                    Log.d("koaca1","koaca1");
                }
                if (radioButton2.isChecked()) {
                    str_radio += radioButton2.getText().toString();
                }
                spinner.setTag(items[position]);

                textView.setText(str_radio+"_"+items[position]);
                }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinner.setTag(""); }
        });
        //outsourcing
        spinner1=v2.findViewById(R.id.spinner1_outsourcing);
        spinner3=v2.findViewById(R.id.spinner3_outsourcing);
        spinner2=v2.findViewById(R.id.spinner2_outsourcing);
        checkBox1=v2.findViewById(R.id.checkBox9_outsourcing);
        checkBox2=v2.findViewById(R.id.checkBox10_outsourcing);
        editText=v2.findViewById(R.id.editText2_outsourcing);

        ArrayAdapter<Integer> spAdapter=new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item,items_outsourcing);
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(spAdapter);
        spinner2.setAdapter(spAdapter);
        spinner3.setAdapter(spAdapter);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str_check9="";
                if(checkBox1.isChecked()){str_check9+=checkBox1.getText().toString();}
                spinner1.setTag(items_outsourcing[position]);
                textView.setText(str_check9+"_"+items_outsourcing[position]);}
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinner1.setTag("");}
        });
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str_check10="";
                if(checkBox2.isChecked()){str_check10+=checkBox2.getText().toString();}
                spinner2.setTag(items_outsourcing[position]);
                textView.append(","+str_check10+"_"+items_outsourcing[position]);}

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinner2.setTag("");
            }
        });
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String ed2="";
                ed2=editText.getText().toString();
                spinner3.setTag(items_outsourcing[position]);
                textView.append(","+ed2+"_"+items_outsourcing[position]);}
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinner3.setTag(""); }
        });
        //equip
        spinner_equip=v3.findViewById(R.id.spinner_equip);
        radioButton_equip = v3.findViewById(R.id.radioButton_equip);
        radioButton2_equip = v3.findViewById(R.id.radioButton2_equip);
        ArrayAdapter<String> equipAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,items_equip);
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_equip.setAdapter(equipAdapter);
        spinner_equip.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str_radio = "";
                if (radioButton_equip.isChecked()) {
                    str_radio += radioButton_equip.getText().toString(); }
                if (radioButton2_equip.isChecked()) {
                    str_radio += radioButton2_equip.getText().toString(); }
                spinner_equip.setTag(items[position]);
                textView.setText(str_radio+"_"+items_equip[position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinner_equip.setTag("");
            }
        });

        //etc
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(
               this,android.R.layout.simple_spinner_item,items_etc );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_etc=v4.findViewById(R.id.spinner_etc);
        spinner_etc.setAdapter(adapter);
        spinner_etc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               spinner_etc.setTag(items_etc[position]);
               textView.setText(items_etc[position]);}
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinner_etc.setTag("");
            } });
        //editable

        editText2_editable=v5.findViewById(R.id.editText2_editable);
        editText2_editable.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String editable=editText2_editable.getText().toString();
                textView.setText(editable);
                return true;
            }
        });
        AutoPermissions.Companion.loadAllPermissions(this, 101);
    }

    AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback(){
        @Override
        public void onAutoFocus(boolean arg0, Camera arg1) {
        }};
    ShutterCallback myShutterCallback = new ShutterCallback(){
        @Override
        public void onShutter() {
        }};

    PictureCallback myPictureCallback_RAW = new PictureCallback(){
        @Override
        public void onPictureTaken(byte[] arg0, Camera arg1) {
        }};
    PictureCallback myPictureCallback_JPG = new PictureCallback(){
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            try {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                String outUriStr = MediaStore.Images.Media.insertImage(
                        getContentResolver(),
                        bitmap,
                        "Captured Image",
                        "Captured Image using Camera.");
                File file=new File(outUriStr);
                if (outUriStr == null) {
                    Log.d("SampleCapture", "Image insert failed.");
                    Toast.makeText(MainActivity.this, "이건가?", Toast.LENGTH_SHORT).show();
                    return;
                } else {

                    Uri outUri = Uri.parse(outUriStr);
                    Cursor cursor = getContentResolver().query(outUri, null, null, null, null );
                    assert cursor != null;
                    cursor.moveToNext();
                    imageFilePath="";
                    imageFilePath = cursor.getString( cursor.getColumnIndex( "_data" ) );
                    cursor.close();
                    sendBroadcast(new Intent(
                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, outUri));
                }
                camera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(imageFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int exifOrientation;
            int exifDegree;
            if (exif != null) {
                exifOrientation = exif.getAttributeInt(android.media.ExifInterface.TAG_ORIENTATION, android.media.ExifInterface.ORIENTATION_NORMAL);
                exifDegree = exifOrientationToDegress(exifOrientation);
            } else {
                exifDegree = 0;
            }
            bitmap = (rotate(bitmap, exifDegree));
//            setCameraOrientation();
            Bitmap dest = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            String timeStamp1 = new SimpleDateFormat("yyyy년MM월dd일E요일").format(new Date());
            String timeStamp2 = new SimpleDateFormat("a_HH시mm분ss초").format(new Date());
            String name;
            name=textView.getText().toString();
            Canvas cs = new Canvas(dest);
            Paint tPaint = new Paint();
            tPaint.setTextSize(100);
            tPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            tPaint.setColor(Color.WHITE);
            tPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            cs.drawBitmap(bitmap, 0f, 0f, null);
            float height = tPaint.measureText("yY");
            cs.drawText(timeStamp1, 20f, height + 20f, tPaint);
            cs.drawText(timeStamp2, 20f, height + 180f, tPaint);
            cs.drawText(name, 20f, height + 340f, tPaint);
            final String strSDpath = Environment.getExternalStorageDirectory().getAbsolutePath();
            final File myDir = new File(strSDpath + "/Fine");
            if (!myDir.exists()) {
                myDir.mkdirs();
            }
            try {
                dest.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File(myDir.getPath() + "/" + name + "_" + timeStamp1 + "_" +timeStamp2 + ".jpg")));
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + myDir.getPath() + "/" + name + "_" + timeStamp1 + "_" +timeStamp2+".jpg")));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            File file = new File(imageFilePath);
            if(file.exists()) {
                boolean isDelete = file.delete();
                if(isDelete) Log.e("file delete ?", String.valueOf(isDelete));
                }
        }};

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//       try{ camera = Camera.open();
//        setCameraOrientation();
//
//    }catch (RuntimeException ex){}
//      android.hardware.Camera.Parameters parameters;
//        parameters = camera.getParameters();
//
//        parameters.setPreviewFrameRate(20);
//    List<Camera.Size> customSizes = parameters.getSupportedPreviewSizes();
//    Camera.Size customSize = customSizes.get(0); //Added size
//        parameters.setPreviewSize(customSize.width, customSize.height);
//                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
//        camera.setParameters(parameters);
//        camera.setDisplayOrientation(90);
//        try {
//            camera.setPreviewDisplay(surfaceHolder);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }}
        camera = Camera.open();
        setCameraOrientation();
        try {
            camera.setPreviewDisplay(surfaceHolder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
          if(previewing){
            camera.stopPreview();
            previewing = false;
        }
        if (camera != null){
            try {
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
                previewing = true;
            } catch (IOException e) {
                e.printStackTrace();
            }}}
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
        previewing = false;
    }


    public void setCameraOrientation() {
            if (camera == null) {
                return;
            }
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(0, info);
            WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            int rotation = manager.getDefaultDisplay().getRotation();
            int degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0: degrees = 0; break;
                case Surface.ROTATION_90: degrees = 90; break;
                case Surface.ROTATION_180: degrees = 180; break;
                case Surface.ROTATION_270: degrees = 270; break;
            }
            int result;
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;
            } else {
                result = (info.orientation - degrees + 360) % 360;
            }
            camera.setDisplayOrientation(result);
        }

    private Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
    private int exifOrientationToDegress(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 270;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 90;
        }


        return 90;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
    }
    @Override
    public void onDenied(int requestCode, @NonNull String[] permissions) {

        Toast.makeText(this, "permissions denied : " + permissions.length, Toast.LENGTH_LONG).show();
    }
    @Override
    public void onGranted(int requestCode, @NonNull String[] permissions) {

        Toast.makeText(this, "permissions granted : " + permissions.length, Toast.LENGTH_LONG).show();
    }

   }
