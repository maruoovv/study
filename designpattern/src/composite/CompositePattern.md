### 컴포지트 패턴 

객체들을 트리 구조로 구성하여 계층 형태로 만들 수 있다.  
클라이언트에서는 개별 객체와 다른 객체들로 구성된 복합 객체를 같은 방법으로 다룰 수 있다.  

간단한 예제를 통해 컴포지트 패턴을 알아보자.

조직도를 구현해보자.  
조직도에는 부서 혹은 사람이 들어갈 수 있다.

```JAVA
class OrganizationChart {
    private Node node;
    public OrganizationChart(Node node) {
        this.node = node;
    }

    public void print() {
        node.print();
    }
}

interface Node {
    String getName();
    void print();
}

class Person implements Node {
    private String name;

    public Person(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void print() {
        System.out.println(name);
    }
}

class Department implements Node {
    private String name;
    private List<Node> nodes;

    public Department(String name) {
        this.name = name;
        nodes = new ArrayList<>();
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void print() {
        for (Node node : nodes) {
            node.print();
        }
    }
}
```

이렇게 컴포지트 패턴을 이용하여 구현하면,  
클라이언트는 다른 객체들로 구성된 계층 구조를 마치 같은 객체를 다루는 것 처럼 다룰 수 있다.

```java
public class CompositePattern {

    public static void main(String[] args) {

        Department department = new Department("서비스개발실");
        Person person = new Person("서비스개발실장");
        department.addNode(person);

        Department department1 = new Department("서비스개발1팀");
        Person person1 = new Person("서비스개발1팀장");
        Person person2 = new Person("개발자1");
        department1.addNode(person1);
        department1.addNode(person2);
        Department department2 = new Department("서비스개발2팀");
        Person person3 = new Person("서비스개발2팀장");
        Person person4 = new Person("개발자2");
        department2.addNode(person3);
        department2.addNode(person4);

        department.addNode(department1);
        department.addNode(department2);

        OrganizationChart chart = new OrganizationChart(department);
        chart.print();
    }
}
```