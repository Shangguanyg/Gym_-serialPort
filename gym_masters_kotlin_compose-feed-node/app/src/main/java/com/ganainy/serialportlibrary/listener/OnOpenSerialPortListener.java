package com.ganainy.serialportlibrary.listener;

import com.ganainy.serialportlibrary.enumerate.SerialPortEnum;
import com.ganainy.serialportlibrary.enumerate.SerialStatus;

import java.io.File;

/**
 * 打开串口监听
 */
public interface OnOpenSerialPortListener {

    void openState(SerialPortEnum serialPortEnum, File device, SerialStatus status);

}
