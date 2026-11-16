package com.nter.insurance.constants;

public final class AppConstants {

    public static final String CLIENTS_PATH = "/api/clients";
    public static final String POLICIES_PATH = "/api/policies";

    public static final String DATE_PATTERN = "yyyy-MM-dd";

    public static final String CLIENT_NOT_FOUND = "Client %d not found";
    public static final String CLIENT_ALREADY_EXISTS = "Client %d already exists";
    public static final String DNI_ALREADY_EXISTS = "There is already a client with DNI %s";
    public static final String INVALID_DNI = "DNI %s is not valid";
    public static final String POLICY_NOT_FOUND = "Policy %d not found";
    public static final String POLICY_ALREADY_EXISTS = "Policy number %s already exists";

    private AppConstants() {
    }
}
