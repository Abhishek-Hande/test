package com.threadresearch.turbo.studyconfigurator.server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.threadresearch.turbo.studyconfigurator.server.domain.ClientActivity;

/**
 * ClientActivityRepository
 * 
 * @author Harinath
 *
 */
@Repository
public interface ClientActivityRepository
        extends JpaRepository<ClientActivity, String>, JpaSpecificationExecutor<ClientActivity> {

    List<ClientActivity> findByClientId(String clientId);
}
