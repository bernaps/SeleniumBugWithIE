/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.notimportant.seleniumbugwithie;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

/**
 * Bug in Selenium Remote Driver: if we register a class extending
 * AbstractWebDriverEventListener and registering it with the WebDriver we get a
 *
 * ClassCastException when accessing any web page.
 *
 * java.lang.ClassCastException:
 * com.google.common.collect.Maps$TransformedEntriesMap cannot be cast to
 * java.util.List at
 * org.openqa.selenium.remote.RemoteLogs.getRemoteEntries(RemoteLogs.java:83) at
 * org.openqa.selenium.remote.RemoteLogs.get(RemoteLogs.java:77) at
 * com.notimportant.seleniumbugwithie.RunningEventListener.getBrowserLogs(RunningEventListener.java:91)
 * at
 * com.notimportant.seleniumbugwithie.RunningEventListener.beforeNavigateTo(RunningEventListener.java:96)
 *
 *
 *
 * Start IE Web driver version 3.5.0.0 64 Bit (Win7) Download from here
 * http://selenium-release.storage.googleapis.com/index.html
 *
 * IEDriverServer.exe /port=6666
 *
 * //Start Maven Build // mvn -Dexec.args="-classpath %classpath
 * com.notimportant.seleniumbugwithie.ShowBug" -Dexec.executable=java
 * process-classes org.codehaus.mojo:exec-maven-plugin:1.2.1:exec
 *
 *
 * @author bguillot
 */
public class ShowBug {

    public void runit() throws MalformedURLException {
        // IE Remote Web driver

        URL wdLocation = new URL("http://127.0.0.1:6666");

        WebDriver wd = null;
        EventFiringWebDriver driver = null;

        DesiredCapabilities cap;

        cap = DesiredCapabilities.internetExplorer();
        wd = new RemoteWebDriver(wdLocation, cap);

        wd.manage().window().setSize(new Dimension(1024, 800));

        wd.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS); // Element find timeout
        wd.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS); // Page load timeout Fail

        try {
            // BUG 2 Cannot set PageLoadTimeout with IE Capabilities
            // Does not work for IE ????!?!?!
            wd.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS); // Page load timeout Fail
            /**
             * Get Exception
             *
             * org.openqa.selenium.InvalidArgumentException: Invalid timeout
             * type specified: page load
             *
             * Build info: version: '3.4.0', revision: 'unknown', time:
             * 'unknown' System info: host: 'BGVPN', ip: '10.17.53.130',
             * os.name: 'Windows 7', os.arch: 'amd64', os.version: '6.1',
             * java.version: '1.8.0_144'
             *
             * Driver info: org.openqa.selenium.remote.RemoteWebDriver
             * Capabilities [{acceptInsecureCerts=false, browserVersion=11,
             * se:ieOptions={nativeEvents=true, browserAttachTimeout=0.0,
             * ie.ensureCleanSession=false, elementScrollBehavior=0.0,
             * enablePersistentHover=true, ie.browserCommandLineSwitches=,
             * ie.forceCreateProcessApi=false, requireWindowFocus=false,
             * initialBrowserUrl=http://localhost:6666/,
             * ignoreZoomSetting=false, ie.fileUploadDialogTimeout=3000.0,
             * ignoreProtectedModeSettings=false}, browserName=internet
             * explorer, pageLoadStrategy=normal,
             * unhandledPromptBehavior=dismiss, javascriptEnabled=true,
             * platformName=windows, setWindowRect=true, platform=ANY}]
             */
        } catch (Throwable t) {
            System.err.println();
            System.err.println("BUG 2-Error Setting Page timeout for Remote IE Selenium");
            t.printStackTrace(System.err);
        }
        driver = new EventFiringWebDriver(wd);

        RunningEventListener eventListener = new RunningEventListener(); // Just extends AbstractWebDriverEventListener
        driver.register(eventListener); // This Registered event Listener is what causes the issue in the driver.

        try {
            //wd is simple WebDriver
            wd.get("http://google.com"); // Works

            //driver is EventFiringWebDriver with a registered Event Handler (Causing the issue)
            driver.get("http://google.com"); // Fails.
        } catch (Throwable t) {
            System.err.println();
            System.err.println("MAIN BUG - Error with IE Web Driver");
            t.getMessage();
            t.printStackTrace(System.err);
            /**
             * Will get following ClassCastException when calling
             * driver.get("http://google.com"); // Fails.
             *
             * java.lang.ClassCastException:
             * com.google.common.collect.Maps$TransformedEntriesMap cannot be
             * cast to java.util.List at
             * org.openqa.selenium.remote.RemoteLogs.getRemoteEntries(RemoteLogs.java:83)
             * at org.openqa.selenium.remote.RemoteLogs.get(RemoteLogs.java:77)
             * at
             * com.notimportant.seleniumbugwithie.RunningEventListener.getBrowserLogs(RunningEventListener.java:91)
             * at
             * com.notimportant.seleniumbugwithie.RunningEventListener.beforeNavigateTo(RunningEventListener.java:96)
             * at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) at
             * sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
             * at
             * sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
             * at java.lang.reflect.Method.invoke(Method.java:498) at
             * org.openqa.selenium.support.events.EventFiringWebDriver$1.invoke(EventFiringWebDriver.java:81)
             * at com.sun.proxy.$Proxy0.beforeNavigateTo(Unknown Source) at
             * org.openqa.selenium.support.events.EventFiringWebDriver.get(EventFiringWebDriver.java:162)
             * at
             * com.notimportant.seleniumbugwithie.ShowBug.runit(ShowBug.java:72)
             * at
             * com.notimportant.seleniumbugwithie.ShowBug.main(ShowBug.java:82)
             *
             */
        }
    }

    public static void main(String[] args) throws MalformedURLException {
        ShowBug showBug = new ShowBug();
        showBug.runit();
    }
}
