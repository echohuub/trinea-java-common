package com.trinea.java.common.serviceImpl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import com.trinea.java.common.ListUtils;
import com.trinea.java.common.MapUtils;
import com.trinea.java.common.ObjectUtils;
import com.trinea.java.common.entity.CacheFullRemoveType;
import com.trinea.java.common.entity.CacheObject;

/**
 * 特殊类型缓存，在get某个key时自动获取新数据并缓存，可以同时向前或向后缓存多个数据，使得数据获取效率大大提高<br/>
 * <ul>
 * 缓存使用及设置
 * <li>缓存初始化同{@link SimpleCache}；使用{@link AutoGetDataCache#setForwardCacheNumber(int)}设置向前缓存个数，默认个数为
 * {@link AutoGetDataCache#DEFAULT_FORWARD_CACHE_SIZE}；使用{@link AutoGetDataCache#setBackCacheNumber(int)}设置向后缓存个数，默认个数为
 * {@link AutoGetDataCache#DEFAULT_BACK_CACHE_SIZE}</li>
 * <li>使用{@link AutoGetDataCache#getAndAutoCacheNewData(Object, List, boolean)}get某个key时自动获取新数据并缓存, 详细规则如下</li>
 * <li>使用{@link AutoGetDataCache#get(Object)}get某个key但不会自动获取新数据进行缓存</li>
 * </ul>
 * <ul>
 * 自动缓存规则
 * <li>见{@link AutoGetDataCache#autoCacheNewData(Object, List, boolean)}</li>
 * </ul>
 * 
 * @author Trinea 2012-3-4 下午12:39:17
 */
public class AutoGetDataCache<K, V> extends SimpleCache<K, V> {

    /** 默认自动向前缓存的个数 **/
    private static final int       DEFAULT_FORWARD_CACHE_SIZE = 3;

    /** 默认自动向后缓存的个数 **/
    private static final int       DEFAULT_BACK_CACHE_SIZE    = 1;

    /** 自动向前缓存的个数，默认个数为{@link AutoGetDataCache#DEFAULT_FORWARD_CACHE_SIZE} **/
    private volatile int           forwardCacheNumber         = DEFAULT_FORWARD_CACHE_SIZE;

    /** 自动向后缓存的个数 ，默认个数为{@link AutoGetDataCache#DEFAULT_BACK_CACHE_SIZE} **/
    private volatile int           backCacheNumber            = DEFAULT_BACK_CACHE_SIZE;

    /** 获取数据的接口 **/
    private GetDataInterface<K, V> getDataInterface;

    /** 存储正在获取数据的线程，防止多个线程同时获取某个key，同时可以获取某个线程的相关信息 **/
    private Map<K, GetDataThread>  gettingDataThreadMap       = new ConcurrentHashMap<K, GetDataThread>();

