package com.brunjoy.dlna.UI;

import java.util.ArrayList;

import org.teleal.cling.support.model.DIDLObject;

import com.brunjoy.video.activity.DeviceItem;

public abstract class DLNAListener {
    /**
     * ==========================================<BR>
     * 功能： <BR>
     * 时间：2013-1-25 下午3:49:20 <BR>
     * 参数：
     * 
     * @param deviceList
     * @param isAddDevice
     *            true 增加了设备 false 减少了设备 <BR>
     *            ==========================================
     */
    public abstract void deviceUpdate(ArrayList<DeviceItem> deviceList, boolean isAddDevice);

    public void onInfomation(String msg) {
    };

    public void onError(String msg) {
    };

    public void ready() {
    };

    public void onPreparing() {
    }

    public void updateContent(ArrayList<DIDLObject> containerList) {
        // TODO Auto-generated method stub

    }

    public void updateBack(ArrayList<String> qurestParentsIDs) {
        
    };

}