package com.example.direa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Controller
@Slf4j
public class GameController {
    @Autowired
    private GameUseCase gameUseCase;

    @Autowired
    private GameService service1;

    private GameProvider provider;

    private Game game;

    @PostMapping("/addDocument")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> add(@RequestBody Game game) throws IOException {
        return gameUseCase.addDocument(game);
    }

    @PostMapping("/indexcreate")
    @ResponseStatus(HttpStatus.CREATED)
    public void create() throws IOException {
        service1.create();
    }

    //@GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Flux<Game> getDocument(@RequestParam(name = "content") String content) {
        System.out.println(content);
        return gameUseCase.searchByMultiQuery(content);
    }

    @GetMapping("/searching")
    public void searchDocument() throws IOException {
        service1.findByContent("1위");
    }

    @GetMapping("/allsearch")
    @ResponseStatus(HttpStatus.OK)
    public void searchAll() throws IOException {
        service1.findByAll();
    }

}
