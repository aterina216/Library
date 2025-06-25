package com.example.library.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.library.R
import com.example.library.api.getBooksWithDescription
import com.example.library.data.Book
import com.example.library.databinding.FragmentDetailBookBinding
import com.example.library.databinding.ItemBookBinding
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DetailBookFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailBookFragment : Fragment() {

    private lateinit var binding: FragmentDetailBookBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d("DetailBookFragment", "onCreateView: Создание фрагмента")

        binding = FragmentDetailBookBinding.inflate(inflater, container, false)

        // Получаем книгу из arguments
        val book = arguments?.getParcelable<Book>(ARG_BOOK)

        // Если книга не null, заполняем данные
        book?.let {
            binding.bookTitle.text = it.title
            binding.bookAuthor.text = it.author_name.joinToString(", ")
            binding.bookPublishYear.text = "Year: ${it.first_publish_year ?: "Unknown Year"}"

            // Здесь можно загрузить изображение обложки
            val coverUrl = if (it.cover_i != null) {
                "https://covers.openlibrary.org/b/id/${it.cover_i}-L.jpg"
            } else {
                "https://via.placeholder.com/150x225?text=No+Cover"
            }

            Glide.with(requireContext())
                .load(coverUrl)
                .into(binding.bookCoverImage)

            // Отображаем описание
            binding.bookDescription.text = it.description ?: "Описание не доступно"
        }

        return binding.root
    }

    companion object {
        private const val ARG_BOOK = "book"

        fun newInstance(book: Book): DetailBookFragment {
            val fragment = DetailBookFragment()
            val bundle = Bundle().apply {
                putParcelable(ARG_BOOK, book)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("DetailBookFragment", "Фрагмент отсоединяется от активности")
    }
}