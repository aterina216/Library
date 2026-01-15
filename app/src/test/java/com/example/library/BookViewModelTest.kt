package com.example.library

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.room.util.query
import com.example.library.data.database.entity.BookEntity
import com.example.library.data.database.entity.BookReminderEntity
import com.example.library.data.models.Author
import com.example.library.data.models.AuthorForDetail
import com.example.library.data.models.AuthorX
import com.example.library.data.models.Availability
import com.example.library.data.models.Created
import com.example.library.data.models.LastModified
import com.example.library.data.models.TypeX
import com.example.library.data.models.response.BookDetailResponse
import com.example.library.data.repository.BookRepository
import com.example.library.ui.BookCategory
import com.example.library.ui.viewmodels.BookViewModel
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verifyOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runner.Runner
import org.junit.runners.JUnit4
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4 ::class)
class BookViewModelTest {

    @get:Rule
    val instantTaskExecurotorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: BookViewModel
    private lateinit var repository: BookRepository

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = BookViewModel(repository)

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
    fun `init should load first category`() = testScope.runTest {
        val category = BookCategory.FICTION

        val mockBooks = listOf(
            BookEntity(
                id = "OL1",
                title = "Test Book 1",
                authors = "Author 1",
                coverId = 123,
                firstPublishYear = 2023,
                subjects = "Fiction",
                description = "Description",
                category = "fiction"
            )
        )

        coEvery {
            repository.loadBooksByCategory(category, 20, 1)
        } returns mockBooks

        viewModel = BookViewModel(repository)
        advanceUntilIdle()

        val currentBooks = viewModel.currentBooks.first()
        assertEquals(1, currentBooks.size)
        assertEquals("OL1", currentBooks.first().id)
        assertEquals(BookCategory.FICTION, viewModel.currentCategory.value)

        // ИЗМЕНЕНИЕ: проверяем, что метод был вызван ХОТЯ БЫ один раз
        coVerify(atLeast = 1) {
            repository.loadBooksByCategory(category, 20, 1)
        }
    }

    @Test
    fun `loadCategory should update books and pagination`() = testScope.runTest {
        val category = BookCategory.SCIENCE_FICTION

        val mockBooksPage1 = List(20) {
            i ->
            BookEntity(
                id = "OL$i",
                title = "Book $i",
                authors = "Author $i",
                coverId = i,
                firstPublishYear = 2023,
                subjects = "Science Fiction",
                description = "Description $i",
                category = "science_fiction"
            )
        }
        val mockBooksPage2 = List(15) { i ->
            BookEntity(
                id = "OL${i + 20}",
                title = "Book ${i + 20}",
                authors = "Author ${i + 20}",
                coverId = i + 20,
                firstPublishYear = 2023,
                subjects = "Science Fiction",
                description = "Description ${i + 20}",
                category = "science_fiction"
            )
        }

        coEvery {
            repository.loadBooksByCategory(category, 20, 1)
        } returns mockBooksPage1

        coEvery { repository.loadBooksByCategory(category, 20, 2) } returns mockBooksPage2

        viewModel.loadCategory(category)
        advanceUntilIdle()

        assertEquals(20, viewModel.currentBooks.value.size)
        assertTrue(viewModel._hasMore.value)

    }

    @Test
    fun `loadCategory should not load when already loading`() = testScope.runTest {
        val category = BookCategory.FICTION
        val mockBooks = List(20) { i ->
            BookEntity(
                id = "OL$i",
                title = "Book $i",
                authors = "Author $i",
                coverId = i,
                firstPublishYear = 2023,
                subjects = "Fiction",
                description = "Description $i",
                category = "fiction"
            )
        }

        coEvery {
            repository.loadBooksByCategory(any(), any(), any())
        } coAnswers {
            delay(1000)
            mockBooks
        }

        viewModel.loadCategory(category)
        viewModel.loadCategory(category)

        coVerify(exactly = 1) {
            repository.loadBooksByCategory(any(), any(), any())
        }
    }

    @Test
    fun `searchBooks should handle empty query`() = testScope.runTest {
        viewModel.searchBooks("")
        advanceUntilIdle()

        assertFalse(viewModel.isSearching.value)
        assertTrue(viewModel._searchBooks.value.isEmpty())
        assertFalse(viewModel.isSearching.value)
    }

