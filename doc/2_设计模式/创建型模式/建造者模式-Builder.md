
# 建造者Builder Pattern

建造者模式（Builder Pattern）是经典的创建型设计模式之一，用于分步骤构建复杂对象，特别适用于对象具有很多字段或构建过程很复杂的场景。

# 思想：
- 将一个复杂对象的构建过程与其表示分离，使同样的构建过程可以创建不同的表示。

✅ 适用场景
•	对象有多个可选参数（如 JavaBean）
•	对象的构建过程复杂，需要分步骤进行
•	想让代码更具可读性（链式调用）

Lombok 的 @Builder 就是使用的这种设计模式。

# 代码

```java
public class Computer {
    private String cpu;
    private String memory;
    private String storage;
    private String gpu;

    // 私有构造函数，只能通过 Builder 创建
    private Computer(Builder builder) {
        this.cpu = builder.cpu;
        this.memory = builder.memory;
        this.storage = builder.storage;
        this.gpu = builder.gpu;
    }

    public static class Builder {
        private String cpu;
        private String memory;
        private String storage;
        private String gpu;

        public Builder setCpu(String cpu) {
            this.cpu = cpu;
            return this;
        }

        public Builder setMemory(String memory) {
            this.memory = memory;
            return this;
        }

        public Builder setStorage(String storage) {
            this.storage = storage;
            return this;
        }

        public Builder setGpu(String gpu) {
            this.gpu = gpu;
            return this;
        }

        public Computer build() {
            return new Computer(this);
        }
    }

    @Override
    public String toString() {
        return "Computer{" +
               "cpu='" + cpu + '\'' +
               ", memory='" + memory + '\'' +
               ", storage='" + storage + '\'' +
               ", gpu='" + gpu + '\'' +
               '}';
    }

    public static void main(String[] args) {
        Computer myPC = new Computer.Builder()
                .setCpu("Intel i9")
                .setMemory("32GB")
                .setStorage("1TB SSD")
                .setGpu("NVIDIA RTX 4080")
                .build();

        System.out.println(myPC);
    }
}
```