package com.cl.myapplication.databinding;
import com.cl.myapplication.R;
import com.cl.myapplication.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ActivityMainJavaBindingImpl extends ActivityMainJavaBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.spinner_devices, 2);
        sViewsWithIds.put(R.id.spinner_baudrate, 3);
        sViewsWithIds.put(R.id.btn_open_device, 4);
        sViewsWithIds.put(R.id.et_data, 5);
        sViewsWithIds.put(R.id.btn_send_data, 6);
        sViewsWithIds.put(R.id.btn_load_list, 7);
        sViewsWithIds.put(R.id.tv_databits, 8);
        sViewsWithIds.put(R.id.sp_databits, 9);
        sViewsWithIds.put(R.id.tv_parity, 10);
        sViewsWithIds.put(R.id.sp_parity, 11);
        sViewsWithIds.put(R.id.tv_stopbits, 12);
        sViewsWithIds.put(R.id.sp_stopbits, 13);
    }
    // views
    @NonNull
    private final android.widget.LinearLayout mboundView0;
    @NonNull
    private final android.widget.LinearLayout mboundView1;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ActivityMainJavaBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 14, sIncludes, sViewsWithIds));
    }
    private ActivityMainJavaBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.widget.Button) bindings[7]
            , (android.widget.Button) bindings[4]
            , (android.widget.Button) bindings[6]
            , (android.widget.EditText) bindings[5]
            , (android.widget.Spinner) bindings[9]
            , (android.widget.Spinner) bindings[11]
            , (android.widget.Spinner) bindings[13]
            , (android.widget.Spinner) bindings[3]
            , (android.widget.Spinner) bindings[2]
            , (android.widget.TextView) bindings[8]
            , (android.widget.TextView) bindings[10]
            , (android.widget.TextView) bindings[12]
            );
        this.mboundView0 = (android.widget.LinearLayout) bindings[0];
        this.mboundView0.setTag(null);
        this.mboundView1 = (android.widget.LinearLayout) bindings[1];
        this.mboundView1.setTag(null);
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