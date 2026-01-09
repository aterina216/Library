package com.example.library

import com.example.library.data.database.entity.BookEntity
import com.example.library.data.database.entity.BookReminderEntity
import com.example.library.data.mapper.BookMapper
import com.example.library.data.mapper.BookMapper.getSafeDescription
import com.example.library.data.mapper.BookMapper.toEntity
import com.example.library.data.models.Author
import com.example.library.data.models.AuthorForDetail
import com.example.library.data.models.AuthorX
import com.example.library.data.models.Availability
import com.example.library.data.models.Book
import com.example.library.data.models.Created
import com.example.library.data.models.LastModified
import com.example.library.data.models.SearchBook
import com.example.library.data.models.TypeX
import com.example.library.data.models.response.BookDetailResponse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.jvm.java
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunWith(JUnit4::class)
class BookMapperTest {

    @Test
    fun `book toEntity should map all fields correctly`() {

        val book = Book(
            key = "OL123W",
            title = "Война и мир",
            authors = listOf(
                Author(key = "/authors/OL123A", name = "Лев Толстой"),
                Author(key = "/authors/OL124A", name = "Редактор")
            ),
            cover_id = 123456,
            first_publish_year = 1869,
            subject = listOf("Роман", "Исторический роман", "Русская классика"),
            // Остальные поля не используются в маппере
            availability = Availability(
                status = "available",
                available_to_browse = true,
                available_to_borrow = false,
                available_to_waitlist = false,
                is_printdisabled = false,
                is_readable = true,
                is_lendable = false,
                is_previewable = false,
                identifier = "test123",
                isbn = "9781234567890",
                oclc = 1,
                openlibrary_work = "OL123W",
                openlibrary_edition = "OL12345678",
                last_loan_date = 1,
                num_waitlist = 1,
                last_waitlist_date = 1,
                is_restricted = false,
                is_browseable = true,
                __src__ = "openlibrary"
            ),
            cover_edition_key = "OL12345678",
            edition_count = 50,
            has_fulltext = true,
            ia = "warandpeace_tolstoy",
            ia_collection = listOf("printdisabled", "inlibrary"),
            lending_edition = "OL12345678",
            lending_identifier = "warandpeace",
            printdisabled = false,
            public_scan = true
        )

        val entity = book.toEntity()

        assertEquals("OL123W", entity.id)
        assertEquals("Война и мир", entity.title)
        assertEquals("Лев Толстой, Редактор", entity.authors)
        assertEquals(123456, entity.coverId)
        assertEquals(1869, entity.firstPublishYear)
        assertEquals("Роман, Исторический роман, Русская классика", entity.subjects)
        assertNull(entity.description)
    }

    @Test
    fun `book toEntity with empty lists should handle correctly`() {
        val book = Book(
            key = "OL999W",
            title = "Книга без данных",
            authors = emptyList(),
            cover_id = 0, // 0 может быть дефолтным значением
            first_publish_year = 0,
            subject = emptyList(),
            availability = Availability(
                status = "unknown",
                available_to_browse = false,
                available_to_borrow = false,
                available_to_waitlist = false,
                is_printdisabled = false,
                is_readable = false,
                is_lendable = false,
                is_previewable = false,
                identifier = "",
                isbn = "",
                oclc = 1,
                openlibrary_work = "",
                openlibrary_edition = "",
                last_loan_date = 1,
                num_waitlist = 1,
                last_waitlist_date = 1,
                is_restricted = false,
                is_browseable = false,
                __src__ = ""
            ),
            cover_edition_key = "",
            edition_count = 0,
            has_fulltext = false,
            ia = "",
            ia_collection = emptyList(),
            lending_edition = "",
            lending_identifier = "",
            printdisabled = false,
            public_scan = false
        )

        val entity = book.toEntity()

        assertEquals("OL999W", entity.id)
        assertEquals("Книга без данных", entity.title)
        assertEquals("", entity.authors)
        assertEquals(0, entity.coverId)
        assertEquals(0, entity.firstPublishYear)
        assertEquals("", entity.subjects)
    }

