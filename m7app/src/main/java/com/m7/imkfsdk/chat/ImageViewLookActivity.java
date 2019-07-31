package com.m7.imkfsdk.chat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.m7.imkfsdk.R;
import com.m7.imkfsdk.utils.ImageUtils;
import com.m7.imkfsdk.utils.ToastUtils;
import com.m7.imkfsdk.view.ActionSheetDialog;
import com.m7.imkfsdk.view.TouchImageView;
import com.moor.imkf.IMChatManager;

/**
 * Created by long on 2015/7/3.
 */
public class ImageViewLookActivity extends Activity{

    private TouchImageView touchImageView;
    private boolean isSaveSuccess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.kf_activity_image_look);
        touchImageView = (TouchImageView) findViewById(R.id.matrixImageView);

        Intent intent = getIntent();
        final String imgPath = intent.getStringExtra("imagePath");
        final int fromother = intent.getIntExtra("fromwho",1);//谁发的

        if(imgPath != null && !"".equals(imgPath)) {
            Glide.with(this).load(imgPath)
//                    .placeholder(R.drawable.kf_pic_thumb_bg)
//                    .error(R.drawable.kf_image_download_fail_icon)
                    .into(touchImageView);
        }else {
            finish();
        }

        touchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        touchImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(fromother==0){//
                    //弹框
                    openDialog(imgPath);
                }else{
//                    ToastUtils.showShort("不保存本地的"+imgPath);
                }
                return true;
            }


        });

    }
    private void openDialog(final String imageUrl) {
        new ActionSheetDialog(this)
                .builder()
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .addSheetItem(
//                        this.getResources().getString(R.string.restartchat),
                        "保存图片",
                        ActionSheetDialog.SheetItemColor.BLACK, new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                saveImage(imageUrl);
                            }
                        }).show();
    }


    //保存图片
    private void saveImage(String ImageUrl) {
        Glide.with(ImageViewLookActivity.this)
                .load(ImageUrl)
//                .asBitmap()
                .into(new SimpleTarget<Drawable>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        isSaveSuccess = ImageUtils.saveImageToGallery(ImageViewLookActivity.this,ImageUtils.drawable2Bitmap(resource) );
                        if (isSaveSuccess) {
                            Toast.makeText(ImageViewLookActivity.this, "保存图片成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ImageViewLookActivity.this, "保存图片失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    }


                });


    }


}
