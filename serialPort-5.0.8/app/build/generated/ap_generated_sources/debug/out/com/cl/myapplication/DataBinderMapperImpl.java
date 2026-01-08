package com.cl.myapplication;

import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import androidx.databinding.DataBinderMapper;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.ViewDataBinding;
import com.cl.myapplication.databinding.ActivityMainBindingImpl;
import com.cl.myapplication.databinding.ActivityMainJavaBindingImpl;
import com.cl.myapplication.databinding.ActivityMultiSerialBindingImpl;
import com.cl.myapplication.databinding.ActivityMultiSerialNewBindingImpl;
import com.cl.myapplication.databinding.ActivityPortMainBindingImpl;
import com.cl.myapplication.databinding.ActivitySelectSerialPortBindingImpl;
import com.cl.myapplication.databinding.FragmentLogBindingImpl;
import com.cl.myapplication.databinding.ItemDeviceBindingImpl;
import com.cl.myapplication.databinding.ItemLogBindingImpl;
import com.cl.myapplication.databinding.SpinnerDefaultItemBindingImpl;
import com.cl.myapplication.databinding.SpinnerItemBindingImpl;
import java.lang.IllegalArgumentException;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.RuntimeException;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataBinderMapperImpl extends DataBinderMapper {
  private static final int LAYOUT_ACTIVITYMAIN = 1;

  private static final int LAYOUT_ACTIVITYMAINJAVA = 2;

  private static final int LAYOUT_ACTIVITYMULTISERIAL = 3;

  private static final int LAYOUT_ACTIVITYMULTISERIALNEW = 4;

  private static final int LAYOUT_ACTIVITYPORTMAIN = 5;

  private static final int LAYOUT_ACTIVITYSELECTSERIALPORT = 6;

  private static final int LAYOUT_FRAGMENTLOG = 7;

  private static final int LAYOUT_ITEMDEVICE = 8;

  private static final int LAYOUT_ITEMLOG = 9;

  private static final int LAYOUT_SPINNERDEFAULTITEM = 10;

  private static final int LAYOUT_SPINNERITEM = 11;

  private static final SparseIntArray INTERNAL_LAYOUT_ID_LOOKUP = new SparseIntArray(11);

  static {
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.cl.myapplication.R.layout.activity_main, LAYOUT_ACTIVITYMAIN);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.cl.myapplication.R.layout.activity_main_java, LAYOUT_ACTIVITYMAINJAVA);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.cl.myapplication.R.layout.activity_multi_serial, LAYOUT_ACTIVITYMULTISERIAL);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.cl.myapplication.R.layout.activity_multi_serial_new, LAYOUT_ACTIVITYMULTISERIALNEW);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.cl.myapplication.R.layout.activity_port_main, LAYOUT_ACTIVITYPORTMAIN);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.cl.myapplication.R.layout.activity_select_serial_port, LAYOUT_ACTIVITYSELECTSERIALPORT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.cl.myapplication.R.layout.fragment_log, LAYOUT_FRAGMENTLOG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.cl.myapplication.R.layout.item_device, LAYOUT_ITEMDEVICE);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.cl.myapplication.R.layout.item_log, LAYOUT_ITEMLOG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.cl.myapplication.R.layout.spinner_default_item, LAYOUT_SPINNERDEFAULTITEM);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.cl.myapplication.R.layout.spinner_item, LAYOUT_SPINNERITEM);
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View view, int layoutId) {
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = view.getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
        case  LAYOUT_ACTIVITYMAIN: {
          if ("layout/activity_main_0".equals(tag)) {
            return new ActivityMainBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_main is invalid. Received: " + tag);
        }
        case  LAYOUT_ACTIVITYMAINJAVA: {
          if ("layout/activity_main_java_0".equals(tag)) {
            return new ActivityMainJavaBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_main_java is invalid. Received: " + tag);
        }
        case  LAYOUT_ACTIVITYMULTISERIAL: {
          if ("layout/activity_multi_serial_0".equals(tag)) {
            return new ActivityMultiSerialBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_multi_serial is invalid. Received: " + tag);
        }
        case  LAYOUT_ACTIVITYMULTISERIALNEW: {
          if ("layout/activity_multi_serial_new_0".equals(tag)) {
            return new ActivityMultiSerialNewBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_multi_serial_new is invalid. Received: " + tag);
        }
        case  LAYOUT_ACTIVITYPORTMAIN: {
          if ("layout/activity_port_main_0".equals(tag)) {
            return new ActivityPortMainBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_port_main is invalid. Received: " + tag);
        }
        case  LAYOUT_ACTIVITYSELECTSERIALPORT: {
          if ("layout/activity_select_serial_port_0".equals(tag)) {
            return new ActivitySelectSerialPortBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_select_serial_port is invalid. Received: " + tag);
        }
        case  LAYOUT_FRAGMENTLOG: {
          if ("layout/fragment_log_0".equals(tag)) {
            return new FragmentLogBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for fragment_log is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMDEVICE: {
          if ("layout/item_device_0".equals(tag)) {
            return new ItemDeviceBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_device is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMLOG: {
          if ("layout/item_log_0".equals(tag)) {
            return new ItemLogBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_log is invalid. Received: " + tag);
        }
        case  LAYOUT_SPINNERDEFAULTITEM: {
          if ("layout/spinner_default_item_0".equals(tag)) {
            return new SpinnerDefaultItemBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for spinner_default_item is invalid. Received: " + tag);
        }
        case  LAYOUT_SPINNERITEM: {
          if ("layout/spinner_item_0".equals(tag)) {
            return new SpinnerItemBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for spinner_item is invalid. Received: " + tag);
        }
      }
    }
    return null;
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View[] views, int layoutId) {
    if(views == null || views.length == 0) {
      return null;
    }
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = views[0].getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
      }
    }
    return null;
  }

  @Override
  public int getLayoutId(String tag) {
    if (tag == null) {
      return 0;
    }
    Integer tmpVal = InnerLayoutIdLookup.sKeys.get(tag);
    return tmpVal == null ? 0 : tmpVal;
  }

  @Override
  public String convertBrIdToString(int localId) {
    String tmpVal = InnerBrLookup.sKeys.get(localId);
    return tmpVal;
  }

  @Override
  public List<DataBinderMapper> collectDependencies() {
    ArrayList<DataBinderMapper> result = new ArrayList<DataBinderMapper>(1);
    result.add(new androidx.databinding.library.baseAdapters.DataBinderMapperImpl());
    return result;
  }

  private static class InnerBrLookup {
    static final SparseArray<String> sKeys = new SparseArray<String>(1);

    static {
      sKeys.put(0, "_all");
    }
  }

  private static class InnerLayoutIdLookup {
    static final HashMap<String, Integer> sKeys = new HashMap<String, Integer>(11);

    static {
      sKeys.put("layout/activity_main_0", com.cl.myapplication.R.layout.activity_main);
      sKeys.put("layout/activity_main_java_0", com.cl.myapplication.R.layout.activity_main_java);
      sKeys.put("layout/activity_multi_serial_0", com.cl.myapplication.R.layout.activity_multi_serial);
      sKeys.put("layout/activity_multi_serial_new_0", com.cl.myapplication.R.layout.activity_multi_serial_new);
      sKeys.put("layout/activity_port_main_0", com.cl.myapplication.R.layout.activity_port_main);
      sKeys.put("layout/activity_select_serial_port_0", com.cl.myapplication.R.layout.activity_select_serial_port);
      sKeys.put("layout/fragment_log_0", com.cl.myapplication.R.layout.fragment_log);
      sKeys.put("layout/item_device_0", com.cl.myapplication.R.layout.item_device);
      sKeys.put("layout/item_log_0", com.cl.myapplication.R.layout.item_log);
      sKeys.put("layout/spinner_default_item_0", com.cl.myapplication.R.layout.spinner_default_item);
      sKeys.put("layout/spinner_item_0", com.cl.myapplication.R.layout.spinner_item);
    }
  }
}
