package iterator;

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
