package com.macbackpackers;

public class MonitorConfig {
    private String command;
    private int periodSeconds;
    private String ipRegex;
    private String ipRegexMatch;
    private int maxFailuresBeforeReboot;

    public String getCommand() {
        return command;
    }

    public void setCommand( String command ) {
        this.command = command;
    }

    public int getPeriodSeconds() {
        return periodSeconds;
    }

    public void setPeriodSeconds( int periodSeconds ) {
        this.periodSeconds = periodSeconds;
    }

    public String getIpRegex() {
        return ipRegex;
    }

    public void setIpRegex( String ipRegex ) {
        this.ipRegex = ipRegex;
    }

    public String getIpRegexMatch() {
        return ipRegexMatch;
    }

    public void setIpRegexMatch( String ipRegexMatch ) {
        this.ipRegexMatch = ipRegexMatch;
    }

    public int getMaxFailuresBeforeReboot() {
        return maxFailuresBeforeReboot;
    }

    public void setMaxFailuresBeforeReboot( int maxFailuresBeforeReboot ) {
        this.maxFailuresBeforeReboot = maxFailuresBeforeReboot;
    }
}
