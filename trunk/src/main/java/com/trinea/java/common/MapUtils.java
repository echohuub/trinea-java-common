package com.trinea.java.common;

import java.util.Map;
import java.util.Map.Entry;

/**
 * map工具类，用于实现一些map的常用操作
 * 
 * @author Trinea 2011-7-22 上午12:37:10
 */
public class MapUtils {

    /**
     * 判断map是否为空或大小为0
     * 
     * <pre>
     * isEmpty(null)   =   true;
     * isEmpty({})     =   true;
     * isEmpty({1, 2})    =   false;
     * </pre>
     * 
     * @param str
     * @return 若map为null或长度为0, 返回true; 否则返回false.
     */
    public static boolean isEmpty(Map<?, ?> sourceMap) {
        return (sourceMap == null || sourceMap.size() == 0);
    }

    /**
     * 向map中put key和value对，key必须非null，并且为非空字符串
     * 
     * @param map
     * @param key
     * @param value
     * @return 若put成功，返回true，否则返回false
     *         <ul>
     *         <li>若map为null，返回false，否则</li>
     *         <li>若key为null或空字符串，返回false，否则</li>
     *         <li>调用{@link Map#put(Object, Object)} 返回true</li>
     *         </ul>
     */
    public static boolean putMapNotEmptyKey(Map<String, String> map, String key, String value) {
        if (map == null || StringUtils.isEmpty(key)) {
            return false;
        }

        map.put(key, value);
        return true;
    }

    /**
     * 向map中put字符串，该字符串必须非null，并且为非空字符串
     * 
     * @param map
     * @param key
     * @param value
     * @return 若put成功，返回true，否则返回false
     *         <ul>
     *         <li>若map为null，返回false，否则</li>
     *         <li>若key为null或空字符串，返回false，否则</li>
     *         <li>若value为null或空字符串，返回false，否则</li>
     *         <li>调用{@link Map#put(Object, Object)} 返回true</li>
     *         </ul>
     */
    public static boolean putMapNotEmptyValue(Map<String, String> map, String key, String value) {
        return StringUtils.isEmpty(value) ? false : putMapNotEmptyKey(map, key, value);
    }

    /**
     * 向map中put字符串(value)，若字符串为null或者为空字符串，put默认值(defaultValue)
     * 
     * @param map
     * @param key
     * @param value
     * @param defaultValue
     * @return 若put成功，返回true，否则返回false
     *         <ul>
     *         <li>若map为null，返回false，否则</li>
     *         <li>若key为null或空字符串，返回false，否则</li>
     *         <li>若value为null或空字符串，put默认值(defaultValue)，返回true</li>
     *         <li>若value不为null，且不为空字符串，put字符串(value)，返回true</li>
     *         </ul>
     */
    public static boolean putMapNotEmptyValue(Map<String, String> map, String key, String value, String defaultValue) {
        if (map == null || StringUtils.isEmpty(key)) {
            return false;
        }

        map.put(key, StringUtils.isEmpty(value) ? defaultValue : value);
        return true;
    }

    /**
     * 根据value得到key的值，从头开始匹配，若存在返回匹配的第一个key，否则返回null
     * 
     * <pre>
     * 如果map为空，返回null;
     * 如果map中存在value，查找第一个value（对于value为null同样适用），返回key，否则返回null.
     * </pre>
     * 
     * <strong>注意：</strong>
     * 由于对于一般的HashMap，元素的顺序并不是put的顺序，所以使用本函数得到的值并不是第一次put的值，如果想得到第一次put的值，请使用TreeMap<br/>
     * <br/>
     * 
     * @param <V>
     * @param map map
     * @param value value值
     * @return
     */
    public static <V> String getKeyByValue(Map<String, V> map, V value) {
        if (isEmpty(map)) {
            return null;
        }

        for (Entry<String, V> entry : map.entrySet()) {
            if (ObjectUtils.isEquals(entry.getValue(), value)) {
                return entry.getKey();
            }
        }

        return null;
    }
}