    @Test
    fun `bookDetailResponse toEntity with russian data should work correctly`() {
        val response = BookDetailResponse(
            key = "/works/OL123W",
            title = "Преступление и наказание",
            authors = listOf(
                AuthorForDetail(
                    author = AuthorX(
                        key = "/authors/OL234A",
                    ),
                    type = TypeX(key = "/type/author_role")
                )
            ),
            covers = listOf(12345, 67890),
            description = mapOf("value" to "Роман о нравственных страданиях Родиона Раскольникова"),
            subjects = listOf("Русская литература", "Психологический роман", "Философия"),
            created = Created(type = "/type/datetime", value = "1866-01-01T00:00:00.000Z"),
            last_modified = LastModified(
                type = "/type/datetime",
                value = "2023-01-01T00:00:00.000Z"
            ),
            latest_revision = 5,
            location = "/works/OL123W",
            revision = 5,
            subject_people = listOf("Раскольников", "Соня Мармеладова"),
            subject_places = listOf("Санкт-Петербург"),
            subject_times = listOf("XIX век"),
            type = TypeX(key = "/type/work")
        )

        val entity = response.toEntity()

        assertEquals("OL123W", entity.id)
        assertEquals("Преступление и наказание", entity.title)
        assertEquals("OL234A", entity.authors)
        assertEquals(12345, entity.coverId)
        assertEquals("Русская литература, Психологический роман, Философия", entity.subjects)
        assertEquals("Роман о нравственных страданиях Родиона Раскольникова", entity.description)
    }

    @Test
    fun `bookDetailResponse toEntity with string description should work correctly`() {
        val response = BookDetailResponse(
            key = "/works/OL456W",
            title = "Мастер и Маргарита",
            authors = emptyList(),
            covers = listOf(999),
            description = "Роман о дьяволе, посещающем Москву 1930-х годов",
            subjects = listOf("Мистика", "Сатира"),
            created = Created(type = "/type/datetime", value = "1967-01-01T00:00:00.000Z"),
            last_modified = LastModified(type = "/type/datetime", value = "2023-01-01T00:00:00.000Z"),
            latest_revision = 3,
            location = "/works/OL456W",
            revision = 3,
            subject_people = listOf("Воланд", "Мастер", "Маргарита"),
            subject_places = listOf("Москва", "Иерусалим"),
            subject_times = listOf("1930-е", "Древний Рим"),
            type = TypeX(key = "/type/work")
        )

        val entity = response.toEntity()

        assertEquals("OL456W", entity.id)
        assertEquals("Мастер и Маргарита", entity.title)
        assertEquals("Unknown author", entity.authors)
        assertEquals(999, entity.coverId)
        assertEquals("Мистика, Сатира", entity.subjects)
        assertEquals("Роман о дьяволе, посещающем Москву 1930-х годов", entity.description)
    }

    @Test
    fun `bookDetailResponse toEntity without cover should set coverId to -1`() {

        val response = BookDetailResponse(
            key = "/works/OL789W",
            title = "Книга без обложки",
            authors = listOf(),
            covers = emptyList(), // ПУСТОЙ СПИСОК!
            description = null,
            subjects = emptyList(),
            created = Created(type = "/type/datetime", value = "2000-01-01T00:00:00.000Z"),
            last_modified = LastModified(type = "/type/datetime", value = "2023-01-01T00:00:00.000Z"),
            latest_revision = 1,
            location = "/works/OL789W",
            revision = 1,
            subject_people = emptyList(),
            subject_places = emptyList(),
            subject_times = emptyList(),
            type = TypeX(key = "/type/work")
        )

        val entity = response.toEntity()

        assertEquals("OL789W", entity.id)
        assertEquals("Книга без обложки", entity.title)
        assertEquals("Unknown author", entity.authors)
        assertEquals(-1, entity.coverId)
        assertEquals("", entity.subjects)
        assertNull(entity.description)
    }

    @Test
    fun `bookDetailResponse toEntity with null authors should return Unknown author`() {
        val response = BookDetailResponse(
            key = "/works/OL111W",
            title = "Анонимная книга",
            authors = null, // NULL вместо списка!
            covers = listOf(111),
            description = "Книга без указания автора",
            subjects = listOf("Анонимная литература"),
            created = Created(type = "/type/datetime", value = "2000-01-01T00:00:00.000Z"),
            last_modified = LastModified(type = "/type/datetime", value = "2023-01-01T00:00:00.000Z"),
            latest_revision = 1,
            location = "/works/OL111W",
            revision = 1,
            subject_people = emptyList(),
            subject_places = emptyList(),
            subject_times = emptyList(),
            type = TypeX(key = "/type/work")
        )

        val entity = response.toEntity()

        assertEquals("Unknown author", entity.authors)
    }

