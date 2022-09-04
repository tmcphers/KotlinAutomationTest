//package model.reddit

import io.appium.java_client.service.local

fun main(args: Array<String>) {
    println("Hello World!")

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Program arguments: ${args.joinToString()}")
//    testfunc()
    println("Done running things")
}

//fun testfunc() {
//    var appiumLocalService = AppiumServiceBuilder().UsingAnyFreePort().Build()
//    appiumLocalService.Start();
//
//    val appiumOptions = AppiumOptions()
////    appiumOptions.AddAdditionalCapability(MobileCapabilityType.DeviceName, "Android_Accelerated_x86_Oreo")
//    appiumOptions.AddAdditionalCapability(MobileCapabilityType.PlatformName, "Android")
////    appiumOptions.AddAdditionalCapability(MobileCapabilityType.PlatformVersion, "7.1")
////    appiumOptions.AddAdditionalCapability(MobileCapabilityType.BrowserName, "Chrome")
//
//    var driver = AndroidDriver<AppiumWebElement>(_appiumLocalService, appiumOptions)
//    driver.CloseApp()
//}