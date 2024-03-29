package com.macbackpackers;

public class ModemConfigProperties {

    private String name;
    private String beanname;
    private MonitorConfig monitor;
    private RouterConfig router;

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getBeanname() {
        return beanname;
    }

    public void setBeanname( String beanname ) {
        this.beanname = beanname;
    }

    public MonitorConfig getMonitor() {
        return monitor;
    }

    public void setMonitor( MonitorConfig monitor ) {
        this.monitor = monitor;
    }

    public RouterConfig getRouter() {
        return router;
    }

    public void setRouter( RouterConfig router ) {
        this.router = router;
    }
}
