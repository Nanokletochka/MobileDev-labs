package com.example.simplenotes

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.simplenotes.databinding.ActivityAddEditNoteBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Активность для создания и редактирования заметок
 * Позволяет пользователю создавать новые заметки или редактировать существующие
 */
class AddEditNoteActivity : AppCompatActivity() {

    // Binding для доступа к элементам интерфейса
    private lateinit var binding: ActivityAddEditNoteBinding

    // Переменная для хранения редактируемой заметки (null если создаём новую)
    private var existingNote: Note? = null

    // Константы для передачи данных между активностями
    companion object {
        const val EXTRA_NOTE = "extra_note"  // Ключ для передачи заметки
        const val RESULT_NOTE_SAVED = 100    // Код результата при успешном сохранении
    }

    /**
     * Инициализация интерфейса
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация view binding
        binding = ActivityAddEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получаем ID заметки из Intent (если передан)
        val noteId = intent.getLongExtra("note_id", -1)

        // Если ID валидный, загружаем заметку для редактирования
        if (noteId != -1L) {
            loadNoteById(noteId)
        }

        // Если заметка загружена, заполняем поля данными
        if (existingNote != null) {
            // Режим редактирования - заполняем поля существующими данными
            binding.etTitle.setText(existingNote!!.title)
            binding.etContent.setText(existingNote!!.content)
        }

        // Настраиваем обработчики кликов
        setupClickListeners()
    }

    /**
     * Настройка обработчиков нажатий на кнопки
     */
    private fun setupClickListeners() {
        // Обработчик нажатия на кнопку сохранения
        binding.btnSave.setOnClickListener {
            saveNote()
        }
    }

    /**
     * Загрузка заметки из базы данных по ID
     * noteId ID - заметки для загрузки
     */
    private fun loadNoteById(noteId: Long) {
        // Запускаем корутину в фоновом потоке для работы с БД
        CoroutineScope(Dispatchers.IO).launch {
            // Получаем экземпляр базы данных
            val database = AppDatabase.getInstance(this@AddEditNoteActivity)

            // Загружаем заметку по ID
            val note = database.noteDao().getNoteById(noteId)

            // Возвращаемся в главный поток для обновления UI
            runOnUiThread {
                if (note != null) {
                    // Сохраняем загруженную заметку и заполняем поля
                    existingNote = note
                    binding.etTitle.setText(note.title)
                    binding.etContent.setText(note.content)
                }
            }
        }
    }

    /**
     * Сохранение заметки в базу данных
     * Проверяет правильность данных и сохраняет новую или обновляет существующую заметку
     */
    private fun saveNote() {
        // Получаем текст из полей ввода
        val title = binding.etTitle.text.toString().trim()
        val content = binding.etContent.text.toString().trim()

        // Проверяем, что заголовок не пустой
        if (title.isEmpty()) {
            Toast.makeText(this, "Введите заголовок", Toast.LENGTH_SHORT).show()
            return
        }

        // Запускаем корутину для работы с БД
        CoroutineScope(Dispatchers.IO).launch {
            // Получаем экземпляр базы данных
            val database = AppDatabase.getInstance(this@AddEditNoteActivity)

            if (existingNote != null) {
                // Режим редактирования - обновляем существующую заметку
                val updatedNote = existingNote!!.copy(
                    title = title,
                    content = content
                )
                database.noteDao().updateNote(updatedNote)
            } else {
                // Режим создания - создаём новую заметку
                val newNote = Note(
                    title = title,
                    content = content
                )
                database.noteDao().insertNote(newNote)
            }

            // Возвращаемся в главный поток для завершения активности
            runOnUiThread {
                // Устанавливаем результат успешного сохранения
                setResult(RESULT_NOTE_SAVED)

                // Закрываем активность
                finish()
            }
        }
    }
}