    @Test
    fun `extractBookId should return correct id`() {
        val method = BookMapper::class.java.getDeclaredMethod("extractBookId", String::class.java)
        method.isAccessible = true

        val testCases = listOf(
            "/works/OL123W" to "OL123W",
            "/works/OL456W?edition=key%3AOL456W" to "OL456W",
            "OL789W" to "OL789W",
            "/authors/OL999A" to "OL999A",
            "" to "unknown",
            "/works/" to "unknown",
            "invalid_key" to "invalid_key",
            "/works/OL123W?param=value&other=test" to "OL123W",
            "/works/OL123W/" to "unknown",
            "OL123W?param=value" to "OL123W",
        )

        testCases.forEach { (input, expected) ->
            val result = method.invoke(BookMapper, input) as String
            assertEquals(expected, result) // ← УБРАЛ СООБЩЕНИЕ
        }
    }

    @Test
    fun `getSafeDescription should handle all description cases`() {
        val stringResponse = BookDetailResponse(
            key = "/works/OL1",
            title = "Test",
            authors = emptyList(),
            covers = emptyList(),
            description = "Просто строка",
            subjects = emptyList(),
            created = Created(type = "", value = ""),
            last_modified = LastModified(type = "", value = ""),
            latest_revision = 0,
            location = "",
            revision = 0,
            subject_people = emptyList(),
            subject_places = emptyList(),
            subject_times = emptyList(),
            type = TypeX(key = "")
        )

        val mapResponse = BookDetailResponse(
            key = "/works/OL2",
            title = "Test",
            authors = emptyList(),
            covers = emptyList(),
            description = mapOf("value" to "Описание из мапы"),
            subjects = emptyList(),
            created = Created(type = "", value = ""),
            last_modified = LastModified(type = "", value = ""),
            latest_revision = 0,
            location = "",
            revision = 0,
            subject_people = emptyList(),
            subject_places = emptyList(),
            subject_times = emptyList(),
            type = TypeX(key = "")
        )

        val complexMapResponse = BookDetailResponse(
            key = "/works/OL3",
            title = "Test",
            authors = emptyList(),
            covers = emptyList(),
            description = mapOf(
                "value" to "Сложное описание",
                "type" to "/type/text",
                "language" to mapOf("key" to "/languages/rus")
            ),
            subjects = emptyList(),
            created = Created(type = "", value = ""),
            last_modified = LastModified(type = "", value = ""),
            latest_revision = 0,
            location = "",
            revision = 0,
            subject_people = emptyList(),
            subject_places = emptyList(),
            subject_times = emptyList(),
            type = TypeX(key = "")
        )

        val nullResponse = BookDetailResponse(
            key = "/works/OL4",
            title = "Test",
            authors = emptyList(),
            covers = emptyList(),
            description = null,
            subjects = emptyList(),
            created = Created(type = "", value = ""),
            last_modified = LastModified(type = "", value = ""),
            latest_revision = 0,
            location = "",
            revision = 0,
            subject_people = emptyList(),
            subject_places = emptyList(),
            subject_times = emptyList(),
            type = TypeX(key = "")
        )

        val wrongTypeResponse = BookDetailResponse(
            key = "/works/OL5",
            title = "Test",
            authors = emptyList(),
            covers = emptyList(),
            description = mapOf("value" to 123), // Int вместо String!
            subjects = emptyList(),
            created = Created(type = "", value = ""),
            last_modified = LastModified(type = "", value = ""),
            latest_revision = 0,
            location = "",
            revision = 0,
            subject_people = emptyList(),
            subject_places = emptyList(),
            subject_times = emptyList(),
            type = TypeX(key = "")
        )

        assertEquals("Просто строка", stringResponse.getSafeDescription())
        assertEquals("Описание из мапы", mapResponse.getSafeDescription())
        assertEquals("Сложное описание", complexMapResponse.getSafeDescription())
        assertNull(nullResponse.getSafeDescription())
        assertNull(wrongTypeResponse.getSafeDescription())
    }

    @Test
    fun `searchBook toEntity should handle search results`() {
        val searchBook = SearchBook(
            key = "OL777W",
            title = "1984",
            author_name = listOf("Джордж Оруэлл"),
            cover_i = 7777,
            first_publish_year = 1949,
        )
        val entity = searchBook.toEntity()

        assertEquals("OL777W", entity.id)
        assertEquals("1984", entity.title)
        assertEquals("Джордж Оруэлл", entity.authors)
        assertEquals(7777, entity.coverId)
        assertEquals(1949, entity.firstPublishYear)
        assertNull(entity.subjects)
        assertNull(entity.description)
    }

