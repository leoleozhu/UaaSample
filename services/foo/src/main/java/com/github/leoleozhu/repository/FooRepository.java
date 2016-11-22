package com.github.leoleozhu.repository;

import com.github.leoleozhu.domain.Foo;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Foo entity.
 */
@SuppressWarnings("unused")
public interface FooRepository extends JpaRepository<Foo,Long> {

}
