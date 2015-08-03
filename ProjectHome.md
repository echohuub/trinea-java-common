# 个人博客 [http://www.trinea.cn/](http://www.trinea.cn/) #

### 已停止维护, 内容并入trinea-android-common，请使用[trinea-android-common](https://github.com/Trinea/AndroidCommon) ###

自己在开发过程中总结的一些Java工具类，主要包括SimpleCache——小型缓存、AutoGetDataCache——自动获取新数据的缓存， ArrayUtils——数组工具类、FileUtils——文件操作工具类、JSONUtils——Json工具类、ListUtils——list工具类、MapUtils——map工具类、ObjectUtils——Object工具类、RandomUtils——随机数工具类、SerializeUtils——序列化工具类、StringUtils——字符串工具类、HttpUtils——http工具函数。单测覆盖率都在80%左右

功能及使用介绍见[Java公用函数库](http://trinea.iteye.com/blog/1533616)

其中重点的AutoGetDataCache介绍见**[自动获取新数据的缓存AutoGetDataCache](http://trinea.iteye.com/blog/1533614)**
### 2.0.3版 ###
1、ListUtils增加isEquals函数

2、SimpleCache去除getValidSize接口，修改getSize接口

### 2.0.2版 ###
1、修改Cache的putAll接口参数

### 2.0.1版 ###
1、FileUtils增加InputStream写文件、得到文件大小工具函数

2、Cache增加putAll、entrysets、keysets、values接口，修改put函数返回值

3、SimpleCache增加putAll、entrysets、keysets、values函数，修改构造函数，修改put函数返回值

4、AutoGetDataCache修改构造函数、函数名，增加注释，修改getAndAutoCacheNewData函数名为get

5、StringUtils增加半角字符、全角字符相互转换函数

6、补充测试用例

### 2.0版 ###
1、增加SimpleCache小型缓存和AutoGetDataCache自动获取新数据的缓存

2、增加SerializeUtils序列化工具类

3、删除之前没用的函数补充注释

### 1.0版 ###
1、ArrayUtils——数组工具类、FileUtils——文件操作工具类、JSONUtils——Json工具类、ListUtils——list工具类、MapUtils——map工具类、ObjectUtils——Object工具类、RandomUtils——随机数工具类、StringUtils——字符串工具类、HttpUtils——http工具函数