package com.jgainey.loggo.objs;

public class V2NozzleData {

    public String ca;
    public String crt;
    public String key;
    public String shardID;
    public String logsProvider;

    public String getLogsProvider() {
        return logsProvider;
    }

    public void setLogsProvider(String logsProvider) {
        this.logsProvider = logsProvider;
    }

    public String getCa() {
        return ca;
    }

    public void setCa(String ca) {
        this.ca = ca;
    }

    public String getCrt() {
        return crt;
    }

    public void setCrt(String crt) {
        this.crt = crt;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getShardID() {
        return shardID;
    }

    public void setShardID(String shardID) {
        this.shardID = shardID;
    }

    @Override
    public String toString() {
        return "V2NozzleData{" +
                "ca='" + ca + '\'' +
                ", crt='" + crt + '\'' +
                ", key='" + key + '\'' +
                ", shardID='" + shardID + '\'' +
                '}';
    }
}