    @Test
    fun `searchBooks should load results`() = testScope.runTest {
        // Given
        val query = "Harry Potter"

        val mockSearchResults = List(15) { i ->
            BookEntity(
                id = "SEARCH$i",
                title = "Harry Potter $i",
                authors = "J.K. Rowling",
                coverId = 1000 + i,
                firstPublishYear = 1997 + i,
                subjects = "Fantasy",
                description = "Magical book $i",
                category = "fiction"
            )
        }

        // Важно: мокаем вызов для init блока ViewModel
        coEvery {
            repository.loadBooksByCategory(BookCategory.FICTION, 20, 1)
        } returns emptyList()

        // Мокаем вызов для поиска
        coEvery {
            repository.searchBooks(query, 20, 1)
        } returns mockSearchResults

        // Создаем ViewModel после мокирования (важно!)
        viewModel = BookViewModel(repository)
        advanceUntilIdle() // Даем выполниться init блоку

        // When
        viewModel.searchBooks(query)
        advanceUntilIdle()

        // Then
        assertEquals(15, viewModel._searchBooks.value.size)
        assertEquals("SEARCH0", viewModel._searchBooks.value.first().id)
        assertFalse(viewModel.isSearching.value)
        assertFalse(viewModel.hasMoreSearch.value) // 15 < 20, поэтому false

        // Проверяем, что метод был вызван
        coVerify(exactly = 1) {
            repository.searchBooks(query, 20, 1)
        }
    }

    @Test
    fun `loadMoreSearch should paginate search results`() = testScope.runTest {
        val query = "test"

        val mockResultsPage1 = List(20) { i ->
            BookEntity(
                id = "P1$i",
                title = "Book P1 $i",
                authors = "Author",
                coverId = i,
                firstPublishYear = 2023,
                subjects = "Test",
                description = "Description"
            )
        }

        val mockResultsPage2 = List(10) { i ->
            BookEntity(
                id = "P2$i",
                title = "Book P2 $i",
                authors = "Author",
                coverId = i + 20,
                firstPublishYear = 2023,
                subjects = "Test",
                description = "Description"
            )
        }

        coEvery {
            repository.searchBooks(query, 20, 1)
        } returns mockResultsPage1

        coEvery { repository.searchBooks(query, 20, 2) } returns mockResultsPage2

        viewModel.searchBooks(query)
        advanceUntilIdle()

        assertEquals(20, viewModel._searchBooks.value.size)
        assertTrue(viewModel.hasMoreSearch.value)

        viewModel.loadMoreSearch()
        advanceUntilIdle()

        assertEquals(30, viewModel._searchBooks.value.size)
        assertFalse(viewModel.hasMoreSearch.value)
    }

    @Test
    fun `openBookById should load book details and update history`() = testScope.runTest {
        val bookId = "OL123W"

        val mockBookDetail = BookDetailResponse(
            authors = listOf(AuthorForDetail(
                author = AuthorX(
                    key = "/authors/OL1A"
                ),
                type = TypeX(
                    key = "/authors/OL1A"
                )
            )),
            covers = listOf(123),
            created = Created("2023-01-01T00:00:00", "1"),
            description = "Test description",
            key = "OL123W",
            last_modified = LastModified("2023-01-02T00:00:00", "1"),
            latest_revision = 1,
            location = "Location",
            revision = 1,
            subject_people = listOf("Person"),
            subject_places = listOf("Place"),
            subject_times = listOf("2023"),
            subjects = listOf("Fiction"),
            title = "Test Book",
            type = TypeX(key = "/type/book")
        )

        val mockBookEntity = BookEntity(
            id = "OL123W",
            title = "Test Book",
            authors = "Test Author",
            coverId = 123,
            firstPublishYear = 2023,
            subjects = "Fiction",
            description = "Test description",
            category = "fiction",
            shelfStatus = "want_to_read",
            viewAt = 1000L
        )

        coEvery { repository.getBookById(bookId) } returns mockBookDetail
        coEvery { repository.getBookShelfStatus(bookId) } returns "want_to_read"
        coEvery { repository.upsertBookForHistory(any()) } returns Unit

        viewModel.openBookById(bookId)
        advanceUntilIdle()

        assertNotNull(viewModel.currentBook.value)
        assertEquals("OL123W", viewModel.currentBook.value?.key)
        assertEquals("want_to_read", viewModel.currentBookShelfStatus.value)
        coVerify {
            repository.upsertBookForHistory(any())
        }
    }

