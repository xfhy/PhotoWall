# PhotoWall

LruCache和DiskLruCache的完美结合: 照片墙

参考郭神的https://blog.csdn.net/guolin_blog/article/details/34093441实现

用RecyclerView实现,简单优化了下代码,将一些繁杂的操作从adapter中抽取出来,adapter只是用来展示布局的,不应该用来实现逻辑.

效果如下:

<p>
<img src="/pic/pic1.png" width="225" height="400"/>
<img src="/pic/pic2.png" width="225" height="400"/>
</p>