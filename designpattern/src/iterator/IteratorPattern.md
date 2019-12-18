### 이터레이터 패턴 

컬렉션 내부 구현을 노출시키지 않고, 내부 요소들에 접근할 수 있게 해주는 방법을 제공해준다.  
내부 구현을 모르는 상태에서, 그 안에 들어있는 모든 항목들에 대해 반복작업을 수행할수 있게 해준다.  
이터레이터 패턴을 사용하면 컬렉션 객체에서 자신의 책임과 상관 없는 내부 항목에 접근하는 작업을 이터레이터 객체로 분리해낼 수 있다.

간단한 에제를 통해 이터레이터 패턴을 구현해보자.

책을 파는 서점이 있다. 
서점에는 책선반과 책을 담는 카트가 있는데, 선반에는 놓을수 있는 책의 갯수가 제한되어 있고,
카트에는 제한이 없다.  
선반은 책의 갯수가 제한되어 있으므로 내부 구현을 배열로 하고, 카트는 List 로 구현했다.

```java
public class Book {
    private String name;
    private int price;

    public Book(String name, int price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public String toString() {
        return "Book{" +
                "name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}

public class BookCart {
    private List<Book> books = new ArrayList<>();

    public void addBook(Book book) {
        books.add(book);
    }

}

public class BookShelf {
    private Book[] shelf;
    private static final int MAX_ITEMS = 5;
    int numberOfBooks = 0;

    public BookShelf() {
        this.shelf = new Book[MAX_ITEMS];
    }

    public void addBook(Book book) {
        if (numberOfBooks >= MAX_ITEMS) {
            throw new IllegalStateException();
        }

        shelf[numberOfBooks++] = book;
    }
}
```

이 상태에서 각각 책 요소에 접근하려면, 내부 변수를 그대로 반환하여 클라이언트에서 변환하여 쓰게 하거나
직접 변환하여 반환해줘야 한다.  
이 책임을 이터레이터 패턴을 이용하여 분리할 수 있다.

```java
public interface Iterator {
    boolean hasNext();
    Object next();
}

class BookShelfIterator implements Iterator {
    private Book[] books;
    int pos = 0;

    public BookShelfIterator(Book[] books) {
        this.books = books;
    }

    @Override
    public boolean hasNext() {
        if (pos >= books.length || books[pos] == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Object next() {
        Book book = books[pos++];
        return book;
    }
}

class BookCartIterator implements Iterator {

    private List<Book> books;
    private int pos = 0;
    public BookCartIterator(List<Book> books) {
        this.books = books;
    }

    @Override
    public boolean hasNext() {
        if (pos >= books.size()) {
            return false;
        }

        return true;
    }

    @Override
    public Object next() {
        return books.get(pos++);
    }
}

public class BookCart {
    private List<Book> books = new ArrayList<>();

    public void addBook(Book book) {
        books.add(book);
    }

    public Iterator iterator() {
        return new BookCartIterator(books);
    }
}

public class BookShelf {
    private Book[] shelf;
    private static final int MAX_ITEMS = 5;
    int numberOfBooks = 0;

    public BookShelf() {
        this.shelf = new Book[MAX_ITEMS];
    }

    public void addBook(Book book) {
        if (numberOfBooks >= MAX_ITEMS) {
            throw new IllegalStateException();
        }

        shelf[numberOfBooks++] = book;
    }

    public Iterator iterator() {
        return new BookShelfIterator(this.shelf);
    }
}
```

Iterator 인터페이스는 다음요소가 존재하는지 여부를 나타내는 hasNext, 다음 요소를 반환하는 next() 메소드를 갖고있다.
이를 이용해 클라이언트 에서는 내부 구현을 알 필요 없이 내부 요소들에 접근할 수 있다.

```java

public class IteratorPattern {

    public static void main(String[] args) {
        BookCart bookCart = new BookCart();
        BookShelf bookShelf = new BookShelf();

        Book book1 = new Book("자바의정석", 10000);
        Book book2 = new Book("토비의스프링", 30000);
        Book book3 = new Book("미비포유", 12000);

        bookCart.addBook(book1);
        bookCart.addBook(book2);
        bookCart.addBook(book3);

        bookShelf.addBook(book1);
        bookShelf.addBook(book2);
        bookShelf.addBook(book3);

        printBookInfo(bookCart.iterator());
        printBookInfo(bookShelf.iterator());

    }

    public static void printBookInfo(Iterator iterator) {
        while(iterator.hasNext()) {
            Book book = (Book) iterator.next();
            System.out.println(book);
        }
    }
}

```