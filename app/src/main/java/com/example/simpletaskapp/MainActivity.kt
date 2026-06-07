package com.example.simpletaskapp
import com.example.simpletaskapp.R

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
2
class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var taskList: ArrayList<String>
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var idList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etTask = findViewById<EditText>(R.id.etTask)
        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val listView = findViewById<ListView>(R.id.listView)

        database = FirebaseDatabase.getInstance().reference.child("tasks")

        taskList = ArrayList()
        idList = ArrayList()

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, taskList)
        listView.adapter = adapter

        // 🔥 Add Task
        btnAdd.setOnClickListener {
            val text = etTask.text.toString()
            if (text.isNotEmpty()) {
                val id = database.push().key!!
                val task = Task(id, text)
                database.child(id).setValue(task)
                etTask.text.clear()
            }
        }

        // 🔥 Load Tasks
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                taskList.clear()
                idList.clear()

                for (data in snapshot.children) {
                    val task = data.getValue(Task::class.java)
                    task?.let {
                        taskList.add(it.title!!)
                        idList.add(it.id!!)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        // 🔥 Delete Task
        listView.setOnItemClickListener { _, _, position, _ ->
            val id = idList[position]
            database.child(id).removeValue()
        }
    }
}