    @Test
    fun `searchBook toEntity with null fields should handle gracefully`() {
        val searchBook = SearchBook(
            author_key = null,
            author_name =null,
            cover_edition_key = null,
            cover_i = null,
            ebook_access = null,
            edition_count = null,
            first_publish_year = null,
            has_fulltext = null,
            ia = null,
            ia_collection_s = null,
            key = null,
            language = null,
            lending_edition_s = null,
            lending_identifier_s =null,
            public_scan_b = null,
            title = null
        )

        val entity = searchBook.toEntity()

        assertTrue("ID должен начинаться с 'unknown_'", entity.id.startsWith("unknown_"))
        assertEquals("Без названия", entity.title)
        assertEquals("Неизвестен", entity.authors)
        assertNull(entity.coverId)
        assertNull(entity.firstPublishYear)
    }

    @Test
    fun `bookEntity creation with full data should work correctly`() {
        val bookEntity = BookEntity(
            id = "OL123W",
            title = "Тестовая книга",
            authors = "Автор 1, Автор 2",
            coverId = 123,
            firstPublishYear = 2023,
            subjects = "Фантастика, Приключения",
            description = "Описание книги",
            category = "fiction",
            shelfStatus = "want_to_read",
            viewAt = 1000000L
        )

        assertEquals("OL123W", bookEntity.id)
        assertEquals("Тестовая книга", bookEntity.title)
        assertEquals("Автор 1, Автор 2", bookEntity.authors)
        assertEquals(123, bookEntity.coverId)
        assertEquals(2023, bookEntity.firstPublishYear)
        assertEquals("Фантастика, Приключения", bookEntity.subjects)
        assertEquals("Описание книги", bookEntity.description)
        assertEquals("fiction", bookEntity.category)
        assertEquals("want_to_read", bookEntity.shelfStatus)
        assertEquals(1000000L, bookEntity.viewAt)
    }

    @Test
    fun `bookEntity creation with minimal data should work correctly`() {
        val bookEntity = BookEntity(
            id = "OL999W",
            title = "Минимальная книга",
            authors = "Автор",
            coverId = null,
            firstPublishYear = null,
            subjects = null,
            description = null,
        )

        assertEquals("OL999W", bookEntity.id)
        assertEquals("Минимальная книга", bookEntity.title)
        assertEquals("Автор", bookEntity.authors)
        assertNull(bookEntity.coverId)
        assertNull(bookEntity.firstPublishYear)
        assertNull(bookEntity.subjects)
        assertNull(bookEntity.description)
        assertEquals("", bookEntity.category)
        assertNull(bookEntity.shelfStatus)
        assertEquals(0L, bookEntity.viewAt)
    }

    @Test
    fun `bookReminderEntity creation should work correctly`() {
        val reminder = BookReminderEntity(
            bookId = "OL123W",
            notificationTime = 1700000000000L,
            bookTitle = "Напоминание о книге",
            bookAuthor = "Автор книги",
            coverId = 123,
            notificationId = 42,
            repeatInterval = 24 * 60 * 60 * 1000L
        )
        assertEquals(0L, reminder.id)
        assertEquals(1700000000000L, reminder.notificationTime)
        assertTrue(reminder.isActive)
        assertEquals("Напоминание о книге", reminder.bookTitle)
        assertEquals("Автор книги", reminder.bookAuthor)
        assertEquals(123, reminder.coverId)
        assertEquals(42, reminder.notificationId)
        assertEquals(24 * 60 * 60 * 1000L, reminder.repeatInterval)
        assertTrue(reminder.createdAt > 0L)
    }

    @Test
    fun `bookReminderEntity creation with minimal data should work correctly`() {
        val reminder = BookReminderEntity(
            bookId = "OL999W",
            notificationTime = 1700000000000L,
            bookTitle = "Минимальное напоминание",
            bookAuthor = null,
            coverId = null,
            notificationId = 1
        )

        assertEquals("OL999W", reminder.bookId)
        assertEquals(1700000000000L, reminder.notificationTime)
        assertEquals("Минимальное напоминание", reminder.bookTitle)
        assertNull(reminder.bookAuthor)
        assertNull(reminder.coverId)
        assertNull(reminder.repeatInterval)
        assertEquals(1, reminder.notificationId)
    }
}