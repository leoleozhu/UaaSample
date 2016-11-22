package com.github.leoleozhu.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.github.leoleozhu.domain.Foo;
import com.github.leoleozhu.security.AuthoritiesConstants;
import com.github.leoleozhu.security.SecurityUtils;
import com.github.leoleozhu.service.FooService;
import com.github.leoleozhu.web.rest.util.HeaderUtil;
import com.github.leoleozhu.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Foo.
 */
@RestController
@RequestMapping("/api")

@Secured(AuthoritiesConstants.ADMIN)
public class FooResource {

    private final Logger log = LoggerFactory.getLogger(FooResource.class);

    @Inject
    private FooService fooService;

    /**
     * POST  /foos : Create a new foo.
     *
     * @param foo the foo to create
     * @return the ResponseEntity with status 201 (Created) and with body the new foo, or with status 400 (Bad Request) if the foo has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/foos")
    @Timed
    public ResponseEntity<Foo> createFoo(@RequestBody Foo foo) throws URISyntaxException {
        String currentUser = SecurityUtils.getCurrentUserLogin();
        foo.setCreator(currentUser);
        log.debug("REST request to save Foo : {}", foo);
        if (foo.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("foo", "idexists", "A new foo cannot already have an ID")).body(null);
        }
        Foo result = fooService.save(foo);
        return ResponseEntity.created(new URI("/api/foos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("foo", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /foos : Updates an existing foo.
     *
     * @param foo the foo to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated foo,
     * or with status 400 (Bad Request) if the foo is not valid,
     * or with status 500 (Internal Server Error) if the foo couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/foos")
    @Timed
    public ResponseEntity<Foo> updateFoo(@RequestBody Foo foo) throws URISyntaxException {
        log.debug("REST request to update Foo : {}", foo);
        if (foo.getId() == null) {
            return createFoo(foo);
        }
        Foo result = fooService.save(foo);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("foo", foo.getId().toString()))
            .body(result);
    }

    /**
     * GET  /foos : get all the foos.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of foos in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/foos")
    @Timed
    public ResponseEntity<List<Foo>> getAllFoos(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Foos");
        Page<Foo> page = fooService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/foos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /foos/:id : get the "id" foo.
     *
     * @param id the id of the foo to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the foo, or with status 404 (Not Found)
     */
    @GetMapping("/foos/{id}")
    @Timed
    public ResponseEntity<Foo> getFoo(@PathVariable Long id) {
        log.debug("REST request to get Foo : {}", id);
        Foo foo = fooService.findOne(id);
        return Optional.ofNullable(foo)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /foos/:id : delete the "id" foo.
     *
     * @param id the id of the foo to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/foos/{id}")
    @Timed
    public ResponseEntity<Void> deleteFoo(@PathVariable Long id) {
        log.debug("REST request to delete Foo : {}", id);
        fooService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("foo", id.toString())).build();
    }

}
