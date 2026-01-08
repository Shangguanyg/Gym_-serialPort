package com.cl.myapplication.databinding;
import com.cl.myapplication.R;
import com.cl.myapplication.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ActivityMultiSerialNewBindingImpl extends ActivityMultiSerialNewBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.btn_refresh_ports, 1);
        sViewsWithIds.put(R.id.spinner_databits, 2);
        sViewsWithIds.put(R.id.spinner_parity, 3);
        sViewsWithIds.put(R.id.spinner_stopbits, 4);
        sViewsWithIds.put(R.id.spinner_gps_device, 5);
        sViewsWithIds.put(R.id.spinner_gps_baudrate, 6);
        sViewsWithIds.put(R.id.btn_gps_toggle, 7);
        sViewsWithIds.put(R.id.spinner_sensor_device, 8);
        sViewsWithIds.put(R.id.spinner_sensor_baudrate, 9);
        sViewsWithIds.put(R.id.btn_sensor_toggle, 10);
        sViewsWithIds.put(R.id.spinner_modbus_device, 11);
        sViewsWithIds.put(R.id.spinner_modbus_baudrate, 12);
        sViewsWithIds.put(R.id.btn_modbus_toggle, 13);
        sViewsWithIds.put(R.id.spinner_custom_device, 14);
        sViewsWithIds.put(R.id.spinner_custom_baudrate, 15);
        sViewsWithIds.put(R.id.btn_custom_toggle, 16);
        sViewsWithIds.put(R.id.btn_open_all, 17);
        sViewsWithIds.put(R.id.btn_close_all, 18);
        sViewsWithIds.put(R.id.btn_send_test, 19);
        sViewsWithIds.put(R.id.btn_clear_log, 20);
        sViewsWithIds.put(R.id.tv_status, 21);
    }
    // views
    @NonNull
    private final android.widget.LinearLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ActivityMultiSerialNewBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 22, sIncludes, sViewsWithIds));
    }
    private ActivityMultiSerialNewBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.widget.Button) bindings[20]
            , (android.widget.Button) bindings[18]
            , (android.widget.Button) bindings[16]
            , (android.widget.Button) bindings[7]
            , (android.widget.Button) bindings[13]
            , (android.widget.Button) bindings[17]
            , (android.widget.Button) bindings[1]
            , (android.widget.Button) bindings[19]
            , (android.widget.Button) bindings[10]
            , (android.widget.Spinner) bindings[15]
            , (android.widget.Spinner) bindings[14]
            , (android.widget.Spinner) bindings[2]
            , (android.widget.Spinner) bindings[6]
            , (android.widget.Spinner) bindings[5]
            , (android.widget.Spinner) bindings[12]
            , (android.widget.Spinner) bindings[11]
            , (android.widget.Spinner) bindings[3]
            , (android.widget.Spinner) bindings[9]
            , (android.widget.Spinner) bindings[8]
            , (android.widget.Spinner) bindings[4]
            , (android.widget.TextView) bindings[21]
            );
        this.mboundView0 = (android.widget.LinearLayout) bindings[0];
        this.mboundView0.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x1L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
            return variableSet;
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        // batch finished
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): null
    flag mapping end*/
    //end
}