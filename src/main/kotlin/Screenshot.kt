import org.apache.commons.io.FileUtils
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot
import java.io.File

class Screenshot {
    // the companion object lets us treat these methods and properties as though they're static methods
    companion object {
        var counter: Int = 0
            private set

        fun takeScreenshot(element: TakesScreenshot, name: String = "screenshot") {
            var scrn = element.getScreenshotAs(OutputType.FILE)
            var indexString = String.format("%05d", increment())

            // Ideally the filename would be sanitized before saving.  Kotlin/Java might take care of that for us,
            // but this is good for now.
            FileUtils.copyFile(scrn, File("screenshots/ss-$indexString-$name.png"))
        }

        fun increment() : Int {
            counter += 1
            return counter
        }
    }
}
