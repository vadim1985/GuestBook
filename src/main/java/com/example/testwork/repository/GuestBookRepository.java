package com.example.testwork.repository;

import com.example.testwork.entity.GuestBook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GuestBookRepository extends CrudRepository<GuestBook, Integer> {
    List<GuestBook> findAll();
    Page findAll(Pageable pageable);
    Page findAllByOrderByUserNameAsc(Pageable pageable);
    Page findAllByOrderByDateAsc(Pageable pageable);
}
