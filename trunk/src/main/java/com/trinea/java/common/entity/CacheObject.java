package com.trinea.java.common.entity;

import java.io.Serializable;

import com.trinea.java.common.ObjectUtils;

/**
 * 缓存中的数据
 * 
 * @author Trinea 2011-12-23 上午01:27:06
 */
public class CacheObject<V> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 对象进入缓存时间 **/
    protected long            enterTime;
    /** 对象上次使用时间， 即上次被get的时间 **/
    protected long            lastUsedTime;
    /** 对象使用次数， get一次表示被使用一次 **/
    protected long            usedCount;
    /** 对象优先级 **/
    protected int             priority;

    /** 对象是否已经过期 **/
    protected boolean         isExpired;
    /** 对象是否永不过期 **/
    protected boolean         isForever;

    /** 对象数据 **/
    protected V               data;

    public CacheObject(){
        this.enterTime = System.currentTimeMillis();
        this.lastUsedTime = System.currentTimeMillis();
        this.usedCount = 0;
        this.priority = 0;
        this.isExpired = false;
        this.isForever = true;
    }

    public long getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(long enterTime) {
        this.enterTime = enterTime;
    }

    public long getLastUsedTime() {
        return lastUsedTime;
    }

    public void setLastUsedTime(long lastUsedTime) {
        this.lastUsedTime = lastUsedTime;
    }

    public long getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(long usedCount) {
        this.usedCount = usedCount;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired(boolean isExpired) {
        this.isExpired = isExpired;
    }

    public boolean isForever() {
        return isForever;
    }

    public void setForever(boolean isForever) {
        this.isForever = isForever;
    }

    public V getData() {
        return data;
    }

    public void setData(V data) {
        this.data = data;
    }

    /**
     * 比较两个元素<br/>
     * <ul>
     * </ul>
     * <strong>关于比较的结果</strong>
     * <ul>
     * <li>当前元素大于obj返回1</li>
     * <li>当前元素等于obj返回0</li>
     * <li>当前元素小于obj返回-1</li>
     * </ul>
     * <strong>关于比较的规则</strong>
     * <ul>
     * <li>若obj为null，则当前元素大于obj返回1</li>
     * <li>若obj不为null，则根据cacheFullRemoveType比较大小,</li>
     * </ul>
     * 
     * @param obj
     * @param cacheFullRemoveType
     * @return
     */
    public int compareTo(CacheObject<V> obj, CacheFullRemoveType cacheFullRemoveType) {
        if (obj == null) {
            return 1;
        }

        switch (cacheFullRemoveType) {
            case NOT_REMOVE:
                return 0;
            case ENTER_TIME_FIRST:
                return compare(this.enterTime, obj.enterTime);
            case ENTER_TIME_LAST:
                return compare(obj.enterTime, this.enterTime);
            case LAST_USED_TIME_FIRST:
                return compare(this.lastUsedTime, obj.lastUsedTime);
            case LAST_USED_TIME_LAST:
                return compare(obj.lastUsedTime, this.lastUsedTime);
            case USED_COUNT_SMALL:
                return compare(this.usedCount, obj.usedCount);
            case USED_COUNT_BIG:
                return compare(obj.usedCount, this.usedCount);
            case PRIORITY_LOW:
                return compare(this.priority, obj.priority);
            case PRIORITY_HIGH:
                return compare(obj.priority, this.priority);
            case DATA_SMALL:
                return ObjectUtils.compare(this.data, obj.data);
            case DATA_BIG:
                return ObjectUtils.compare(obj.data, this.data);
            default:
                return compare(this.enterTime, obj.enterTime);
        }
    }

    /**
     * 比较两个数值<br/>
     * <ul>
     * </ul>
     * <strong>关于比较的结果</strong>
     * <ul>
     * <li>l1大于l2返回1</li>
     * <li>l1等于l2返回0</li>
     * <li>l1小于l2返回-1</li>
     * </ul>
     * 
     * @param l1
     * @param l2
     * @return
     */
    private int compare(long l1, long l2) {
        return (l1 > l2) ? 1 : ((l1 == l2) ? 0 : -1);
    }
}
