package com.trinea.java.common.entity;

/**
 * 缓存满时清空数据的类型
 * 
 * @author Trinea 2011-12-26 下午11:40:39
 */
public enum CacheFullRemoveType {
    /** 不删除 **/
    NOT_REMOVE,

    /** 对象进入缓存时间，先进入先删除 **/
    ENTER_TIME_FIRST,

    /** 对象进入缓存时间，后进入先删除 **/
    ENTER_TIME_LAST,

    /** 对象上次使用时间(即上次被get的时间)，先使用先删除 **/
    LAST_USED_TIME_FIRST,

    /** 对象上次使用时间(即上次被get的时间)，后使用先删除 **/
    LAST_USED_TIME_LAST,

    /** 对象使用次数(即被get的次数)，使用少先删除 **/
    USED_COUNT_SMALL,

    /** 对象使用次数(即被get的次数)，使用多先删除 **/
    USED_COUNT_BIG,

    /** 对象优先级，优先级低先删除 **/
    PRIORITY_LOW,

    /** 对象优先级，优先级高先删除 **/
    PRIORITY_HIGH,

    /** 对象值，值小先删除 **/
    DATA_SMALL,

    /** 对象值，值大先删除 **/
    DATA_BIG,
}
