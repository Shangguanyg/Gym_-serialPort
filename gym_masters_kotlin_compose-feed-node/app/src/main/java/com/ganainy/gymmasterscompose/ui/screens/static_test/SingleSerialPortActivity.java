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

import java.util.Arrays;

/**
 * 单串口演示
 */
// 用于处理下拉选项的选择事件
//
public class SingleSerialPortActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_java);
        //调用verifyStoragePermissions(this)检查并请求外部存储的权限。
        verifyStoragePermissions(this);
        //调用initFragment()初始化日志fragment
        initFragment();
        //调用initDevice()初始化串口设备列表
        initDevice();
        //调用initSpinners()设置串口和波特率的下拉选择框
        initSpinners();

        //设置数据位
        //适配器创建：创建一个SpAdapter实例并将数据位数组databits设置到适配器中
        SpAdapter spAdapter1 = new SpAdapter(this);

        spAdapter1.setDatas(databits);
        binding.spDatabits.setAdapter(spAdapter1);
        binding.spDatabits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //选择监听：设置监听器来处理用户选择的数据位：
            //关闭串口：在选择变化时先关闭可能已打开的串口。
            //获取数据位值：根据选择的位索引取得数据位的值。
            //更新串口管理器：调用串口管理器实例的setDatabits()方法更新数据位。
            //日志记录：使用SerialPortLogUtil.i记录当前设置的数据位。
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                closeSerialPort();
                int dataBitValue = Integer.parseInt(databits[position]);
                SimpleSerialPortManager.getInstance().setDatabits(dataBitValue);
                SerialPortLogUtil.i("MainJavaActivity", "设置数据位: " + dataBitValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //设置校验位
        SpAdapter spAdapter2 = new SpAdapter(this);
        spAdapter2.setDatas(paritys);
        binding.spParity.setAdapter(spAdapter2);
        binding.spParity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                closeSerialPort();
                SimpleSerialPortManager.getInstance().setParity(position);
                SerialPortLogUtil.i("MainJavaActivity", "设置校验位: " + paritys[position] + " (值: " + position + ")");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //设置停止位
        SpAdapter spAdapter3 = new SpAdapter(this);
        spAdapter3.setDatas(stopbits);
        binding.spStopbits.setAdapter(spAdapter3);
        binding.spStopbits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                closeSerialPort();
                int stopBitValue = Integer.parseInt(stopbits[position]);
                SimpleSerialPortManager.getInstance().setStopbits(stopBitValue);
                SerialPortLogUtil.i("MainJavaActivity", "设置停止位: " + stopBitValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        打开/关闭按钮：为btnOpenDevice设置点击事件，根据串口状态选择打开或关闭串口。
        binding.btnOpenDevice.setOnClickListener(v -> {
            if (mOpened) {
                closeSerialPort();
            } else {
                openSerialPort();
            }
        });

//      发送数据按钮：为btnSendData设置点击事件，触发onSend()方法发送数据。
        binding.btnSendData.setOnClickListener((view) -> {
            onSend();
        });
    }
    
    /**
     * 打开串口
     */
//    方法定义：用于打开串口的主要逻辑。
//    获取串口路径和波特率：通过mDevice实例获取设备的名称和波特率（root）。
//    日志记录：记录打开的串口路径和波特率。
    private void openSerialPort() {
        String devicePath = mDevice.getName();
        int baudRate = Integer.parseInt(mDevice.getRoot());
        
        SerialPortLogUtil.i("MainJavaActivity", "打开的串口为：" + devicePath + "----" + baudRate);
        
        // 使用SimpleSerialPortManager打开串口
        // 通过调用SimpleSerialPortManager.getInstance().openSerialPort()来打开指定设备的串口
        boolean success = SimpleSerialPortManager.getInstance()
                .openSerialPort(devicePath, baudRate,
                        // 打开状态回调
                        // 状态回调：给定一个Lambda表达式以接收回调，表明串口打开是否成功。成功后更新UI状态，并根据状态通知用户。
                        (isSuccess, status) -> {
                            runOnUiThread(() -> {
                                switch (status) {
                                    case SUCCESS_OPENED:
                                        //弹出成功消息，更新mOpened为true，并调用updateViewState(true)更新视图状态
                                        ToastUtils.show("串口打开成功");
                                        mOpened = true;
                                        updateViewState(true);
                                        break;
                                    case NO_READ_WRITE_PERMISSION:
                                        //如果没有读写权限，弹出相应提示，更新状态为false
                                        ToastUtils.show("没有读写权限");
                                        updateViewState(false);
                                        break;
                                    case OPEN_FAIL:
                                        //显示打开失败的信息
                                        ToastUtils.show("串口打开失败");
                                        updateViewState(false);
                                        break;
                                }
                            });
                        },
                        // 数据接收回调
                        new SimpleSerialPortManager.OnDataReceivedCallback() {
                            @Override
                            //数据接收时：记录日志，并将接收到的数据转换为字符串或十六进制格式，使用LogManager发送相应信息。
                            public void onDataReceived(byte[] data) {
                                SerialPortLogUtil.i("MainJavaActivity", "onDataReceived [ byte[] ]: " + Arrays.toString(data));
                                SerialPortLogUtil.i("MainJavaActivity", "onDataReceived [ String ]: " + new String(data));
                                
                                runOnUiThread(() -> {
                                    if (mConversionNotice) {
                                        LogManager.instance().post(new RecvMessage(bytesToHex(data)));
                                    } else {
                                        LogManager.instance().post(new RecvMessage(Arrays.toString(data)));
                                    }
                                });
                            }
                            
                            @Override
                            //数据发送时：同样记录日志，数据发送成功后在UI上显示
                            public void onDataSent(byte[] data) {
                                SerialPortLogUtil.i("MainJavaActivity", "onDataSent [ byte[] ]: " + Arrays.toString(data));
                                SerialPortLogUtil.i("MainJavaActivity", "onDataSent [ String ]: " + new String(data));
                                
                                runOnUiThread(() -> {
                                    if (mConversionNotice) {
                                        LogManager.instance().post(new SendMessage(bytesToHex(data)));
                                    } else {
                                        LogManager.instance().post(new SendMessage(Arrays.toString(data)));
                                    }
                                });
                            }
                        });
    }

    //关闭当前打开的串口
    private void closeSerialPort() {
        //调用SimpleSerialPortManager.getInstance().closeSerialPort()关闭串口
        SimpleSerialPortManager.getInstance().closeSerialPort();
        //将mOpened设置为false，并调用updateViewState(mOpened)更新UI
        mOpened = false;
        updateViewState(mOpened);
    }

    // 用于检查并请求外部存储的访问权限
    //
    public static void verifyStoragePermissions(Activity activity) {
        try {
            SerialPortLogUtil.i("MainJavaActivity", "try verifyStoragePermissions");
            //检测是否有写的权限
            //使用ActivityCompat.checkSelfPermission检查是否具备写权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                // 若没有相关权限，则请求相应的权限，弹出对话框提示用户
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新视图状态
     *
     * @param isSerialPortOpened
     */
    //根据isSerialPortOpened的值设置按钮上的文字（关闭或打开串口）。

    //更新UI控件的状态，根据串口是否打开来调整控件是否可用
    private void updateViewState(boolean isSerialPortOpened) {
        //根据串口的打开状态启用或禁用设备选择器、波特率选择器、发送数据和加载列表的按钮
        int stringRes = isSerialPortOpened ? R.string.close_serial_port : R.string.open_serial_port;
        binding.btnOpenDevice.setText(stringRes);
        binding.spinnerDevices.setEnabled(!isSerialPortOpened);
        binding.spinnerBaudrate.setEnabled(!isSerialPortOpened);
        binding.btnSendData.setEnabled(isSerialPortOpened);
        binding.btnLoadList.setEnabled(isSerialPortOpened);
    }

    /**
     * 初始化设备列表
     */
    private void initDevice() {
        //使用PrefHelper初始化默认配置
        PrefHelper.initDefault(this);
        //通过SerialPortFinder获取所有可用串口路径
        SerialPortFinder serialPortFinder = new SerialPortFinder();
        // 设备
        mDevices = serialPortFinder.getAllDevicesPath();
        if (mDevices.length == 0) {
            mDevices = new String[]{
                    //提示找不到串口设备
                    getString(R.string.no_serial_device)
            };
        }
        // 波特率
        // 从资源中获取所有可用波特率的数组
        mBaudrates = getResources().getStringArray(R.array.baudrates);

        //从共享偏好中读取用户的上次选择的设备和波特率索引
        mDeviceIndex = PrefHelper.getDefault().getInt(PreferenceKeys.SERIAL_PORT_DEVICES, 0);
        mDeviceIndex = mDeviceIndex >= mDevices.length ? mDevices.length - 1 : mDeviceIndex;
        mBaudrateIndex = PrefHelper.getDefault().getInt(PreferenceKeys.BAUD_RATE, 0);

        mDevice = new Device(mDevices[mDeviceIndex], mBaudrates[mBaudrateIndex], null);
    }


    /**
     * 初始化下拉选项
     */
    //用于初始化设备选择器和波特率选择器的下拉菜单
    private void initSpinners() {
        //设备适配器创建：创建一个新的ArrayAdapter实例，作为设备选择器的数据适配器
        ArrayAdapter<String> deviceAdapter =
                new ArrayAdapter<String>(this, R.layout.spinner_default_item, mDevices);
        //指定下拉菜单的视图资源
        deviceAdapter.setDropDownViewResource(R.layout.spinner_item);
        //
        binding.spinnerDevices.setAdapter(deviceAdapter);
        binding.spinnerDevices.setOnItemSelectedListener(this);

        ArrayAdapter<String> baudrateAdapter =
                new ArrayAdapter<String>(this, R.layout.spinner_default_item, mBaudrates);
        baudrateAdapter.setDropDownViewResource(R.layout.spinner_item);
        binding.spinnerBaudrate.setAdapter(baudrateAdapter);
        binding.spinnerBaudrate.setOnItemSelectedListener(this);

        binding.spinnerDevices.setSelection(mDeviceIndex);
        binding.spinnerBaudrate.setSelection(mBaudrateIndex);
    }


    /**
     * 发送数据
     */
    public void onSend() {
        String sendContent = binding.etData.getText().toString().trim();
        if (TextUtils.isEmpty(sendContent)) {
            SerialPortLogUtil.i("MainJavaActivity", "onSend: 发送内容为 null");
            return;
        }
        byte[] sendContentBytes = sendContent.getBytes();
        // 使用SimpleSerialPortManager发送数据
        boolean sendBytes = SimpleSerialPortManager.getInstance().sendData(sendContentBytes);
        SerialPortLogUtil.i("MainJavaActivity", "onSend: sendBytes = " + sendBytes);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Spinner 选择监听
        int parentId = parent.getId();
        if (parentId == R.id.spinner_devices) {
            mDeviceIndex = position;
            mDevice.setName(mDevices[mDeviceIndex]);
        } else if (parentId == R.id.spinner_baudrate) {
            mBaudrateIndex = position;
            mDevice.setRoot(mBaudrates[mBaudrateIndex]);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onDestroy() {
        SimpleSerialPortManager.getInstance().closeSerialPort();
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshLogList();
    }

    /**
     * 初始化日志Fragment
     */
    protected void initFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        mLogFragment = (LogFragment) fragmentManager.findFragmentById(R.id.log_fragment);
    }


    /**
     * 刷新日志列表
     */
    protected void refreshLogList() {
        mLogFragment.updateAutoEndButton();
        mLogFragment.updateList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(IMessage message) {
        // 收到时间，刷新界面
        mLogFragment.add(message);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConversionNotice(ConversionNoticeEvent messageEvent) {
        if (messageEvent.getMessage().equals("1")) {
            mConversionNotice = false;
        } else {
            mConversionNotice = true;
        }

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