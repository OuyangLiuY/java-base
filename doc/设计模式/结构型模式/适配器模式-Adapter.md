# 什么是适配器模式

适配器模式（Adapter Pattern）是一种结构型设计模式，主要用于将一个类的接口转换成客户端所期望的另一个接口。它就像一个“插头转换器”，让原本接口不兼容的两个类能协同工作。


# 🎯 一、使用场景

当你希望复用一个已有类，但它的接口与当前系统不兼容时。

# ✅ 二、现实类比

你在中国（220V），买了个美国产的电吹风（110V三孔），插不进去怎么办？

👉 你需要一个“插头转换器”，这就是“适配器”。

# 🛠️ 三、Java 示例

📘 场景设定：

- 我们的系统期望使用接口 Target 来调用 request() 方法。
- 但现有的类 Adaptee 提供的是 specificRequest() 方法
- 适配器 Adapter 帮我们转换接口。

原接口： Target

```java
public interface Target {
    void request();
}
```

现有类： Adaptee 不兼容

```java
public class Adaptee {
    public void specificRequest() {
        System.out.println("Specific request from Adaptee");
    }
}
```

适配器类： Adapter

```java
public class Adapter implements Target {
    private Adaptee adaptee;

    public Adapter(Adaptee adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void request() {
        // 调用现有类的方法
        adaptee.specificRequest();
    }
}
```

客户端代码：

```java
public class Main {
    public static void main(String[] args) {
        Adaptee adaptee = new Adaptee();
        Target adapter = new Adapter(adaptee);

        adapter.request();  // 客户端无感知
    }
}
```

# ✅ 四、适配器模式的分类


| 类型       | 说明                                       |
| ---------- | ------------------------------------------ |
| 对象适配器 | 使用组合方式，适配类作为成员（推荐，灵活） |
| 类适配器   | 使用继承方式（Java 单继承限制，使用较少）  |

类适配器示例：通过继承

也就是说一个类中有继承类Adaptee，有实现类Target。那么在interface的接口中调用继承类的方法，以此来适配。

```java
public class ClassAdapter extends Adaptee implements Target {
    @Override
    public void request() {
        specificRequest();
    }
}
```

# 🔧 五、实际应用场景


| 场景    | 举例                                           |
| ------- | ---------------------------------------------- |
| Java IO | InputStreamReader 把 InputStream 适配为 Reader |
| JDBC    | DriverAdapter                                  |
| Spring  | HandlerAdapter：用于兼容不同类型的 Controller  |

# ⚖️ 六、对比其他模式


| 模式       | 核心目的                 |
| ---------- | ------------------------ |
| 适配器模式 | 接口不兼容，但需要协作   |
| 外观模式   | 简化子系统多个接口的调用 |
| 代理模式   | 控制访问、权限或增强功能 |

# 📌 七、总结


| 优点 ✅                | 缺点 ❌                  |
| ---------------------- | ------------------------ |
| 解耦客户端与不兼容的类 | 增加代码层级             |
| 提高复用性             | 可能带来性能或调试复杂度 |
| 易于集成已有系统       | 对多个接口适配较复杂     |
