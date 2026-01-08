package com.ganainy.gymmasterscompose.ui.screens.static_test;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import com.ganainy.PortApplication.adapter.SpAdapter;
import com.ganainy.PortApplication.constant.PreferenceKeys;
import com.ganainy.PortApplication.fragment.LogFragment;
import com.ganainy.PortApplication.message.ConversionNoticeEvent;
import com.ganainy.PortApplication.message.IMessage;
import com.ganainy.PortApplication.message.LogManager;
import com.ganainy.PortApplication.message.RecvMessage;
import com.ganainy.PortApplication.message.SendMessage;
import com.ganainy.PortApplication.util.PrefHelper;
import com.ganainy.gymmasterscompose.R;
import com.ganainy.gymmasterscompose.databinding.ActivityMainJavaBinding;
import com.ganainy.serialportlibrary.Device;
import com.ganainy.serialportlibrary.SerialPortFinder;
import com.ganainy.serialportlibrary.SimpleSerialPortManager;
import com.ganainy.serialportlibrary.utils.SerialPortLogUtil;
import com.hjq.toast.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import com.ganainy.serialportlibrary.enumerate.SerialStatus;

import java.util.Arrays;

import android.util.Log;

public class SerialPort {
    private ActivityMainJavaBinding binding;
    private Device mDevice;

    //mDevice: 当前选择的串口设备
    private String[] mDevices;
    private String[] mBaudrates;
    private int mDeviceIndex;
    private int mBaudrateIndex;
    private boolean mOpened = false;
    private boolean mConversionNotice = true;
    private LogFragment mLogFragment;

    //databits、paritys 和 stopbits 定义了可用的数据位、校验位和停止位。
    final String[] databits = new String[]{"8", "7", "6", "5"};
    final String[] paritys = new String[]{"NONE", "ODD", "EVEN", "SPACE", "MARK"};
    final String[] stopbits = new String[]{"1", "2"};

    //先定义
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    public interface SerialPortCallback {
        void onStatusChanged(boolean success, SerialStatus status, String message);
        void onDataReceived(byte[] data);
        void onDataSent(byte[] data);
    }

    private SerialPortCallback callback;


    private void initSerialPort() {

    }

    public void openSerialPort(SerialPortCallback callback) {
        SimpleSerialPortManager.getInstance()
                .openSerialPort("/dev/ttyS4", 115200,
                        (isSuccess, status) -> {
                            if (callback != null) {
                                String message = getStatusMessage(status);
                                callback.onStatusChanged(isSuccess, status, message);
                            }
                        },
                        new SimpleSerialPortManager.OnDataReceivedCallback() {
                            @Override
                            public void onDataReceived(byte[] data) {
                                Log.d("SerialPort", "onDataReceived [ byte[] ]: " + Arrays.toString(data));
                                Log.d("SerialPort", "onDataReceived [ String ]: " + new String(data));
                                if (callback != null) {
                                    callback.onDataReceived(data);
                                }
                            }

                            @Override
                            public void onDataSent(byte[] data) {
                                if (callback != null) {
                                    callback.onDataSent(data);
                                }
                            }
                        });
    }
    private String getStatusMessage(SerialStatus status) {
        switch (status) {
            case SUCCESS_OPENED:
                return "串口打开成功";
            case NO_READ_WRITE_PERMISSION:
                return "没有读写权限";
            case OPEN_FAIL:
                return "串口打开失败";
            default:
                return "未知错误";
        }
    }


    /**
     * 发送数据 - 对应原Activity中的onSend方法
     */
    public boolean sendData(String data) {
        if (data == null || data.trim().isEmpty()) {
            Log.w("SerialPort", "sendData: 发送内容为空");
            return false;
        }

        byte[] dataBytes = data.getBytes();
        boolean result = SimpleSerialPortManager.getInstance().sendData(dataBytes);

        Log.i("SerialPort", "sendData: 发送结果 = " + result);

        // 通知回调数据已发送
        if (callback != null) {
            callback.onDataSent(dataBytes);
        }

        return result;
    }

    //关闭当前打开的串口
    public void closeSerialPort() {
        //调用SimpleSerialPortManager.getInstance().closeSerialPort()关闭串口
        SimpleSerialPortManager.getInstance().closeSerialPort();
        //将mOpened设置为false，并调用updateViewState(mOpened)更新UI
        mOpened = false;
    }

    /**
     * 字节数组转16进制
     *
     * @param bytes 需要转换的byte数组
     * @return 转换后的Hex字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}

