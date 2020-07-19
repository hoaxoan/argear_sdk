#import "ArgearSdkPlugin.h"
#if __has_include(<argear_sdk/argear_sdk-Swift.h>)
#import <argear_sdk/argear_sdk-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "argear_sdk-Swift.h"
#endif

@implementation ArgearSdkPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftArgearSdkPlugin registerWithRegistrar:registrar];
}
@end
