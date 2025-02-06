package org.example;
import org.testng.annotations.Test;

public class TestConnection 
{
    @Test
    public void testConnection()
    {
        String token =EstablishSalesforceConnection.establishConnection();

        System.out.println("token: "+token);
        System.out.println("Connection succesfull");
    }       
}
