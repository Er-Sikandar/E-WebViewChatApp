package com.shailersolutions.chatapp;

import static com.shailersolutions.chatapp.utils.Consts.INTERVAL_TIME;
import static com.shailersolutions.chatapp.utils.Consts.INTERVAL_TIME2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.shailersolutions.chatapp.baseui.BaseActivity;
import com.shailersolutions.chatapp.databinding.ActivityMainBinding;
import com.shailersolutions.chatapp.utils.Consts;

public class MainActivity extends BaseActivity {
private ActivityMainBinding binding;
    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final int FILECHOOSER_RESULTCODE = 1;
    private ValueCallback<Uri[]> mFilePathCallback;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    private String filePath;

    @Override
    protected void onStart() {
        super.onStart();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissProgressDialog();
            }
        },INTERVAL_TIME2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_main);
        showProgressDialog(Consts.PLEASE_WAIT);
        binding.webview.setWebViewClient(new WebViewClient());
        binding.webview.getSettings().setJavaScriptEnabled(true);
        binding.webview.getSettings().setDomStorageEnabled(true);
        binding.webview.getSettings().setAllowFileAccess(true);
        binding.webview.getSettings().setMediaPlaybackRequiresUserGesture(false);
        binding.webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        binding.webview.getSettings().setAllowContentAccess(true);
        binding.webview.getSettings().setLoadsImagesAutomatically(true);
        binding.webview.getSettings().setPluginState(WebSettings.PluginState.ON);
        binding.webview.setWebChromeClient(new WebChromeClient() {

            @SuppressLint("ObsoleteSdkInt")
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                Log.d("TAG", "onPermissionRequest");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    request.grant(request.getResources());
                } else {
                    request.grant(request.getResources());
                }
            }
            @Override
            public void onPermissionRequestCanceled(PermissionRequest request) {
                super.onPermissionRequestCanceled(request);
                Log.d("TAG", "Permission Denied");
            }
            public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath,
                                             WebChromeClient.FileChooserParams fileChooserParams) {
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePath;

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("*/*");
                Intent[] intentArray = new Intent[0];
                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Select Option:");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);

                return true;
            }

        });

        if (isInternetConnected(MainActivity.this)) {
            binding.webview.loadUrl("https://sspl20.com/chat/public/");
        }else {
            Toast.makeText(MainActivity.this, "No Internet Available!", Toast.LENGTH_SHORT).show();
        }

    }
    // TODO Back Logic here.

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (binding.webview.canGoBack()) {
                        binding.webview.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        } else {
            /* Toast.makeText(getApplicationContext(),"File Selected Successfully.",Toast.LENGTH_LONG).show();*/
        }
        Uri[] results = null;
        //check good response
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                if (filePath != null) {
                    results = new Uri[]{Uri.parse(filePath)};
                }
            } else {
                String dataString = data.getDataString();
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
            }
        }
        mFilePathCallback.onReceiveValue(results);
        mFilePathCallback = null;
        if (requestCode != FILECHOOSER_RESULTCODE || mUploadMessage == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        Uri result = null;
        try {
            if (resultCode != RESULT_OK) {
                result = null;
            } else {
                result = data == null ? mCapturedImageURI : data.getData();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Activity :" + e, Toast.LENGTH_LONG).show();
        }
        mUploadMessage.onReceiveValue(result);
        mUploadMessage = null;
    }
}