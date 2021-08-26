package com.example.direa;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Service
public interface GameUseCase {
    Mono<Void> addDocument(Game game) throws IOException;

    Flux<Game> searchByMultiQuery(String content);

    //Flux<Game> searchByMatchPhraseQuery(String input);

}
