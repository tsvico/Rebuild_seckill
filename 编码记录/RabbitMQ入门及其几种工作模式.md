## 1.简介

MQ全程Message Queue，用于应用程序和应用程序间进行通信。RabbitMQ采用Erlang编写，实现了AMQP(高级消息队列)协议，跨平台，支持各种主流的操作系统和多种客户端。
RabbitMQ相比其他同类型的消息队列，最大的特点在保证可观的单机吞吐量的同时，延时方面非常出色。

## 2. 相关术语

Broker：通俗讲就是server，接收客户端连接，实现AMQP协议的消息队列和路由功能的进程；
Virtual Host：虚拟主机，类似于权限控制组。一个Virtual Host里可以有若干的Exchange和Queue，但权限控制的最小粒度是Virtual Host；
Producer：消息生产者；
Consumer：消息消费者；
Queue：存储消息的队列容器；
Message：生产者和消费者需要的消息数据；
Connection：一个tcp连接；
Channel：一个管道连接，是tcp连接内的连接(broker),使用现有的TCP连接进行数据传输；
Exchange：交换机，消息路由，生产者发送的消息并不是直接发送到队列中而是先到指定的路由中，然后由路由根据路由key绑定的队列发送到指定队列中；
Binding：建立路由和队列容器的绑定关系；
Routing key：路由key，主要用于寻找队列。

## 3. Exchange的几种工作模式

### 1. Direct--路由模式

任何发送到Direct Exchange的消息都会被转发到RouteKey指定的Queue。
这种模式下不需要将Exchange进行任何绑定(binding)操作。
消息传递时需要一个“RouteKey”，可以简单的理解为要发送到的队列名字。
如果vhost中不存在RouteKey中指定的队列名，则该消息会被抛弃。

### 2. Fanout--发布/订阅模式

任何发送到Fanout Exchange的消息都会被转发到与该Exchange绑定(Binding)的所有Queue上。
这种模式不需要RouteKey。
这种模式需要提前将Exchange与Queue进行绑定，一个Exchange可以绑定多个Queue，一个Queue可以同多个Exchange进行绑定。
如果接受到消息的Exchange没有与任何Queue绑定，则消息会被抛弃。

### 3. Topic--匹配订阅模式

任何发送到Topic Exchange的消息都会被转发到所有关心RouteKey中指定话题的Queue上。
就是每个队列都有其关心的主题，所有的消息都带有一个“标题”(RouteKey)，Exchange会将消息转发到所有关注主题能与RouteKey模糊匹配的队列。
这种模式需要RouteKey，也许要提前绑定Exchange与Queue。
在进行绑定时，要提供一个该队列关心的主题。
.“#”表示0个或若干个关键字，“*”表示一个关键字。
同样，如果Exchange没有发现能够与RouteKey匹配的Queue，则会抛弃此消息。

### 4. headers

headers exchange主要通过发送的request message中的header进行匹配，其中匹配规则（x-match）又分为all和any，all代表必须所有的键值对匹配，any代表只要有一个键值对匹配即可。headers exchange的默认匹配规则（x-match）是any。

## RabbitMQ工作模式

在 RabbitMQ 官网上提供了 6 中工作模式：简单模式、工作队列模式、发布/订阅模式、路由模式、主题模式 和 RPC 模式。

### 简单模式和工作队列模式

这两种模式非常简单，只涉及生产者、队列、消费者。
生产者负责生产消息，将消息发送到队列中，消费者监听队列，队列有消息就进行消费。
工作队列模式其实就是有多个消费者的简单模式。
当有多个消费者时，消费者平均消费队列中的消息。

### 发布/订阅、路由与主题模式

这三种模式就要用到Exchange了。
生产者不直接与队列交互，而是将消息发送到交换机中，再由交换机将消息发送到已绑定改交换机的队列中给消费者消费。
常用的交换机类型有 3 种：fanout、direct、topic。
fanout不处理路由键，很像子网广播，每台子网内的主机都获得了一份复制的消息。
发布/订阅模式就是指使用fanout交换模式。fanout 类型交换机转发消息是最快的。

direct模式处理路由键，需要路由键匹配才能转发。
路由模式使用的是 direct 类型的交换机。

topic：将路由键和某模式进行匹配。
主题模式使用的是 topic 类型的交换机。

### RPC 模式

客户端发送一个请求消息然后服务器回复一个响应消息。为了收到一个响应，我们需要发送一个'回调'的请求的队列地址。

https://www.cnblogs.com/xl2432/p/11026618.html