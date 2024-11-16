package com.sorveteria;

import com.sorveteria.CotacaoFrete;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            String cepDestino = "90570020";

            Double resposta = CotacaoFrete.calcularFrete(cepDestino);

            System.out.println("Resposta da API: " + resposta);

        } catch (IOException | InterruptedException e) {
            System.out.println("Erro ao calcular frete: " + e.getMessage());
        }
    }
}
