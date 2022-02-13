package ir.snapp.box.interview.task.repository.datasource.retrofit

import ir.snapp.box.interview.task.model.entity.AddressEntity
import retrofit2.http.GET
import retrofit2.http.Url

// api calls
interface ApiServices {

    @GET
    suspend fun getAddressFromPoint(@Url url: String) : AddressEntity


}