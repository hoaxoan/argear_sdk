import 'dart:async';

import 'package:flutter/services.dart';

class ArgearSdk {
  static const MethodChannel _channel = const MethodChannel('argear_sdk');
  static const EventChannel _eventChannel = const EventChannel('argear_sdk_event_channel');

  static StreamSubscription<dynamic> _sink;

  // Core Events
  /// Reports a warning during SDK runtime.
  ///
  /// In most cases, the app can ignore the warning reported by the SDK because the SDK can usually fix the issue and resume running.
  static void Function(int warn) onWarning;

  /// Reports an error during SDK runtime.
  ///
  /// In most cases, the SDK cannot fix the issue and resume running. The SDK requires the app to take action or informs the user about the issue.
  static void Function(dynamic err) onError;

  // Core Methods
  /// Creates an ARGear instance.
  static Future<void> create(String apiUrl, String apiKey, String secretKey, String authKey) async {
    await _channel.invokeMethod('create', {'apiUrl': apiUrl, 'apiKey' : apiKey, 'secretKey' : secretKey, 'authKey' : authKey});
  }

  /// Destroy
  static Future<void> resume() async {
    await _channel.invokeMethod('resume');
    _addEventChannelHandler();
  }

  /// Destroy
  static Future<void> destroy() async {
    await _removeEventChannelHandler();
    await _channel.invokeMethod('destroy');
  }

  /// Destroy
  static Future<void> pause() async {
    await _channel.invokeMethod('pause');
    _addEventChannelHandler();
  }

  // setParameters
  static Future<int> setParameters(String params) async {
    final int res =
    await _channel.invokeMethod('setParameters', {"params": params});
    return res;
  }

  // getParameters
  static Future<String> getParameters(String params, String args) async {
    final String res = await _channel
        .invokeMethod('getParameters', {"params": params, "args": args});
    return res;
  }

  static void _addEventChannelHandler() async {
    _sink = _eventChannel
        .receiveBroadcastStream()
        .listen(_eventListener, onError: onError);
  }

  static void _removeEventChannelHandler() async {
    await _sink.cancel();
  }

  // CallHandler
  static void _eventListener(dynamic event) {
    final Map<dynamic, dynamic> map = event;
    switch (map['event']) {
    // Core Events
      case 'onWarning':
        if (onWarning != null) {
          onWarning(map['errorCode']);
        }
        break;
      case 'onError':
        if (onError != null) {
          onError(map['errorCode']);
        }
        break;
      default:
    }
  }

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

}
