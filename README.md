不要重复造轮子，撸一个可扩展的自定义直方图
结合之前做过的以及参考的文档，撸了一个自定义的直方统计图，注意考虑有一下几点。

1：间距的可扩展 
2：矩形宽度的可扩展 
3：颜色的可扩展（文本，矩形，坐标轴） 
4：数据的可扩展性，通过一个接口，传递各项数据
说了这么多看一下效果图再说：
![image](https://github.com/wangchang163/HistogramView/blob/master/images/device-2017-07-31-103542.png) 

![image](https://github.com/wangchang163/HistogramView/blob/master/images/aa.gif) 
![image](https://github.com/wangchang163/HistogramView/blob/master/images/bb.gif) 
![image](https://github.com/wangchang163/HistogramView/blob/master/images/dd.gif) 
![image](https://github.com/wangchang163/HistogramView/blob/master/images/ee.gif) 
我觉得大多数就是在此基础上修改数据，颜色，尺寸等等，因此我觉得还是有一定的实用性。

博客地址：

http://blog.csdn.net/qq_16131393/article/details/76233999


如何使用

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
Step 2. Add the dependency

	dependencies {
	        compile 'com.github.wangchang163:HistogramView:v1.0'
	}
