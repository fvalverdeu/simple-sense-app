package com.kull.simplesense.providers

import com.kull.simplesense.api.ApiRoutes
import com.kull.simplesense.models.Client
import com.kull.simplesense.routes.IClientRoutes
import retrofit2.Call


class ClientProvider {
    private var clientRoutes: IClientRoutes? = null

    init {
        val api = ApiRoutes()
        clientRoutes = api.getClientsRoutes()
    }

    fun getClientsByUser(iduser: String): Call<ArrayList<Client>>? {
        return clientRoutes?.getClientsByUser(iduser)
    }
}