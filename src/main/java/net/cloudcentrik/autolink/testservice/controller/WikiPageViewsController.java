package net.cloudcentrik.autolink.testservice.controller;

import net.cloudcentrik.autolink.testservice.model.WikiPageViews;
import net.cloudcentrik.autolink.testservice.repository.WikiPageViewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("wiki-page-views")
public class WikiPageViewsController {

    private WikiPageViewsRepository wikiPageViewsRepository;

    public WikiPageViewsController(WikiPageViewsRepository wikiPageViewsRepository) {
        this.wikiPageViewsRepository = wikiPageViewsRepository;

    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public Mono<String> save(@RequestBody WikiPageViews wikiPageViews) {
        return wikiPageViewsRepository.save(wikiPageViews).map(g -> "Saved: " + g.getArticle());
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public Mono<WikiPageViews> get(@PathVariable("id") String id) {
        return wikiPageViewsRepository.findById(id);
    }

    @GetMapping(produces = "application/json")
    public Flux<WikiPageViews> get() {
        return wikiPageViewsRepository.findAll();
    }


    @PutMapping(value = "/{id}", produces = "application/json")
    public Mono<WikiPageViews> update(@PathVariable("id") long id, @RequestBody WikiPageViews wikiPageViews) {
        wikiPageViewsRepository.save(wikiPageViews);
        return Mono.just(wikiPageViews);
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public Mono<WikiPageViews> delete(@PathVariable("id") String id) {
        return wikiPageViewsRepository.findById(id);
    }
}
