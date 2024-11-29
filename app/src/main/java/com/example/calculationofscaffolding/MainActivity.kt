package com.example.calculationofscaffolding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.calculationofscaffolding.DB.ElementsDatabase
import com.example.calculationofscaffolding.adapters.WallAdapter
import com.example.calculationofscaffolding.adapters.WallElementAdapter
import com.example.calculationofscaffolding.models.Element
import com.example.calculationofscaffolding.models.Wall
import com.example.calculationofscaffolding.models.WallsEntity
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var walls:MutableList<Wall> = mutableListOf()
    private lateinit var wallAdapter: WallAdapter
    private lateinit var elementsFromDb:List<Element>
    private lateinit var elementWallWeightAdapter: WallElementAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val settingsbt: Button = binding.settingsbt
        val comeback: Button = binding.comeback
        val clearall:Button = binding.clearAll
        val addWall:Button = binding.add



        setupRecyclers()
        clearall.setOnClickListener {
            // Диалог для запроса "Вы уверены, что хотите очистить все?"
            val dialog = AlertDialog.Builder(this)
                .setMessage("Вы уверены, что хотите очистить все?")
                .setPositiveButton("Да") { dialogInterface, which ->
                    // Когда нажата кнопка "Да"
                    walls = mutableListOf<Wall>()  // Очищаем список walls
                    updateRecyclers(walls)  // Обновляем адаптер с пустым списком
                }
                .setNegativeButton("Нет") { dialogInterface, which ->
                    // Когда нажата кнопка "Нет", ничего не делаем
                    dialogInterface.dismiss()
                }
                .create()

            // Показываем диалог
            dialog.show()
        }

        comeback.setOnClickListener {
            val db = Room.databaseBuilder(this, ElementsDatabase::class.java, "elements_database").build()

            // Диалог для запроса "Загрузить последнее?"
            val dialog = AlertDialog.Builder(this)
                .setMessage("Загрузить последнее?")
                .setPositiveButton("Да") { dialogInterface, which ->
                    // Когда нажата кнопка "Да"
                    CoroutineScope(Dispatchers.IO).launch {
                        val savedWalls = db.wallsDao().getLatestWalls()

                        if (savedWalls == null) {
                            // Если данных нет, показываем тост
                            withContext(Dispatchers.Main) {
                                Toast.makeText(applicationContext, "Нет сохраненных данных", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            // Если данные есть, парсим и обновляем UI
                            val wallsList = Gson().fromJson(savedWalls.wallsJson, Array<Wall>::class.java).toList()
                            walls = wallsList.toMutableList()
                            withContext(Dispatchers.Main) {
                                updateRecyclers(walls)
                            }
                        }
                    }
                }
                .setNegativeButton("Нет") { dialogInterface, which ->
                    // Когда нажата кнопка "Нет", ничего не делаем
                    dialogInterface.dismiss()
                }
                .create()

            // Показываем диалог
            dialog.show()
        }

        settingsbt.setOnClickListener {
            settingsButtonClick(this)
        }
        addWall.setOnClickListener {
            it.isEnabled = false
            showWallDialog()

            it.postDelayed({ it.isEnabled = true }, 500) // Разблокируем через 500 мс
        }




    }
    private fun settingsButtonClick(context: Context) {
        val intent = Intent(context, SettingsActivity::class.java)
        startActivity(intent)
    }
    private var isDialogVisible = false

    fun showWallDialog( wallToEdit: Wall? = null, position: Int? = null) {
        if (isDialogVisible) return
        isDialogVisible = true

        // Создание диалога
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_wall, null)
        builder.setView(dialogView)
        val dialog = builder.create()

        // Отображение номера стены
        val wallNumberTv = dialogView.findViewById<TextView>(R.id.wallNumber)
        val wallsNumber = if (wallToEdit != null) position?.plus(1) ?: walls.size + 1 else walls.size + 1
        wallNumberTv.text = "Стена № $wallsNumber"

        // Поля для ввода данных
        val etWidth = dialogView.findViewById<EditText>(R.id.etWidth)
        val etHeight = dialogView.findViewById<EditText>(R.id.etHeight)
        val etTiers = dialogView.findViewById<EditText>(R.id.etTiers)

        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)

        // Инициализация чекбоксов
        val heelCheckBox = dialogView.findViewById<CheckBox>(R.id.heel)
        val jaskCheckBox = dialogView.findViewById<CheckBox>(R.id.jask)

        // Установка значений для редактирования
        if (wallToEdit != null) {
            etWidth.setText(wallToEdit.width.toString())
            etHeight.setText(wallToEdit.height.toString())
            etTiers.setText(wallToEdit.tiers.toString())
            heelCheckBox.isChecked = wallToEdit.isHeelSelected
            jaskCheckBox.isChecked = wallToEdit.isJaskSelected
        }

        // Логика для чекбоксов
        heelCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                jaskCheckBox.isChecked = false
            } else {
                jaskCheckBox.isEnabled = true
            }
        }

        jaskCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                heelCheckBox.isChecked = false
            } else {
                heelCheckBox.isEnabled = true
            }
        }

        // Обработчик кнопки "Отмена"
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setOnDismissListener { isDialogVisible = false }

        // Обработчик кнопки "Сохранить"
        btnSave.setOnClickListener {
            val width = etWidth.text.toString().toDoubleOrNull()
            val height = etHeight.text.toString().toDoubleOrNull()
            val tiers = etTiers.text.toString().toIntOrNull()
            val isHeelChecked = heelCheckBox.isChecked
            val isJaskChecked = jaskCheckBox.isChecked

            // Проверка на корректность данных
            if (width != null && height != null && tiers != null) {
                // Дополнительные проверки
                if (height % 2 != 0.0) {
                    Toast.makeText(this, "Высота должна быть кратна 2", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (width % 3 != 0.0) {
                    Toast.makeText(this, "Ширина должна быть кратна 3", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (tiers > (height / 2)) {
                    Toast.makeText(this, "Количество рабочих ярусов не может превышать высоту / 2", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val database = ElementsDatabase.getDatabase(this)

                lifecycleScope.launch {
                    elementsFromDb = withContext(Dispatchers.IO) {
                        database.elementDao().getAllElements()

                    }
                    // Расчет элементов стены
                    val calculatedElements = calculateElementQuantities(elementsFromDb, width, height, tiers, isHeelChecked, isJaskChecked)

                    val newWall = Wall(
                        id = wallToEdit?.id ?: generateWallId(), // Генерация нового ID если это новая стена
                        width = width,
                        height = height,
                        tiers = tiers,
                        elements = calculatedElements,
                        isHeelSelected = isHeelChecked,
                        isJaskSelected = isJaskChecked
                    )

                    if (wallToEdit != null) {
                        // Обновление существующей стены
                        val index = walls.indexOfFirst { it.id == wallToEdit.id }
                        if (index != -1) {
                            walls[index] = newWall
                        }
                    } else {
                        // Добавление новой стены
                        walls.add(newWall)
                        Log.d("MainActivityWalls", "Walls: ${walls}")

                    }

                    // Обновление адаптера и других частей UI
                    updateRecyclers(walls)
                    updateTotal(walls)

                }


                dialog.dismiss()
            } else {
                Toast.makeText(this, "Пожалуйста, заполните все поля!", Toast.LENGTH_SHORT).show()
            }
        }

        // Показать диалог
        dialog.show()
    }

    
    fun generateWallId(): Int {
        return System.currentTimeMillis().toInt()  // Генерация уникального ID для стены
    }

    fun updateRecyclers(walls: List<Wall>){
        val elements = updateTotalElements(walls).toMutableList()
        wallAdapter.updateWalls(walls)  // Обновляем адаптер с новыми данными
        updateTotal(walls)

        elementWallWeightAdapter.submitList(elements)
        updateWallsTableDb(walls)
    }
    fun setupRecyclers(){
        setupWallsRecycler()
        setupTotalWallWeightRecycler()
    }
    fun setupWallsRecycler(){
        val recyclerView = binding.rvWalls
        recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        wallAdapter = WallAdapter(walls,
            onEdit = {wall,position ->
                showWallDialog(wall,position)
            },
            onDelete = {
                val builder = android.app.AlertDialog.Builder(this)
                builder.setTitle("Удаление элемента")
                builder.setMessage("Вы уверены, что хотите удалить эту стену}\"?")
                builder.setPositiveButton("Удалить") { _, _ ->
                    val index = walls.indexOf(it)
                    if (index != -1) {
                        walls.removeAt(index)
                        wallAdapter.notifyItemRemoved(index)
                    }
                }
                builder.setNegativeButton("Отмена ") { dialog, _ -> dialog.dismiss() }
                builder.show()

            }
        )
        recyclerView.adapter = wallAdapter


    }


    fun setupTotalWallWeightRecycler(){
        val elements = updateTotalElements(walls).toMutableList()
        elementWallWeightAdapter = WallElementAdapter(elements)

        val totalWallPriceRecycler:RecyclerView = binding.totalWallsWeightRecycler
        totalWallPriceRecycler.layoutManager = LinearLayoutManager(this)
        totalWallPriceRecycler.adapter = elementWallWeightAdapter
    }

    fun updateTotal(walls: List<Wall>){
        updateTotalWallSquare(walls)
        updateTotalWalsPrice(walls)
        updateTotalWallsWeight(walls)
    }
    fun updateTotalWallSquare(walls:List<Wall>){
        var totalSquare = walls.sumOf {
            it.height * it.width
        }
        var tvTotalWallSquare:TextView = binding.totalWallsSquare

        tvTotalWallSquare.text = "Общая площадь: \n ${totalSquare} м2"
    }
    fun updateTotalWalsPrice(walls:List<Wall>){
        val totalPrice = walls.sumOf {wall ->
            wall.elements
                .filter { element ->
                    !(wall.isJaskSelected && element.name == "пятки")&&
                    !(wall.isHeelSelected && element.name == "домкрат")
                }
                .sumOf { element ->
                    element.price * element.quantity
                }
        }
        var tvTotalWallPrice:TextView = binding.totalWallsPrice
        tvTotalWallPrice.text = "Общая цена элементов: \n ${totalPrice} p"

    }
    fun updateTotalWallsWeight(walls: List<Wall>){
        val totalWeight = walls.sumOf {wall ->
            wall.elements
                .filter { element ->
                    !(wall.isJaskSelected && element.name == "пятки")&&
                    !(wall.isHeelSelected && element.name == "домкрат")
                }
                .sumOf {  element ->
                element.weight * element.quantity
            }
        }
        var tvTotalWeight:TextView = binding.totalWallsWeight
        tvTotalWeight.text = "Общий вес элементов : \n ${totalWeight} кг"
    }
    fun updateTotalElements(walls: List<Wall>): List<Element> {
        // Создаем Map для хранения обновленных элементов с локальными полями для quantity
        val elementsMap = mutableMapOf<String, Element>()

        walls.forEach { wall ->
            wall.elements.forEach { element ->
                // Проверяем, есть ли этот элемент в нашем списке
                val existingElement = elementsMap[element.name]

                if (existingElement != null) {
                    // Если элемент уже есть, увеличиваем количество
                    existingElement.quantity += element.quantity
                } else {
                    // Если элемента нет, создаем новый элемент вручную
                    val newElement = Element(
                        id = element.id,
                        name = element.name,
                        price = element.price,
                        weight = element.weight
                    ).apply {
                        quantity = element.quantity // Устанавливаем значение quantity
                    }
                    elementsMap[element.name] = newElement
                }
            }
        }

        // Возвращаем список элементов из Map
        return elementsMap.values.toList()
    }

    fun updateWallsTableDb(walls: List<Wall>){
        val db = Room.databaseBuilder(this,ElementsDatabase::class.java,"elements_database").build()
        val wallsJson = Gson().toJson(walls)
        val wallsEntity = WallsEntity(wallsJson= wallsJson)

        CoroutineScope(Dispatchers.IO).launch {
            db.wallsDao().deleteAll()
            db.wallsDao().insert(wallsEntity)
        }

    }
    private fun calculateElementQuantities(
        elements: List<Element>,
        width: Double,
        height: Double,
        tiers: Int,
        isHeelChecked: Boolean,
        isJaskChecked: Boolean
    ): List<Element> {
        val frameWithLadderQuantity = kotlin.math.ceil(width / 18).toInt() * (height / 2).toInt()
        val framePassableQuantity = ((width / 3).toInt() + 1) * (height / 2).toInt() - frameWithLadderQuantity
        val totalArea = width * height
        val diagonal = Math.round(totalArea / 12 * 2).toInt()
        val horizontal = diagonal
        val rigels = width * (tiers.toDouble() / 3.0) * 2 //0
        val wallFixing = Math.round(totalArea / 12).toInt() //0
        val turnbuckle = wallFixing
        val jack = if (isJaskChecked) ((width / 3) + 1) * 2 else 0
        val flooring = (tiers / 3.0) * width * 3
        val heels = if (isHeelChecked) ((width / 3) + 1) * 2 else 0

        // Обновляем количество для каждого элемента на основе их имени и расчетов
        elements.forEach { element ->
            when (element.name) {
                "рама проходная" -> element.quantity = framePassableQuantity
                "рама с лестницей" -> element.quantity = frameWithLadderQuantity
                "диагональ" -> element.quantity = diagonal
                "горизонталь" -> element.quantity = horizontal
                "ригеля" -> element.quantity = rigels.toInt()
                "крепления к стене" -> element.quantity = wallFixing
                "хомут поворотный" -> element.quantity = turnbuckle
                "домкрат" -> element.quantity = jack.toInt()
                "настил" -> element.quantity = flooring.toInt()
                "пятки" -> element.quantity = heels.toInt()
                else -> element.quantity = 0 // Для остальных элементов
            }
        }

        // Возвращаем только те элементы, у которых quantity больше 0
        return elements
    }










}
