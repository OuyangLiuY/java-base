


# 一、工厂模式
定义：

工厂模式通过一个工厂类，根据输入参数创建 一个产品（一个类层级中的对象）。

✅ 适用场景：
•	你只需要创建 一类产品（如一种接口的多个实现）。
•	实例化过程复杂，或者需要统一管理。

⸻

🔧 示例：创建不同类型的水果

```java
public interface Fruit {
    void eat();
}
```
2 产品实现类：
```java
public class Apple implements Fruit {
    public void eat() {
        System.out.println("Eating Apple");
    }
}

public class Banana implements Fruit {
    public void eat() {
        System.out.println("Eating Banana");
    }
}
```

3、工厂类
```java
public class FruitFactory {
    public static Fruit createFruit(String type) {
        if (type.equalsIgnoreCase("apple")) {
            return new Apple();
        } else if (type.equalsIgnoreCase("banana")) {
            return new Banana();
        }
        throw new IllegalArgumentException("Unknown fruit type");
    }
}
```

Fruit fruit = FruitFactory.createFruit("apple");
fruit.eat();  // 输出：Eating Apple

🏭🛠 二、抽象工厂模式（Abstract Factory Pattern）

✅ 定义：

抽象工厂模式提供一个接口，用于创建 一族相关或相互依赖的对象，而无需指定具体类。

✅ 适用场景：
•	需要创建多个产品族（一组相关联的对象，比如按钮 + 输入框 + 下拉框）。
•	客户端希望通过同一接口获取不同的产品组合。

## 示例：不同平台的 UI 控件（Windows vs Mac）

1. 产品接口：
```java
interface Button {
    void click();
}

interface TextField {
    void input();
}
```
2. 不同平台的产品实现：
```java
class WinButton implements Button {
    public void click() {
        System.out.println("Click Windows Button");
    }
}

class MacButton implements Button {
    public void click() {
        System.out.println("Click Mac Button");
    }
}

class WinTextField implements TextField {
    public void input() {
        System.out.println("Input in Windows TextField");
    }
}

class MacTextField implements TextField {
    public void input() {
        System.out.println("Input in Mac TextField");
    }
} 
```
3. 抽象工厂接口：
```java
interface UIFactory {
    Button createButton();
    TextField createTextField();
}
```
4. 具体工厂类：
```java
class WindowsFactory implements UIFactory {
    public Button createButton() {
        return new WinButton();
    }

    public TextField createTextField() {
        return new WinTextField();
    }
}

class MacFactory implements UIFactory {
    public Button createButton() {
        return new MacButton();
    }

    public TextField createTextField() {
        return new MacTextField();
    }
}
```
5. 客户端使用：
```java
UIFactory factory = new MacFactory(); // 或 new WindowsFactory()

Button button = factory.createButton();
TextField textField = factory.createTextField();

button.click();        // 输出：Click Mac Button
textField.input();     // 输出：Input in Mac TextField
```

## 总结：区别一览


| 特性 | 工厂模式（Factory） |抽象工厂模式（Abstract Factory）|
| --|---------------| -------------- |
|创建对象数量| 一个类或接口的子类     |一组相关的产品族|
|产品结构|  单一产品等级结构     |多个产品等级结构|
|客户端传参| 通常传一个标识类型     |选择一个产品族工厂，不传具体类型|
|适用场景| 创建一种对象，逻辑分支少  |创建一组相关对象，产品组合灵活|

