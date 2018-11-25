package com.example.javine.firstffmpeg;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DecodeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int CHOOSE_VIDEO_REQUEST = 0X01;
    private static final int PERMISSION_REQUEST = 0X02;

    private TextView mInputText;
    private Button mBtnStart, mBtnChoose;
    private String mInputPath, mOutputPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decode);
        mInputText = findViewById(R.id.tv_input);
        mBtnChoose = findViewById(R.id.btn_choose);
        mBtnChoose.setOnClickListener(this);
        mBtnStart = findViewById(R.id.btn_start);
        mBtnStart.setOnClickListener(this);
        checkStoragePermission();
    }

    private void checkStoragePermission() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_choose:
                chooseInputFile();
                break;
            case R.id.btn_start:
                startDecode();
                break;
        }

    }

    private void startDecode() {
        if (mInputPath != null && mOutputPath != null) {
            AsyncTask task = new AsyncTask<Object, Void, Integer>(){

                @Override
                protected Integer doInBackground(Object... objects) {
                    return JniHelper.decodeVideo(mInputPath, mOutputPath);
                }

                @Override
                protected void onPostExecute(Integer ret) {
                    String tips = "";
                    if (ret == 0) {
                        tips = "decode success!";
                    }else {
                        tips = "decode failed!";
                    }
                    Toast.makeText(DecodeActivity.this, tips, Toast.LENGTH_SHORT).show();
                }
            };
            task.execute();
        }
    }

    private void chooseInputFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, CHOOSE_VIDEO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CHOOSE_VIDEO_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                String[] filePathColumn = { MediaStore.Video.Media.DATA };

                Cursor cursor = getContentResolver().query(uri ,
                        filePathColumn, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    mInputPath = cursor.getString(columnIndex);
                    Log.d("Javine", mInputPath);
                    if (mInputPath != null) {
                        String[] filePaths = mInputPath.split("/");
                        generateOutputPath(filePaths);
                        mInputText.setText(filePaths[filePaths.length - 1]);
                    }
                    cursor.close();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void generateOutputPath(String[] filePaths) {
        if (filePaths == null || filePaths.length==0) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < filePaths.length-1; i++){
            if (i == 0) {
                builder.append(filePaths[i]);
            } else {
                builder.append("/").append(filePaths[i]);
            }
        }
        builder.append("Output.yuv");
        mOutputPath = builder.toString();
        Log.d("Javine", mOutputPath);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "permission got!", Toast.LENGTH_SHORT).show();
            mBtnStart.setEnabled(true);
        } else {
            mBtnStart.setEnabled(false);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
