package com.example.simplenotes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

/**
 * Адаптер для отображения списка заметок в RecyclerView
 * Отвечает за создание и привязку элементов списка к данным
 */
class NotesAdapter(
    private var notes: List<Note> = emptyList(),      // Список заметок для отображения
    private val onNoteClick: (Note) -> Unit,          // Обработчик клика по заметке
    private val onNoteLongClick: (Note) -> Unit       // Обработчик долгого нажатия на заметку
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    /**
     * ViewHolder для элемента списка заметок
     * Содержит ссылки на элементы интерфейса и логику привязки данных
     */
    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Элементы интерфейса элемента списка
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvContent: TextView = itemView.findViewById(R.id.tvContent)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)

        /**
         * Привязка данных заметки к элементам интерфейса
         * note - заметка для отображения
         */
        fun bind(note: Note) {
            // Установка заголовка и содержимого заметки
            tvTitle.text = note.title
            tvContent.text = note.content

            // Форматирование даты создания заметки
            val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            tvDate.text = dateFormat.format(Date(note.createdDate))

            // Установка обработчика клика по элементу
            itemView.setOnClickListener { onNoteClick(note) }

            // Установка обработчика долгого нажатия
            itemView.setOnLongClickListener {
                onNoteLongClick(note)
                true
            }
        }
    }

    /**
     * Создание нового ViewHolder при необходимости
     * parent - Родительская ViewGroup
     * viewType - Тип view (не используется в данном случае)
     * Возвращает Новый экземпляр NoteViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        // Создание view из layout-файла
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    /**
     * Привязка данных к существующему ViewHolder
     * holder ViewHolder для заполнения
     * position Позиция элемента в списке
     */
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        // Привязка данных заметки к ViewHolder
        holder.bind(notes[position])
    }

    /**
     * Получение количества элементов в списке
     * Возвращает: Количество заметок
     */
    override fun getItemCount(): Int = notes.size

    /**
     * Обновление списка заметок
     * newNotes Новый список заметок для отображения
     */
    fun updateNotes(newNotes: List<Note>) {
        notes = newNotes
        notifyDataSetChanged()  // Уведомление адаптера об изменении данных
    }
}