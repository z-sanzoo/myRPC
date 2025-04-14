# 构建自己的RPC
## V0版本笔记
在使用ObjectOutputStream和ObjectInputStream时的初始化顺序非常重要
- ObjectOutputStream的初始化必须在ObjectInputStream之前
- 当创建objectOutputStream时，它会立即给输出流一个序列话的头部AC ED 00 05...表示这是一个JAVA的序列话流,
- AC ED 00 05是序列化流的魔数,为了与别的数据流区分,表示是一个序列话流,对方的ObjectInputStream会初始化反序列化的环境
- 如果另一个端没有先创建objectInputStream流,而是也先创建objectOutputStream流,那么双方都会等待对方发送头部导致死锁
- ObjectInputStream的初始化会阻塞的读取头部,如果没有头部,就会一直等待


## V1版本笔记
class<T> 是一个范型类如果 , class stringClass = String.class 编译器会警告不匹配 , 用 class<?> stringClass = String.class 这样就不会有警告了

### Java的动态代理
java的动态代理都是针对借口来实现的而不是针对类
Proxy.newProxyInstance(ClassLoader loader, Class<?>[] interfaces, InvocationHandler h) 是动态代理的核心方法
这个方法会创建一个新的类,这个类实现了所有的接口,并且重写了所有的方法,在方法中调用InvocationHandler的invoke方法
如果在本地有方法的实现,可以定义一个实现类然后在invoke方法中调用实现类的方法,如果在远程实现,可以在invoke方法中调用远程的方法

```java
// 下面是接口
public interface UserService {
    void saveUser(String name);
}
//下面是实现类
public class UserServiceImpl implements UserService {
    @Override
    public void saveUser(String name) {
        System.out.println("保存用户: " + name);
    }
}
//实现InvocationHandler
public class MyInvocationHandler implements InvocationHandler {
    private final Object target; // 真实对象

    public MyInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("代理拦截方法: " + method.getName());
        return method.invoke(target, args); // 调用真实对象的方法
    }
}
// 创建一个代理对象
public static void main(String[] args) {
    UserService realService = new UserServiceImpl();
    InvocationHandler handler = new MyInvocationHandler(realService);

    // 创建代理对象
    UserService proxy = (UserService) Proxy.newProxyInstance(
            realService.getClass().getClassLoader(),//这个
            realService.getClass().getInterfaces(),
            handler
    );

    proxy.saveUser("Alice"); // 输出代理逻辑和真实逻辑
}
//代理类的结构:
// JVM 动态生成的代理类（示例）
public final class $Proxy0 extends Proxy implements UserService, Loggable {
    private final InvocationHandler handler;

    public $Proxy0(InvocationHandler handler) {
        this.handler = handler;
    }

    @Override
    public void addUser(String name) {
        handler.invoke(this,
                Method.of("addUser"),  // 反射获取方法
                new Object[]{name}     // 参数
        );
    }
}
```

代理让本地用户无痛使用远程服务
```java
ClientProxy clientProxy = new ClientProxy("127.0.0.1",8080);
UserService userService = clientProxy.getProxy(UserService.class);
User user = userService.getUserById(1);
```
## V2版本
封装了类WorkThread使用线程池加快效率
WorkThread中建立socket的输入输出流

使用ServiceProvider来注册服务,只要输入实现类就可以注册到map里面

## V3 版本
* 对前端代码进行重构,抽象出RPCClient接口,这个接口只有sent方法,
* 这个接口可以有多种实现,在实现时传入host和port参数,生成一个客户端对象
* 用这个客户端初始化代理对象

```java
    RPCClient RPCClient = new SimpleRPCClient("127.0.0.1", 8080);
    // 把这个客户端传入代理客户端
    ClientProxy clientProxy = new ClientProxy(RPCClient);
    // 代理客户端根据不同的服务，获得一个代理类， 并且这个代理类的方法以或者增强（封装数据，发送请求）
    UserService userService = clientProxy.getProxy(UserService.class);
```


* 当前服务端采用BIO的方式,效率很低,尝试采用NIO的方法传输数据,gRPC,Dubbo都是采用Netty来实现的,我们也用Netty
