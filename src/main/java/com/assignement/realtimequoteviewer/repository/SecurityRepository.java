package com.assignement.realtimequoteviewer.repository;

import com.assignement.realtimequoteviewer.model.Security;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityRepository extends JpaRepository<Security, String> {

    Security findByTickerId(String tickerId);
}
