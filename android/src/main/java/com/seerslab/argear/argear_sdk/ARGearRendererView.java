package com.seerslab.argear.argear_sdk;

import android.view.SurfaceView;
import android.view.View;

import io.flutter.plugin.platform.PlatformView;

public class ARGearRendererView implements PlatformView  {
  private final SurfaceView mSurfaceView;
  private final long uid;

  ARGearRendererView(SurfaceView surfaceView, int uid) {
    this.mSurfaceView = surfaceView;
    this.uid = uid;
  }

  @Override
  public View getView() {
    return mSurfaceView;
  }

  @Override
  public void dispose() {}
}