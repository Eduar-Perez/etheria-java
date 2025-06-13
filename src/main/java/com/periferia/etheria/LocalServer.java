package com.periferia.etheria;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

public class LocalServer {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/user", new LambdaHandlerAdapter());
        server.setExecutor(null);
        server.start();
        System.out.println("Servidor corriendo en http://localhost:" + port + "/user");
    }

    static class LambdaHandlerAdapter implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                return;
            }

            // Leer cuerpo
            String body = new String(exchange.getRequestBody().readAllBytes());

            // Preparar evento simulado
            APIGatewayProxyRequestEvent lambdaRequest = new APIGatewayProxyRequestEvent();
            lambdaRequest.setBody(body);
            
            Map<String, String> headers = new HashMap<>();
            exchange.getRequestHeaders().forEach((key, values) -> {
                if (!values.isEmpty()) {
                    headers.put(key, values.get(0));
                }
            });
            lambdaRequest.setHeaders(headers);
            
            // Llamar a tu handler de Lambda
            UserHandler handler = new UserHandler();
            APIGatewayProxyResponseEvent lambdaResponse = handler.handleRequest(lambdaRequest, null);

            // Escribir respuesta
            String responseBody = lambdaResponse.getBody();
            byte[] responseBytes = responseBody.getBytes();

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(lambdaResponse.getStatusCode(), responseBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseBytes);
            os.close();
        }
    }
}
