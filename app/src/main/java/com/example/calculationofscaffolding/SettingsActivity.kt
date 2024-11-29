package com.example.calculationofscaffolding

import android.app.AlertDialog
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calculationofscaffolding.DB.ElementsDatabase
import com.example.calculationofscaffolding.adapters.ElementsSettingsAdapter
import com.example.calculationofscaffolding.models.Element
import com.example.myapplication.databinding.ActivitySettingsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var adapter: ElementsSettingsAdapter
    private var elementToEdit: Element? = null // Хранит элемент, который редактируется

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val settingsRecycler = binding.settingsRecycler
        val db = ElementsDatabase.getDatabase(this)

        settingsRecycler.layoutManager = LinearLayoutManager(this)
        adapter = ElementsSettingsAdapter(
            emptyList(),
            onEditClick = { element: Element ->
                // Подготовка к редактированию элемента
                binding.constraintLayout.visibility = View.VISIBLE
                elementToEdit = element
                binding.etName.setText(element.name)
                binding.etPrice.setText(element.price.toString())
                binding.etWeight.setText(element.weight.toString())
                binding.savebt.text = "Сохранить изменения" // Изменяем текст кнопки
            }
        )

        settingsRecycler.adapter = adapter

        // Загрузка данных из базы
        CoroutineScope(Dispatchers.IO).launch {
            val elements = db.elementDao().getAllElements()
            withContext(Dispatchers.Main) {
                adapter.updateData(elements)
            }
        }

        binding.savebt.setOnClickListener {
            val name = binding.etName.text.toString()
            val priceText = binding.etPrice.text.toString()
            val weightText = binding.etWeight.text.toString()

            // Валидация ввода
            var hasError = false
            if (name.isEmpty()) {
                binding.etName.error = "Имя не может быть пустым"
                hasError = true
            }
            val price = priceText.toIntOrNull()
            if (price == null) {
                binding.etPrice.error = "Введите корректное число"
                hasError = true
            }
            val weight = weightText.toDoubleOrNull()
            if (weight == null) {
                binding.etWeight.error = "Введите корректное число"
                hasError = true
            }

            if (hasError) return@setOnClickListener

            // Если элемент для редактирования найден
            if (elementToEdit != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    // Редактирование элемента
                    val updatedElement = elementToEdit!!.copy(name = name, price = price!!, weight = weight!!)
                    db.elementDao().updateElement(updatedElement)

                    val updatedElements = db.elementDao().getAllElements()
                    withContext(Dispatchers.Main) {
                        adapter.updateData(updatedElements)
                        resetInputFields()
                    }
                }
            } else {
                // Тут можно вывести сообщение, что элемент не выбран для редактирования
                Toast.makeText(this, "Пожалуйста, выберите элемент для редактирования.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetInputFields() {
        binding.constraintLayout.visibility = View.GONE
    }
}