    /**
     * 获取某个key对应的值，并自动获取新数据缓存。如果只获取key对应值不获取新数据缓存，可使用{@link AutoGetDataCache#get(Object)}
     * <ul>
     * <li>new线程获取新数据进行缓存，缓存方法见{@link AutoGetDataCache#autoCacheNewData(Object, List, boolean)}，规则见
     * {@link AutoGetDataCache}，keyList为空表示不进行缓存</li>
     * <li>若该key为null，返回null并且不进行缓存</li>
     * <li>若该key存在，返回该key对应的值</li>
     * <li>若该key不存在，会自动调用其{@link GetDataInterface#getData(Object)}方法获取数据将其返回，getData为null时返回null</li>
     * </ul>
     * 
     * @param key 待获取值的key
     * @param keyList key list，为空表示不进行缓存
     * @param isForward true表示向前缓存，false表示向后缓存
     * @return
     */
    public CacheObject<V> getAndAutoCacheNewData(K key, List<K> keyList, boolean isForward) {
        if (key == null) {
            return null;
        }

        // 先进行预取
        autoCacheNewData(key, keyList, isForward);

        CacheObject<V> object = get(key);
        if (object == null) {
            GetDataThread getDataThread = getOneKey(key, isForward);
            // 实时获取需要等待获取完成
            if (getDataThread != null) {
                try {
                    getDataThread.getLatch().await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                object = get(key);
                setUsedInfo(object);
            }
        }
        return object;
    }

    /**
     * 自动获取新数据缓存，返回获取的数据个数即new的线程数，小于等于要缓存的个数
     * <ul>
     * 缓存规则如下
     * <li>从前(后)到后(前)依次循环keyList中每个元素，若向前(向后)缓存，则从等于key的元素的下一个元素开始判断; a.不在缓存中;
     * b.没有其他线程正在获取该元素对应的数据时，new线程获取该元素对应的数据put进缓存，如此反复直到key前(后)第 {@link AutoGetDataCache#forwardCacheNumber} (
     * {@link AutoGetDataCache#backCacheNumber})个元素或list尾(头)时结束</li>
     * </ul>
     * 
     * @param key 当前要获取值的key
     * @param keyList key list，为空表示不进行缓存
     * @param isForward true表示向前缓存，false表示向后缓存
     * @return 预取的key已经存在的个数
     */
    protected int autoCacheNewData(K key, List<K> keyList, boolean isForward) {
        int cachingCount = 0;
        if (key != null && !ListUtils.isEmpty(keyList)) {
            int beginIndex = isForward ? 0 : (keyList.size() - 1), toCacheCount = 0;
            int maxCacheNumber = isForward ? forwardCacheNumber : backCacheNumber;
            boolean beginCount = false;
            int i = beginIndex;
            while ((isForward ? i < keyList.size() : i >= 0) && toCacheCount <= maxCacheNumber) {
                K k = keyList.get(i);
                i = (isForward ? ++i : --i);

                if (ObjectUtils.isEquals(k, key)) {
                    beginCount = true;
                    continue;
                }
                if (k != null && beginCount) {
                    toCacheCount++;
                    if (getOneKey(k, isForward) == null) {
                        cachingCount++;
                    }
                }
            }
        }
        return cachingCount;
    }

    /**
     * 获得某个key，若key不存在启线程获取
     * 
     * @param k
     * @param isForward true表示向前缓存，false表示向后缓存
     * @return 若已经在缓存中返回null，否则返回获取数据的线程
     */
    protected synchronized GetDataThread getOneKey(K k, boolean isForward) {
        if (containsKey(k)) {
            return null;
        }

        if (gettingDataThreadMap.containsKey(k)) {
            return gettingDataThreadMap.get(k);
        }

        GetDataThread getDataThread = new GetDataThread(this, k, isForward, getDataInterface);
        gettingDataThreadMap.put(k, getDataThread);
        getDataThread.start();
        return getDataThread;

    }

    /**
     * @param getDataInterface 获取数据的方法
     * @param 其他参数 见{@link SimpleCache#SimpleCache(int, long, CacheFullRemoveType)}
     */
    public AutoGetDataCache(int maxSize, long validTime, CacheFullRemoveType cacheFullRemoveType,
                            GetDataInterface<K, V> getDataInterface){
        super(maxSize, validTime, cacheFullRemoveType);
        this.getDataInterface = getDataInterface;
    }

    /**
     * @param getDataInterface 获取数据的方法
     * @param 其他参数 见{@link SimpleCache#SimpleCache(int, long)}
     */
    public AutoGetDataCache(int maxSize, long validTime, GetDataInterface<K, V> getDataInterface){
        super(maxSize, validTime);
        this.getDataInterface = getDataInterface;
    }

    /**
     * @param getDataInterface 获取数据的方法
     * @param 其他参数 见{@link SimpleCache#SimpleCache(int)}
     */
    public AutoGetDataCache(int maxSize, GetDataInterface<K, V> getDataInterface){
        super(maxSize);
        this.getDataInterface = getDataInterface;
    }

    /**
     * @param getDataInterface 获取数据的方法
     * @param 其他参数 见{@link SimpleCache#SimpleCache()}
     */
    public AutoGetDataCache(GetDataInterface<K, V> getDataInterface){
        super();
        this.getDataInterface = getDataInterface;
    }

    /**
     * 得到自动向前缓存的个数
     * 
     * @return
     */
    public int getForwardCacheNumber() {
        return forwardCacheNumber;
    }

    /**
     * 设置自动向前缓存的个数，缓存规则见{@link AutoGetDataCache#autoCacheNewData(Object, List, boolean)}
     * 
     * @param forwardCacheNumber
     */
    public void setForwardCacheNumber(int forwardCacheNumber) {
        this.forwardCacheNumber = forwardCacheNumber;
    }

    /**
     * 得到自动向后缓存的个数
     * 
     * @return
     */
    public int getBackCacheNumber() {
        return backCacheNumber;
    }

    /**
     * 设置自动向后缓存的个数，缓存规则见{@link AutoGetDataCache#autoCacheNewData(Object, List, boolean)}
     * 
     * @param backCacheNumber
     */
    public void setBackCacheNumber(int backCacheNumber) {
        this.backCacheNumber = backCacheNumber;
    }

    /**
     * 得到getDataInterface
     * 
     * @return the getDataInterface
     */
    public GetDataInterface<K, V> getGetDataInterface() {
        return getDataInterface;
    }

    /**
     * 设置getDataInterface
     * 
     * @param getDataInterface
     */
    public void setGetDataInterface(GetDataInterface<K, V> getDataInterface) {
        this.getDataInterface = getDataInterface;
    }

    /**
     * 获取新数据的类
     * 
     * @author Trinea 2012-3-4 下午01:34:27
     */
    public interface GetDataInterface<K, V> {

        /**
         * 获取数据的方法
         * 
         * @param key
         * @return
         */
        public CacheObject<V> getData(K key);
    }

    /**
     * 获取新数据的线程
     * 
     * @author Trinea 2012-3-4 下午02:09:10
     */
    protected class GetDataThread extends Thread {

        private AutoGetDataCache<K, V> cache;
        private K                      key;
        private boolean                isForward;
        private GetDataInterface<K, V> getDataInterface;

        /** put结束的锁 **/
        private CountDownLatch         finishPutLock;

        /**
         * 获取数据
         * 
         * @param cache 存储数据的缓存
         * @param key 获取数据的key
         * @param isForward true表示向前缓存，false表示向后缓存
         * @param getDataInterface 获取数据的接口
         */
        public GetDataThread(AutoGetDataCache<K, V> cache, K key, boolean isForward,
                             GetDataInterface<K, V> getDataInterface){
            super("GetDataThread whose key is " + key);
            this.cache = cache;
            this.key = key;
            this.isForward = isForward;
            this.getDataInterface = getDataInterface;
            finishPutLock = new CountDownLatch(1);
        }

        public void run() {
            if (key != null && getDataInterface != null && cache != null) {
                CacheObject<V> object = getDataInterface.getData(key);
                if (object != null) {
                    if (isForward) {
                        cache.put(key, object);
                    } else {
                        cache.putByOppositeRemove(key, object);
                    }
                }
            }
            // 执行结束释放锁
            finishPutLock.countDown();

            if (gettingDataThreadMap != null && key != null) {
                gettingDataThreadMap.remove(key);
            }
        }

        /**
         * 得到latch
         * 
         * @return the latch
         */
        public CountDownLatch getLatch() {
            return finishPutLock;
        }
    };

    /**
     * 向缓存中添加元素，并且在缓存已满时，从缓存中按照{@link SimpleCache#cacheFullRemoveType}相反地顺序删除一个元素
     * <ul>
     * <li>若元素个数{@link SimpleCache#getSize()}小于最大容量，直接put进入，否则</li>
     * <li>若有效元素个数{@link SimpleCache#getValidSize()}小于元素个数{@link SimpleCache#getSize()}，去除无效元素
     * {@link SimpleCache#removeExpired()}后直接put进入，否则</li>
     * <li>若{@link SimpleCache#cacheFullRemoveType}等于{@link CacheFullRemoveType#NOT_REMOVE}，直接返回null，否则</li>
     * <li>按{@link SimpleCache#cacheFullRemoveType}相反地顺序删除元素后直接put进入</li>
     * </ul>
     * 
     * @param key key
     * @param obj 元素
     * @return
     */
    protected synchronized void putByOppositeRemove(K key, CacheObject<V> obj) {
        if (getSize() >= getMaxSize()) {
            if (getValidSize() < cache.size()) {
                if (removeExpired() <= 0) {
                    return;
                }
            } else {
                if (getCacheFullRemoveType() == CacheFullRemoveType.NOT_REMOVE) {
                    return;
                }
                if (fullOppositeRemoveOne() == null) {
                    return;
                }
            }
        }
        cache.put(key, obj);
    }

    /**
     * 从缓存中按照{@link SimpleCache#cacheFullRemoveType}相反地顺序删除一个元素
     * <ul>
     * <li>若{@link SimpleCache#cacheFullRemoveType}为{@link CacheFullRemoveType#NOT_REMOVE}返回null，否则</li>
     * <li>按{@link SimpleCache#cacheFullRemoveType}相反的顺序从未过期元素中查找删除的元素删除，未查找到返回null</li>
     * </ul>
     * 
     * @param key
     * @return 返回删除的元素
     */
    protected CacheObject<V> fullOppositeRemoveOne() {
        if (MapUtils.isEmpty(cache) || getCacheFullRemoveType() == CacheFullRemoveType.NOT_REMOVE) {
            return null;
        }

        K keyToRemove = null;
        CacheObject<V> valueToRemove = null;
        for (Entry<K, CacheObject<V>> entry : cache.entrySet()) {
            if (entry != null && !isExpired(entry.getValue())) {
                if (valueToRemove == null) {
                    valueToRemove = entry.getValue();
                    keyToRemove = entry.getKey();
                } else {
                    if (compare(entry.getValue(), valueToRemove) > 0) {
                        valueToRemove = entry.getValue();
                        keyToRemove = entry.getKey();
                    }
                }
            }
        }
        if (keyToRemove != null) {
            cache.remove(keyToRemove);
        }
        return valueToRemove;
    }
}
