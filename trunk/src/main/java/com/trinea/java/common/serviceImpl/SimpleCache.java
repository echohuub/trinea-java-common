package com.trinea.java.common.serviceImpl;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.trinea.java.common.MapUtils;
import com.trinea.java.common.entity.CacheFullRemoveType;
import com.trinea.java.common.entity.CacheObject;
import com.trinea.java.common.service.Cache;

/**
 * 小型缓存<br/>
 * <ul>
 * 缓存使用
 * <li>使用下面缓存初始化中介绍的几种构造函数之一初始化缓存</li>
 * <li>使用{@link SimpleCache#put(Object, CacheObject)}或{@link SimpleCache#put(Object, Object)}向缓存中put元素</li>
 * <li>使用{@link SimpleCache#get(Object)}从缓存中get元素</li>
 * </ul>
 * <ul>
 * 缓存初始化
 * <li>{@link SimpleCache#SimpleCache(int, long, CacheFullRemoveType)}</li>
 * <li>{@link SimpleCache#SimpleCache(int, long)}</li>
 * <li>{@link SimpleCache#SimpleCache(int, CacheFullRemoveType)}</li>
 * <li>{@link SimpleCache#SimpleCache(int)}</li>
 * <li>{@link SimpleCache#SimpleCache()}</li>
 * </ul>
 * <ul>
 * 对于<strong>缓存的大小</strong>
 * <li>{@link SimpleCache#getMaxSize()}表示缓存最大容量</li>
 * <li>{@link SimpleCache#getSize()}表示缓存中所有元素个数</li>
 * <li>{@link SimpleCache#getValidSize()}表示缓存中当前有效元素个数</li>
 * </ul>
 * <ul>
 * 对于缓存中<strong>元素是否存在</strong>，可以{@link SimpleCache#containsKey(Object)}判断，判断规则为：
 * <li>若元素不存在，返回false</li>
 * <li>若元素存在但已经失效，返回false</li>
 * <li>以上都不符合，返回true</li>
 * </ul>
 * 
 * @author Trinea 2011-12-23 上午01:46:01
 */
public class SimpleCache<K, V> implements Cache<K, V> {

    /** 默认缓存最大容量 **/
    public static final int           DEFAULT_MAX_SIZE = 64;

    /** 缓存最大容量 **/
    private final int                 maxSize;

    /** 有效时间，以毫秒计 **/
    private final long                validTime;

    /** cache满时删除元素类型 **/
    private final CacheFullRemoveType cacheFullRemoveType;

    /** 缓存体 **/
    protected Map<K, CacheObject<V>>  cache;

    /** 命中次数和未命中次数 **/
    protected AtomicLong              hitCount         = new AtomicLong(0), missCount = new AtomicLong(0);

    /**
     * 初始化缓存
     * 
     * @param maxSize 缓存最大容量
     * @param validTime 缓存中元素有效时间，小于0表示元素不会失效，失效规则见{@link SimpleCache#isExpired(CacheObject)}
     * @param cacheFullRemoveType cache满时删除元素类型，见{@link CacheFullRemoveType}
     */
    public SimpleCache(int maxSize, long validTime, CacheFullRemoveType cacheFullRemoveType){
        if (maxSize <= 0) {
            throw new IllegalArgumentException("The maxSize of cache must be greater than 0.");
        }
        this.maxSize = maxSize;
        this.validTime = validTime < 0 ? -1 : validTime;
        this.cacheFullRemoveType = cacheFullRemoveType;
        this.cache = new ConcurrentHashMap<K, CacheObject<V>>(maxSize);
    }

    /**
     * 初始化缓存，默认元素不会失效，cache满时删除元素类型为{@link CacheFullRemoveType#ENTER_TIME_FIRST}
     * 
     * @param maxSize 缓存最大容量
     * @param validTime 缓存中元素有效时间，小于0表示元素不会失效
     */
    public SimpleCache(int maxSize, long validTime){
        this(maxSize, validTime, CacheFullRemoveType.ENTER_TIME_FIRST);
    }

    /**
     * 初始化缓存，默认元素不会失效
     * 
     * @param maxSize 缓存最大容量
     * @param cacheFullRemoveType cache满时删除元素类型，见{@link CacheFullRemoveType}
     */
    public SimpleCache(int maxSize, CacheFullRemoveType cacheFullRemoveType){
        this(maxSize, -1, cacheFullRemoveType);
    }

