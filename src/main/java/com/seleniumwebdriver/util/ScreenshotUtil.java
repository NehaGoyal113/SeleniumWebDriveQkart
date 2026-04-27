package com.seleniumwebdriver.util;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;

public class ScreenshotUtil {

    public static void takeScreenshot(WebDriver driver, String type, String description) {
        try {
            File dir = new File("screenshots");
            if (!dir.exists()) dir.mkdirs();

            String timestamp = String.valueOf(java.time.LocalDateTime.now());
            String fileName  = String.format("screenshot_%s_%s_%s.png", timestamp, type, description);

            File src  = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File dest = new File("screenshots/" + fileName);
            FileUtils.copyFile(src, dest);

        } catch (Exception e) {
            System.out.println("Screenshot failed: " + e.getMessage());
        }
    }
}