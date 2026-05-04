package com.weather.client;

import com.weather.grpc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @Autowired
    private WeatherGrpcService grpcService;

    @GetMapping("/temperatura")
    public Map<String, Object> getTemperatura(@RequestParam String cidade) {
        try {
            TemperaturaResponse response = grpcService.getTemperatura(cidade);
            return criarResposta(true, "Sucesso", mapearTemperatura(response));
        } catch (Exception e) {
            return criarResposta(false, "Erro ao obter temperatura: " + e.getMessage(), null);
        }
    }

    @GetMapping("/previsao")
    public Map<String, Object> getPrevisao(@RequestParam String cidade) {
        try {
            PrevisaoResponse response = grpcService.getPrevisao(cidade);
            return criarResposta(true, "Sucesso", mapearPrevisao(response));
        } catch (Exception e) {
            return criarResposta(false, "Erro ao obter previsão: " + e.getMessage(), null);
        }
    }

    @GetMapping("/cidades")
    public Map<String, Object> listarCidades() {
        try {
            CidadesListResponse response = grpcService.listarCidades();
            Map<String, Object> data = new HashMap<>();
            data.put("cidades", response.getCidadesList());
            data.put("total", response.getTotal());
            return criarResposta(true, "Sucesso", data);
        } catch (Exception e) {
            return criarResposta(false, "Erro ao listar cidades: " + e.getMessage(), null);
        }
    }

    @PostMapping("/cadastrar")
    public Map<String, Object> cadastrarCidade(@RequestBody Map<String, Object> payload) {
        try {
            String nome = (String) payload.get("nome");
            Double temperatura = (Double) payload.get("temperatura");
            
            if (nome == null || nome.isEmpty() || temperatura == null) {
                return criarResposta(false, "Nome e temperatura são obrigatórios", null);
            }

            CidadeResponse response = grpcService.cadastrarCidade(nome, temperatura.floatValue());
            
            Map<String, Object> data = new HashMap<>();
            data.put("sucesso", response.getSucesso());
            data.put("mensagem", response.getMensagem());
            data.put("cidade", response.getCidade());
            
            return criarResposta(response.getSucesso(), response.getMensagem(), data);
        } catch (Exception e) {
            return criarResposta(false, "Erro ao cadastrar cidade: " + e.getMessage(), null);
        }
    }

    @GetMapping("/estatisticas")
    public Map<String, Object> getEstatisticas(@RequestParam String cidade) {
        try {
            EstatisticasResponse response = grpcService.getEstatisticas(cidade);
            return criarResposta(true, "Sucesso", mapearEstatisticas(response));
        } catch (Exception e) {
            return criarResposta(false, "Erro ao obter estatísticas: " + e.getMessage(), null);
        }
    }

    private Map<String, Object> mapearTemperatura(TemperaturaResponse response) {
        Map<String, Object> map = new HashMap<>();
        map.put("cidade", response.getCidade());
        map.put("temperatura", response.getTemperatura());
        map.put("condicao", response.getCondicao());
        map.put("umidade", response.getUmidade());
        return map;
    }

    private Map<String, Object> mapearPrevisao(PrevisaoResponse response) {
        Map<String, Object> map = new HashMap<>();
        map.put("cidade", response.getCidade());
        map.put("previsoes", response.getPrevisoesList().stream()
                .map(dia -> {
                    Map<String, Object> diaMapa = new HashMap<>();
                    diaMapa.put("data", dia.getData());
                    diaMapa.put("temperaturaMaxima", dia.getTemperaturaMaxima());
                    diaMapa.put("temperaturaMinima", dia.getTemperaturaMinima());
                    diaMapa.put("condicao", dia.getCondicao());
                    diaMapa.put("probabilidadeChuva", dia.getProbabilidadeChuva());
                    return diaMapa;
                })
                .toList());
        return map;
    }

    private Map<String, Object> mapearEstatisticas(EstatisticasResponse response) {
        Map<String, Object> map = new HashMap<>();
        map.put("cidade", response.getCidade());
        map.put("temperaturaMedia", response.getTemperaturaMedia());
        map.put("temperaturaMinima", response.getTemperaturaMinima());
        map.put("temperaturaMaxima", response.getTemperaturaMaxima());
        map.put("diasComChuva", response.getDiasComChuva());
        map.put("diasSemChuva", response.getDiasSemChuva());
        return map;
    }

    private Map<String, Object> criarResposta(boolean sucesso, String mensagem, Object dados) {
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("sucesso", sucesso);
        resposta.put("mensagem", mensagem);
        resposta.put("dados", dados);
        return resposta;
    }
}
