package com.example.simplenotes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import com.example.simplenotes.databinding.ActivityViewNoteBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Активность для просмотра содержимого заметки
 * Отображает полную информацию о заметке и предоставляет возможность редактирования
 */
class ViewNoteActivity : AppCompatActivity() {

    // Binding для доступа к элементам интерфейса
    private lateinit var binding: ActivityViewNoteBinding

    // Текущая отображаемая заметка
    private lateinit var currentNote: Note

    /**
     * Инициализация активности, загрузка интерфейса и данных
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация View Binding
        binding = ActivityViewNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получение ID заметки из Intent
        val noteId = intent.getLongExtra("note_id", -1)

        // Если ID валидный, загружаем заметку, иначе закрываем активность
        if (noteId != -1L) {
            loadNote(noteId)
        } else {
            finish()
        }
    }

    /**
     * Отображение данных заметки в элементах интерфейса
     */
    private fun displayNote() {
        binding.tvTitle.text = currentNote.title      // Установка заголовка
        binding.tvContent.text = currentNote.content  // Установка содержимого
    }

    /**
     * Загрузка заметки из базы данных по ID
     * noteId - ID заметки для загрузки
     */
    private fun loadNote(noteId: Long) {
        // Запуск корутины в фоновом потоке для работы с БД
        CoroutineScope(Dispatchers.IO).launch {
            // Получение экземпляра базы данных
            val database = AppDatabase.getInstance(this@ViewNoteActivity)

            // Загрузка заметки по ID
            val note = database.noteDao().getNoteById(noteId)

            // Возврат в главный поток для обновления UI
            runOnUiThread {
                if (note != null) {
                    // Сохранение заметки и отображение данных
                    currentNote = note
                    displayNote()
                    setupClickListeners()  // Настройка обработчиков после загрузки данных
                } else {
                    // Если заметка не найдена, закрываем активность
                    finish()
                }
            }
        }
    }

    /**
     * Настройка обработчиков нажатий для кнопок
     */
    private fun setupClickListeners() {
        // Обработчик нажатия на кнопку редактирования
        binding.btnEdit.setOnClickListener {
            // Создание Intent для перехода к редактированию
            val intent = Intent(this, AddEditNoteActivity::class.java).apply {
                putExtra("note_id", currentNote.id)  // Передача ID заметки
            }
            // Запуск активности редактирования с ожиданием результата
            startActivityForResult(intent, EDIT_NOTE_REQUEST)
        }
    }

    /**
     * Обработка результата от дочерних активностей
     * requestCode - Код запроса
     * resultCode - Код результата
     * data - Данные, возвращенные из дочерней активности
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Проверяем, что это результат редактирования и оно успешно завершено
        if (requestCode == EDIT_NOTE_REQUEST && resultCode == AddEditNoteActivity.RESULT_NOTE_SAVED) {
            // Перезагружаем заметку для отображения обновленных данных
            loadNote(currentNote.id)
        }
    }

    /**
     * Компаньон объект для хранения констант
     */
    companion object {
        const val EDIT_NOTE_REQUEST = 100  // Код запроса для редактирования заметки
    }
}