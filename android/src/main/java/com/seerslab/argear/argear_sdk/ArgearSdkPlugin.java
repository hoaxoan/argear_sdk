package com.seerslab.argear.argear_sdk;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import io.agora.rtc.RtcEngine;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.common.StandardMessageCodec;

import com.seerslab.argear.session.ARGContents;
import com.seerslab.argear.session.ARGMedia;
import com.seerslab.argear.session.ARGSession;
import com.seerslab.argear.session.config.ARGConfig;
import com.seerslab.argear.session.config.ARGInferenceConfig;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Set;

/** ArgearSdkPlugin */
public class ArgearSdkPlugin implements  FlutterPlugin, MethodCallHandler, EventChannel.StreamHandler, ActivityAware {
  private Context mContext;
  private MethodChannel channel;
  private Handler mEventHandler = new Handler(Looper.getMainLooper());
  private EventChannel.EventSink sink;

  private HashMap<String, SurfaceView> mRendererViews;

  private static ARGSession mARGSession;
  private static ARGMedia mARGMedia;

  void addView(SurfaceView view, int id) {
    mRendererViews.put("" + id, view);
  }

  private void removeView(int id) {
    mRendererViews.remove("" + id);
  }

  private SurfaceView getView(int id) {
    return mRendererViews.get("" + id);
  }

  public static ARGSession getARGSession() {
    return mARGSession;
  }

  /**
   * FlutterPlugin
   * @param flutterPluginBinding
   */
  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "argear_sdk");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  /**
   * Activity
   * @param binding
   */
  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    this.mContext = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {

  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {

  }

  @Override
  public void onDetachedFromActivity() {

  }

  /**
   * Stream Handler
   * @param arguments
   * @param events
   */
  @Override
  public void onListen(Object arguments, EventChannel.EventSink events) {
    this.sink = events;
  }

  @Override
  public void onCancel(Object arguments) {
    this.sink = null;
  }

  private void sendEvent(final String eventName, final HashMap map) {
    map.put("event", eventName);
    mEventHandler.post(new Runnable() {
      @Override
      public void run() {
        if (sink != null) {
          sink.success(map);
        }
      }
    });
  }

  /**
   * Plugin registration.
   */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "argear_sdk");
    final EventChannel eventChannel = new EventChannel(registrar.messenger(), "argear_sdk_event_channel");

    ArgearSdkPlugin plugin = new ArgearSdkPlugin();
    channel.setMethodCallHandler(plugin);
    eventChannel.setStreamHandler(plugin);

    ARGearRenderViewFactory fac = new ARGearRenderViewFactory(StandardMessageCodec.INSTANCE, plugin);
    registrar.platformViewRegistry().registerViewFactory("ARGearRendererView", fac);
  }

  public ArgearSdkPlugin() {
    this.sink = null;
    this.mRendererViews = new HashMap<>();
  }

  private Context getActiveContext() {
    return mContext;
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    Context context = getActiveContext();
    switch (call.method) {
      // Core Method
      case "create": {
        try {
          String apiUrl = call.argument("apiUrl");
          String apiKey = call.argument("apiKey");
          String secretKey = call.argument("secretKey");
          String authKey = call.argument("appId");
          ARGConfig config = new ARGConfig(apiUrl, apiKey, secretKey, authKey);
          Set<ARGInferenceConfig.Feature> inferenceConfig = EnumSet.of(ARGInferenceConfig.Feature.FACE_HIGH_TRACKING);
          if (context != null){
            mARGSession = new ARGSession(context, config, inferenceConfig);
          }
          //mARGMedia = new ARGMedia(mARGSession);
          result.success(null);
        } catch (Exception e) {
          throw new RuntimeException("NEED TO check argear sdk init fatal error\n");
        }
      }
      break;
      case "destroy": {
        getARGSession().destroy();
        result.success(null);
      }
      break;
      case "resume": {
        getARGSession().resume();
        result.success(null);
      }
      break;
      case "pause": {
        getARGSession().pause();
        result.success(null);
      }
      break;
      case "clearBulge": {
        getARGSession().contents().clear(ARGContents.Type.Bulge);
        result.success(null);
      }
      break;
      case "clearStickers": {
        getARGSession().contents().clear(ARGContents.Type.ARGItem);
        result.success(null);
      }
      break;
      case "clearFilter": {
        getARGSession().contents().clear(ARGContents.Type.FilterItem);
        result.success(null);
      }
      break;
      case "getVersion": {
        getARGSession().getVersion();
        result.success(null);
      }
      break;
      case "getPlatformVersion": {
        try {
          result.success("Android " + android.os.Build.VERSION.RELEASE);
        } catch (Exception e) {
          throw new RuntimeException("NEED TO check rtc sdk init fatal error\n");
        }
      }
      break;
      default:
        result.notImplemented();
        break;
    }
  }
}
