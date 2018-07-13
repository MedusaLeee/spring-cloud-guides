# 分布式配置中心Spring Cloud Config

## 为什么需要分布式配置中心

[这里只推荐一篇比较好的文章](https://mp.weixin.qq.com/s/FYxVbWiY__HBUyb73mfimA)

## 介绍

`Spring Cloud Config`是用来为分布式系统中的基础设施和微服务应用提供集中化的外部配置支持，
它分为服务端与客户端两个部分。

### 服务端

`服务端`是一个配置中心，可以部署多实例，并注册到`eureka`，支持从`git`、`svn`、`本地文件`加载配置文件。

### 客户端

`客户端`就是我们微服务体系中的各个微服务应用或者设施，从配置中心的`服务端`中加载自己所需的配置项。


## 项目

### server

引入

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-config-server</artifactId>
    </dependency>
    
application.yml

    spring:
      application:
        name: config-server
      cloud:
        config:
          server:
            git:
              uri: https://github.com/MedusaLeee/spring-cloud-config-repo-demo.git
              # searchPaths: repo  # 指定扫描仓库下的指定目录
              username: xxxxx
              password: xxx
    server:
      port: 7080

启动类上添加@EnableConfigServer注解开启配置服务器的功能

spring.cloud.config.server.git.uri：配置git仓库地址 
spring.cloud.config.server.git.searchPaths：配置仓库路径，可以配置多个 
spring.cloud.config.label：配置仓库的分支 
spring.cloud.config.server.git.username：访问git仓库的用户名 
spring.cloud.config.server.git.password：访问git仓库的用户密码

访问配置信息的URL与配置文件的映射关系如下：

    /{application}/{profile}[/{label}]
    /{application}-{profile}.yml
    /{label}/{application}-{profile}.yml
    /{application}-{profile}.properties
    /{label}/{application}-{profile}.properties
    
上面的url会映射{application}-{profile}.properties对应的配置文件，其中{label}对应Git上不同的分支，
默认为master。我们可以尝试构造不同的url来访问不同的配置内容，比如，要访问master分支，config-client应用的dev环境，
就可以访问这个url：`http://localhost:7080/config-client/dev/master`，并获得如下返回：

    {
        "name": "config-client",
        "profiles": [
            "dev"
        ],
        "label": "master",
        "version": "a24766a3b0cd2cb8135b57e16a24583a7d4eea25",
        "state": null,
        "propertySources": [
            {
                "name": "https://github.com/MedusaLeee/spring-cloud-config-repo-demo.git/config-client-dev.yml",
                "source": {
                    "info.profile": "dev",
                    "info.from": "git"
                }
            },
            {
                "name": "https://github.com/MedusaLeee/spring-cloud-config-repo-demo.git/config-client.yml",
                "source": {
                    "info.profile": "default",
                    "info.from": "git",
                    "test": "test"
                }
            }
        ]
    }
    
### client

引入

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-config</artifactId>
    </dependency>
    
application.yml

    spring:
      application:
        name: config-client
    server:
      port: 7081
 
bootstrap.yml

    spring:
      cloud:
        config:
          uri: http://localhost:7080/
          profile: default
          label: master
          

注意Spring读取顺序bootstrap.properties在application.yml前，所以基础配置最好在
application.yml配置

配置参数与Git中存储的配置文件中各个部分的对应关系如下：

    spring.application.name：对应配置文件规则中的{application}部分
    spring.cloud.config.profile：对应配置文件规则中的{profile}部分
    spring.cloud.config.label：对应配置文件规则中的{label}部分
    spring.cloud.config.uri：配置中心config-server的地址
    
启动类代码

    @SpringBootApplication
    @RestController
    public class ClientApplication {
        public static void main(String[] args) {
            SpringApplication.run(ClientApplication.class, args);
        }
        @Value("${info.from}")
        String from;
        @RequestMapping(value = "/info/from")
        public String from(){
            return from;
        }
    }
    
访问`http://localhost:7081/info/from`会返回`info.from`配置项的值为`git`  