    @Test
    fun `openBookById should handle errors gracefully`() = testScope.runTest {
        val bookId = "INVALID"

        coEvery { repository.getBookById(bookId) } throws RuntimeException("Book not found")
        advanceUntilIdle()

        assertNull(viewModel.currentBook.value)
        assertNull(viewModel.currentBookShelfStatus.value)
    }

    @Test
    fun `addBookToshelf should save book and update status`() = testScope.runTest {
        val bookDetail = BookDetailResponse(
            authors = listOf(AuthorForDetail(
                author = AuthorX(
                    key = "/authors/OL1A"
                ),
                type = TypeX(
                    key = "/authors/OL1A"
                )
            )),
            covers = listOf(123),
            created = Created("2023-01-01T00:00:00", "1"),
            description = "Test description",
            key = "OL123W",
            last_modified = LastModified("2023-01-02T00:00:00", "1"),
            latest_revision = 1,
            location = "Location",
            revision = 1,
            subject_people = listOf("Person"),
            subject_places = listOf("Place"),
            subject_times = listOf("2023"),
            subjects = listOf("Fiction"),
            title = "Test Book",
            type = TypeX(key = "/type/book")
        )

        coEvery { repository.getBookById("OL123W") } returns bookDetail
        coEvery { repository.getBookShelfStatus("OL123W") } returns null
        coEvery { repository.upsertBookForHistory(any()) } returns Unit

        viewModel.openBookById("OL123W")
        advanceUntilIdle()

        coEvery { repository.saveBookToShelf(any(), "reading") } returns Unit

        viewModel.addBookToShelf("reading")
        advanceUntilIdle()

        assertEquals("reading", viewModel.currentBookShelfStatus.value)
        coVerify { repository.saveBookToShelf(any(), "reading") }
    }

    @Test
    fun `removeBookFromShelf should delete book and clear status`() = testScope.runTest {

        val bookDetail = BookDetailResponse(
            authors = listOf(AuthorForDetail(
                author = AuthorX(
                    key = "/authors/OL1A"
                ),
                type = TypeX(
                    key = "/authors/OL1A"
                )
            )),
            covers = listOf(123),
            created = Created("2023-01-01T00:00:00", "1"),
            description = "Test description",
            key = "OL123W",
            last_modified = LastModified("2023-01-02T00:00:00", "1"),
            latest_revision = 1,
            location = "Location",
            revision = 1,
            subject_people = listOf("Person"),
            subject_places = listOf("Place"),
            subject_times = listOf("2023"),
            subjects = listOf("Fiction"),
            title = "Test Book",
            type = TypeX(key = "/type/book")
        )

        coEvery { repository.getBookById("OL123W") } returns bookDetail
        coEvery { repository.getBookShelfStatus("OL123W") } returns "reading"
        coEvery { repository.upsertBookForHistory(any()) } returns Unit

        viewModel.openBookById("OL123W")
        advanceUntilIdle()

        coEvery { repository.removeBookFromShelf(any(), null) } returns Unit
        viewModel.removeBookFromShelf()
        advanceUntilIdle()

        assertNull(viewModel.currentBookShelfStatus.value)
        coVerify { repository.removeBookFromShelf(any(),null) }
    }

    @Test
    fun `loadAllShelves should load books from all statuses`() = testScope.runTest {
        val wantToReadBooks = listOf(
            BookEntity(
                id = "WTR1",
                title = "Want to Read Book",
                authors = "Author",
                coverId = 1,
                firstPublishYear = 2023,
                subjects = "Fiction",
                description = "Description",
                shelfStatus = "want_to_read"
            )
        )

        val readingBooks = listOf(
            BookEntity(
                id = "R1",
                title = "Reading Book",
                authors = "Author",
                coverId = 2,
                firstPublishYear = 2023,
                subjects = "Fiction",
                description = "Description",
                shelfStatus = "reading"
            )
        )

        val readBooks = listOf(
            BookEntity(
                id = "RD1",
                title = "Read Book",
                authors = "Author",
                coverId = 3,
                firstPublishYear = 2023,
                subjects = "Fiction",
                description = "Description",
                shelfStatus = "read"
            )
        )

        coEvery { repository.getBooksFromShelf("want_to_read") } returns wantToReadBooks
        coEvery { repository.getBooksFromShelf("reading") } returns readingBooks
        coEvery { repository.getBooksFromShelf("read") } returns readBooks

        viewModel.loadAllShelves()
        advanceUntilIdle()

        val shelfBooks = viewModel.shelfBooks.value
        assertEquals(1, shelfBooks["want_to_read"]?.size)
        assertEquals(1, shelfBooks["reading"]?.size)
        assertEquals(1, shelfBooks["read"]?.size)
        assertEquals("WTR1", shelfBooks["want_to_read"]?.first()?.id)
    }

