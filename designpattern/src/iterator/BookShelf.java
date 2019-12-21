package iterator;

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
