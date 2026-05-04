package com.weather.client;

import com.weather.grpc.*;
import io.grpc.Channel;
import org.springframework.stereotype.Service;

@Service
public class WeatherGrpcService {

    private final WeatherServiceGrpc.WeatherServiceBlockingStub weatherServiceStub;

    public WeatherGrpcService(Channel grpcChannel) {
        this.weatherServiceStub = WeatherServiceGrpc.newBlockingStub(grpcChannel);
    }

    public TemperaturaResponse getTemperatura(String cidade) {
        CidadeRequest request = CidadeRequest.newBuilder()
                .setNome(cidade)
                .build();
        return weatherServiceStub.getTemperaturaAtual(request);
    }

    public PrevisaoResponse getPrevisao(String cidade) {
        CidadeRequest request = CidadeRequest.newBuilder()
                .setNome(cidade)
                .build();
        return weatherServiceStub.getPrevisaoCincoDias(request);
    }

    public CidadesListResponse listarCidades() {
        Empty empty = Empty.newBuilder().build();
        return weatherServiceStub.listarCidades(empty);
    }

    public CidadeResponse cadastrarCidade(String nome, float temperatura) {
        CadastrarCidadeRequest request = CadastrarCidadeRequest.newBuilder()
                .setNome(nome)
                .setTemperatura(temperatura)
                .build();
        return weatherServiceStub.cadastrarCidade(request);
    }

    public EstatisticasResponse getEstatisticas(String cidade) {
        CidadeRequest request = CidadeRequest.newBuilder()
                .setNome(cidade)
                .build();
        return weatherServiceStub.getEstatisticasClimaticas(request);
    }
}
