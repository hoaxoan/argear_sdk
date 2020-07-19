package com.seerslab.argear.argear_sdk;

import android.content.Context;
import android.view.SurfaceView;

import io.agora.rtc.RtcEngine;
import io.flutter.plugin.common.MessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class ARGearRenderViewFactory extends PlatformViewFactory {
  private final ArgearSdkPlugin mEnginePlugin;

  public ARGearRenderViewFactory(MessageCodec<Object> createArgsCodec, ArgearSdkPlugin enginePlugin) {
    super(createArgsCodec);
    this.mEnginePlugin = enginePlugin;
  }

  @Override
  public PlatformView create(Context context, int id, Object o) {
    SurfaceView view = RtcEngine.CreateRendererView(context.getApplicationContext());
    ARGearRendererView rendererView = new ARGearRendererView(view, id);
    mEnginePlugin.addView(view, id);
    return rendererView;
  }
}