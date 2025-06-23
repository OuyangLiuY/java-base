

## 责任链模式

### 基本介绍

在责任链模式中，客户只需要将请求发送到责任链上即可，无须关心请求的处理细节和请求的传递过程，请求会自动进行传递。所以责任链将请求的发送者和请求的处理者解耦了。

责任链模式是一种对象行为型模式，

**其主要优点如下：**

1. 降低了对象之间的耦合度。该模式使得一个对象无须知道到底是哪一个对象处理其请求以及链的结构，发送者和接收者也无须拥有对方的明确信息。
2. 增强了系统的可扩展性。可以根据需要增加新的请求处理类，满足开闭原则。
3. 增强了给对象指派职责的灵活性。当工作流程发生变化，可以动态地改变链内的成员或者调动它们的次序，也可动态地新增或者删除责任。
4. 责任链简化了对象之间的连接。每个对象只需保持一个指向其后继者的引用，不需保持其他所有处理者的引用，这避免了使用众多的 if 或者 if···else 语句。
5. 责任分担。每个类只需要处理自己该处理的工作，不该处理的传递给下一个对象完成，明确各类的责任范围，符合类的单一职责原则。

**其主要缺点如下：**

1. 不能保证每个请求一定被处理。由于一个请求没有明确的接收者，所以不能保证它一定会被处理，该请求可能一直传到链的末端都得不到处理。
2. 对比较长的职责链，请求的处理可能涉及多个处理对象，系统性能将受到一定影响。
3. 职责链建立的合理性要靠客户端来保证，增加了客户端的复杂性，可能会由于职责链的错误设置而导致系统出错，如可能会造成循环调用。

### 模式的应用实例

分析：假如规定学生请假小于或等于 2 天，班主任可以批准；小于或等于 7 天，系主任可以批准；小于或等于 10 天，院长可以批准；其他情况不予批准；这个实例适合使用职责链模式实现。

**代码实现：**

```java
package ChainOfResponsibility;

/**
 * 分析：假如规定学生请假小于或等于 2 天，班主任可以批准；
 * 小于或等于 7 天，系主任可以批准；
 * 小于或等于 10 天，院长可以批准；
 * 其他情况不予批准；这个实例适合使用职责链模式实现。
 */
public class LeaveApprovalTest {
    public static void main(String[] args) {
        ClassAdviser adviser = new ClassAdviser();
        HeaderTeacher teacher = new HeaderTeacher();
        Dean dean = new Dean();
        DeanOfStudies deanOfStudies = new DeanOfStudies();
        adviser.setNext(teacher);
        teacher.setNext(dean);
        dean.setNext(deanOfStudies);
        adviser.handlerRequest("张三",8);
    }
}
// 抽象领导类
abstract class Leader{
    private Leader next;

    public Leader getNext() {
        return next;
    }

    public void setNext(Leader next) {
        this.next = next;
    }
    public abstract void handlerRequest(String name ,int leaveDays);
}
// 班主任处理类
class ClassAdviser extends Leader{
    @Override
    public void handlerRequest(String name ,int leaveDays) {
        if(leaveDays <= 2){
            System.out.println("班主任批准"+name+"请假[" + leaveDays + "]天。");
        }else {
            if(getNext() != null){
                System.out.println("班主任无权限处理，请求传递中..." + name);
                getNext().handlerRequest(name,leaveDays);
            }else {
                System.out.println("请假天数太多，没有人能批准该假条！");
            }
        }
    }
}

// 系班主任处理类
class HeaderTeacher extends Leader{
    @Override
    public void handlerRequest(String name ,int leaveDays) {
        if(leaveDays <= 7){
            System.out.println("系班主任批准"+name+"请假[" + leaveDays + "]天。");
        }else {
            if(getNext() != null){
                System.out.println("系班主任无权限处理，请求传递中..." + name);
                getNext().handlerRequest(name,leaveDays);
            }else {
                System.out.println("请假天数太多，没有人能批准该假条！");
            }
        }
    }
}
// 院长处理类
class Dean extends Leader{
    @Override
    public void handlerRequest(String name ,int leaveDays) {
        if(leaveDays <= 10){
            System.out.println("院长批准"+name+"请假[" + leaveDays + "]天。");
        }else {
            if(getNext() != null){
                System.out.println("院长无权限处理，请求传递中..." + name);
                getNext().handlerRequest(name,leaveDays);
            }else {
                System.out.println("请假天数太多，没有人能批准该假条！");
            }
        }
    }
}
// 教务处长处理类
class DeanOfStudies extends Leader{
    @Override
    public void handlerRequest(String name ,int leaveDays) {
        if(leaveDays <= 20){
            System.out.println("教务处长批准"+name+"请假[" + leaveDays + "]天。");
        }else {
            if(getNext() != null){
                System.out.println("教务处长无权限处理，请求传递中..." + name);
                getNext().handlerRequest(name,leaveDays);
            }else {
                System.out.println("请假天数太多，没有人能批准该假条！");
            }
        }
    }
}
```

### 模式的应用场景

1. 多个对象可以处理一个请求，但具体由哪个对象处理该请求在运行时自动确定。
2. 可动态指定一组处理请求，或添加新的处理者
3. 需要在不明确指定处理请求者的情况下，向多个处理者中的一个提交请求。

### 模式的扩展

职责链模式存在以下两种情况：

1. 纯的职责链模式：一个请求必须被某一个处理者对象所接收，且一个具体处理者对某个请求的处理只能采用以下两种行为之一：自己处理（承担责任）；把责任推给下家处理。
2. 不纯的职责链模式：允许出现某一个具体处理者对象在承担了请求的一部分责任后又将剩余的责任传给下家的情况，且一个请求可以最终不被任何接收端对象所接收。