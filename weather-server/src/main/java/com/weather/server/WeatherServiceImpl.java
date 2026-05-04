package com.weather.server;

import io.grpc.stub.StreamObserver;
import com.weather.grpc.*;

import java.util.*;

public class WeatherServiceImpl extends WeatherServiceGrpc.WeatherServiceImplBase {

    private static final Map<String, CidadeData> cidadesDatabase = new HashMap<>();

    static {
        cidadesDatabase.put("Urutaí", new CidadeData("Urutaí", 26.5f, "Ensolarado"));
        cidadesDatabase.put("Brasília", new CidadeData("Brasília", 24.0f, "Nublado"));
        cidadesDatabase.put("Rio de Janeiro", new CidadeData("Rio de Janeiro", 28.5f, "Chuvoso"));
        cidadesDatabase.put("São Paulo", new CidadeData("São Paulo", 25.0f, "Ensolarado"));
        cidadesDatabase.put("Salvador", new CidadeData("Salvador", 30.0f, "Ensolarado"));
    }

    @Override
    public void getTemperaturaAtual(CidadeRequest request, StreamObserver<TemperaturaResponse> responseObserver) {
        String nomeCidade = request.getNome();
        
        if (cidadesDatabase.containsKey(nomeCidade)) {
            CidadeData cidade = cidadesDatabase.get(nomeCidade);
            TemperaturaResponse response = TemperaturaResponse.newBuilder()
                    .setCidade(nomeCidade)
                    .setTemperatura(cidade.temperatura)
                    .setCondicao(cidade.condicao)
                    .setUmidade(generateRandomUmidade())
                    .build();
            responseObserver.onNext(response);
        } else {
            TemperaturaResponse response = TemperaturaResponse.newBuilder()
                    .setCidade(nomeCidade)
                    .setTemperatura(0f)
                    .setCondicao("Cidade não encontrada")
                    .setUmidade(0)
                    .build();
            responseObserver.onNext(response);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getPrevisaoCincoDias(CidadeRequest request, StreamObserver<PrevisaoResponse> responseObserver) {
        String nomeCidade = request.getNome();
        
        PrevisaoResponse.Builder builder = PrevisaoResponse.newBuilder()
                .setCidade(nomeCidade);

        if (cidadesDatabase.containsKey(nomeCidade)) {
            CidadeData cidade = cidadesDatabase.get(nomeCidade);
            
            for (int i = 1; i <= 5; i++) {
                float variacao = (float) (Math.random() * 4 - 2);
                PrevisaoDia previsao = PrevisaoDia.newBuilder()
                        .setData("2026-05-0" + i)
                        .setTemperaturaMaxima(cidade.temperatura + variacao + 2)
                        .setTemperaturaMinima(cidade.temperatura + variacao - 2)
                        .setCondicao(cidade.condicao)
                        .setProbabilidadeChuva(generateRandomChance())
                        .build();
                builder.addPrevisoes(previsao);
            }
        }

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void listarCidades(Empty request, StreamObserver<CidadesListResponse> responseObserver) {
        CidadesListResponse response = CidadesListResponse.newBuilder()
                .addAllCidades(cidadesDatabase.keySet())
                .setTotal(cidadesDatabase.size())
                .build();
        
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void cadastrarCidade(CadastrarCidadeRequest request, StreamObserver<CidadeResponse> responseObserver) {
        String nomeCidade = request.getNome();
        float temperatura = request.getTemperatura();

        if (cidadesDatabase.containsKey(nomeCidade)) {
            CidadeResponse response = CidadeResponse.newBuilder()
                    .setSucesso(false)
                    .setMensagem("Cidade já existe no sistema")
                    .setCidade(nomeCidade)
                    .build();
            responseObserver.onNext(response);
        } else {
            cidadesDatabase.put(nomeCidade, new CidadeData(nomeCidade, temperatura, "Ensolarado"));
            CidadeResponse response = CidadeResponse.newBuilder()
                    .setSucesso(true)
                    .setMensagem("Cidade cadastrada com sucesso")
                    .setCidade(nomeCidade)
                    .build();
            responseObserver.onNext(response);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getEstatisticasClimaticas(CidadeRequest request, StreamObserver<EstatisticasResponse> responseObserver) {
        String nomeCidade = request.getNome();

        if (cidadesDatabase.containsKey(nomeCidade)) {
            CidadeData cidade = cidadesDatabase.get(nomeCidade);
            
            float tempMedia = cidade.temperatura;
            float tempMinima = cidade.temperatura - 3;
            float tempMaxima = cidade.temperatura + 3;
            int diasChuva = generateRandomDias();
            int diasSemChuva = 5 - diasChuva;

            EstatisticasResponse response = EstatisticasResponse.newBuilder()
                    .setCidade(nomeCidade)
                    .setTemperaturaMedia(tempMedia)
                    .setTemperaturaMinima(tempMinima)
                    .setTemperaturaMaxima(tempMaxima)
                    .setDiasComChuva(diasChuva)
                    .setDiasSemChuva(diasSemChuva)
                    .build();
            
            responseObserver.onNext(response);
        } else {
            EstatisticasResponse response = EstatisticasResponse.newBuilder()
                    .setCidade(nomeCidade)
                    .setTemperaturaMedia(0)
                    .setTemperaturaMinima(0)
                    .setTemperaturaMaxima(0)
                    .setDiasComChuva(0)
                    .setDiasSemChuva(0)
                    .build();
            responseObserver.onNext(response);
        }
        responseObserver.onCompleted();
    }

    private int generateRandomUmidade() {
        return 40 + (int) (Math.random() * 50);
    }

    private int generateRandomChance() {
        return (int) (Math.random() * 100);
    }

    private int generateRandomDias() {
        return (int) (Math.random() * 5);
    }

    static class CidadeData {
        String nome;
        float temperatura;
        String condicao;

        CidadeData(String nome, float temperatura, String condicao) {
            this.nome = nome;
            this.temperatura = temperatura;
            this.condicao = condicao;
        }
    }
}
