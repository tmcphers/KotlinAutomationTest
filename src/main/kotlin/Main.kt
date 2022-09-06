
import Screenshot.Companion.takeScreenshot
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.remote.MobileCapabilityType
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.DesiredCapabilities
import java.net.URL
import kotlin.random.Random

fun main(args: Array<String>) {
    // Ideally we'd want to use a logger of some sort, but that's a bit beyond the scope of this project.
    println("Program arguments: ${args.joinToString()}")
    println("Starting Android test")
    androidTest()
    println("Android test complete")
}

fun androidTest() {
    // We can start the Appium server through code like this.  I think we should probably investigate that in the
    // future, but for now we can use the CLI to handle it for us.
    //  var appiumLocalService = AppiumServiceBuilder().usingAnyFreePort().build()
    //  appiumLocalService.start()

    val desiredCapabilities = DesiredCapabilities()
    desiredCapabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android")
    desiredCapabilities.setCapability(MobileCapabilityType.UDID, "emulator-5554")

    // UiAutomator2 is the specific Android driver we're using.  There are a bunch of drivers,
    // including these that are maintained by the Appium team.
    // https://github.com/appium/appium/tree/master/#drivers-maintained-by-the-appium-team
    desiredCapabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "UiAutomator2")

    // These are other possibly relevant desired capabilities that were included in example code I found.
    //  PLATFORM_VERSION, "7.1"
    //  BROWSER_NAME, "Chrome"
    //  DEVICE_NAME, "Android_Accelerated_x86_Oreo"

    // Parameters like the Appium server URL would probably be in a configuration file to make it easier to
    // switch between local and AWS testing.
    var url = URL("http://127.0.0.1:4723/")
    var driver = AndroidDriver(url, desiredCapabilities)
    var mobileDeviceTime = driver.executeScript("mobile: getDeviceTime")
    println("got mobile device time: $mobileDeviceTime")

    // We need to clear the existing instances of Reddit and Gmail so they're in the default state when we open them.
    driver.terminateApp("com.reddit.frontpage")
    driver.terminateApp("com.google.android.gm")

    // driver.activateApp is used in favor of the now deprecated driver.launchApp.  I'm not sure of the details,
    // but apparently driver.launchApp was confusing and error prone.
    driver.activateApp("com.reddit.frontpage")

    // I dislike hard sleeps like this.  Eventually I would want to have a robust waiting mechanism to handle the time
    // that it takes the app to load.  There might even be one in Appium already.  These fixed wait times will do
    // for now.
    Thread.sleep(3000)

    var frontPageItems = driver.findElement(By.xpath("//*[@resource-id='com.reddit.frontpage:id/link_list']"))
    takeScreenshot(frontPageItems, "feed items")

    // For reasons I can't determine, the xpath ./* seems to be returning the context node instead of the
    // children of the context node.  However, ./*/* correctly returns the children of the context node.
    // This seems like a defect, but if it isn't, it's either something I misunderstand about xpath or a substantial
    // difference between how browsers handle xpath and how Android, Appium, or UiAutomator2 handles xpath.
    var children = frontPageItems.findElements(By.xpath("./*/*"))
    for ((index, child) in children.withIndex()) {
        takeScreenshot(child, "child $index")
    }

    var firstItem = frontPageItems.findElement(By.xpath("./*/android.widget.LinearLayout"))

    var firstHeader = firstItem.findElement(By.id("com.reddit.frontpage:id/subscribe_header"))
    var firstPostSubredditField = firstHeader.findElement(By.id("com.reddit.frontpage:id/detail_subreddit_name"))
    var firstPostSubreddit = firstPostSubredditField.text
    println("got first post's subreddit $firstPostSubreddit")
    var firstPostUsernameField = firstHeader.findElement(
        By.xpath(".//android.widget.TextView[@resource-id='com.reddit.frontpage:id/bottom_row_metadata_before_indicators']")
    )
    var firstPostUsernameValue = firstPostUsernameField.text
    println("got first post's username $firstPostUsernameValue")

    var firstPostTitleField = firstItem.findElement(
        By.xpath(".//*[@resource-id='com.reddit.frontpage:id/link_title' or @resource-id='com.reddit.frontpage:id/title']")
    )
    var firstPostTitle = firstPostTitleField.text
    println("got first post's title $firstPostTitle")

    driver.activateApp("com.google.android.gm")
    Thread.sleep(3000)

    var composeButton = driver.findElement(By.id("com.google.android.gm:id/compose_button"))
    composeButton.click()
    Thread.sleep(3000)

    var missingFeatureOkay = driver.findElement(By.xpath("//android.widget.Button[@resource-id='android:id/button1']"))
    missingFeatureOkay.click()

    var composeToField = driver.findElement(By.id("com.google.android.gm:id/to"))
    var composeSubjectField = driver.findElement(By.id("com.google.android.gm:id/subject"))
    var composeEmailField = driver.findElement(
        By.xpath("//android.widget.LinearLayout[@resource-id='com.google.android.gm:id/wc_body_layout']//android.widget.EditText")
    )

    composeToField.sendKeys("hammerheadkotlinthings@gmail.com")

    var randomValue = randomString(4, ('0'..'9').toList())
    var subject = "This just in from Earth-$randomValue"

    composeSubjectField.sendKeys(subject)

    var message = "We bring you the latest news from Earth-$randomValue:\n\n" +
            "Current time is $mobileDeviceTime.  The weather is balmy and cool.  Trust us.\n" +
            "$firstPostSubreddit includes $firstPostTitle, as reported by someone who claims to be \"$firstPostUsernameValue\"\n"

    composeEmailField.sendKeys(message)

    var sendMessage = driver.findElement(By.id("com.google.android.gm:id/send"))
    sendMessage.click()
}

// I'm not using this printItemDetails method anymore, but it's pretty useful for getting some debug info.
// I would probably end up with a few debug methods like this.
fun printItemDetails(item:WebElement, name:String) {
    println("Stats for ${name}:")
    println("    tag name: ${item.tagName}")
    println("    resource ID: ${item.getAttribute("resource-id")}")
    println("    location: ${item.location}")
    println("    size: ${item.size}")
    println("    text: ${item.text}")
    println("Done printing stats for $name")
}

fun randomString(
        length: Int,
        validCharacters: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')): String {
    return (1..length)
        .map { _ -> Random.nextInt(0, validCharacters.size) }
        .map(validCharacters::get)
        .joinToString("")
}
