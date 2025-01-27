package com.sorveteria;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;

public class CotacaoFrete {
    private static final String API_URL = "https://www.melhorenvio.com.br/api/v2/me/shipment/calculate";
    private static final String TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxIiwianRpIjoiYzI2M2I5YWZmMTQ5NWViMTQ2NzNiNWUzOWZhMmQ0MDNiNjdiMWQxYTM5YmEwMzcyNGZiYzU4YTI2ZjUwNGZiZmJlZDVhMWE5MWQ2YjNjNGIiLCJpYXQiOjE3MzE2ODMyNzIuNzU4ODY2LCJuYmYiOjE3MzE2ODMyNzIuNzU4ODY3LCJleHAiOjE3NjMyMTkyNzIuNzQ4NjksInN1YiI6IjlkN2VjZmFiLTBkNTAtNDNjNi05MzlkLWMxOGU1NGNlMzk3ZSIsInNjb3BlcyI6WyJzaGlwcGluZy1jYWxjdWxhdGUiXX0.EZSmqIqNyn8NlXddJCiRm1g5oZwiSjF1lxk09mQzKGX4m8s_eZj31-RNLBCYQ8utpOBiiaq5guedqcKtsnTKG0tikztbBGggFDkFOM3qqdaooomzXneaC-x7kQK9UogcSQZPHygos33wC0tTu-iPLr3Ik25Skma5SL7by48_IOBBx8zrujezd7Lz406BfwwgMdDDzufOQQWWUK2CMSOXapI_FMX7OJE5bGvi1030-HFT21oKyHL1Yn7QK2AhnjJRuD2X7Z87n12tJWb6OZrR6rOUCXSk6GgqjWq6sZCnHLeY1aMdtfUibXkwAaIVeJflLKGn8YDnYsDZPLXBlHRUgRkhuVy037wciE00sy6RuNmXk5rjzawdz3cMUYZpJ-V5H_gSNd1Da7Xo4UGIiGESpDb4Q6URSpHhLr17IJ-fxw2W7wG7WXJBrg_o4KE7j8EfPE-JV1xZwIzdF-SZp5twGiO0Tqjq2pRm2SzaWqPBn0FPd0_MfXfSOM9Zflxe2UcFeNrQ4nzTxebJXq-5mXAK-TfeE2Bu974H9TB6J_vnlq4lEPtxSCZ1ZormllVIV6YnfWUtzUPT5bAhhghdUer_qM0hiBpxcKsQsCizh-xHqMK5eTq7t_Ku4J97I7ShUC6p6WutG7Hw0vgsp3UTy3ReoZZ4UNcr_Kn0ORb8hYl1wj0";  // Token de autenticação

    public static double calcularFrete(String cepDestino) throws IOException, InterruptedException {

        String jsonBody = String.format(
                "{\n" +
                        "    \"from\": {\n" +
                        "        \"postal_code\": \"07115000\"\n" +
                        "    },\n" +
                        "    \"to\": {\n" +
                        "        \"postal_code\": \"%s\"\n" +
                        "    },\n" +
                        "    \"products\": [\n" +
                        "        {\n" +
                        "            \"id\": \"x\",\n" +
                        "            \"width\": 11,\n" +
                        "            \"height\": 17,\n" +
                        "            \"length\": 11,\n" +
                        "            \"weight\": 0.3,\n" +
                        "            \"insurance_value\": 10.1,\n" +
                        "            \"quantity\": 1\n" +
                        "        }\n" +
                        "    ],\n" +
                        "    \"options\": {\n" +
                        "        \"receipt\": false,\n" +
                        "        \"own_hand\": false\n" +
                        "    },\n" +
                        "    \"services\": \"1,2,18\"\n" +
                        "}", cepDestino
        );

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + TOKEN)
                .header("User-Agent", "Aplicação (carolinyr9)")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            System.out.println("Erro: " + response.statusCode());
            System.out.println("Mensagem: " + response.body());
            throw new IOException("Unexpected code " + response.statusCode() + ": " + response.body());
        }

        return processarResposta(response.body());
    }

    private static double processarResposta(String respostaJson) {
        try {
            JSONArray jsonResponse = new JSONArray(respostaJson);

            double menorValorFrete = Double.MAX_VALUE;
            boolean entregaDistante = false;

            for (int i = 0; i < jsonResponse.length(); i++) {
                JSONObject service = jsonResponse.getJSONObject(i);

                if (service.has("error")) {
                    String erro = service.getString("error");
                    System.out.println("Erro: " + erro);
                    continue;
                }

                double valorFrete = service.getDouble("price");

                if (valorFrete > 20) {
                    entregaDistante = true;
                }

                if (valorFrete < menorValorFrete) {
                    menorValorFrete = valorFrete;
                }
            }

            if (entregaDistante || menorValorFrete == Double.MAX_VALUE) {
                return 1;
            }

            return menorValorFrete;

        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }



}