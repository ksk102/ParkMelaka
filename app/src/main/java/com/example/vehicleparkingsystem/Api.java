package com.example.vehicleparkingsystem;

public class Api {
    private static final String ROOT_URL = "http://192.168.1.4/ParkMelaka_WebServices/api.php?api=";
//    private static final String ROOT_URL = "http://parkmelaka-webservices.000webhostapp.com/api.php?api=";
    public static final String URL_GET_USER_PASSWORD = ROOT_URL + "getUserPassword";
    public static final String URL_GET_USER_DETAIL = ROOT_URL + "getUserDetail";
    public static final String URL_GET_LOCATION = ROOT_URL + "getLocation";
    public static final String URL_START_TRANSACTION = ROOT_URL + "startTransaction";
    public static final String URL_END_TRANSACTION = ROOT_URL + "endTransaction";
    public static final String URL_UPDATE_BALANCE = ROOT_URL + "updateBalance";
}