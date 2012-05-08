package com.trinea.java.common;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class StringUtilsTest extends TestCase {

    protected void setUp() throws Exception {
        StringUtils stringUtils = new StringUtils();
        assertNotNull(stringUtils);
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testIsEmpty() {
        assertTrue(StringUtils.isEmpty(null));
        assertTrue(StringUtils.isEmpty(""));
        assertFalse(StringUtils.isEmpty("  "));
        assertFalse(StringUtils.isEmpty("aa"));
        assertFalse(StringUtils.isEmpty("啊啊"));
    }

    public void testIsBlank() {
        assertTrue(StringUtils.isBlank(null));
        assertTrue(StringUtils.isBlank(""));
        assertTrue(StringUtils.isBlank("  "));
        assertFalse(StringUtils.isBlank("aa"));
        assertFalse(StringUtils.isBlank(" aa"));
        assertFalse(StringUtils.isBlank("aa "));
        assertFalse(StringUtils.isBlank(" aa "));
        assertFalse(StringUtils.isBlank(" a "));
        assertFalse(StringUtils.isBlank("啊啊"));
    }

    public void testCapitalizeFirstLetter() {
        assertEquals(StringUtils.capitalizeFirstLetter(null), null);
        assertEquals(StringUtils.capitalizeFirstLetter(""), "");
        assertEquals(StringUtils.capitalizeFirstLetter("2ab"), "2ab");
        assertEquals(StringUtils.capitalizeFirstLetter("lab"), "Lab");
        assertEquals(StringUtils.capitalizeFirstLetter("a"), "A");
        assertEquals(StringUtils.capitalizeFirstLetter("ab"), "Ab");
        assertEquals(StringUtils.capitalizeFirstLetter("Abc"), "Abc");
    }

    public void testUtf8EncodeString() {
        assertEquals(StringUtils.utf8Encode(null), null);
        assertEquals(StringUtils.utf8Encode(""), "");
        assertEquals(StringUtils.utf8Encode("aa"), "aa");
        assertEquals(StringUtils.utf8Encode("啊啊啊啊"), HttpUtils.utf8Encode("啊啊啊啊"));
    }

    public void testUtf8EncodeStringString() {
        assertEquals(StringUtils.utf8Encode(null, "default"), null);
        assertFalse("default".equals(StringUtils.utf8Encode("", "default")));
        assertFalse("default".equals(StringUtils.utf8Encode("aa", "default")));
        assertFalse("default".equals(StringUtils.utf8Encode("啊啊啊啊", "default")));
    }

    public void testNullStrToEmpty() {
        assertEquals(StringUtils.nullStrToEmpty(null), "");
        assertEquals(StringUtils.nullStrToEmpty(""), "");
        assertEquals(StringUtils.nullStrToEmpty("aa"), "aa");
    }

    public void testGetHrefInnerHtml() {
        assertEquals(StringUtils.getHrefInnerHtml(null), "");
        assertEquals(StringUtils.getHrefInnerHtml(""), "");
        assertEquals(StringUtils.getHrefInnerHtml("mp3"), "mp3");
        assertEquals(StringUtils.getHrefInnerHtml("<a innerHtml</a>"), "<a innerHtml</a>");
        assertEquals(StringUtils.getHrefInnerHtml("<a>innerHtml</a>"), "innerHtml");
        assertEquals(StringUtils.getHrefInnerHtml("< A >innerHtml</a>"), "innerHtml");
        assertEquals(StringUtils.getHrefInnerHtml("<a<a>innerHtml</a>"), "innerHtml");
        assertEquals(StringUtils.getHrefInnerHtml("<a href=\"baidu.com\">innerHtml</a>"), "innerHtml");
        assertEquals(StringUtils.getHrefInnerHtml("<a href=\"baidu.com\" title=\"baidu\">innerHtml</a>"), "innerHtml");
        assertEquals(StringUtils.getHrefInnerHtml("   <a>innerHtml</a>  "), "innerHtml");
        assertEquals(StringUtils.getHrefInnerHtml("<a>innerHtml</a></a>"), "innerHtml");
        assertEquals(StringUtils.getHrefInnerHtml("jack<a>innerHtml</a></a>"), "innerHtml");
        assertEquals(StringUtils.getHrefInnerHtml("<a>innerHtml1</a><a>innerHtml2</a>"), "innerHtml2");
    }

    public void testGetRandomNumbersAndLetters() {
        assertTrue(StringUtils.getRandomNumbersAndLetters(1).length() == 1);
        assertTrue(StringUtils.getRandomNumbersAndLetters(2).length() == 2);
        assertTrue(StringUtils.getRandomNumbersAndLetters(5).length() == 5);
        assertTrue(StringUtils.getRandomNumbersAndLetters(9).length() == 9);
        assertTrue(StringUtils.getRandomNumbersAndLetters(13).length() == 13);
        assertTrue(StringUtils.getRandomNumbersAndLetters(25).length() == 25);
        assertTrue(StringUtils.getRandomNumbersAndLetters(46).length() == 46);
        assertTrue(StringUtils.getRandomNumbersAndLetters(67).length() == 67);
    }

    public void testGetRandomNumbers() {
        assertTrue(StringUtils.getRandomNumbers(1).length() == 1);
        assertTrue(StringUtils.getRandomNumbers(2).length() == 2);
        assertTrue(StringUtils.getRandomNumbers(5).length() == 5);
        assertTrue(StringUtils.getRandomNumbers(9).length() == 9);
        assertTrue(StringUtils.getRandomNumbers(13).length() == 13);
        assertTrue(StringUtils.getRandomNumbers(25).length() == 25);
        assertTrue(StringUtils.getRandomNumbers(46).length() == 46);
        assertTrue(StringUtils.getRandomNumbers(67).length() == 67);
    }

    public void testGetRandomLetters() {
        assertTrue(StringUtils.getRandomLetters(1).length() == 1);
        assertTrue(StringUtils.getRandomLetters(2).length() == 2);
        assertTrue(StringUtils.getRandomLetters(5).length() == 5);
        assertTrue(StringUtils.getRandomLetters(9).length() == 9);
        assertTrue(StringUtils.getRandomLetters(13).length() == 13);
        assertTrue(StringUtils.getRandomLetters(25).length() == 25);
        assertTrue(StringUtils.getRandomLetters(46).length() == 46);
        assertTrue(StringUtils.getRandomLetters(67).length() == 67);
    }

    public void testGetRandomCapitalLetters() {
        assertTrue(StringUtils.getRandomCapitalLetters(1).length() == 1);
        assertTrue(StringUtils.getRandomCapitalLetters(46).length() == 46);
    }

    public void testGetRandomLowerCaseLetters() {
        assertTrue(StringUtils.getRandomLowerCaseLetters(1).length() == 1);
        assertTrue(StringUtils.getRandomLowerCaseLetters(46).length() == 46);
    }

    public void testGetRandomStringInt() {
        String source = null;
        assertNull(StringUtils.getRandom(source, -1));
        assertNull(StringUtils.getRandom("", -1));
        assertNull(StringUtils.getRandom("", 1));
        assertTrue(StringUtils.getRandom("qqq", 46).length() == 46);
    }

    public void testGetRandomCharArrayInt() {
        char[] source = null;
        assertNull(StringUtils.getRandom(source, -1));
        assertNull(StringUtils.getRandom(new char[0], -1));
        assertNull(StringUtils.getRandom(new char[0], 1));
        assertNull(StringUtils.getRandom(new char[] {'a'}, -1));
        assertTrue(StringUtils.getRandom(new char[] {'a'}, 1).length() == 1);
        assertTrue(StringUtils.getRandom(new char[] {'a', 'b'}, 46).length() == 46);
    }

    public void testHtmlEscapeCharsToString() {
        assertEquals(StringUtils.htmlEscapeCharsToString(null), null);
        assertEquals(StringUtils.htmlEscapeCharsToString(""), "");
        assertEquals(StringUtils.htmlEscapeCharsToString("mp3"), "mp3");
        assertEquals(StringUtils.htmlEscapeCharsToString("mp3&lt;"), "mp3<");
        assertEquals(StringUtils.htmlEscapeCharsToString("mp3&gt;"), "mp3>");
        assertEquals(StringUtils.htmlEscapeCharsToString("mp3&amp;mp4"), "mp3&mp4");
        assertEquals(StringUtils.htmlEscapeCharsToString("mp3&quot;mp4"), "mp3\"mp4");
        assertEquals(StringUtils.htmlEscapeCharsToString("mp3&lt;&gt;&amp;&quot;mp4"), "mp3<>&\"mp4");
    }

    public void testIsEquals() {
        assertTrue(StringUtils.isEquals(null, null));
        assertFalse(StringUtils.isEquals(null, "aa"));
        assertFalse(StringUtils.isEquals("aa", null));
        assertTrue(StringUtils.isEquals("aa", "aa"));
        assertFalse(StringUtils.isEquals("aa", "ab"));
    }

    public void testParseKeyAndValueToMapStringStringStringString() {
        assertTrue(JUnitTestUtils.assertEquals(StringUtils.parseKeyAndValueToMap("", "", "", ""), null));
        assertTrue(JUnitTestUtils.assertEquals(StringUtils.parseKeyAndValueToMap(null, "", "", ""), null));
        Map<String, String> parasMap = new HashMap<String, String>();
        parasMap.put("a", "b");
        assertTrue(JUnitTestUtils.assertEquals(StringUtils.parseKeyAndValueToMap("a:b,:", "", "", ""), parasMap));
        assertTrue(JUnitTestUtils.assertEquals(StringUtils.parseKeyAndValueToMap("a:b,  : d", "", "", ""), parasMap));
        assertTrue(JUnitTestUtils.assertEquals(StringUtils.parseKeyAndValueToMap("a:b, c ", "", "", ""), parasMap));
        parasMap.put("c", "d");
        assertTrue(JUnitTestUtils.assertEquals(StringUtils.parseKeyAndValueToMap("a:b, c : d", "", "", ""), parasMap));
        assertTrue(JUnitTestUtils.assertEquals(StringUtils.parseKeyAndValueToMap("a:b, c : d", ":", "", ""), parasMap));
        assertTrue(JUnitTestUtils.assertEquals(StringUtils.parseKeyAndValueToMap("a:b, c : d", ":", ",", ""), parasMap));
        assertTrue(JUnitTestUtils.assertEquals(StringUtils.parseKeyAndValueToMap("a:b, c : d", ":", ",", "'"), parasMap));
        assertTrue(JUnitTestUtils.assertEquals(StringUtils.parseKeyAndValueToMap("a=b, c = d", "=", ",", ""), parasMap));
        assertTrue(JUnitTestUtils.assertEquals(StringUtils.parseKeyAndValueToMap("a=b, c=d", "=", ",", "'"), parasMap));
        assertTrue(JUnitTestUtils.assertEquals(StringUtils.parseKeyAndValueToMap("\"a:\"b, c : \"d", ":", ",", "\""),
                                               parasMap));
        assertTrue(JUnitTestUtils.assertEquals(StringUtils.parseKeyAndValueToMap("'a':'b', 'c' : d", ":", ",", "'"),
                                               parasMap));
    }

    public void testParseKeyAndValueToMapString() {
        assertTrue(JUnitTestUtils.assertEquals(StringUtils.parseKeyAndValueToMap(null), null));
        Map<String, String> parasMap = new HashMap<String, String>();
        parasMap.put("a", "b");
        assertTrue(JUnitTestUtils.assertEquals(StringUtils.parseKeyAndValueToMap("a:b,  : d"), parasMap));
        assertTrue(JUnitTestUtils.assertEquals(StringUtils.parseKeyAndValueToMap("a:b, c "), parasMap));
        parasMap.put("c", "d");
        assertTrue(JUnitTestUtils.assertEquals(StringUtils.parseKeyAndValueToMap("a:b, c : d"), parasMap));
        assertTrue(JUnitTestUtils.assertEquals(StringUtils.parseKeyAndValueToMap("a:b, c : d"), parasMap));
        assertTrue(JUnitTestUtils.assertEquals(StringUtils.parseKeyAndValueToMap("a:b, c : d"), parasMap));
        assertTrue(JUnitTestUtils.assertEquals(StringUtils.parseKeyAndValueToMap("a:b, c : d"), parasMap));

        assertTrue(StringUtils.parseKeyAndValueToMap("\"QQGenius\" : \"微博精灵\", \"renquan\" : \"任泉\", \"renzhiqiang\" : \"任志强\"") != null);
        assertTrue(StringUtils.parseKeyAndValueToMap("\"QQGenius\":\"微博精灵\",\"renquan\":\"任泉\",\"renzhiqiang\":\"任志强\"") != null);
        assertTrue(StringUtils.parseKeyAndValueToMap("\"QQGenius\":\"微博精灵\",\"renquan\":\"任泉\",\"renzhiqiang\":\"任志强\"",
                                                     ":", ",", "?") != null);
    }

    public void testRemoveBothSideSymbol() {
        assertEquals(StringUtils.RemoveBothSideSymbol(null, null), null);
        assertEquals(StringUtils.RemoveBothSideSymbol("", null), "");
        assertEquals(StringUtils.RemoveBothSideSymbol("aa", ""), "aa");
        assertEquals(StringUtils.RemoveBothSideSymbol("'aa", "'"), "aa");
        assertEquals(StringUtils.RemoveBothSideSymbol("''''aa", "'"), "'''aa");
        assertEquals(StringUtils.RemoveBothSideSymbol("aa'", "'"), "aa");
        assertEquals(StringUtils.RemoveBothSideSymbol("aa''''", "'"), "aa'''");
        assertEquals(StringUtils.RemoveBothSideSymbol("aabcd", "cd"), "aab");
        assertEquals(StringUtils.RemoveBothSideSymbol("cdaabcd", "cd"), "aab");
    }

}
