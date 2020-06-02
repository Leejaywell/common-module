## Spring boot 初始化流程
### 1. Spring boot 两种启动方式
#### 1.1 Jar方式启动启动
  > 在main方法中使用加入这段代码即可生效。
  ```java
    class App {
        public static void main(String[] args){
          SpringApplication.run(Application.class, args);
        }
    }
  ```
#### 1.2 war包启动
 > 这种方式是以web容器启动应用，在servlet 3.0之后加入了SCIs机制。Spring boot实现了ServletContainerInitializer接口，配合HandlesTypes注解
    对Spring boot应用初始化，而SpringServletContainerInitializer会对WebApplicationInitializer的实现类进行初始化。
    
```java
    @HandlesTypes(WebApplicationInitializer.class)
    public class SpringServletContainerInitializer implements ServletContainerInitializer {
    }   
```

### 2. Spring Application环境初始化
 > SpringApplication类会从spring.factories文件中读取ApplicationContextInitializer和ApplicationListener进行初始化，并将SpringApplication的primarySource
    作为spring扫描配置的基础配置源。
    
#### 2.1 初始化spring environment
>根据环境类型WebApplicationType.NONE,WebApplicationType.SERVLET,WebApplicationType.REACTIVE创建相应的Environment，并发布ApplicationEnvironmentPreparedEvent事件，
    并发布ApplicationEnvironmentPreparedEvent事件会触发ConfigFileApplicationListener监听器加载EnvironmentPostProcessor的实现类并执行postProcessEnvironment。
    postProcessEnvironment方法会将初始化的Environment按Ordered排序顺序执行。
    
```java
    private ConfigurableEnvironment prepareEnvironment(
                SpringApplicationRunListeners listeners,
                ApplicationArguments applicationArguments) {
            // Create and configure the environment
            ConfigurableEnvironment environment = getOrCreateEnvironment();
            configureEnvironment(environment, applicationArguments.getSourceArgs());
            listeners.environmentPrepared(environment);
            bindToSpringApplication(environment);
            if (!this.isCustomEnvironment) {
                environment = new EnvironmentConverter(getClassLoader())
                        .convertEnvironmentIfNecessary(environment, deduceEnvironmentClass());
            }
            ConfigurationPropertySources.attach(environment);
            return environment;
        }
```

#### 2.2 日志系统的初始化
  > 主要分为两个流程，在初始化是执行SpringApplication.run会触发ApplicationStartingEvent事件，LoggingApplicationListener监听到此事件会检测当前classpath里采用的日志系统,如果存在多个日志系统
    会按照Logback,log4j2,java logging的顺序取第一个。之后Envrioment实现类，注意这个时候的Environment还没有加载任何配置，ConfigFileApplicationListener和LoggingApplicationListener会依次
    接收到ApplicationEnvironmentPreparedEvent事件，当ConfigFileApplicationListener会完成初始化加载配置，这个时候LoggingApplicationListener会完成日志配置的初始化。这意味着LoggingApplicationListener
    初始化之前应用启动的日志是不能输出的（在web容器环境下启动例外，例如tomcat日志会输出到catalina.out）。
    
### 3. spring context初始化
 > 依旧是检测环境类型WebApplicationType.NONE,WebApplicationType.SERVLET,WebApplicationType.REACTIVE创建相应的spring context,执行ApplicationContextInitializer对spring context进行初始化。

### 4. 刷新spring context
 > spring framework的执行流程（略）