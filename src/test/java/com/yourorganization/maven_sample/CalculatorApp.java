package com.yourorganization.maven_sample;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class CalculatorApp {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Serve the Calculator UI
        server.createContext("/", exchange -> {
            byte[] response = getHtml().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        });

        // Handle calculation requests
        server.createContext("/api/calculate", exchange -> {
            Map<String, String> params = parseQuery(exchange.getRequestURI().getQuery());
            String result;
            try {
                double a = Double.parseDouble(params.get("a"));
                double b = Double.parseDouble(params.get("b"));
                String op = params.get("op");

                if ("add".equals(op)) {
                    result = String.valueOf(a + b);
                } else if ("sub".equals(op)) {
                    result = String.valueOf(a - b);
                } else if ("mul".equals(op)) {
                    result = String.valueOf(a * b);
                } else if ("div".equals(op)) {
                    result = (b == 0) ? "Error: Divide by zero" : String.valueOf(a / b);
                } else {
                    result = "Invalid operation";
                }
            } catch (Exception e) {
                result = "Invalid input";
            }

            byte[] response = result.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        });

        server.setExecutor(null);
        server.start();
        System.out.println("Calculator UI running on port " + port);
    }

    private static Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        if (query != null) {
            for (String pair : query.split("&")) {
                String[] kv = pair.split("=");
                if (kv.length == 2) {
                    map.put(kv[0], kv[1]);
                }
            }
        }
        return map;
    }

    private static String getHtml() {
        return "<!DOCTYPE html>"
            + "<html lang='en'><head><meta charset='UTF-8'><title>Java Calculator</title>"
            + "<style>"
            + "body{font-family:Arial,sans-serif;display:flex;justify-content:center;margin-top:60px;background:#f4f4f9;}"
            + ".calc{background:#fff;padding:25px;border-radius:10px;box-shadow:0 4px 12px rgba(0,0,0,0.1);width:280px;}"
            + "input,select,button{width:100%;padding:10px;margin:8px 0;box-sizing:border-box;font-size:16px;}"
            + "button{background:#007bff;color:#fff;border:none;border-radius:5px;cursor:pointer;}"
            + "button:hover{background:#0056b3;}"
            + "#result{font-weight:bold;font-size:18px;margin-top:12px;text-align:center;}"
            + "</style></head><body>"
            + "<div class='calc'><h2>Calculator</h2>"
            + "<input type='number' id='a' placeholder='First number' step='any'>"
            + "<select id='op'>"
            + "<option value='add'>+ Add</option>"
            + "<option value='sub'>- Subtract</option>"
            + "<option value='mul'>* Multiply</option>"
            + "<option value='div'>/ Divide</option>"
            + "</select>"
            + "<input type='number' id='b' placeholder='Second number' step='any'>"
            + "<button onclick='calc()'>Calculate</button>"
            + "<div id='result'>Result: &mdash;</div></div>"
            + "<script>"
            + "async function calc(){"
            + "const a=document.getElementById('a').value;"
            + "const b=document.getElementById('b').value;"
            + "const op=document.getElementById('op').value;"
            + "const res=await fetch('/api/calculate?a='+a+'&b='+b+'&op='+op);"
            + "document.getElementById('result').innerText='Result: '+await res.text();"
            + "}"
            + "</script></body></html>";
    }
}
