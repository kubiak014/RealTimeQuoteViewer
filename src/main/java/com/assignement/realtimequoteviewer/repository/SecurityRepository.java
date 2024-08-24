package com.assignement.realtimequoteviewer.repository;

import com.assignement.realtimequoteviewer.model.Security;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SecurityRepository extends JpaRepository<Security, String> {

    Security findByTickerId(String tickerId);
    List<Security> findAllBySecurityType(String securityType);
}
