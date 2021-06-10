首次尝试微服务，项目中还存在着很多问题

项目使用了springcloudAlibaba、dubbo、RabbitMQ、Redis等工具

开发笔记见编码记录目录

使用方式
1. 启动rabbit-mq
2. 启动Nacos 1.2.1
3. 启动redis
4. 启动MySQL，并导入sql文件

   导入项目文件并依次启动

​    `miaosha-other`  8087
​    `miaosha-service` 8886

​	`api-gateway` 8666 

