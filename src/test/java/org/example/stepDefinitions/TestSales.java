package org.example.stepDefinitions;

import org.example.*;
import org.json.JSONObject;
import org.junit.Before;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;


public class TestSales {
    SOQLQuery soqlQuery = new SOQLQuery();
    TestDataProvider testDataProvider = new TestDataProvider();
    RequestHandler request = new RequestHandler();

    @Before
    public void establishSalesforceConnection() {

        EstablishSalesforceConnection.establishConnection();
        System.out.println("prueba 1");
    }


    @Test(description = "Validar que al completar los campos obligatorios de la oportunidad se crea el registro")
    public void camposObligatoriosCompletos_CreacionExitosa() {
        JSONObject jsonPayload = testDataProvider.generateOpportunityJsonPayload();
        //se envia los datos para insertar el registro
        String idRecord = request.postRequestCreated(jsonPayload, "Opportunity");
        //Verificamos que no sea nulo el id del registro
        Assert.assertNotNull(idRecord);
        System.out.println("Response: " + idRecord);
    }

    @Test(description = "Verificar que si no se completa los campos requeridos no se crea el registro")
    public void camposObligatoriosVacios_CreacionFallida() {
        JSONObject jsonPayload = new JSONObject();
        //se envia datos vacios
        List<String> message = request.postBadRequest(jsonPayload, "Opportunity");

        //Verificamos el message
        Assert.assertEquals(message.get(0), "Required fields are missing: [Name, StageName, CloseDate]");

    }

    @Test(description = "Verificar que para cada etapa de la oportunidad le corresponda una probabilidad distinta")
    public void modificacionDeEtapa_AutocompletadoDeProbabilidad() {
        String idOpp;
        JSONObject jsonPayload = testDataProvider.generateOpportunityJsonPayload();
        idOpp = request.postRequestCreated(jsonPayload, "Opportunity");

        //se verifica la probabilidad del registro creado
        Map<String, Object> opportunity = soqlQuery.getOpportunityById(idOpp);
        Assert.assertEquals(opportunity.get("Probability").toString(), "100.0");

        //modificar etapa a Qualification
        jsonPayload.put("StageName", "Qualification");
        request.pathRequest(jsonPayload, idOpp, "Opportunity");
        Map<String, Object> opportunityQualification = soqlQuery.getOpportunityById(idOpp);
        //se verifica que se actualice la probabilidad
        Assert.assertEquals(opportunityQualification.get("Probability").toString(), "50.0");

        //modificar etapa a Value Proposition
        jsonPayload.put("StageName", "Value Proposition");
        request.pathRequest(jsonPayload, idOpp, "Opportunity");
        Map<String, Object> oppValueProposition = soqlQuery.getOpportunityById(idOpp);
        //se verifica que se actualice la probabilidad
        Assert.assertEquals(oppValueProposition.get("Probability").toString(), "10.0");

    }


    @Test(description = "Validar que al crear o actualizar una oportunidad si cumple con la condicion de etapa closed won e importe mayor a 25000 se cree un contrato en estado borrador. "
            + "Precondicion: Asociar un cliente sin contratos")
    public void validacionflow_creacionDeContratoExitoso() {
        JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("Name", "Oportunidad de Prueba");
        jsonPayload.put("CloseDate", "2025-12-31");
        jsonPayload.put("StageName", "Closed Won");
        jsonPayload.put("Amount", 30000);
        jsonPayload.put("AccountId", "001ak00000SjNcAAAV");

        // se envia la solicitud para crear la oportunidad
        String idOpp = request.postRequestCreated(jsonPayload, "Opportunity");

        // Obtener el contrato usando el AccountId
        Map<String, Object> contract = soqlQuery.getContractByAccountId(jsonPayload.getString("AccountId"));

        // Validar que el contrato no sea null
        Assert.assertNotNull(contract, "ERROR: No se encontró el contrato asociado a la cuenta con AccountId: " + jsonPayload.getString("AccountId"));

        // Verificar que el contrato esté en estado "Draft"
        Assert.assertEquals(contract.get("Status"), "Draft", "El estado del contrato no es 'Draft'");
    }

    @Test(description = "Verificar que si el importe de la oportunidad es menor a 25000 no se crea el contrato. " +
            "Precondicion: Asociar un cliente sin contratos")
    public void validacionflow_creacionDeContratoFallida() {
        JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("Name", "Oportunidad de Prueba");
        jsonPayload.put("CloseDate", "2025-01-30");
        jsonPayload.put("StageName", "Closed Won");
        jsonPayload.put("Amount", 10000);
        jsonPayload.put("AccountId", "001ak00000SjNcAAAV");

        //se envia la solicitud para crear la oportunidad
        request.postRequestCreated(jsonPayload, "Opportunity");

        // Obtener el contrato usando el AccountId
        Map<String, Object> contract = soqlQuery.getContractByAccountId(jsonPayload.getString("AccountId"));

        // Validar que el contrato no sea null
        Assert.assertNull(contract, "Error: Se creo el contrato");

    }

}