    @Test
    fun `loadAllShelves should handle empty shelves`() = testScope.runTest {
        coEvery { repository.getBooksFromShelf("want_to_read") } returns emptyList()
        coEvery{ repository.getBooksFromShelf("reading")} returns emptyList()
        coEvery { repository.getBooksFromShelf("read") } returns emptyList()

        viewModel.loadAllShelves()
        advanceUntilIdle()

        val shelfBooks = viewModel.shelfBooks.value
        assertEquals(0, shelfBooks["want_to_read"]?.size ?: 0)
        assertEquals(0, shelfBooks["reading"]?.size ?: 0)
        assertEquals(0, shelfBooks["read"]?.size ?: 0)
    }

    @Test
    fun `loadHistory should load view history`() =testScope.runTest{
        val historyBooks = listOf(
            BookEntity(
                id = "H1",
                title = "History Book 1",
                authors = "Author",
                coverId = 1,
                firstPublishYear = 2023,
                subjects = "History",
                description = "Description",
                viewAt = 1000L
            ),
            BookEntity(
                id = "H2",
                title = "History Book 2",
                authors = "Author 2",
                coverId = 2,
                firstPublishYear = 2022,
                subjects = "History",
                description = "Description 2",
                viewAt = 2000L
            )
        )

        coEvery { repository.getViewHistory() } returns historyBooks
        viewModel.loadHistory()
        advanceUntilIdle()

        assertEquals(2, viewModel._booksInHistory.value.size)
        assertEquals("H1", viewModel._booksInHistory.value[0]?.id)
    }

    @Test
    fun `clearHistory should empty history list`() = testScope.runTest {
        // Given
        val historyBooks = listOf(
            BookEntity(
                id = "H1",
                title = "History Book",
                authors = "Author",
                coverId = 1,
                firstPublishYear = 2023,
                subjects = "History",
                description = "Description",
                viewAt = 1000L
            )
        )

        // Мокаем метод загрузки истории
        coEvery { repository.getViewHistory() } returns historyBooks
        // Мокаем метод очистки истории
        coEvery { repository.clearHistory() } returns Unit

        // Шаг 1: Загружаем историю
        viewModel.loadHistory()
        advanceUntilIdle()

        // Проверяем, что история загрузилась (не пустая)
        assertEquals(1, viewModel._booksInHistory.value.size)
        assertEquals("H1", viewModel._booksInHistory.value[0]?.id)

        // Шаг 2: Очищаем историю
        viewModel.clearHistory()
        advanceUntilIdle()

        // Теперь проверяем, что история пустая
        assertTrue(viewModel._booksInHistory.value.isEmpty())

        // Проверяем, что метод clearHistory был вызван
        coVerify(exactly = 1) {
            repository.clearHistory()
        }
    }

    @Test
    fun `addBookFromNotification should save reminder`() = testScope.runTest {
        // Given
        val reminder = BookReminderEntity(
            bookId = "OL123W",
            notificationTime = System.currentTimeMillis() + 86400000, // +1 day
            bookTitle = "Test Book",
            bookAuthor = "Test Author",
            coverId = 123,
            notificationId = 42
        )

        // Мокаем вызов для init блока ViewModel (если есть)
        coEvery {
            repository.loadBooksByCategory(BookCategory.FICTION, 20, 1)
        } returns emptyList()

        // Мокаем метод добавления напоминания с возвратом ID
        coEvery { repository.addBookNotification(reminder) } returns 99L

        // Создаем ViewModel после мокирования
        viewModel = BookViewModel(repository)
        advanceUntilIdle()

        // Когда - вызываем метод добавления напоминания
        viewModel.addBookFromNotification(reminder)

        // Ждем завершения корутины
        advanceUntilIdle()

        // Тогда - проверяем, что напоминание добавлено
        assertEquals(1, viewModel.notifications.value.size)
        assertEquals(99L, viewModel.notifications.value.first().id)
        assertEquals("Test Book", viewModel.notifications.value.first().bookTitle)

        // Проверяем, что метод был вызван
        coVerify(exactly = 1) {
            repository.addBookNotification(reminder)
        }
    }

