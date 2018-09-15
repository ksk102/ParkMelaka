package com.example.vehicleparkingsystem;

public class Api {
//    private static final String ROOT_URL = "http://10.0.82.93/ParkMelaka_WebServices/api.php?api=";
    private static final String ROOT_URL = "http://parkmelaka-webservices.000webhostapp.com/api.php?api=";
    public static final String URL_GET_USER_PASSWORD = ROOT_URL + "getUserPassword";
    public static final String URL_GET_USER_DETAIL = ROOT_URL + "getUserDetail";
    public static final String URL_GET_LOCATION = ROOT_URL + "getLocation";
    public static final String URL_START_TRANSACTION = ROOT_URL + "startTransaction";
    public static final String URL_END_TRANSACTION = ROOT_URL + "endTransaction";
    public static final String URL_UPDATE_BALANCE = ROOT_URL + "updateBalance";
    public static final String URL_UPDATE_CHARGES = ROOT_URL + "updateCharges";
    public static final String URL_CHECK_EMAIL_EXISTS = ROOT_URL + "checkEmailExists";
    public static final String URL_CREATE_USER = ROOT_URL + "createUser";
    public static final String URL_GET_HISTORY_LIST = ROOT_URL + "getHistoryList";
}