package com.example.library

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.library.data.database.BookDataBase
import com.example.library.data.database.dao.BookDao
import com.example.library.data.database.entity.BookEntity
import com.example.library.data.database.entity.BookReminderEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class BookDaoTest {

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: BookDataBase
    private lateinit var dao: BookDao

    @Before
    fun setup(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            BookDataBase::class.java
        ).allowMainThreadQueries().build()

        dao = database.getDao()
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun `inserBook and getById should work correctly`() = runBlocking {
        val book = BookEntity(
            id = "OL1",
            title = "Test Book",
            authors = "Test Author",
            coverId = 123,
            firstPublishYear = 2023,
            subjects = "Fiction, Test",
            description = "Test description",
            category = "fiction"
        )

        dao.insertBook(book)
        val retrieved = dao.getById("OL1")
        assertNotNull(retrieved)
        assertEquals("OL1", retrieved.id)
        assertEquals("Test Book", retrieved.title)
        assertEquals("Test Author", retrieved.authors)
        assertEquals(123, retrieved.coverId)
        assertEquals(2023, retrieved.firstPublishYear)
        assertEquals("Fiction, Test", retrieved.subjects)
        assertEquals("fiction", retrieved.category)
    }

    @Test
    fun `insert book with existing id should replace`() = runBlocking {
        val book1 = BookEntity(
            id = "OL1",
            title = "Old Title",
            authors = "Author",
            coverId = 1,
            firstPublishYear = 2020,
            subjects = "Old",
            description = "Old desc",
            category = "fiction"
        )

        val book2 = BookEntity(
            id = "OL1", // Тот же ID
            title = "New Title",
            authors = "New Author",
            coverId = 2,
            firstPublishYear = 2023,
            subjects = "New",
            description = "New desc",
            category = "fiction"
        )

        dao.insertBook(book1)
        dao.insertBook(book2)

       val retrieved = dao.getById("OL1")
        assertNotNull(retrieved)
        assertEquals("New Title", retrieved.title)
        assertEquals("New Author", retrieved.authors)
        assertEquals(2, retrieved.coverId)
    }

    @Test
    fun `insertBooks should insert multiple books`() = runBlocking {

        val books = listOf(
            BookEntity(
                id = "OL1",
                title = "Book 1",
                authors = "Author 1",
                coverId = 1,
                firstPublishYear = 2023,
                subjects = "Fiction",
                description = "Desc 1",
                category = "fiction"
            ),
            BookEntity(
                id = "OL2",
                title = "Book 2",
                authors = "Author 2",
                coverId = 2,
                firstPublishYear = 2022,
                subjects = "Science",
                description = "Desc 2",
                category = "science"
            ),
            BookEntity(
                id = "OL3",
                title = "Book 3",
                authors = "Author 3",
                coverId = 3,
                firstPublishYear = 2021,
                subjects = "History",
                description = "Desc 3",
                category = "history"
            )
        )

        dao.insertBooks(books)
        val allBooks = dao.getAllBooks().first()
        assertEquals(3, allBooks.size)
        assertEquals("OL1", allBooks[0].id)
        assertEquals("OL2", allBooks[1].id)
        assertEquals("OL3", allBooks[2].id)
    }

    @Test
    fun `get Books by category should return filtered books`() = runBlocking {
        val fictionBooks = listOf(
            BookEntity(
                id = "F1",
                title = "Fiction Book 1",
                authors = "Author",
                coverId = 1,
                firstPublishYear = 2023,
                subjects = "Fiction",
                description = "Desc",
                category = "fiction"
            ),
            BookEntity(
                id = "F2",
                title = "Fiction Book 2",
                authors = "Author",
                coverId = 2,
                firstPublishYear = 2022,
                subjects = "Fiction",
                description = "Desc",
                category = "fiction"
            )
        )

        val scienceBooks = listOf(
            BookEntity(
                id = "S1",
                title = "Science Book",
                authors = "Author",
                coverId = 3,
                firstPublishYear = 2023,
                subjects = "Science",
                description = "Desc",
                category = "science"
            )
        )

        dao.insertBooks(fictionBooks + scienceBooks)
        val fictionResult = dao.getBooksByCategory("fiction")
        val scienceResult = dao.getBooksByCategory("science")
        val unknownResult = dao.getBooksByCategory("unknown")

        assertEquals(2, fictionResult.size)
        assertEquals("Fiction Book 1", fictionResult[0].title)
        assertEquals("Fiction Book 2", fictionResult[1].title)

        assertEquals(1, scienceResult.size)
        assertEquals("Science Book", scienceResult[0].title)

        assertEquals(0, unknownResult.size)
    }

    @Test
    fun `update book shelf status should update status`() = runBlocking {
        val book = BookEntity(
            id = "OL1",
            title = "Test Book",
            authors = "Author",
            coverId = 123,
            firstPublishYear = 2023,
            subjects = "Test",
            description = "Description",
            category = "test"
        )

        dao.insertBook(book)
        dao.updateBookShelfStatus("OL1", "want_to_read")
        val afterSet = dao.getById("OL1")

        assertEquals("want_to_read", afterSet?.shelfStatus)

        dao.updateBookShelfStatus("OL1", "read")
        val afterUpdate = dao.getById("OL1")

        assertEquals("read", afterUpdate?.shelfStatus)
        dao.updateBookShelfStatus("OL1", null)
        val afterClear = dao.getById("OL1")
        assertNull(afterClear?.shelfStatus)
    }

    @Test
    fun `selectBooksByShelfStatus should return correct books`() = runBlocking {
        val books = listOf(
                BookEntity(
                    id = "W1",
                    title = "Want to Read",
                    authors = "Author",
                    coverId = 1,
                    firstPublishYear = 2023,
                    subjects = "Test",
                    description = "Desc",
                    category = "test",
                    shelfStatus = "want_to_read"
                ),
        BookEntity(
            id = "R1",
            title = "Reading",
            authors = "Author",
            coverId = 2,
            firstPublishYear = 2023,
            subjects = "Test",
            description = "Desc",
            category = "test",
            shelfStatus = "reading"
        ),
        BookEntity(
            id = "R2",
            title = "Reading 2",
            authors = "Author",
            coverId = 3,
            firstPublishYear = 2023,
            subjects = "Test",
            description = "Desc",
            category = "test",
            shelfStatus = "reading"
        ),
        BookEntity(
            id = "D1",
            title = "No Status",
            authors = "Author",
            coverId = 4,
            firstPublishYear = 2023,
            subjects = "Test",
            description = "Desc",
            category = "test"
            // shelfStatus = null
        )
        )
        dao.insertBooks(books)

        val wantToRead = dao.selectBooksByShelfStatus("want_to_read")
        val reading = dao.selectBooksByShelfStatus("reading")
        val read = dao.selectBooksByShelfStatus("read")

        assertEquals(1, wantToRead.size)
        assertEquals("Want to Read", wantToRead[0].title)

        assertEquals(2, reading.size)
        assertTrue { reading.all {
            it.shelfStatus == "reading"
        } }

        assertEquals(0, read.size)
    }

    @Test
    fun `updateViewAt should set timestamp`() = runBlocking {
        val book = BookEntity(
            id = "OL1",
            title = "Test Book",
            authors = "Author",
            coverId = 123,
            firstPublishYear = 2023,
            subjects = "Test",
            description = "Description",
            category = "test"
        )
        dao.insertBook(book)
        val currentTime = System.currentTimeMillis()
        dao.updateViewAt(book.id, currentTime)
        val updateBook = dao.getById("OL1")
        assertEquals(currentTime, updateBook?.viewAt)
    }

    @Test
    fun `getBooksByViewTime should return books orderered by viewAt`() = runBlocking {
        val books = listOf(
            BookEntity(
                id = "B1",
                title = "Book 1",
                authors = "Author",
                coverId = 1,
                firstPublishYear = 2023,
                subjects = "Test",
                description = "Desc",
                category = "test",
                viewAt = 1000
            ),
            BookEntity(
                id = "B2",
                title = "Book 2",
                authors = "Author",
                coverId = 2,
                firstPublishYear = 2023,
                subjects = "Test",
                description = "Desc",
                category = "test",
                viewAt = 3000 // Самый новый
            ),
            BookEntity(
                id = "B3",
                title = "Book 3",
                authors = "Author",
                coverId = 3,
                firstPublishYear = 2023,
                subjects = "Test",
                description = "Desc",
                category = "test",
                viewAt = 2000
            ),
            BookEntity(
                id = "B4",
                title = "Book 4",
                authors = "Author",
                coverId = 4,
                firstPublishYear = 2023,
                subjects = "Test",
                description = "Desc",
                category = "test"
                // viewAt = 0 (не в истории)
            )
        )

        dao.insertBooks(books)
        val history = dao.getBooksByViewTime()
        assertEquals(3, history.size)
        assertEquals("Book 2", history[0].title)
        assertEquals("Book 3", history[1].title)
        assertEquals("Book 1", history[2].title)

        assertFalse { history.any {it.id == "84"} }
    }

    @Test
    fun `clearHistory should remove all books from history`() = runBlocking {
        val books = listOf(
            BookEntity(
                id = "B1",
                title = "Book 1",
                authors = "Author",
                coverId = 1,
                firstPublishYear = 2023,
                subjects = "Test",
                description = "Desc",
                category = "test",
                viewAt = 1000
            ),
            BookEntity(
                id = "B2",
                title = "Book 2",
                authors = "Author",
                coverId = 2,
                firstPublishYear = 2023,
                subjects = "Test",
                description = "Desc",
                category = "test",
                viewAt = 2000
            )
        )

        dao.insertBooks(books)
        val historyBefore = dao.getBooksByViewTime()
        assertEquals(2, historyBefore.size)
        dao.clearHistory()
        val historyAfter = dao.getBooksByViewTime()
        assertEquals(0, historyAfter.size)

        val book1 = dao.getById("B1")
        val  book2 = dao.getById("B2")
        assertEquals(0, book1?.viewAt)
        assertEquals(0, book2?.viewAt)
    }

    @Test
    fun `insertNotificationBook should save reminder and return id`()  = runBlocking {
        val reminder = BookReminderEntity(
            bookId = "OL123W",
            notificationTime = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1),
            bookTitle = "Test Book",
            bookAuthor = "Test Author",
            coverId = 123,
            notificationId = 42
        )

        val insertedId = dao.insertNotificationBook(reminder)
        assertTrue { insertedId> 0 }
        val allReminders = dao.getAllNotifications()
        assertEquals(1, allReminders.size)
        assertEquals(insertedId, allReminders[0].id)
        assertEquals("Test Book", allReminders[0].bookTitle)
    }

    @Test
    fun `getAllNotifications should return all reminders`() = runBlocking {
        val reminders = listOf(
            BookReminderEntity(
                bookId = "OL1",
                notificationTime = System.currentTimeMillis() + 1000,
                bookTitle = "Book 1",
                bookAuthor = "Author 1",
                coverId = 1,
                notificationId = 1
            ),
            BookReminderEntity(
                bookId = "OL2",
                notificationTime = System.currentTimeMillis() + 2000,
                bookTitle = "Book 2",
                bookAuthor = "Author 2",
                coverId = 2,
                notificationId = 2
            )
        )

        reminders.forEach {
            dao.insertNotificationBook(it)
        }

        val allReminders = dao.getAllNotifications()
        assertEquals(2, allReminders.size)
        assertEquals("Book 1", allReminders[0].bookTitle)
        assertEquals("Book 2", allReminders[1].bookTitle)
    }

    @Test
    fun `deleteNotificationBook should remove reminder`() = runBlocking {
        val reminder = BookReminderEntity(
            id = 1,
            bookId = "OL1",
            notificationTime = System.currentTimeMillis() + 1000,
            bookTitle = "Book to Delete",
            bookAuthor = "Author",
            coverId = 123,
            notificationId = 1
        )

        val insertedId = dao.insertNotificationBook(reminder)
        assertEquals(1, dao.getAllNotifications().size)

        dao.deleteNotificationBook(reminder.copy(id = insertedId))
        assertEquals(0, dao.getAllNotifications().size)
    }

    @Test
    fun `updateNotificationBook should modify reminder`() = runBlocking {
        val originalReminder = BookReminderEntity(
            bookId = "OL1",
            notificationTime = System.currentTimeMillis() + 1000,
            bookTitle = "Original Title",
            bookAuthor = "Author",
            coverId = 123,
            notificationId = 1
        )

        val insertedId = dao.insertNotificationBook(originalReminder)

        val updateReminder = originalReminder.copy(
            id = insertedId,
            bookTitle = "Updated Title",
            notificationTime = System.currentTimeMillis() + 2000
        )

        dao.insertNotificationBook(updateReminder)
        val reminders = dao.getAllNotifications()
        assertEquals(1, reminders.size)
        assertEquals("Updated Title", reminders[0].bookTitle)
        assertEquals(updateReminder.notificationTime, reminders[0].notificationTime)
    }

    @Test
    fun `getNotificationsCount should return correct count`() = runBlocking {
        assertEquals(0, dao.getNotificationsCount())
        repeat(3){i ->
            dao.insertNotificationBook(
                BookReminderEntity(
                    bookId = "OL$i",
                    notificationTime = System.currentTimeMillis() + (i * 1000),
                    bookTitle = "Book $i",
                    bookAuthor = "Author",
                    coverId = i,
                    notificationId = i
                )
            )
        }
        assertEquals(3, dao.getNotificationsCount())
    }

    @Test
    fun `deleteNotificationById should remove reminder`() = runBlocking {
        val reminder1 = BookReminderEntity(
            bookId = "OL1",
            notificationTime = System.currentTimeMillis() + 1000,
            bookTitle = "Book 1",
            bookAuthor = "Author",
            coverId = 1,
            notificationId = 1
        )

        val reminder2 = BookReminderEntity(
            bookId = "OL2",
            notificationTime = System.currentTimeMillis() + 2000,
            bookTitle = "Book 2",
            bookAuthor = "Author",
            coverId = 2,
            notificationId = 2
        )

        val id1 = dao.insertNotificationBook(reminder1)
        val id2 = dao.insertNotificationBook(reminder2)

        assertEquals(2, dao.getAllNotifications().size)
        dao.deleteNotificationById(id1)

        val remainding = dao.getAllNotifications()
        assertEquals(1, remainding.size)
        assertEquals("Book 2", remainding[0].bookTitle)
    }

    @Test
    fun `deleteNotificationsByBookId should remove reminders`() = runBlocking {
        repeat(3) { i ->
            dao.insertNotificationBook(
                BookReminderEntity(
                    bookId = "SAME_BOOK", // Одна и та же книга
                    notificationTime = System.currentTimeMillis() + (i * 1000),
                    bookTitle = "Same Book $i",
                    bookAuthor = "Author",
                    coverId = i,
                    notificationId = i
                )
            )
        }

        // И одно напоминание для другой книги
        dao.insertNotificationBook(
            BookReminderEntity(
                bookId = "OTHER_BOOK",
                notificationTime = System.currentTimeMillis() + 10000,
                bookTitle = "Other Book",
                bookAuthor = "Author",
                coverId = 99,
                notificationId = 99
            )
        )

        assertEquals(4, dao.getAllNotifications().size)
        dao.deleteNotificationsByBookId("SAME_BOOK")
        val remaining = dao.getAllNotifications()
        assertEquals(1, remaining.size)
        assertEquals("Other Book", remaining[0].bookTitle)
    }

    @Test
    fun `getById should return null for not existed book`() = runBlocking {
        val result = dao.getById("NOT_EXISTED")
        assertNull(result)
    }

    @Test
    fun `updateBook should modify book`() = runBlocking {
        val book = BookEntity(
            id = "OL1",
            title = "Original Title",
            authors = "Original Author",
            coverId = 1,
            firstPublishYear = 2020,
            subjects = "Original",
            description = "Original desc",
            category = "fiction"
        )
        dao.insertBook(book)

        val updatedBook = book.copy(
            title = "Updated Title",
            authors = "Updated Author",
            coverId = 2,
            firstPublishYear = 2023
        )

        dao.updateBook(updatedBook)
        val retrieved = dao.getById("OL1")
        assertNotNull(retrieved)
        assertEquals("Updated Title", retrieved?.title)
        assertEquals("Updated Author", retrieved?.authors)
        assertEquals(2, retrieved?.coverId)
        assertEquals(2023, retrieved?.firstPublishYear)
    }

    @Test
    fun `delete book should remove book`() = runBlocking {
        val book = BookEntity(
            id = "OL1",
            title = "Book to Delete",
            authors = "Author",
            coverId = 1,
            firstPublishYear = 2023,
            subjects = "Test",
            description = "Desc",
            category = "test"
        )

        dao.insertBook(book)
        assertNotNull(dao.getById("OL1"))
        dao.deleteBook(book)
        assertNull(dao.getById("OL1"))
    }
}