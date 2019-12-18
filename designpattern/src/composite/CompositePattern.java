package composite;

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
