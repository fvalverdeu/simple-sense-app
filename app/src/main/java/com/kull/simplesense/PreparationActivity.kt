package com.kull.simplesense

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.gson.Gson
import com.kull.simplesense.databinding.ActivityPreparationBinding
import com.kull.simplesense.models.Client
import com.kull.simplesense.models.Preparation
import com.kull.simplesense.models.User
import com.kull.simplesense.providers.ClientProvider
import com.kull.simplesense.providers.PreparationProvider
import com.kull.simplesense.utils.SessionHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PreparationActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private lateinit var binding: ActivityPreparationBinding
    var preparationProvider = PreparationProvider()
    var clientProvider = ClientProvider()
    var clients: ArrayList<Client> = ArrayList()
    var preparations: ArrayList<Preparation> = ArrayList()
    var positionSelectedClient = 0
    var positionSelectedPreparation = 0
    var userSession: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreparationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var sessionHandler = SessionHandler()
        userSession = sessionHandler.getUserFromSession(this)

        getPreparations()
        getClients()

        val btnPreparation: Button = findViewById<Button>(R.id.btnPreparation)
        val btnCancel: Button = findViewById(R.id.btnPreparationCancel)

        btnPreparation.setOnClickListener {
            val place = binding.txtPlace.text.toString()
            Log.e("PLACE: ", place)
            var objPreparation = Gson().toJson(preparations[positionSelectedPreparation])
            Log.e("OBJPREP", "${objPreparation.toString()}")
            var objClient = Gson().toJson(clients[positionSelectedClient])
            Log.e("OBJPCLI", "${objClient.toString()}")

            if (!place.isEmpty() && objPreparation !== null && objClient !== null) {
                val i = Intent(this, TemperatureActivity::class.java)
                i.putExtra("client", objClient)
                i.putExtra("preparation", objPreparation)
                i.putExtra("place", place)
                startActivity(i)
            }
        }
        btnCancel.setOnClickListener {
            val i = Intent(this, com.kull.simplesense.HomeActivity::class.java)
            startActivity(i)
        }
    }

    private fun getPreparations() {
        preparationProvider.getPreparations()?.enqueue(object: Callback<ArrayList<Preparation>> {
            override fun onResponse(
                call: Call<ArrayList<Preparation>>,
                response: Response<ArrayList<Preparation>>
            ) {
                if (response.body() != null) {
                    preparations = response.body()!!
                    /*val sizeOptions = preparations.size + 1 // delete when implements autocomplete
                    val preparationOptions = Array<String?>(sizeOptions){null}
                    preparationOptions[0] = "Preparación"
                    for (i in 1 until preparations.size) {
                        preparationOptions[i] = preparations[i].description
                    }
                    val adapter = ArrayAdapter(this@PreparationActivity,R.layout.list_item,preparationOptions)*/

                    val preparationOptions = Array<String?>(preparations.size){null}
                    for (i in 0 until preparations.size) {
                        preparationOptions[i] = preparations[i].description
                    }
                    val adapter = ArrayAdapter(this@PreparationActivity,R.layout.list_item,preparationOptions)

                    binding.idautocompletepreparation.setAdapter(adapter)
                    binding.idautocompletepreparation.setOnItemClickListener(AdapterView.OnItemClickListener {parent, view, position, id ->
                        Log.e("AUTOCOMPLETE", "POS: ${position}, VIEW: ${view}, ID: ${id}")
                        Log.e("AUTOCOMPLETETEXT", "TEXT: ${binding.idautocompletepreparation.text}")
                        val autocompleteText = binding.idautocompletepreparation.text
                        // positionSelectedPreparation = position
                        positionSelectedPreparation = preparationOptions.indexOf(autocompleteText.toString())
                    })

                    /*binding.idautocompletepreparation.onItemSelectedListener = object:AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            Log.e("NOTHING", "SPINNER_NOTHING")
                        }
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            Log.e("SPINNER", "POS: ${position}, VIEW: ${view}, ID: ${id}")
                            positionSelectedPreparation = position
                        }
                    }*/
                    /*with(binding.idautocompletepreparation){
                        setAdapter(adapter)
                        onItemClickListener = this@PreparationActivity
                    }*/
                }
            }

            override fun onFailure(call: Call<ArrayList<Preparation>>, t: Throwable) {
                Toast.makeText(this@PreparationActivity, t.message, Toast.LENGTH_LONG).show()
                Log.e("Error", "${t.message}")
            }
        })
    }

    private fun getClients() {
        clientProvider.getClientsByUser(userSession?.id.toString())?.enqueue(object: Callback<ArrayList<Client>> {
            override fun onResponse(
                call: Call<ArrayList<Client>>,
                response: Response<ArrayList<Client>>
            ) {
                if (response.body() != null) {
                    clients = response.body()!!

                    val clientOptions = Array<String?>(clients.size){null}
                    for (i in 0 until clients.size) {
                        clientOptions[i] = clients[i].name
                    }
                    val adapter = ArrayAdapter(this@PreparationActivity,R.layout.list_item,clientOptions)

                    binding.idautocompleteclient.setAdapter(adapter)
                    binding.idautocompleteclient.setOnItemClickListener(AdapterView.OnItemClickListener {parent, view, position, id ->
                        positionSelectedClient = position
                    })

                    /*with(binding.idautocompleteclient){
                        setAdapter(adapter)
                        onItemClickListener = this@PreparationActivity
                    }*/
                }
            }

            override fun onFailure(call: Call<ArrayList<Client>>, t: Throwable) {
                Toast.makeText(this@PreparationActivity, t.message, Toast.LENGTH_LONG).show()
                Log.e("Error", "${t.message}")
            }
        })
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item = parent?.getItemAtPosition(position)
        Log.e("ADAPT", "${parent} | ${view} | ${position} | ${id}")
        if (item is Client) positionSelectedClient = position
        if (item is Preparation) positionSelectedPreparation = position
        Toast.makeText(this@PreparationActivity, item.toString(), Toast.LENGTH_LONG).show()
    }
}