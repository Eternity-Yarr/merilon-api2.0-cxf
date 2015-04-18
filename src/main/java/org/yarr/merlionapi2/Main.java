package org.yarr.merlionapi2;

import https.api_merlion_com.dl.mlservice2.MLPort;

public class Main
{
    public static void main(String... args) {
        String login = System.getProperty("login");
        String password = System.getProperty("password");
        MLPort port = new MLPortProvider(login, password).get();

        String he = port.helloWorld("hai");
        System.out.println(he);
    }
}
