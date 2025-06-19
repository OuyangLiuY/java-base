
# 🧠 一、核心思想

通过 克隆已有对象（原型） 来快速创建新对象，避免重复初始化成本。


# 二、适用场景
- 创建对象的成本比较高（如数据库查询、复杂运算）
- 需要大量创建结构相似的对象
- 想要避免使用 new 导致耦合，或者隐藏复杂构造逻辑
- 创建对象时需要保留某种状态作为模板

⸻

## 🛠️ 三、Java 示例（实现 Cloneable）

1、原型类：

```java
public class Document implements Cloneable {
    private String title;
    private String content;

    public Document(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // 克隆方法
    @Override
    public Document clone() {
        try {
            return (Document) super.clone(); // 浅拷贝
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void show() {
        System.out.println("Title: " + title + ", Content: " + content);
    }
}
```
2. 使用方式：

```java
public class Main {
    public static void main(String[] args) {
        Document doc1 = new Document("Prototype Pattern", "This is a design pattern.");
        Document doc2 = doc1.clone(); // 克隆

        doc2.setTitle("Cloned Document");

        doc1.show();  // 输出: Prototype Pattern
        doc2.show();  // 输出: Cloned Document
    }
}
```
# 🔍 四、浅拷贝 vs 深拷贝


| 类型 | 描述 |
| --|---------------|
|浅拷贝|复制对象本身，但引用类型成员仍指向同一内存地址|
|深拷贝|除了对象本身，连引用的成员对象也一并复制|

➕ 示例：浅拷贝引用共享问题
```java
class Person implements Cloneable {
    String name;
    Address address; // 引用类型

    public Person(String name, Address address) {
        this.name = name;
        this.address = address;
    }

    @Override
    public Person clone() {
        try {
            return (Person) super.clone(); // 浅拷贝：address 是同一个对象
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
```
✅ 若需深拷贝，可以这样做：
```java
@Override
public Person clone() {
    try {
        Person cloned = (Person) super.clone();
        cloned.address = address.clone(); // 手动复制内部对象
        return cloned;
    } catch (CloneNotSupportedException e) {
        throw new RuntimeException(e);
    }
}
```
优点
缺点
避免重复创建对象
实现深拷贝麻烦
性能更高（省去构造成本）
若对象复杂或含循环引用，克隆容易出错
对已有对象做备份或回滚
Cloneable 接口设计不优雅


| 优点 | 缺点 |
| --|---------------|
|避免重复创建对象|实现深拷贝麻烦|
|性能更高（省去构造成本）|若对象复杂或含循环引用，克隆容易出错|
|对已有对象做备份或回滚|Cloneable 接口设计不优雅|

# ✅ 六、原型模式 vs 工厂模式

特点
工厂模式
原型模式
通过构造创建对象
✅
❌（通过 clone）
对象是否共享模板
否，每次 new
是，从已有对象复制
是否支持自定义状态
通常构造时传参
是，可从已有对象复制并修改


| 特点 | 工厂模式 |原型模式|
| --|---------------| ----|
|通过构造创建对象|✅|❌（通过 clone）|
|对象是否共享模板）|否，每次 new|是，从已有对象复制|
|是否支持自定义状态|通常构造时传参|是，可从已有对象复制并修改|

🚀 实战应用场景

- Spring 中的 bean 若设置为 prototype scope，就是每次获取都克隆一个实例
- 游戏中创建大量相似角色（怪物、子弹、道具）
- 图形界面复制粘贴组件
- 编辑器中 Ctrl+C / Ctrl+V 功能