    /**
     * 初始化缓存，默认元素不会失效，cache满时删除元素类型为{@link CacheFullRemoveType#ENTER_TIME_FIRST}
     * 
     * @param maxSize 缓存最大容量
     */
    public SimpleCache(int maxSize){
        this(maxSize, -1);
    }

    /**
     * 初始化缓存，默认大小为64，元素不会失效，cache满时删除元素类型为{@link CacheFullRemoveType#ENTER_TIME_FIRST}
     * 
     * @param maxSize 缓存最大容量
     */
    public SimpleCache(){
        this(DEFAULT_MAX_SIZE, -1);
    }

    /**
     * 得到缓存最大容量
     * 
     * @return
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * 得到有效时间，以毫秒计
     * 
     * @return
     */
    public long getValidTime() {
        return validTime;
    }

    /**
     * 得到cache满时删除元素类型，见{@link CacheFullRemoveType}
     * 
     * @return
     */
    public CacheFullRemoveType getCacheFullRemoveType() {
        return cacheFullRemoveType;
    }

    /**
     * 得到缓存中元素个数，可能包含已经失效的元素。
     * 若需要得到有效元素个数 ，请使用{@link SimpleCache#getValidSize()}
     * 
     * @return
     */
    @Override
    public int getSize() {
        return cache.size();
    }

    /**
     * 得到缓存中有效元素个数
     * 
     * @return
     */
    public int getValidSize() {
        if (validTime == -1) {
            return cache.size();
        }

        int count = 0;
        for (Entry<K, CacheObject<V>> entry : cache.entrySet()) {
            if (entry != null && !isExpired(entry.getValue())) {
                count++;
            }
        }
        return count;
    }

    /**
     * 从缓存中获取元素，若元素不存在或是已经失效，返回null
     * 
     * @param key
     * @return
     */
    @Override
    public CacheObject<V> get(K key) {
        CacheObject<V> obj = cache.get(key);
        if (isExpired(obj)) {
            return null;
        } else {
            if (obj != null) {
                hitCount.incrementAndGet();
                setUsedInfo(obj);
            } else {
                missCount.incrementAndGet();
            }
            return obj;
        }
    }

    /**
     * 设置使用信息
     * 
     * @param obj
     */
    protected synchronized void setUsedInfo(CacheObject<V> obj) {
        if (obj != null) {
            obj.setUsedCount(obj.getUsedCount() + 1);
            obj.setLastUsedTime(System.currentTimeMillis());
        }
    }

    /**
     * 向缓存中添加元素
     * <ul>
     * <li>见{@link SimpleCache#put(Object, CacheObject)}</li>
     * </ul>
     * 
     * @param key key
     * @param value 元素值
     * @return
     */
    @Override
    public void put(K key, V value) {
        CacheObject<V> obj = new CacheObject<V>();
        obj.setData(value);
        obj.setForever(validTime == -1);
        put(key, obj);
    }

    /**
     * 向缓存中添加元素
     * <ul>
     * <li>若元素个数{@link SimpleCache#getSize()}小于最大容量，直接put进入，否则</li>
     * <li>若有效元素个数{@link SimpleCache#getValidSize()}小于元素个数{@link SimpleCache#getSize()}，去除无效元素
     * {@link SimpleCache#removeExpired()}后直接put进入，否则</li>
     * <li>若{@link SimpleCache#cacheFullRemoveType}等于{@link CacheFullRemoveType#NOT_REMOVE}，直接返回null，否则</li>
     * <li>按{@link SimpleCache#cacheFullRemoveType}删除元素后直接put进入</li>
     * </ul>
     * 
     * @param key key
     * @param obj 元素
     * @return
     */
    @Override
    public synchronized void put(K key, CacheObject<V> obj) {
        if (getSize() >= maxSize) {
            if (getValidSize() < cache.size()) {
                if (removeExpired() <= 0) {
                    return;
                }
            } else {
                if (cacheFullRemoveType == CacheFullRemoveType.NOT_REMOVE) {
                    return;
                }
                if (fullRemoveOne() == null) {
                    return;
                }
            }
        }
        obj.setEnterTime(System.currentTimeMillis());
        cache.put(key, obj);
    }

    /**
     * 缓存中某个key是否存在，若元素不存在或是已经失效，返回false
     * 
     * @param key
     * @return
     */
    @Override
    public boolean containsKey(K key) {
        return cache.containsKey(key) ? !isExpired(key) : false;
    }

