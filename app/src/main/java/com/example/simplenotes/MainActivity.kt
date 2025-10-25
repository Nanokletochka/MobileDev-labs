package com.example.simplenotes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplenotes.databinding.ActivityMainBinding

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.withContext

import android.app.AlertDialog
import android.os.Parcelable

/**
 * Главная активность приложения - экран со списком заметок
 * Отображает все созданные заметки и предоставляет доступ к основным функциям
 */
class MainActivity : AppCompatActivity() {

    // Binding для доступа к элементам интерфейса через View Binding
    private lateinit var binding: ActivityMainBinding

    // Адаптер для управления списком заметок в RecyclerView
    private lateinit var notesAdapter: NotesAdapter

    // Лаунчер для обработки результата от активности добавления/редактирования заметок
    private val addNoteLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Если заметка успешно сохранена, обновляем список
        if (result.resultCode == AddEditNoteActivity.RESULT_NOTE_SAVED) {
            loadNotes()
        }
    }

    // Лаунчер для обработки результата от активности просмотра заметок
    private val viewNoteLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // После редактирования заметки обновляем список
        if (result.resultCode == ViewNoteActivity.EDIT_NOTE_REQUEST) {
            loadNotes()
        }
    }

    /**
     * Инициализация активности, создание пользовательского интерфейса
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Настройка компонентов интерфейса
        setupRecyclerView()
        setupClickListeners()

        // Загрузка заметок при запуске приложения
        loadNotes()
    }

    /**
     * Настройка RecyclerView для отображения списка заметок
     */
    private fun setupRecyclerView() {
        // Создание адаптера с обработчиками кликов
        notesAdapter = NotesAdapter(
            onNoteClick = { note ->
                // Обработчик клика по заметке - открываем просмотр
                val intent = Intent(this, ViewNoteActivity::class.java).apply {
                    putExtra("note_id", note.id)
                }
                viewNoteLauncher.launch(intent)
            },
            onNoteLongClick = { note ->
                // Обработчик долгого нажатия - показываем диалог удаления
                showDeleteDialog(note)
            }
        )

        // Настройка RecyclerView
        binding.rvNotes.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)  // Линейный макет
            adapter = notesAdapter  // Установка адаптера
        }
    }

    /**
     * Настройка обработчиков нажатий для кнопок
     */
    private fun setupClickListeners() {
        // Обработчик нажатия на плавающую кнопку добавления
        binding.fabAddNote.setOnClickListener {
            val intent = Intent(this, AddEditNoteActivity::class.java)
            addNoteLauncher.launch(intent)
        }
    }

    /**
     * Загрузка списка заметок из базы данных
     * Автоматич. обновление при изменении данных
     */
    private fun loadNotes() {
        // Получение экземпляра базы данных
        val database = AppDatabase.getInstance(this@MainActivity)

        // Получение все заметки
        val notes = database.noteDao().getAllNotes()

        // Наблюдение за изменениями в списке заметок
        notes.observe(this) { notesList ->
            // Обновление адаптера новыми данными
            notesAdapter.updateNotes(notesList)

            // Показ/скрытие состояния пустого списка
            showEmptyState(notesList.isEmpty())
        }
    }

    /**
     * Показ диалога подтверждения удаления заметки
     * note - заметка для удаления
     */
    private fun showDeleteDialog(note: Note) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete))  // Заголовок диалога
            .setMessage(getString(R.string.delete_note_confirmation))  // Сообщение подтверждения
            .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                // Подтверждение удаления
                deleteNote(note)
            }
            .setNegativeButton(getString(R.string.no)) { dialog, which ->
                // Отмена удаления
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Удаление заметки из базы данных
     * note - заметка для удаления
     */
    private fun deleteNote(note: Note) {
        // Запуск корутины в фоновом потоке для операции с БД
        lifecycleScope.launch(Dispatchers.IO) {
            val database = AppDatabase.getInstance(this@MainActivity)

            // Удаление заметки из базы данных
            database.noteDao().deleteNote(note)

            // Возврат в главный поток для обновления UI
            runOnUiThread {
                // Перезагрузка списка после удаления
                loadNotes()
            }
        }
    }

    /**
     * Показ или скрытие состояния пустого списка
     * show true - показать сообщение о пустом списке, false - скрыть
     */
    private fun showEmptyState(show: Boolean) {
        binding.tvEmpty.visibility = if (show) View.VISIBLE else View.GONE
        binding.rvNotes.visibility = if (show) View.GONE else View.VISIBLE
    }
}