    @Test
    fun `addBookFromNotification should handle negative id`() = testScope.runTest {
        val reminder = BookReminderEntity(
            bookId = "OL123W",
            notificationTime = System.currentTimeMillis() + 86400000,
            bookTitle = "Test Book",
            bookAuthor = "Test Author",
            coverId = 123,
            notificationId = 42
        )

        coEvery { repository.addBookNotification(reminder) } returns -1L
        viewModel.addBookFromNotification(reminder)
        advanceUntilIdle()
        assertTrue(viewModel.notifications.value.isEmpty())
        coVerify {
            repository.addBookNotification(reminder)
        }
    }

    @Test
    fun `getAllNotifications should load future reminders`() = testScope.runTest {
        val currentTime = System.currentTimeMillis()
        val futureReminder = BookReminderEntity(
            id = 1,
            bookId = "OL1",
            notificationTime = currentTime + 86400000, // +1 day
            bookTitle = "Future Book",
            bookAuthor = "Author",
            coverId = 123,
            notificationId = 1
        )
        val pastReminder = BookReminderEntity(
            id = 2,
            bookId = "OL2",
            notificationTime = currentTime - 86400000, // -1 day
            bookTitle = "Past Book",
            bookAuthor = "Author",
            coverId = 456,
            notificationId = 2
        )
        coEvery { repository.getAllNotitfications() } returns listOf(futureReminder, pastReminder)
        viewModel.getAllNotifications()
        advanceUntilIdle()

        assertEquals(1, viewModel.notifications.value.size)
        assertEquals("Future Book", viewModel.notifications.value.first().bookTitle)
    }

    @Test
    fun `getAllNotifications should handle empty list`() = testScope.runTest {
        coEvery { repository.getAllNotitfications() } returns emptyList()
        viewModel.getAllNotifications()
        advanceUntilIdle()
        assertEquals(0, viewModel.notifications.value.size)
    }

    @Test
    fun `deleteNotification should remove reminder`() = testScope.runTest {
        val reminder = BookReminderEntity(
            id = 1,
            bookId = "OL1",
            notificationTime = System.currentTimeMillis() + 86400000,
            bookTitle = "Book to Delete",
            bookAuthor = "Author",
            coverId = 123,
            notificationId = 1
        )

        coEvery { repository.addBookNotification(any()) } returns 1L
        coEvery { repository.getAllNotitfications() } returns listOf(reminder)

        viewModel.addBookFromNotification(reminder)
        viewModel.getAllNotifications()
        advanceUntilIdle()

        assertEquals(1, viewModel.notifications.value.size)
        coEvery { repository.deleteNotification(reminder) } returns Unit
        viewModel.deleteNotification(reminder) {

        }
        advanceUntilIdle()
        assertTrue(viewModel.notifications.value.isEmpty())
        coVerify { repository.deleteNotification(reminder) }
    }

    @Test
    fun `updateReminder should modify notification time`() = testScope.runTest {
        val oldReminder = BookReminderEntity(
            id = 1,
            bookId = "OL1",
            notificationTime = System.currentTimeMillis(),
            bookTitle = "Old Book",
            bookAuthor = "Author",
            coverId = 123,
            notificationId = 1
        )

        val newNotificationTime = System.currentTimeMillis() + 86400000 * 7 // +7 days
        val newNotificationId = 2

        coEvery { repository.updateNotification(any()) } returns Unit
        coEvery { repository.getAllNotitfications() } returns
                listOf(
                    oldReminder.copy(
                        notificationTime = newNotificationTime,
                        notificationId = newNotificationId
                    )
                )

        viewModel.updateReminder(oldReminder, newNotificationTime, newNotificationId)
        advanceUntilIdle()

        coVerify {
            repository.updateNotification(
                withArg {
                    updated -> assertEquals(newNotificationTime, updated.notificationTime)
                    assertEquals(newNotificationId, updated.notificationId)
                }
            )
        }
    }

    @Test
    fun `scroll position should be saved and restored`() = testScope.runTest {
        val category = BookCategory.FICTION
        val position = 10 to 50

        viewModel.saveScrollPosition(category, position.first, position.second)
        val retrieved = viewModel.getScrollPosition(category)
        assertEquals(position, retrieved)
    }

    @Test
    fun `getScrollposition should return null for unklown category`() = testScope.runTest {
        val unjnownCategory = BookCategory.SCIENCE_FICTION

        val result = viewModel.getScrollPosition(unjnownCategory)
        assertNull(result)
    }

