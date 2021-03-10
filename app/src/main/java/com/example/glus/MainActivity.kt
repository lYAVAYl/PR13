package com.example.glus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CountriesAdapter(val countries:List<Country>, val mainActivity:MainActivity):
    RecyclerView.Adapter<CountriesAdapter.CountryHolder>(){

    class CountryHolder(val view: ViewGroup):RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountriesAdapter.CountryHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.country, parent, false) as ViewGroup
        return CountryHolder(view)
    }

    override fun onBindViewHolder(holder: CountryHolder, position: Int) {
        val country = countries[position]

        holder.view.findViewById<TextView>(R.id.name).text = country.name
        holder.view.findViewById<TextView>(R.id.region).text = country.region
    }

    override fun getItemCount(): Int {
        return countries.count()
    }

}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = Room.databaseBuilder(applicationContext,
                CountryDatabase::class.java, "Country").build()

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_countries)


        CoroutineScope(Dispatchers.Main).launch {
            var list:List<CountryDB>? = null

            withContext(Dispatchers.IO){

                list = db.countryDao().getAll()
            }

            val listForRecyclerView = list!!.map { Country().apply {
                name = it.name
                capital = it.capital
                population = it.population
                region = it.region
                subregion = it.subregion
            } }

            recyclerView.adapter = CountriesAdapter(listForRecyclerView, this@MainActivity)
            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        }


        // обработка нажатия кнопки Загрузить
        findViewById<Button>(R.id.load).setOnClickListener{

            val currency = findViewById<EditText>(R.id.currency).text.toString()

            // загрузка в потоке
            val retrofit = Retrofit.Builder().baseUrl("https://restcountries.eu/").addConverterFactory(GsonConverterFactory.create()).build()
            val service = retrofit.create(RestCountries::class.java)
            val call = service.getCountriesByCurrency(currency)
            call.enqueue(object:Callback<List<Country>>{
                override fun onResponse(call: Call<List<Country>>, response: Response<List<Country>>
                ) {
                    // recyclerView.post{
                        val list = response.body()!! // надо бы проверять на null

                        recyclerView.adapter = CountriesAdapter(list, this@MainActivity)
                        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)

                    CoroutineScope(Dispatchers.IO).launch {
                        db.countryDao().clear()
                        db.countryDao().insert(
                                list.map { CountryDB(
                                        it.name!!
                                        ,it.region
                                        ,it.subregion
                                        ,it.capital
                                        ,it.population
                                )
                                }
                        )
                    }
                }

                override fun onFailure(call: Call<List<Country>>, t: Throwable) {
                    Toast.makeText(this@MainActivity, getString(R.string.error) + t.localizedMessage,
                            Toast.LENGTH_LONG).show()
                }
            })
        }

    }
}