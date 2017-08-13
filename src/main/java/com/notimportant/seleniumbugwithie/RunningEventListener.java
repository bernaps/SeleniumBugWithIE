/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.notimportant.seleniumbugwithie;

import java.util.logging.Level;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;

/**
 *
 * @author bguillot
 */
public class RunningEventListener extends AbstractWebDriverEventListener {

    boolean isJSErrorFound;

//    @Override
//    public void onException(Throwable throwable, WebDriver driver) {
//        TestLogging.log("*** 2 Test Exception at URL: "
//                + driver.getCurrentUrl() + "\r\n"
//                + getErrorMessage(throwable, 1),
//                true);
//        System.err.println("Exception is : " + throwable.getClass().getName());
//        throwable.printStackTrace(System.err);
//    }
    /**
     *
     * @param t
     * @param indentNum Indent level
     * @return
     */
    public String getErrorMessage(Throwable t, int indentNum) {
        if (indentNum > 2) {
            return "";
        }
        String results = "";
        StringBuffer indent = new StringBuffer("");
        for (int i = 0; i < indentNum; i++) {
            indent = indent.append("    ");
        }
        results = "\r\n"
                + indent + "Exception: " + t.getClass().getName() + "\r\n"
                + indent + "Message:" + t.getMessage() + "\r\n"
                + (t.getCause() != null ? indent + "Caused by: " + getErrorMessage(t.getCause(), indentNum + 1) + "\r\n" : "");

        return results;
    }

    private void logErrors(String url, LogEntries logEntries) {
        if (logEntries.getAll().isEmpty()) {
            System.out.println("********* No Severe Error on Browser Console *********");
        } else {
            for (LogEntry logEntry : logEntries) {
                if (logEntry.getLevel().equals(Level.SEVERE)) {
                    System.out.println("URL: " + url);
                    System.out.println("Time stamp: " + logEntry.getTimestamp() + ", "
                            + "Log level: " + logEntry
                                    .getLevel() + ", Log message: " + logEntry.getMessage());
                    isJSErrorFound = true;
                }
            }
            assert !isJSErrorFound;
        }
    }

    private void logErrors(String event, WebElement element, LogEntries logEntries) {
        if (logEntries.getAll().isEmpty()) {
            System.out.println("********* No Severe Error on Browser Console *********");
        } else {
            for (LogEntry logEntry : logEntries) {
                if (logEntry.getLevel().equals(Level.SEVERE)) {
                    System.out.println("Time stamp: "
                            + logEntry.getTimestamp() + ", "
                            + "Log level: " + logEntry.getLevel()
                            + ", Log message: " + logEntry.getMessage());
                    isJSErrorFound = true;
                }
            }
            assert !isJSErrorFound;
        }
    }

    private LogEntries getBrowserLogs(WebDriver webDriver) {
        return webDriver.manage().logs().get(LogType.BROWSER);
    }

    @Override
    public void beforeNavigateTo(String url, WebDriver webDriver) {
        logErrors(url, getBrowserLogs(webDriver));
    }

    @Override
    public void afterNavigateTo(String url, WebDriver webDriver) {
        logErrors(url, getBrowserLogs(webDriver));
        // Let us know what is the new windowsHandler
//        callback.popupHandler();
    }

    @Override
    public void beforeClickOn(WebElement element, WebDriver driver) {
        logErrors("before", element, getBrowserLogs(driver));
        // Let us know what is the new windowsHandler
//        callback.popupHandler();
    }

    @Override
    public void afterClickOn(WebElement element, WebDriver driver) {
        logErrors("after", element, getBrowserLogs(driver));
    }
}
