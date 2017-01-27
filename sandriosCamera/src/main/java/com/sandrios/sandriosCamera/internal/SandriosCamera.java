package com.sandrios.sandriosCamera.internal;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.IntRange;
import android.support.v7.app.AlertDialog;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.sandrios.sandriosCamera.R;
import com.sandrios.sandriosCamera.internal.configuration.CameraConfiguration;
import com.sandrios.sandriosCamera.internal.ui.camera.Camera1Activity;
import com.sandrios.sandriosCamera.internal.ui.camera2.Camera2Activity;
import com.sandrios.sandriosCamera.internal.ui.model.QualityOptions;
import com.sandrios.sandriosCamera.internal.utils.CameraHelper;

import java.util.ArrayList;

/**
 * Sandrios Camera Builder Class
 * Created by Arpit Gandhi on 7/6/16.
 */
public class SandriosCamera {

    private SandriosCamera mInstance = null;
    private Activity mActivity;
    private int requestCode;
    private int mediaAction = CameraConfiguration.MEDIA_ACTION_BOTH;
    private boolean showPicker = true;
    private boolean enableImageCrop = false;
    private long videoSize = -1;
    private QualityOptions quality = QualityOptions.QUALITY_NONE;

    /***
     * Creates SandriosCamera instance with default configuration set to both.
     *
     * @param activity - fromList which request was invoked
     * @param code     - request code which will return in onActivityForResult
     */
    public SandriosCamera(Activity activity, @IntRange(from = 0) int code) {
        mInstance = this;
        mActivity = activity;
        requestCode = code;
    }

    public SandriosCamera setShowPicker(boolean showPicker) {
        this.showPicker = showPicker;
        return mInstance;
    }

    public SandriosCamera setMediaAction(int mediaAction) {
        this.mediaAction = mediaAction;
        return mInstance;
    }

    public SandriosCamera enableImageCropping(boolean enableImageCrop) {
        this.enableImageCrop = enableImageCrop;
        return mInstance;
    }

    public SandriosCamera setVideoFileSize(@IntRange(from = 0, to = 10) int fileSize) {
        this.videoSize = fileSize;
        return mInstance;
    }

    public SandriosCamera setDefaultMediaQuality(QualityOptions quality) {
        this.quality = quality;
        return mInstance;
    }

    public void launchCamera() {
        new TedPermission(mActivity)
                .setDeniedMessage(mActivity.getString(R.string.sandriocamara_permission_denied_message))
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        launchIntent();
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {

                    }
                })
                .setPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO)
                .check();
    }

    private void launchIntent() {
        if (CameraHelper.hasCamera(mActivity)) {
            Intent cameraIntent;
            if (CameraHelper.hasCamera2(mActivity)) {
                cameraIntent = new Intent(mActivity, Camera2Activity.class);
            } else {
                cameraIntent = new Intent(mActivity, Camera1Activity.class);
            }
            if (quality != QualityOptions.QUALITY_NONE) {
                cameraIntent.putExtra(CameraConfiguration.Arguments.MEDIA_DEFAULT, quality);
            }
            cameraIntent.putExtra(CameraConfiguration.Arguments.REQUEST_CODE, requestCode);
            cameraIntent.putExtra(CameraConfiguration.Arguments.SHOW_PICKER, showPicker);
            cameraIntent.putExtra(CameraConfiguration.Arguments.MEDIA_ACTION, mediaAction);
            cameraIntent.putExtra(CameraConfiguration.Arguments.ENABLE_CROP, enableImageCrop);
            if (videoSize > 0) {
                cameraIntent.putExtra(CameraConfiguration.Arguments.VIDEO_FILE_SIZE, videoSize * 1024 * 1024);
            }
            mActivity.startActivityForResult(cameraIntent, requestCode);
        } else {
            AlertDialog.Builder b = new AlertDialog.Builder(mActivity);
            b.setTitle(R.string.sandriocamara_no_camara_message);
            b.setPositiveButton(R.string.sandriocamara_ok_label, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            b.show();
        }
    }
}
