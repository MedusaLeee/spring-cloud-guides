# Spring Cloud服务发现Eureka示例

包含三个模块两个`server`分别是`server1`和`server2`，一个`client`

实现了`eureka`集群相互发现和高可用

# 集群相互发现方式

`server1`和`server2`相互注册对方

## 测试

分别访问`http://localhost:8760`和`http://localhost:8761`

# client注册到集群

`client`分别将server1`和`server2`注册为server.



