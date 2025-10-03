package com.example.lab1

/**
 * Класс для обработки списка чисел
 * Содержит функциональные методы для работы со списком
 */
class ListProcessor {

    /**
     * Генерирует список случайных чисел
     * @param size размер списка
     * @param min минимальное значение
     * @param max максимальное значение
     * @return список случайных чисел
     */
    fun generateRandomList(size: Int = 10, min: Int = 1, max: Int = 100): List<Int> {
        val result = mutableListOf<Int>()
        for (i in 0 until size) {
            val randomNumber = (min..max).random()
            result.add(randomNumber)
        }
        return result // Возвращаем готовый список
    }

    /**
     * Находит количество элементов меньше заданного
     * @param numbers список чисел
     * @param threshold пороговое значение
     * @return количество элементов меньше threshold
     */
    fun countElementsLessThan(numbers: List<Int>, threshold: Int): Int {
        var count = 0
        for (number in numbers) {
            if (number < threshold) {
                count++
            }
        }
        return count
    }

    /**
     * Находит сами элементы меньше заданного
     * @param numbers список чисел
     * @param threshold пороговое значение
     * @return список элементов меньше threshold
     */
    fun findElementsLessThan(numbers: List<Int>, threshold: Int): List<Int> {
        val result = mutableListOf<Int>()
        for (number in numbers) {
            if (number < threshold) {
                result.add(number)
            }
        }
        return result
    }
}