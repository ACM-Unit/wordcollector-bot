package org.telegram.antischool.repositories;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import org.telegram.antischool.dto.SendRequestModel;
import org.telegram.antischool.dto.WordItem;
import org.telegram.antischool.handlers.Client;
import org.telegram.antischool.utils.Converter;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;


@Component
public class WordSocketRepository {

    private static final URI URL = URI.create("wss://proxy.edvibe.com/websocket?token=8b76d7d7-6a05-44ea-bbd7-645027483719");
    private final WebSocketClient webSocketClient = new ReactorNettyWebSocketClient();

/*     public void getSocketMessage(final SendRequestModel message, String nodeName, int countToTake, boolean isArray) {

            webSocketClient.execute(URL, session ->
                    session.send(Flux.from(session.textMessage(Converter.convertMsg(message))))
                            .thenMany(session.receive()
                                    .map(WebSocketMessage::getPayloadAsText)
                                    .filter(m -> !m.isEmpty())
                                    .flatMap(payload -> receiveMessage(payload, nodeName, isArray))
                                    .take(countToTake)
                                    .subscribeWith(output)
                            )
                            .doOnNext(m -> System.out.println("Client id=[{}] -> received: [{}]")
                            )
                            .then()
            ).subscribe();
            return output;
        }*/

    public void getMessage(SendRequestModel message, Consumer<List<WordItem>> callback, Converter converter) {
        Client client = new Client();
        client.connect(webSocketClient);
        Mono
                .fromRunnable(
                        () -> client.send(Converter.convertMsg(message))
                )
                .thenMany(client.receive())
                .map(converter::convert)
                .doOnNext(callback)
                .subscribe();

        Mono
                .delay(Duration.ofSeconds(10))
                .publishOn(Schedulers.boundedElastic())
                .subscribe(value -> {
                    client.disconnect();
                });
    }

    public void getArrayMessage(SendRequestModel message, Consumer<List<WordItem>> callback) {
        getMessage(message, callback, Converter.ArrayConverter());
    }

    public void getDataMessage(SendRequestModel message, Consumer<List<WordItem>> callback) {
        getMessage(message, callback, Converter.DataConverter());
    }

    public void getValueMessage(SendRequestModel message, Consumer<List<WordItem>> callback) {
        getMessage(message, callback, Converter.ValueConverter());
    }

    public void getArrayDataMessage(SendRequestModel message, Consumer<List<WordItem>> callback) {
        getMessage(message, callback, Converter.ArrayDataConverter());
    }


}
