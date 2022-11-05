package ar.edu.itba.houseitba.WebService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebServiceWrapper {

    public static Webservice ws;
    private static String IP = "192.168.1.227";
    private static String PORT = "8080";

    public static final String SHARED_PREF_IP = "shared_preference_ip_key";
    public static final String SHARED_PREF_PORT = "shared_preference_port_key";

    public static void GenerateInstance(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + IP + ":" + PORT +"/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ws = retrofit.create(Webservice.class);
    }

    public static void setIP(String ip){
        IP = ip;
    }

    public static void setPort(String port){
        PORT = port;
    }



    public static String getIP(){
        return IP;
    }

    public static String getPort(){
        return PORT;
    }

    public static Webservice GetServiceInstance(){
        return ws;
    }
}
