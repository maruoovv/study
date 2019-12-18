package iterator;

import java.util.List;

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