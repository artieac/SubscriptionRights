package com.alwaysmoveforward.subscriptionrights.data.repositories;

import com.alwaysmoveforward.subscriptionrights.data.Entities.ApplicationEntity;
import com.alwaysmoveforward.subscriptionrights.data.dao.ApplicationDAO;
import com.alwaysmoveforward.subscriptionrights.data.mapper.ApplicationMapper;
import com.alwaysmoveforward.subscriptionrights.domainmodel.Application;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * The only layer allowed to touch {@link ApplicationEntity}/{@link ApplicationDAO}.
 * Inputs and outputs are Domain Models or primitives.
 */
@Repository
public class ApplicationRepository {

    private final ApplicationDAO applicationDAO;
    private final ApplicationMapper applicationMapper;

    public ApplicationRepository(ApplicationDAO applicationDAO, ApplicationMapper applicationMapper) {
        this.applicationDAO = applicationDAO;
        this.applicationMapper = applicationMapper;
    }

    public List<Application> findAll() {
        return applicationDAO.findAll().stream().map(applicationMapper::toDomainModel).toList();
    }

    public Optional<Application> findById(Long id) {
        return applicationDAO.findById(id).map(applicationMapper::toDomainModel);
    }

    public Application save(Application application) {
        ApplicationEntity saved = applicationDAO.save(applicationMapper.toEntity(application));
        return applicationMapper.toDomainModel(saved);
    }

    public void deleteById(Long id) {
        applicationDAO.deleteById(id);
    }

    public boolean existsById(Long id) {
        return applicationDAO.existsById(id);
    }
}
