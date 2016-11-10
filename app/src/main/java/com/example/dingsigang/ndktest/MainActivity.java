package com.example.dingsigang.ndktest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;



import java.io.File;

/**
 * @Description TODO
 * @Package com.xiaosai.selectphoto
 * @Class MainActivity
 * @Copyright: Copyright (c) 2016
 * @author XiaoSai
 * @version V1.0.0
 */
public class MainActivity extends Activity implements CameraCore.CameraResult {
    private Button choose_image;
    private CameraProxy cameraProxy;
    private String fileDir = "";
    private ImageView choose_bit;
    /** SD卡根目录 */
    private final String externalStorageDirectory = Environment.getExternalStorageDirectory().getPath()+"/atest/picture/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //APP缓存目录 拍照缓存
        fileDir = getExternalCacheDir().getPath();
        //压缩后保存临时文件目录
        File tempFile = new File(externalStorageDirectory);
        if(!tempFile.exists()){
            tempFile.mkdirs();
        }
        cameraProxy = new CameraProxy(this, MainActivity.this);

        choose_image = (Button)findViewById(R.id.choose_image);
        choose_image.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                cameraProxy.getPhoto2Camera(fileDir+"/temp.jpg");
            }
        });

        choose_bit = (ImageView)findViewById(R.id.choose_bit);
    }

    //拍照选图片成功回调
    @Override
    public void onSuccess(final String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            new Thread(){
                public void run() {
                    final File file = new File(externalStorageDirectory+"/temp.jpg");
                    NDKTestUtil.compressBitmap(BitmapFactory.decodeFile(filePath),file.getPath());
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            choose_bit.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));
                        }
                    });
                };
            }.start();
        }
    }

    //拍照选图片失败回调
    @Override
    public void onFail(String message) {
        System.out.println("message = "+ message);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        cameraProxy.onResult(requestCode, resultCode, data);
    }
}