    /**
     * 缓存中某个Key对应元素是否过期
     * 
     * @param key
     * @return
     */
    protected boolean isExpired(K key) {
        return validTime == -1 ? false : isExpired(cache.get(key));
    }

    /**
     * 从缓存中删除某个元素
     * <ul>
     * <li>若元素不存在，返回null</li>
     * <li>否则删除并返回该元素</li>
     * </ul>
     * 
     * @param key
     * @return
     */
    @Override
    public CacheObject<V> remove(K key) {
        return cache.remove(key);
    }

    /**
     * 缓存满时从缓存中按照{@link SimpleCache#cacheFullRemoveType}规则删除一个元素
     * <ul>
     * <li>若{@link SimpleCache#cacheFullRemoveType}为{@link CacheFullRemoveType#NOT_REMOVE}返回null，否则</li>
     * <li>按{@link SimpleCache#cacheFullRemoveType}从未过期元素中查找删除的元素删除，未查找到返回null</li>
     * </ul>
     * 
     * @param key
     * @return 返回删除的元素
     */
    protected CacheObject<V> fullRemoveOne() {
        if (MapUtils.isEmpty(cache) || cacheFullRemoveType == CacheFullRemoveType.NOT_REMOVE) {
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
                    if (compare(entry.getValue(), valueToRemove) < 0) {
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

    /**
     * 比较两个元素<br/>
     * <ul>
     * </ul>
     * <strong>关于比较的结果</strong>
     * <ul>
     * <li>obj1大于obj2返回1</li>
     * <li>obj1等于obj2返回0</li>
     * <li>obj1小于obj2返回-1</li>
     * </ul>
     * <strong>关于比较的规则</strong>
     * <ul>
     * <li>若obj1为null，obj2为null，则相等</li>
     * <li>若obj1为null，obj2不为null，则obj1小于obj2</li>
     * <li>若obj1不为null，则返回obj1的{@link CacheObject#compareTo(CacheObject, CacheFullRemoveType)}结果，函数参数为obj2和
     * {@link SimpleCache#cacheFullRemoveType}</li>
     * </ul>
     * 
     * @param obj1
     * @param obj2
     * @return
     */
    protected int compare(CacheObject<V> obj1, CacheObject<V> obj2) {
        return obj1 == null ? (obj2 == null ? 0 : -1) : obj1.compareTo(obj2, cacheFullRemoveType);
    }

    /**
     * 移除缓存中过期的元素
     * <ul>
     * <li>若validTime为-1，则返回0</li>
     * </ul>
     * 
     * @return 删除的元素个数
     */
    protected int removeExpired() {
        if (validTime == -1) {
            return 0;
        }

        int count = 0;
        for (Entry<K, CacheObject<V>> entry : cache.entrySet()) {
            if (entry != null && isExpired(entry.getValue())) {
                cache.remove(entry.getKey());
                count++;
            }
        }
        return count;
    }

    /**
     * 清空缓存
     */
    @Override
    public void clear() {
        cache.clear();
    }

    /**
     * 判断某个元素是否过期，过期返回true，否则返回false
     * <ul>
     * <li>若validTime为-1表示始终不过期</li>
     * <li>若元素为空或元素是否过期属性为true表示已经过期</li>
     * <li>否则，若缓存中元素非长期有效，并且元素进入时间加上过期时间小于当前时间，表示已经过期</li>
     * <li>否则未过期</li>
     * </ul>
     * 
     * @param obj
     * @return
     */
    protected boolean isExpired(CacheObject<V> obj) {
        return validTime != -1
               && (obj == null || obj.isExpired() || (obj.getEnterTime() + validTime) <= System.currentTimeMillis());
    }

    /**
     * 获取数据的监听器，在获取到数据后进行调用
     * 
     * @author Trinea 2012-4-4 下午11:49:04
     */
    public interface GetDataListener<K, V> {

        /**
         * 数据监听方法，获取到数据后，执行该方法
         * 
         * @param key key
         * @param value 数据
         */
        public void getDataListener(K key, CacheObject<V> value);
    }

    /**
     * 得到缓存命中次数
     **/
    public long getHitCount() {
        return hitCount.get();
    }

    /**
     * 得到缓存未命中次数
     **/
    public long getMissCount() {
        return missCount.get();
    }

    /**
     * 得到缓存命中率
     * 
     * @return
     */
    public synchronized double getHitRate() {
        long total = hitCount.get() + missCount.get();
        return (total == 0 ? 0 : ((double)hitCount.get()) / total);
    }
}
