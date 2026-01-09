package com.example.library

import android.util.Log
import com.example.library.data.api.ApiBookService
import com.example.library.data.database.BookDataBase
import com.example.library.data.database.dao.BookDao
import com.example.library.data.database.entity.BookEntity
import com.example.library.data.database.entity.BookReminderEntity
import com.example.library.data.models.Author
import com.example.library.data.models.Availability
import com.example.library.data.models.Book
import com.example.library.data.models.SearchBook
import com.example.library.data.models.response.OpenLibraryResponse
import com.example.library.data.models.response.OpenLibrarySearchResponse
import com.example.library.data.repository.BookRepository
import com.example.library.ui.BookCategory
import io.mockk.clearAllMocks
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okio.IOException
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class BookRepositoryTest {

    private lateinit var repository: BookRepository
    private lateinit var mockApi: ApiBookService
    private lateinit var mockDb: BookDataBase
    private lateinit var mockDao: BookDao

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockApi = mockk()
        mockDb = mockk()
        mockDao = mockk()

        every {
            mockDb.getDao()
        } returns mockDao
        repository = BookRepository(mockApi, mockDb)

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `loadBooksByCategory success should save to db and return books`() = testScope.runTest {
        val category = BookCategory.FICTION
        val offset = (1 - 1) * 20

        val mockBook = Book(
            key = "OL123W",
            title = "Test Book",
            authors = listOf(Author(key = "/authors/OL1A", name = "Test Author")),
            cover_id = 123,
            first_publish_year = 2023,
            subject = listOf("Fiction"),
            availability = Availability(
                status = "available",
                available_to_browse = true,
                available_to_borrow = false,
                available_to_waitlist = false,
                is_printdisabled = false,
                is_readable = true,
                is_lendable = false,
                is_previewable = false,
                identifier = "test",
                isbn = "",
                oclc = 1,
                openlibrary_work = "OL123W",
                openlibrary_edition = "OL123456",
                last_loan_date = 1,
                num_waitlist = 1,
                last_waitlist_date = 1,
                is_restricted = false,
                is_browseable = true,
                __src__ = "openlibrary"
            ),
            cover_edition_key = "OL123456",
            edition_count = 1,
            has_fulltext = true,
            ia = "test",
            ia_collection = listOf("inlibrary"),
            lending_edition = "OL123456",
            lending_identifier = "test",
            printdisabled = false,
            public_scan = true
        )

        val mockResponse = OpenLibraryResponse(
            key = "/subjects/fiction",
            name = "Fiction",
            subject_type = "subject",
            work_count = 100,
            works = listOf(mockBook),
            solr_query = ""
        )

        coEvery { mockApi.getBooksBySubject("fiction", 20, offset) } returns mockResponse
        coEvery { mockDao.insertBooks(any()) } returns Unit
        coEvery { mockDao.getBooksByCategory("fiction") } returns emptyList()

        val result = repository.loadBooksByCategory(category, 20, 1)

        assertNotNull(result)
        assertEquals(1, result?.size)
        assertEquals("OL123W", result?.first()?.id)
        assertEquals("Test Book", result?.first()?.title)
        assertEquals("fiction", result?.first()?.category)

        coVerify {
            mockApi.getBooksBySubject("fiction", 20, offset)
            mockDao.insertBooks(any())
        }
    }

    @Test
    fun `loadBooksByCategory network error should return cache`() = testScope.runTest {
        val category = BookCategory.SCIENCE_FICTION
        val cachedBooks = listOf(
            BookEntity(
                id = "OL1",
                title = "Cached Sci-Fi Book",
                authors = "Cached Author",
                coverId = 111,
                firstPublishYear = 2020,
                subjects = "Science Fiction",
                description = "Cached description",
                category = "science_fiction"
            )
        )
        coEvery {
            mockApi.getBooksBySubject("science_fiction", 20, 0)
        } throws IOException("Network error")
        coEvery { mockDao.getBooksByCategory("science_fiction") } returns cachedBooks

        val result = repository.loadBooksByCategory(category)

        assertNotNull(result)
        assertEquals(1, result?.size)
        assertEquals("Cached Sci-Fi Book", result?.first()?.title)

        coVerify { mockDao.getBooksByCategory("science_fiction") }
    }

    @Test
    fun `searchBoks success`() = testScope.runTest {
        val query = "Harry Potter"
        val offset = (1 - 1) * 20

        val mockSearchResponse = OpenLibrarySearchResponse(
            docs = listOf(
                SearchBook(
                    author_key = null,
                    author_name = listOf("J.K. Rowling"),
                    cover_edition_key = null,
                    cover_i = 123456,
                    ebook_access = null,
                    edition_count = null,
                    first_publish_year = 1997,
                    has_fulltext = null,
                    ia = null,
                    ia_collection_s = null,
                    key = "OL1",
                    language = null,
                    lending_edition_s = null,
                    lending_identifier_s = null,
                    public_scan_b = null,
                    title = "Harry Potter and the Philosopher's Stone"
                )
            ),
            numFound = 1,
            start = 0,
            documentation_url = "null",
            numFoundExact = true,
            num_found = 0,
            offset = 1,
            q = "Harry Potter"
        )
        coEvery { mockApi.getSearchResult(query, 20, offset) } returns mockSearchResponse
        val result = repository.searchBooks(query)

        assertEquals(1, result.size)
        assertEquals("OL1", result.first().id)
        assertEquals("Harry Potter and the Philosopher's Stone", result.first().title)
        assertEquals("J.K. Rowling", result.first().authors)
        assertEquals(1997, result.first().firstPublishYear)
    }

    @Test
    fun `searchBooks with short query should return empty list`() = testScope.runTest {
        val result1 = repository.searchBooks("")
        val result2 = repository.searchBooks("a")
        val result3 = repository.searchBooks("ab")
        val result4 = repository.searchBooks("abc")

        assertEquals(0, result1.size)
        assertEquals(0, result2.size)
        assertEquals(0, result3.size)
    }

    @Test
    fun `searchBooks network error should return empty list`() = testScope.runTest {
        val query = "test"
        coEvery { mockApi.getSearchResult(query, 20, 0) } throws IOException("Network error")

        val result = repository.searchBooks(query)
        assertEquals(0, result.size)
    }

    @Test
    fun `saveBookToShelf new book should insert with status`() = testScope.runTest {
        val book = BookEntity(
            id = "NEW1",
            title = "New Book",
            authors = "New Author",
            coverId = 123,
            firstPublishYear = 2023,
            subjects = "Fiction",
            description = "New book description"
        )

        coEvery { mockDao.getById("NEW1") } returns null
        coEvery { mockDao.insertBook(any()) } returns Unit
        repository.saveBookToShelf(book, "want_to_read")

        coVerify {
            mockDao.getById("NEW1")
            mockDao.insertBook(book.copy(shelfStatus = "want_to_read"))
        }
    }

    @Test
    fun `saveBookToShelf existing book should update status`() = testScope.runTest {
        val existingBook = BookEntity(
            id = "EXIST1",
            title = "Existing Book",
            authors = "Existing Author",
            coverId = 456,
            firstPublishYear = 2022,
            subjects = "Existing",
            description = "Existing description",
            shelfStatus = "old_status"
        )

        val newBookData = BookEntity(
            id = "EXIST1",
            title = "Updated Book",
            authors = "Updated Author",
            coverId = 789,
            firstPublishYear = 2023,
            subjects = "Updated",
            description = "Updated description"
        )

        coEvery { mockDao.getById("EXIST1") } returns existingBook
        coEvery { mockDao.updateBookShelfStatus("EXIST1", "reading") } returns Unit
        repository.saveBookToShelf(newBookData, "reading")

        coVerify {
            mockDao.getById("EXIST1")
            mockDao.updateBookShelfStatus("EXIST1", "reading")
        }
        coVerify(exactly = 0) { mockDao.insertBook(any()) }
    }

    @Test
    fun `getBookFromShelf should return book with status`() = testScope.runTest {
        val status = "reading"

        val books = listOf(
            BookEntity(
                id = "OL1",
                title = "Read Book 1",
                authors = "Author 1",
                coverId = 111,
                firstPublishYear = 2020,
                subjects = "Fiction",
                description = "Description",
                shelfStatus = "read"
            ),
            BookEntity(
                id = "OL2",
                title = "Read Book 2",
                authors = "Author 2",
                coverId = 222,
                firstPublishYear = 2021,
                subjects = "Science",
                description = "Description 2",
                shelfStatus = "read"
            )
        )

        coEvery { mockDao.selectBooksByShelfStatus(status) } returns books
        val result = repository.getBooksFromShelf(status)

        assertNotNull(result)
        assertEquals(2, result.size)
        assertEquals("read", result.first().shelfStatus)
    }

    @Test
    fun `removeBookFromShelf should delete book`() = testScope.runTest {
        val existingBook = BookEntity(
            id = "BOOK1",
            title = "Book to remove",
            authors = "Author",
            coverId = 123,
            firstPublishYear = 2023,
            subjects = "Fiction",
            description = "Description",
            shelfStatus = "reading" // Currently on shelf
        )

        val bookToRemove = BookEntity(
            id = "BOOK1",
            title = "Book to remove",
            authors = "Author",
            coverId = 123,
            firstPublishYear = 2023,
            subjects = "Fiction",
            description = "Description"
        )

        coEvery { mockDao.getById("BOOK1") } returns existingBook
        coEvery { mockDao.updateBookShelfStatus("BOOK1", null) } returns Unit

        repository.removeBookFromShelf(bookToRemove, null)

        coVerify {
            mockDao.getById("BOOK1")
            mockDao.updateBookShelfStatus("BOOK1", null)
        }
    }

    @Test
    fun `getBookShelfStatus should return status`() = testScope.runTest {
        val bookWithStatus = BookEntity(
            id = "BOOK1",
            title = "Book with status",
            authors = "Author",
            coverId = 123,
            firstPublishYear = 2023,
            subjects = "Fiction",
            description = "Description",
            shelfStatus = "want_to_read"
        )

        val bookWithoutStatus = BookEntity(
            id = "BOOK2",
            title = "Book without status",
            authors = "Author 2",
            coverId = 456,
            firstPublishYear = 2022,
            subjects = "Science",
            description = "Description 2",
            shelfStatus = null
        )

        coEvery { mockDao.getById("BOOK1") } returns bookWithStatus
        coEvery { mockDao.getById("BOOK2") } returns bookWithoutStatus
        coEvery { mockDao.getById("NOT_EXIST") } returns null

        assertEquals("want_to_read", repository.getBookShelfStatus("BOOK1"))
        assertNull(repository.getBookShelfStatus("BOOK2"))
        assertNull(repository.getBookShelfStatus("NOT_EXIST"))
    }

    @Test
    fun `upsertBookForHistory should preserve existing shelf status`() = testScope.runTest {
        val existingBook = BookEntity(
            id = "OL123W",
            title = "Existing Book",
            authors = "Existing Author",
            coverId = 123,
            firstPublishYear = 2022,
            subjects = "Existing",
            description = "Existing description",
            shelfStatus = "reading",
            viewAt = 1000L
        )

        val newBookData = BookEntity(
            id = "OL123W",
            title = "Updated Book",
            authors = "Updated Author",
            coverId = 456,
            firstPublishYear = 2023,
            subjects = "Updated",
            description = "Updated description"
        )

        coEvery { mockDao.getById("OL123W") } returns existingBook
        coEvery { mockDao.insertBook(any()) } returns Unit

        repository.upsertBookForHistory(newBookData)

        coVerify {
            mockDao.insertBook(
                withArg { saveBookToShelf ->
                    assertEquals("reading", saveBookToShelf.shelfStatus)
                    assertTrue(saveBookToShelf.viewAt > 0)
                    assertEquals("Updated Book", saveBookToShelf.title)
                }
            )
        }
    }

    @Test
    fun `getViewHistory should return books ordered by viewAt`() = testScope.runTest {
        val historyBooks = listOf(
            BookEntity(
                id = "H1",
                title = "History Book 1",
                authors = "Author 1",
                coverId = 111,
                firstPublishYear = 2023,
                subjects = "History",
                description = "Desc 1",
                viewAt = 1000L
            ),
            BookEntity(
                id = "H2",
                title = "History Book 2",
                authors = "Author 2",
                coverId = 222,
                firstPublishYear = 2022,
                subjects = "History",
                description = "Desc 2",
                viewAt = 2000L
            )
        )

        coEvery { mockDao.getBooksByViewTime() } returns historyBooks

        val result = repository.getViewHistory()

        assertEquals(2, result.size)
        assertEquals("History Book 1", result[0].title)
    }

    @Test
    fun `clearHistory should reset viewAt`() = testScope.runTest {
        coEvery { mockDao.clearHistory() } returns Unit
        repository.clearHistory()
        coVerify { mockDao.clearHistory()}
    }

    @Test
    fun `addBookNotification success should return id`() = testScope.runTest {
        val reminder = BookReminderEntity(
            bookId = "OL123W",
            notificationTime = System.currentTimeMillis() + 86400000, // +1 day
            bookTitle = "Test Book",
            bookAuthor = "Test Author",
            coverId = 123,
            notificationId = 42
        )

        coEvery { mockDao.insertNotificationBook(reminder) } returns 99L
        coEvery { mockDao.getNotificationsCount() } returns 5

        val result = repository.addBookNotification(reminder)

        assertEquals(99L, result)
        coVerify {
            mockDao.insertNotificationBook(reminder)
            mockDao.getNotificationsCount()
        }
    }

    @Test
    fun `getAllNotifications should return list from dao`() = testScope.runTest {
        val reminders = listOf(
            BookReminderEntity(
                id = 1,
                bookId = "OL1",
                notificationTime = System.currentTimeMillis() + 100000,
                bookTitle = "Book 1",
                bookAuthor = "Author 1",
                coverId = 111,
                notificationId = 1
            ),
            BookReminderEntity(
                id = 2,
                bookId = "OL2",
                notificationTime = System.currentTimeMillis() + 200000,
                bookTitle = "Book 2",
                bookAuthor = "Author 2",
                coverId = 222,
                notificationId = 2
            )
        )

        coEvery { mockDao.getAllNotifications() } returns reminders
        val result = repository.getAllNotitfications()

        assertEquals(2, result.size)
        assertEquals("Book 1", result[0].bookTitle)
    }

    @Test
    fun `deleteNotification should remove from db`() = testScope.runTest {
        val reminder = BookReminderEntity(
            id = 1,
            bookId = "OL123W",
            notificationTime = System.currentTimeMillis() + 100000,
            bookTitle = "Book to delete",
            bookAuthor = "Author",
            coverId = 123,
            notificationId = 1
        )

        coEvery { mockDao.deleteNotificationBook(reminder) } returns Unit

        repository.deleteNotification(reminder)

        coVerify { mockDao.deleteNotificationBook(reminder) }
    }

    @Test
    fun `deleteNotificationById should remove from db`() = testScope.runTest {
        val reminderId = 42L
        coEvery { mockDao.deleteNotificationById(reminderId) } returns Unit

        repository.deleteNotificationById(reminderId)

        coVerify { mockDao.deleteNotificationById(42L) }
    }
}






