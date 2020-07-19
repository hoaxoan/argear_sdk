import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:argear_sdk/argear_sdk.dart';

void main() {
  const MethodChannel channel = MethodChannel('argear_sdk');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await ArgearSdk.platformVersion, '42');
  });
}
