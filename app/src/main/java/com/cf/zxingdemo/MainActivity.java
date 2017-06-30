package com.cf.zxingdemo;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cf.zxinglibrary.zxing.activity.CaptureActivity;
import com.cf.zxinglibrary.zxing.encoding.EncodingHandler;
import com.google.zxing.WriterException;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText et_content;
    private Button btn1;
    private Button btn2;
    private ImageView mImageView;

    private static final int PHOTO_PIC = 1;
    private static final int REQUEST_CODE_PERMISSION_CAMERA = 100;
    public static final int REQUEST_CODE_SETTING= 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_content = (EditText) findViewById(R.id.et_content);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        mImageView = (ImageView) findViewById(R.id.imageView);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn1:
                try {

                    String content = et_content.getText().toString();
                    if(TextUtils.isEmpty(content)){
                        Toast.makeText(MainActivity.this,"内容不能为空！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //生成二维码图片，第一个参数是二维码的内容，第二个参数是正方形图片的边长，单位是像素
                    Bitmap qrCodeBitmap = EncodingHandler.createQRCode(content, DensityUtil.dp2px(this,200));
                    mImageView.setImageBitmap(qrCodeBitmap);

                    //------------------添加logo部分------------------
//                    Bitmap logoBmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//
//                    //二维码和logo合并
//                    Bitmap bitmap = Bitmap.createBitmap(qrCodeBitmap.getWidth(), qrCodeBitmap
//                            .getHeight(), qrCodeBitmap.getConfig());
//                    Canvas canvas = new Canvas(bitmap);
//                    //二维码
//                    canvas.drawBitmap(qrCodeBitmap, 0,0, null);
//                    //logo绘制在二维码中央
//                    canvas.drawBitmap(logoBmp, qrCodeBitmap.getWidth() / 2
//                            - logoBmp.getWidth() / 2, qrCodeBitmap.getHeight()
//                            / 2 - logoBmp.getHeight() / 2, null);
//                    mImageView.setImageBitmap(bitmap);
                    //------------------添加logo部分------------------//

                } catch (WriterException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn2:
                //申请权限
                AndPermission.with(this)
                        .requestCode(REQUEST_CODE_PERMISSION_CAMERA)
                        .permission(Manifest.permission.CAMERA)
                        // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框，避免用户勾选不再提示。
                        .rationale(new RationaleListener() {
                            @Override
                            public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                                // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
                                AndPermission.rationaleDialog(MainActivity.this, rationale).show();
                            }
                        })
                        .send();

                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            switch (requestCode) {
                case PHOTO_PIC:
                    String result = data.getExtras().getString("result");
                    Bitmap bitmap1 = data.getExtras().getParcelable("bitmap");
                    Toast.makeText(this,result,Toast.LENGTH_SHORT).show();
                    if(bitmap1 != null){
                        mImageView.setImageBitmap(bitmap1);
                    }
                    break;

            }
        }
    }

    //----------------------------------权限回调处理----------------------------------//

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AndPermission.onRequestPermissionsResult(requestCode, permissions, grantResults, listener);
    }

    private PermissionListener listener = new PermissionListener() {

        @Override
        public void onSucceed(int requestCode, List<String> grantPermissions) {
            // 权限申请成功回调。
            switch (requestCode) {
                case REQUEST_CODE_PERMISSION_CAMERA:

                    //跳转到拍照界面扫描二维码
                    Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                    startActivityForResult(intent, PHOTO_PIC);

                    break;
            }

        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。

            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
            if (AndPermission.hasAlwaysDeniedPermission(MainActivity.this, deniedPermissions)) {
                // 第一种：用默认的提示语。
                AndPermission.defaultSettingDialog(MainActivity.this, REQUEST_CODE_SETTING).show();

            }

        }
    };
}
