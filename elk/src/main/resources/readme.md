#springboot with elk and redis demo
这个程序将日志直接发送给redis，redis再将日志发送给logstash，logstash然后发送给ES

#程序发送给logstash的例子
https://www.cnblogs.com/zhyg/p/6994314.html

#注意
这两个例子都使用一个额外的参数：appname,这个参数可以作为ES的index，
便于区分是哪个程序的日志，也便于查询处理日志数据
MDC可以在分布式系统中作为追踪日志流程的标记