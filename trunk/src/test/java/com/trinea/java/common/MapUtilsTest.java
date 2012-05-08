package com.trinea.java.common;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import junit.framework.TestCase;

public class MapUtilsTest extends TestCase {

    protected void setUp() throws Exception {
        MapUtils mapUtils = new MapUtils();
        assertTrue(mapUtils != null);
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testIsEmpty() {
        assertTrue(MapUtils.isEmpty(null));
        Map<String, String> sourceMap = new HashMap<String, String>();
        assertTrue(MapUtils.isEmpty(sourceMap));
        sourceMap.put("a", "b");
        assertFalse(MapUtils.isEmpty(sourceMap));
    }

    public void testPutMapNotEmptyKey() {
        assertFalse(MapUtils.putMapNotEmptyKey(null, null, null));

        Map<String, String> sourceMap = new HashMap<String, String>();
        assertFalse(MapUtils.putMapNotEmptyKey(sourceMap, null, null));
        assertFalse(MapUtils.putMapNotEmptyKey(sourceMap, "", null));
        assertFalse(MapUtils.putMapNotEmptyKey(sourceMap, "", "b"));
        assertTrue(sourceMap.size() == 0);

        assertTrue(MapUtils.putMapNotEmptyKey(sourceMap, "a", "b"));
        assertTrue(sourceMap.size() > 0);
    }

    public void testPutMapNotEmptyValueMapOfStringStringStringString() {
        assertFalse(MapUtils.putMapNotEmptyValue(null, null, null));

        Map<String, String> sourceMap = new HashMap<String, String>();
        assertFalse(MapUtils.putMapNotEmptyValue(sourceMap, null, null));
        assertFalse(MapUtils.putMapNotEmptyValue(sourceMap, "", null));
        assertTrue(sourceMap.size() == 0);

        assertFalse(MapUtils.putMapNotEmptyValue(sourceMap, null, "b"));
        assertFalse(MapUtils.putMapNotEmptyValue(sourceMap, "", "b"));
        assertFalse(MapUtils.putMapNotEmptyValue(sourceMap, "a", null));
        assertFalse(MapUtils.putMapNotEmptyValue(sourceMap, "a", ""));
        assertTrue(sourceMap.size() == 0);

        assertTrue(MapUtils.putMapNotEmptyValue(sourceMap, "a", "b"));
        assertTrue(sourceMap.size() > 0);
    }

    public void testPutMapNotEmptyValueMapOfStringStringStringStringString() {
        assertFalse(MapUtils.putMapNotEmptyValue(null, null, null, null));

        Map<String, String> sourceMap = new HashMap<String, String>();
        assertFalse(MapUtils.putMapNotEmptyValue(sourceMap, null, null, null));
        assertFalse(MapUtils.putMapNotEmptyValue(sourceMap, "", null, null));
        assertFalse(MapUtils.putMapNotEmptyValue(sourceMap, "", "b", null));
        assertTrue(sourceMap.size() == 0);

        assertTrue(MapUtils.putMapNotEmptyValue(sourceMap, "a", null, "b"));
        assertTrue(sourceMap.size() > 0);
        assertTrue(sourceMap.containsKey("a") && sourceMap.get("a").equals("b"));
        assertTrue(MapUtils.putMapNotEmptyValue(sourceMap, "c", "", "d"));
        assertTrue(sourceMap.size() > 0);
        assertTrue(sourceMap.containsKey("c") && sourceMap.get("c").equals("d"));
        assertTrue(MapUtils.putMapNotEmptyValue(sourceMap, "e", "f", "g"));
        assertTrue(sourceMap.size() > 0);
        assertTrue(sourceMap.containsKey("e") && sourceMap.get("e").equals("f"));
    }

    public void testGetKeyByValue() {
        assertNull(MapUtils.getKeyByValue(null, null));

        Map<String, String> sourceMap = new TreeMap<String, String>();
        assertNull(MapUtils.getKeyByValue(sourceMap, null));
        sourceMap.put("a", "b");
        assertTrue("a".equals((MapUtils.getKeyByValue(sourceMap, "b"))));
        assertNull(MapUtils.getKeyByValue(sourceMap, "c"));

        sourceMap.put("c", "d");
        sourceMap.put("e", "f");
        sourceMap.put("g", "d");
        sourceMap.put("h", null);
        sourceMap.put("i", "j");
        assertTrue("c".equals((MapUtils.getKeyByValue(sourceMap, "d"))));
        assertTrue("h".equals((MapUtils.getKeyByValue(sourceMap, null))));
    }

}
