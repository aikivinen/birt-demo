package com.github.aikivinen.birtdemo.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.github.aikivinen.birtdemo.domain.AuditLogEntry;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntry, Integer>{

}