    @Test
    fun `search should reset pagination for new query`() = testScope.runTest {
        // Given
        val query1 = "first query"
        val query2 = "second query"

        val mockResults1 = List(5) { i ->
            BookEntity(
                id = "Q1$i",
                title = "Book $i",
                authors = "Author",
                coverId = i,
                firstPublishYear = 2023,
                subjects = "Test",
                description = "Description",
                category = "test"
            )
        }

        val mockResults2 = List(3) { i ->
            BookEntity(
                id = "Q2$i",
                title = "Second Book $i",
                authors = "Author",
                coverId = i + 100,
                firstPublishYear = 2023,
                subjects = "Test",
                description = "Description",
                category = "test"
            )
        }

        // Мокаем вызов для init блока
        coEvery {
            repository.loadBooksByCategory(BookCategory.FICTION, 20, 1)
        } returns emptyList()

        // Мокаем ПЕРВЫЙ запрос - страница 1
        coEvery { repository.searchBooks(query1, 20, 1) } returns mockResults1

        // Мокаем ВТОРОЙ запрос - тоже страница 1 (потому что сброс)
        coEvery { repository.searchBooks(query2, 20, 1) } returns mockResults2

        // Создаем ViewModel после мокирования
        viewModel = BookViewModel(repository)
        advanceUntilIdle()

        // Когда - первый поиск
        viewModel.searchBooks(query1)
        advanceUntilIdle()

        // Проверяем первый результат
        assertEquals(5, viewModel._searchBooks.value.size)
        assertEquals("Q10", viewModel._searchBooks.value.first().id)

        // Когда - второй поиск (новый запрос)
        viewModel.searchBooks(query2)
        advanceUntilIdle()

        // Тогда - должен быть второй результат
        assertEquals(3, viewModel._searchBooks.value.size)
        assertEquals("Q20", viewModel._searchBooks.value.first().id)

        // Проверяем вызовы
        coVerify(exactly = 1) { repository.searchBooks(query1, 20, 1) }
        coVerify(exactly = 1) { repository.searchBooks(query2, 20, 1) }
    }

    @Test
    fun `error in loadCategory should not crush and update loading state`() = testScope.runTest {
        val category = BookCategory.FICTION

        coEvery {
            repository.loadBooksByCategory(category, 20, 1)
        } throws RuntimeException("Network error")

        viewModel.loadCategory(category)
        advanceUntilIdle()

        assertFalse(viewModel._isLoading.value)
        assertTrue(viewModel.currentBooks.value.isEmpty())
    }

    @Test
    fun `loadMoreSearch should not load when no more results`() = testScope.runTest {
        val query = "test"
        val mockResults = List(10) { i ->
            BookEntity(
                id = "T$i",
                title = "Test Book $i",
                authors = "Author",
                coverId = i,
                firstPublishYear = 2023,
                subjects = "Test",
                description = "Description"
            )
        }

        coEvery { repository.searchBooks(query, 20, 1) } returns mockResults
        viewModel.searchBooks(query)
        advanceUntilIdle()
        assertFalse (viewModel.hasMoreSearch.value)
        viewModel.loadMoreSearch()
        advanceUntilIdle()
        coVerify(exactly = 1){
            repository.searchBooks(any(), any(), any())
        }
    }

    @Test
    fun `loadMoreSearch should not load when currently searching`() = testScope.runTest {
        val query = "test"
        val mockResults = List(20) { i ->
            BookEntity(
                id = "T$i",
                title = "Test Book $i",
                authors = "Author",
                coverId = i,
                firstPublishYear = 2023,
                subjects = "Test",
                description = "Description"
            )
        }

        coEvery { repository.searchBooks(query, 20, 1) } returns mockResults
        viewModel.searchBooks(query)
        viewModel.loadMoreSearch()
    }

    @Test
    fun `addBookToShelf without current shelf should do nothing`() = testScope.runTest {
        assertNull(viewModel.currentBook.value)
        viewModel.addBookToShelf("reading")
        advanceUntilIdle()
        coVerify(exactly = 0) {
            repository.saveBookToShelf(any(), any())
        }
    }

    @Test
    fun `removeBookFromBookShelf without book should do nothing`() = testScope.runTest {
        assertNull(viewModel.currentBook.value)
        viewModel.removeBookFromShelf()
        advanceUntilIdle()
        coVerify(exactly = 0) {
            repository.removeBookFromShelf(any(), any())
        }
    }
  }