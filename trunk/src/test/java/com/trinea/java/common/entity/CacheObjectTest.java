package com.trinea.java.common.entity;

import junit.framework.TestCase;

public class CacheObjectTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testCacheObject() {
        CacheObject<String> object = new CacheObject<String>();
        object.setEnterTime(System.currentTimeMillis());
        object.setExpired(false);
        object.setForever(false);
        object.setUsedCount(object.getUsedCount() + 1);
        object.setPriority(1);
        object.setLastUsedTime(System.currentTimeMillis());
        object.setData("data");
        assertTrue(object.getEnterTime() <= System.currentTimeMillis());
        assertTrue(object.getLastUsedTime() <= System.currentTimeMillis());
        assertFalse(object.isExpired());
        assertFalse(object.isForever());
        assertTrue(object.getUsedCount() >= 0);
        assertTrue(object.getPriority() >= 0);
        assertTrue(object.getData() != null);
    }

    public void testCompareTo() {
        CacheObject<Integer> obj1 = new CacheObject<Integer>();
        CacheObject<Integer> obj2 = new CacheObject<Integer>();
        obj1.setEnterTime(10000);
        obj1.setLastUsedTime(10000);
        obj1.setPriority(1);
        obj1.setUsedCount(1);
        obj1.setData(100);

        obj2.setEnterTime(20000);
        obj2.setLastUsedTime(20000);
        obj2.setPriority(2);
        obj2.setUsedCount(2);
        obj2.setData(200);
        assertTrue(obj1.compareTo(obj2, CacheFullRemoveType.NOT_REMOVE) == 0);
        assertTrue(obj1.compareTo(obj2, CacheFullRemoveType.ENTER_TIME_FIRST) < 0);
        assertTrue(obj1.compareTo(obj2, CacheFullRemoveType.ENTER_TIME_LAST) > 0);
        assertTrue(obj1.compareTo(obj2, CacheFullRemoveType.LAST_USED_TIME_FIRST) < 0);
        assertTrue(obj1.compareTo(obj2, CacheFullRemoveType.LAST_USED_TIME_LAST) > 0);
        assertTrue(obj1.compareTo(obj2, CacheFullRemoveType.PRIORITY_LOW) < 0);
        assertTrue(obj1.compareTo(obj2, CacheFullRemoveType.PRIORITY_HIGH) > 0);
        assertTrue(obj1.compareTo(obj2, CacheFullRemoveType.USED_COUNT_SMALL) < 0);
        assertTrue(obj1.compareTo(obj2, CacheFullRemoveType.USED_COUNT_BIG) > 0);
        assertTrue(obj1.compareTo(obj2, CacheFullRemoveType.DATA_SMALL) < 0);
        assertTrue(obj1.compareTo(obj2, CacheFullRemoveType.DATA_BIG) > 0);

        obj1.setPriority(1);
        obj2.setPriority(1);
        assertTrue(obj1.compareTo(obj2, CacheFullRemoveType.PRIORITY_LOW) == 0);

        CacheObject<String> obj3 = new CacheObject<String>();
        CacheObject<String> obj4 = new CacheObject<String>();
        obj3.setData(null);
        obj4.setData(null);
        assertTrue(obj3.compareTo(obj4, CacheFullRemoveType.DATA_SMALL) == 0);
        assertTrue(obj3.compareTo(obj4, CacheFullRemoveType.DATA_BIG) == 0);
        obj3.setData("aa");
        obj4.setData("ab");
        assertTrue(obj3.compareTo(obj4, CacheFullRemoveType.DATA_SMALL) < 0);
        assertTrue(obj3.compareTo(obj4, CacheFullRemoveType.DATA_BIG) > 0);
        obj4.setData("aa");
        assertTrue(obj3.compareTo(obj4, CacheFullRemoveType.DATA_BIG) == 0);

        CacheObject<TestClass> obj5 = new CacheObject<TestClass>();
        CacheObject<TestClass> obj6 = new CacheObject<TestClass>();
        TestClass class5 = new TestClass();
        class5.setName("aa");
        obj5.setData(null);
        obj6.setData(null);
        assertTrue(obj5.compareTo(obj6, CacheFullRemoveType.DATA_SMALL) == 0);
        obj5.setData(class5);
        assertTrue(obj5.compareTo(obj6, CacheFullRemoveType.DATA_SMALL) > 0);
        
        TestClass class6 = new TestClass();
        class6.setName("bb");
        obj6.setData(class6);
        assertTrue(obj5.compareTo(obj6, CacheFullRemoveType.DATA_SMALL) < 0);
        class5.setName("ca");
        assertTrue(obj5.compareTo(obj6, CacheFullRemoveType.DATA_SMALL) > 0);
    }

    public class TestClass implements Comparable {

        private String name;

        public void setName(String name) {
            this.name = name;
        }
        
        @Override
        public int compareTo(Object o) {
            if (o == null) {
                return 1;
            }
            return this.name.compareTo(((TestClass)o).name);
        }

    }

}
