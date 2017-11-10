package zookeeper.master;

import java.io.Serializable;

/**
 * Created by neil on 2017/11/8.
 */
public class RunningData implements Serializable{

    //服务器id
    private long cid;
    //服务器名称
    private String name;

    public long getCid() {
        return cid;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
