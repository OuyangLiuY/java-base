# 什么是桥接模式

桥接模式（Bridge Pattern）是一种结构型设计模式，将抽象部分与实现部分分离，使它们可以独立变化。它通过**组合（而非继承）**的方式来实现代码的解耦，特别适合应对多维度变化的场景。

# 🎯 一、核心思想

“抽象”和“实现”分离，各自独立扩展，不再耦合。

和适配器不同，它不是“兼容”，而是“解耦”。

# 📘 二、现实类比：遥控器控制设备

有多个遥控器（抽象），多个设备（实现）：


| 维度1                     | 遥控器种类                          |
| ------------------------------ | ---------------------------------- |
| 维度2：设备种类 | 电视、音响、风扇等                 |

→ 每种遥控器都可以控制每种设备，不用每对组合都写一套代码。

# 🧱 三、Java 示例：遥控器控制设备

1️⃣ 设备接口（实现部分）

```java
public interface Device {
void turnOn();
void turnOff();
}
```

2️⃣ 具体设备

```java
public class TV implements Device {
public void turnOn() {
System.out.println("电视打开");
}
public void turnOff() {
System.out.println("电视关闭");
}
}
public class Radio implements Device {
public void turnOn() {
System.out.println("收音机打开");
}
public void turnOff() {
System.out.println("收音机关闭");
}
}
```


3️⃣ 抽象类：遥控器

```java
public abstract class RemoteControl {
    protected Device device;

    public RemoteControl(Device device) {
        this.device = device;
    }

    public abstract void togglePower();
}
```


4️⃣ 扩展抽象类：高级遥控器

```java
public class BasicRemote extends RemoteControl {
    private boolean isOn = false;

    public BasicRemote(Device device) {
        super(device);
    }

    @Override
    public void togglePower() {
        if (isOn) {
            device.turnOff();
        } else {
            device.turnOn();
        }
        isOn = !isOn;
    }
}
```

5️⃣ 客户端调用

```java
public class Main {
    public static void main(String[] args) {
        Device tv = new TV();
        Device radio = new Radio();

        RemoteControl tvRemote = new BasicRemote(tv);
        RemoteControl radioRemote = new BasicRemote(radio);

        tvRemote.togglePower();    // 打开电视
        radioRemote.togglePower(); // 打开收音机
    }
}
```

✅ 输出示例

```plaintext
电视打开
收音机打开
```

# 💡 四、应用场景

适用于：

- 一个类存在多个维度变化（如平台 + 设备）
- 避免类爆炸（如 3 个遥控器 * 4 个设备 = 12 个类）
- 希望在运行时组合不同的实现

# 🧾 五、与其他模式对比

模式
目的
桥接模式
抽象和实现解耦（多维度扩展）
适配器模式
接口不兼容的类之间兼容
装饰器模式
不改变原有接口基础上添加功能
代理模式
控制或增强对目标对象的访问

| 模式                     | 目的                          |
| ------------------------------ | ---------------------------------- |
| 桥接模式 | 抽象和实现解耦（多维度扩展）                 |
|适配器模式|接口不兼容的类之间兼容|
|装饰器模式|不改变原有接口基础上添加功能|
|代理模式|控制或增强对目标对象的访问|

📌 六、优缺点

优点 ✅
缺点 ❌
抽象与实现解耦，灵活扩展
增加系统结构复杂性
支持运行时组合，满足变化需求
抽象类和实现类建立桥接关系
降低子类数量（避免类爆炸）
初期设计需要良好的抽象能力

| 优点 ✅                     | 缺点 ❌                          |
| ------------------------------ | ---------------------------------- |
| 抽象与实现解耦，灵活扩展 | 增加系统结构复杂性                 |
|支持运行时组合，满足变化需求|抽象类和实现类建立桥接关系|
|降低子类数量（避免类爆炸）|初期设计需要良好的抽象能力|


📦 七、现实项目中的例子
- JDBC 桥接驱动： java.sql.DriverManager 作为桥接，屏蔽了底层数据库实现。
- 日志框架： SLF4J 是桥接接口，后面可以桥接 Log4j、Logback、Log4j2 等实现。
- Spring AOP： 使用代理桥接不同的切面逻辑和业务代码。


# 例子：
消息类型（抽象维度）：
•	报警消息
•	营销消息
•	通知消息
•	发送渠道（实现维度）：
•	短信（SMS）
•	邮件（Email）
•	微信（WeChat）

每种消息都可能使用不同的发送渠道组合，若用继承会导致类爆炸。
Sender 实现部分
```java
public interface Sender {
void send(String message);
}
```
✅ 发送渠道实现类

```java
public class EmailSender implements Sender {
    public void send(String message) {
        System.out.println("发送邮件：" + message);
    }
}

public class SmsSender implements Sender {
    public void send(String message) {
        System.out.println("发送短信：" + message);
    }
}

public class WeChatSender implements Sender {
    public void send(String message) {
        System.out.println("发送微信：" + message);
    }
}
```

Message: 抽象部分

```java
public abstract class Message {
    protected Sender sender;

    public Message(Sender sender) {
        this.sender = sender;
    }

    public abstract void send(String content);
}
```

不同消息类型：扩展抽象

```java
public class AlertMessage extends Message {
    public AlertMessage(Sender sender) {
        super(sender);
    }

    @Override
    public void send(String content) {
        sender.send("[报警] " + content);
    }
}

public class MarketingMessage extends Message {
    public MarketingMessage(Sender sender) {
        super(sender);
    }

    @Override
    public void send(String content) {
        sender.send("[营销] " + content);
    }
}

public class NotifyMessage extends Message {
    public NotifyMessage(Sender sender) {
        super(sender);
    }

    @Override
    public void send(String content) {
        sender.send("[通知] " + content);
    }
}
```

客户端：
```java
public class Main {
    public static void main(String[] args) {
        // 报警用短信发送
        Message alert = new AlertMessage(new SmsSender());
        alert.send("服务器宕机");

        // 通知用微信
        Message notify = new NotifyMessage(new WeChatSender());
        notify.send("会议将在3点开始");

        // 营销用邮件
        Message marketing = new MarketingMessage(new EmailSender());
        marketing.send("双11大促，满100减50！");
    }
}
```

优势总结：

| 特性                   | 描述                        |
| ------------------------------ | ---------------------------------- |
| 解耦抽象与实现 | 消息类型与发送渠道可以独立扩展                 |
|组合替代继承|避免子类爆炸，如 3 消息类型 × 3 渠道 = 9 类|
|运行时灵活组合|可以在运行时选择发送渠道|
|易测试、易维护|每个类职责单一，模块边界清晰|



