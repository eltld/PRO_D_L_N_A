package com.wireme.util;

public class RecommendHolder {
    private String name;
    private String version;
    private String desc;
    private String downUrl;
    private String icon;
    
    
    public RecommendHolder(String name, String version, String desc, String downUrl,String icon) {
        this.name = name;
        this.version = version;
        this.desc = desc;
        this.downUrl = downUrl;
        this.icon=icon;
    }
    
    
    @Override
    public String toString() {
        return "RecommendHolder [name=" + name + ", version=" + version + ", desc=" + desc + ", downUrl=" + downUrl + ", icon=" + icon + "]";
    }


    public String getIcon() {
        return icon;
    }


    public void setIcon(String icon) {
        this.icon = icon;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getDownUrl() {
        return downUrl;
    }
    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }
    
}
