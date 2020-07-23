package com.netease.nim.uikit.business.session.actions;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.business.session.activity.LocationActivity;
import com.netease.nim.uikit.business.session.fragment.MessageFragment;

/**
 * Created by hzxuwen on 2015/6/12.
 */
public class LocationAction extends BaseAction {
    private final static String TAG = "LocationAction";
    private MessageFragment fragment;

    public LocationAction() {
        super(R.drawable.nim_message_plus_location_selector, R.string.input_panel_location);
    }

    public LocationAction(MessageFragment fragment) {
        super(R.drawable.nim_message_plus_location_selector, R.string.input_panel_location);
        this.fragment = fragment;
    }

    @Override
    public void onClick() {

        LocationActivity.activityStartForResult(fragment);

//        if (NimUIKitImpl.getLocationProvider() != null) {
//            NimUIKitImpl.getLocationProvider().requestLocation(getActivity(), new LocationProvider.Callback() {
//                @Override
//                public void onSuccess(double longitude, double latitude, String address) {
//                    IMMessage message = MessageBuilder.createLocationMessage(getAccount(), getSessionType(), latitude, longitude,
//                            address);
//                    sendMessage(message);
//                }
//            });
//        }
    }

}
