

package com.example.todo

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ToDoActivity :AppCompatActivity() {
    lateinit var todoRecyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<ImageView>(R.id.wrtie).setOnClickListener {
            startActivity(Intent(this, ToDoWriteActivity::class.java))
        }
        todoRecyclerView = findViewById(R.id.todo_list)
        getToDoList()
        findViewById<EditText>(R.id.search_edittext).doAfterTextChanged {
            searchToDoList(it.toString())
        }
    }
    fun searchToDoList(Keyword: String){
        val retrofit = Retrofit.Builder()
            .baseUrl("http://mellowcode.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retrofitService = retrofit.create(RetrofitService::class.java)

        val header = HashMap<String, String>()
        val sp = this.getSharedPreferences(
            "user_info",
            Context.MODE_PRIVATE
        )
        val token = sp.getString("token", "")
        header.put("Authorization", "token " + token!!)
        retrofitService.searchToDoList(header, keyword).enqueue(object :Callback<ArrayList<ToDo>>{
            override fun onResponse(
                call: Call<ArrayList<ToDo>>,
                response: Response<ArrayList<ToDo>>
            ) {
                if(response.isSuccessful){
                    val todoList = response.body()
                    makeToDoList(todoList!!)
                }
            }

            override fun onFailure(call: Call<ArrayList<ToDo>>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    fun makeToDoList(todoList: ArrayList<ToDo>){
        todoRecyclerView.adapter =
        ToDoListRecyclerAdapter(
            todoList!!,
            LayoutInflater.from(this@ToDoActivity),
            this@ToDoActivity)
    }

    fun changeToDoComplete(todoId:Int, activity:ToDoActivity){
        val retrofit = Retrofit.Builder()
            .baseUrl("http://mellowcode.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retrofitService = retrofit.create(RetrofitService::class.java)

        val header = HashMap<String, String>()
        val sp = this.getSharedPreferences(
            "user_info",
            Context.MODE_PRIVATE
        )
        val token = sp.getString("token", "")
        header.put("Authorization", "token " + token!!)

        retrofitService.changeToDoComplete(header, todoId).enqueue(object : Callback<Any>{
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                activity.getToDoList()
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                activity.getToDoList()
            }
        })
    }

    fun getToDoList(){
        val retrofit = Retrofit.Builder()
            .baseUrl("http://mellowcode.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retrofitService = retrofit.create(RetrofitService::class.java)

        val header = HashMap<String, String>()
        val sp = this.getSharedPreferences(
            "user_info",
            Context.MODE_PRIVATE
        )
        val token = sp.getString("token", "")
        header.put("Authorization", "token " + token!!)

        retrofitService.getToDoList(header).enqueue(object :Callback<ArrayList<ToDo>>{
            override fun onResponse(
                call: Call<ArrayList<ToDo>>,
                response: Response<ArrayList<ToDo>>
            ) {
                if(response.isSuccessful){
                    val todoList = response.body()
                    makeToDoList(todoList)
                }
            }

            override fun onFailure(call: Call<ArrayList<ToDo>>, t: Throwable) {
            }
        })
    }
}

class ToDoListRecyclerAdapter(
   val todoList : ArrayList<ToDo>,
   val inflater: LayoutInflater,
   val activity: ToDoActivity


): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var previousdate: String=""

    inner class DateViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView
        init {
            dateTextView = itemView.findViewById(R.id.date)
        }
    }

    inner class ContentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val content: TextView
        val isComplete : ImageView
        init {
            content = itemView.findViewById(R.id.content)
            isComplete = itemView.findViewById(R.id.is_complete)
            isComplete.setOnClickListener {
                activity.changeToDoComplete(todoList.get(adapterPosition).id,activity)

            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val todo = todoList.get(position)
        val tempDate = todo.created.split("T")[0]
        if (previousdate == tempDate){
            return 0
        }else{
            previousdate = tempDate
            return 1
    }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            1 -> return DateViewHolder(inflater.inflate(R.layout.todo_date, parent, false))
            else -> return DateViewHolder(inflater.inflate(R.layout.todo_content, parent, false))

        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val todo = todoList.get(position)
        if (holder is DateViewHolder){
            (holder as DateViewHolder).dateTextView.text = todo.created.split("T")[0]
        } else {
            (holder as ContentViewHolder).content.text = todo.content
            if (todo.is_complete) {
                (holder as ContentViewHolder).isComplete.setImageDrawable(activity.resources.getDrawable(R.drawable.btn_radio_check,activity.theme))
            } else {
                (holder as ContentViewHolder).isComplete.setImageDrawable(activity.resources.getDrawable(R.drawable.btn_radio,activity.theme))

            }
        }
    }

    override fun getItemCount(): Int {
        return todoList.size
    }
}