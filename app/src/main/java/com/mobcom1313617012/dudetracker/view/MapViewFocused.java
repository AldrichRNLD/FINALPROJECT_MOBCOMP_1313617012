package com.mobcom1313617012.dudetracker.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;

public class MapViewFocused extends MapView {

    public MapViewFocused(Context context) {
        super(context);
    }

    public MapViewFocused(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public MapViewFocused(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public MapViewFocused(Context context, GoogleMapOptions googleMapOptions) {
        super(context, googleMapOptions);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }
}
