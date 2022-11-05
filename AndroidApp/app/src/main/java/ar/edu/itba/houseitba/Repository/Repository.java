package ar.edu.itba.houseitba.Repository;

import ar.edu.itba.houseitba.WebService.Webservice;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class Repository {
    /*
    protected Webservice ws;
    private static String IP = "10.2.70.145";

    public Repository(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + IP + ":8080/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.ws = retrofit.create(Webservice.class);
    }

    public void setIP(String ip){
        IP = ip;
    }

     */
}
