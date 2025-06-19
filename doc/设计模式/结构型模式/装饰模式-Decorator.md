
# 什么是装饰器模式？
装饰器模式（Decorator Pattern）是一种结构型设计模式，在不改变原有对象结构的前提下，动态地扩展对象的功能。相比继承，它更灵活、可组合，常用于增强已有对象的行为。


🎯 一、核心思想

使用一个或多个“包装类”，包裹原始对象，在调用时添加新行为。


🧱 二、使用场景
•	动态添加功能（而不是静态继承）
•	控制增强粒度（可按需组合多个装饰器）
•	如：IO流、日志系统、权限增强、AOP 等


📘 三、现实类比

咖啡是基础（原始对象），你可以加奶、加糖、加冰、加香草等（装饰器）。
每种“添加”都是一层装饰，但咖啡本质没变。


🛠️ 四、Java 实例：咖啡装饰器
```java
public interface Coffee {
    String getDescription();
    double getCost();
}
```
具体组件
```java
public class SimpleCoffee implements Coffee {
    public String getDescription() {
        return "原味咖啡";
    }

    public double getCost() {
        return 5.0;
    }
}
```

抽象装饰器
```java
public abstract class CoffeeDecorator implements Coffee {
    protected Coffee coffee;

    public CoffeeDecorator(Coffee coffee) {
        this.coffee = coffee;
    }
}
```
具体装饰器：加奶
```java
public class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }

    public String getDescription() {
        return coffee.getDescription() + " + 牛奶";
    }

    public double getCost() {
        return coffee.getCost() + 2.0;
    }
}

public class SugarDecorator extends CoffeeDecorator {
    public SugarDecorator(Coffee coffee) {
        super(coffee);
    }

    public String getDescription() {
        return coffee.getDescription() + " + 糖";
    }

    public double getCost() {
        return coffee.getCost() + 1.0;
    }
}
```

客户端调用：
```java
public class Main {
    public static void main(String[] args) {
        Coffee coffee = new SimpleCoffee();                     // 原味
        coffee = new MilkDecorator(coffee);                     // 加牛奶
        coffee = new SugarDecorator(coffee);                    // 加糖

        System.out.println(coffee.getDescription()); // 原味咖啡 + 牛奶 + 糖
        System.out.println("价格: " + coffee.getCost()); // 5 + 2 + 1 = 8
    }
}
```
🔍 五、与继承对比

| 特性  | 继承|装饰器|
| --- | --- | --- |
| 编译时确定行为 | ✅ |❌（运行时组合）|
|代码复用方式|单一继承|组合多个装饰器|
|灵活性|差（层级一改全改）|高（可组合、可解耦）|
|可扩展性|差|强|


六、真实应用示例
•	Java IO： BufferedReader 装饰 FileReader

```java
Reader r = new BufferedReader(new FileReader("a.txt"));
```

Spring AOP： 动态添加日志、事务、权限等功能
日志系统： 装饰器实现动态输出到控制台、文件、网络等

📌 七、总结

| 优点 ✅ | 缺点 ❌|
| ------------------------------ | ---------------------------------- |
| 不修改原对象结构即可增强功能 | 增加了类的数量（每种增强一个类） |
|可灵活组合多个功能|复杂调用链可能降低调试可读性|
|遵循开闭原则（Open/Closed）|实现细节要遵守统一接口|

如果你有一个业务模块（比如订单、用户、日志、鉴权）想“插件式增强”，可以考虑装饰器。
适用于你希望为 已有业务逻辑 添加 日志、鉴权、限流、埋点、事务 等功能，而不改动原方法代码的场景。

