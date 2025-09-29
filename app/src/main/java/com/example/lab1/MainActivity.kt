package com.example.lab1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    // Объявляем UI элементы
    private lateinit var generateButton: Button
    private lateinit var thresholdInput: EditText
    private lateinit var resultOutput: TextView
    private lateinit var generatedListText: TextView

    // Создаем экземпляр нашего класса с логикой
    private val listProcessor = ListProcessor()

    // Переменная для хранения сгенерированного списка
    private var currentList: List<Int> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализируем UI элементы
        initializeViews()

        // Настраиваем обработчики событий
        setupClickListeners()

        // Генерируем начальный список
        generateNewList()
    }

    /**
     * Находим все UI элементы по их ID
     */
    private fun initializeViews() {
        generateButton = findViewById(R.id.generateButton)
        thresholdInput = findViewById(R.id.thresholdInput)
        resultOutput = findViewById(R.id.resultOutput)
        generatedListText = findViewById(R.id.generatedListText)
    }

    /**
     * Настраиваем обработчики нажатий
     */
    private fun setupClickListeners() {
        generateButton.setOnClickListener {
            processList()
        }
    }

    /**
     * Генерирует новый случайный список и обновляет интерфейс
     */
    private fun generateNewList() {
        // Используем класс для генерации списка
        currentList = listProcessor.generateRandomList(size = 15, min = 1, max = 50)

        // Показываем сгенерированный список на экране
        generatedListText.text = "Сгенерированный список:\n${currentList.joinToString()}"

        // Очищаем предыдущие результаты
        resultOutput.text = "Введите пороговое значение и нажмите кнопку"
        thresholdInput.text.clear()
    }

    /**
     * Обрабатывает список и выводит результаты (основная логика)
     */
    private fun processList() {
        val thresholdText = thresholdInput.text.toString()

        // Проверяем, что пользователь ввел число
        if (thresholdText.isEmpty()) {
            resultOutput.text = "Ошибка: Введите пороговое значение"
            return
        }

        try {
            val threshold = thresholdText.toInt()

            // Используем методы нашего класса для вычислений
            val count = listProcessor.countElementsLessThan(currentList, threshold)
            val elements = listProcessor.findElementsLessThan(currentList, threshold)

            // Формируем результат
            val result = if (elements.isEmpty()) {
                "Элементов меньше $threshold не найдено"
            } else {
                """
                Количество элементов меньше $threshold: $count
                Найденные элементы: ${elements.joinToString()}
                """.trimIndent()
            }

            resultOutput.text = result

        } catch (e: NumberFormatException) {
            resultOutput.text = "Ошибка: Введите корректное число"
        }
    }
}