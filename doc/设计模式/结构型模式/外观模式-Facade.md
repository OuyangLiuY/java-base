# 什么是外观模式

`外观模式（Facade Pattern）是一种结构型设计模式，用于为复杂子系统提供一个统一的简单接口，让调用者不需要了解系统内部的复杂逻辑，只通过这个“门面”类来完成操作。`

# 🎯 一、核心思想

> 提供一个高层接口，**封装多个底层子系统**，简化外部调用者的使用。

# ✅ 二、使用场景

- 一个复杂子系统对外只需要暴露一个简单接口
- 为旧系统增加统一接口
- 减少模块之间的依赖和耦合
- 前后端之间统一调用接口（如Controller层）

🧱 三、生活类比

比如你去看电影，你只要：

```plaintext
买票 → 进场 → 坐下 → 看电影
```

但实际上影院系统可能涉及：

* 检票子系统
* 灯光子系统
* 音响子系统
* 播放系统

如果你直接和这些系统交互，复杂而低效。影院服务员就是“外观”：你只需和一个人沟通，就能完成整个流程。

# 🛠️ 四、Java 示例：家庭影院

🎬 子系统类

```java
class DVDPlayer {
public void on() { System.out.println("DVD Player ON"); }
public void play() { System.out.println("DVD Playing"); }
public void off() { System.out.println("DVD Player OFF"); }
}class Projector {
public void on() { System.out.println("Projector ON"); }
public void off() { System.out.println("Projector OFF"); }
}class TheaterLights {
public void dim() { System.out.println("Lights dimmed"); }
public void on() { System.out.println("Lights ON"); }
}
```

外观类（Facade）

```java
class HomeTheaterFacade {
    private DVDPlayer dvd;
    private Projector projector;
    private TheaterLights lights;

    public HomeTheaterFacade(DVDPlayer dvd, Projector projector, TheaterLights lights) {
        this.dvd = dvd;
        this.projector = projector;
        this.lights = lights;
    }

    public void watchMovie() {
        System.out.println("=== Start Movie ===");
        lights.dim();
        projector.on();
        dvd.on();
        dvd.play();
    }

    public void endMovie() {
        System.out.println("=== End Movie ===");
        dvd.off();
        projector.off();
        lights.on();
    }
}
```

客户端调用：

```java
public class Main {
public static void main(String[] args) {
DVDPlayer dvd = new DVDPlayer();
Projector projector = new Projector();
TheaterLights lights = new TheaterLights();

HomeTheaterFacade homeTheater = new HomeTheaterFacade(dvd, projector, lights);

homeTheater.watchMovie();
System.out.println();
homeTheater.endMovie();
}
}
```

输出结果

```plaintext
=== Start Movie ===
Lights dimmed
Projector ON
DVD Player ON
DVD Playing=== End Movie ===
DVD Player OFF
Projector OFF
Lights ON
```

# 📦 五、优缺点总结


| 优点 ✅                        | 缺点 ❌                            |
| ------------------------------ | ---------------------------------- |
| 降低系统复杂度（简化外部接口） | 不易扩展子系统功能                 |
| 隐藏子系统细节，增强封装性     | 若过度包装，可能造成新的“上帝类” |
| 解耦客户端与子系统             | 外观类可能成为单点故障/耦合集中点  |

# 六、与其他模式对比


| 模式      | 作用                         |
| ---------- | ---------------------------- |
| 外观模式   | 对多个子系统提供统一简化接口 |
| 适配器模式 | 适配一个接口到另一个接口     |
| 装饰器模式 | 扩展对象功能，强调行为增强   |
| 代理模式   | 控制访问（如权限、缓存）     |

实际应用场景举例

- Spring MVC 的 DispatcherServlet 就是前端控制器的“外观”，封装了请求分发、数据绑定、视图解析等一系列子系统。
- 电商系统中创建订单，可能涉及库存、积分、支付等模块，使用外观统一入口封装这些服务。
- KafkaProducer、HttpClient、JdbcTemplate等封装复杂底层调用。
