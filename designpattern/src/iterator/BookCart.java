package iterator;

import java.util.ArrayList;
import java.util.List;

public class BookCart {
    private List<Book> books = new ArrayList<>();

    public void addBook(Book book) {
        books.add(book);
    }

    public Iterator iterator() {
        return new BookCartIterator(books);
    }
}
