# Spring Cloud客户端负载均衡Ribbon示例

## 组件介绍

### eureka-server

提供服务注册的`eureka server`，监听`8760`端口

`application.yml`配置如下：

    spring:
      application:
        name: EUREKA-SERVER1  # name必须为大写，因为EUREKA会把服务的名字全部大写，即使写的是小写，如果写小写将找不到服务
    
    server:
      port: 8760
    
    eureka:
      instance:
        # 如果hostname写成localhost会相互发现不了，集群中hostname不能一样
        hostname: eureka-server1
      server:
        # 只能在开发环境关闭
        enable-self-preservation: false
      client:
        registerWithEureka: false
        fetchRegistry: false




### provider

作为服务接口提供者并注册到`eureka-server`上，我们将开启俩个实例，来看`Ribbon`的客户端
负载均衡效果，`provider`默认监听`8080`端口，另一个实例手动指定为`8081`端口

`application.yml`配置如下：

    spring:
      application:
        name: PROVIDER # name必须为大写，否则在消费时通过这个name找不到provider
    server:
      port: 8080
    eureka:
      client:
        serviceUrl:
          defaultZone: http://localhost:8760/eureka/

### consumer

作为服务接口的消费者并注册到`eureka-server`上，监听`8082`端口

`application.yml`配置如下：

    spring:
      application:
        name: CONSUMER
    server:
      port: 8082
    eureka:
      client:
        serviceUrl:
          defaultZone: http://localhost:8760/eureka/

`ConsumerController`代码如下：

    @RestController
    public class ConsumerController {
        @Autowired
        RestTemplate restTemplate;
        @RequestMapping(value = "/ribbon-consumer",method = RequestMethod.GET)
        public String helloController() {
            // http://PROVIDER/hello中的PROVIDER为服务提供的application name，必须为大写
            return restTemplate.getForEntity("http://PROVIDER/hello", String.class).getBody();
        }
    }

## 测试

1. 在项目根目录(`spring-cloud-ribbon`)下执行`maven clean package`
2. 进入`eureka-server/target`目录执行`java -jar eureka-server-1.0.jar`，开启服务注册server
3. 进入`eureka-server/provider`目录分别执行`java -jar provider-1.0.jar`和`java -jar provider-1.0.jar --server.port=8081 `，
开启2个服务接口提供者实例
4. 进入`eureka-server/consumer`目录执行`java -jar consumer-1.0.jar `，开启服务接口的消费者
5. 访问`consumer`的测试接口`http://localhost:8082/ribbon-consumer`，观察`provider`的日志，然后停掉一个`provider`实例，观察日志

## 结果

两个`provider`实例默认是轮训，每个处理一个请求。停掉一个实例后，`consumer`不受影响，仅由剩下的`provider`实例提供服务