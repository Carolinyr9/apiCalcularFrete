package com.sorveteria;


import com.sun.net.httpserver.HttpServer;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebServlet("/frete")
public class FreteServelet extends HttpServlet {


    @Override
    public void doGet (HttpServletRequest request, HttpServletResponse response) throws IOException {

        Double resposta = null;
        try {
            resposta = CotacaoFrete.calcularFrete(request.getParameter("cep"));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        response.getWriter().print(resposta);
    }

}
