/*
 * Copyright 2012 Trinea.com All right reserved. This software is the
 * confidential and proprietary information of Trinea.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Trinea.com.
 */
package com.trinea.java.common.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.trinea.java.common.ObjectUtils;
import com.trinea.java.common.entity.CacheObject;
import com.trinea.java.common.serviceImpl.AutoGetDataCache.GetDataInterface;
import com.trinea.java.common.utils.SleepUtils;

/**
 * 类AutoGetDataCacheTest.java的实现描述：TODO 类实现描述
 * 
 * @author Trinea 2012-5-6 下午08:39:27
 */
public class AutoGetDataCacheTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test method for
     * {@link com.trinea.java.common.serviceImpl.AutoGetDataCache#getAndAutoCacheNewData(java.lang.Object, java.util.List, boolean)}
     * .
     */
    public void testGetAndAutoCacheNewData() {
        // 数据源，用map代替网络数据源
        final Map<String, String> dataSource = new HashMap<String, String>();
        List<String> keyList = new ArrayList<String>();
        for (int i = 0; i < 20; i++) {
            String temp = Integer.toString(i);
            dataSource.put(temp, temp);
            keyList.add(temp);
        }

        // 新建缓存
        AutoGetDataCache<String, String> cache = null;
        cache = new AutoGetDataCache<String, String>(5, -1, new RemoveTypeEnterTimeFirst<String>(),
                                                     new GetDataInterface<String, String>() {

                                                         @Override
                                                         public CacheObject<String> getData(String key) {
                                                             CacheObject<String> o = new CacheObject<String>();
                                                             o.setData(dataSource.get(key));
                                                             return o;
                                                         }
                                                     });
        // 设置向后缓存1个，向前缓存2个
        cache.setBackCacheNumber(1);
        cache.setForwardCacheNumber(2);

        CacheObject<String> value = cache.get(Integer.toString(0));
        assertTrue(value != null && ObjectUtils.isEquals(value.getData(), Integer.toString(0)));
        assertTrue(cache.getValidSize() == 1);
        assertTrue(cache.getHitRate() == 1);
        value = cache.getAndAutoCacheNewData(Integer.toString(2), keyList);
        assertTrue(cache.getHitRate() == 1);
        assertTrue(value != null && ObjectUtils.isEquals(value.getData(), Integer.toString(2)));
        SleepUtils.sleep();
        if (cache.getValidSize() == 5) {
            value = cache.getAndAutoCacheNewData(Integer.toString(3), keyList);
            assertTrue(cache.getHitRate() == 1);
            value = cache.getAndAutoCacheNewData(Integer.toString(4), keyList);
            assertTrue(cache.getHitRate() == 1);
            value = cache.getAndAutoCacheNewData(Integer.toString(1), keyList);
            assertTrue(cache.getHitRate() == 1);
        }
        value = cache.getAndAutoCacheNewData(Integer.toString(5), keyList);
    }

    /**
     * Test method for {@link com.trinea.java.common.serviceImpl.SimpleCache#getHitRate()}.
     */
    public void testGetHitRate() {
    }
}
