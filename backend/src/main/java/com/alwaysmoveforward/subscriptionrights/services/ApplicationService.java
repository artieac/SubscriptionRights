package com.alwaysmoveforward.subscriptionrights.services;

import com.alwaysmoveforward.subscriptionrights.data.repositories.ApplicationRepository;
import com.alwaysmoveforward.subscriptionrights.domainmodel.Application;
import com.alwaysmoveforward.subscriptionrights.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public List<Application> listApplications() {
        return applicationRepository.findAll();
    }

    public Application getApplication(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Application " + id + " not found"));
    }

    public Application getApplicationByExternalId(String externalId) {
        return applicationRepository.findByExternalId(externalId)
                .orElseThrow(() -> new NotFoundException("Application " + externalId + " not found"));
    }

    @Transactional
    public Application createApplication(String name, String externalId, String description) {
        return applicationRepository.save(Application.create(name, externalId, description));
    }

    @Transactional
    public Application updateApplication(Long id, String name, String externalId, String description) {
        Application application = getApplication(id);
        application.rename(name);
        application.changeExternalId(externalId);
        application.updateDescription(description);
        return applicationRepository.save(application);
    }

    @Transactional
    public void deleteApplication(Long id) {
        if (!applicationRepository.existsById(id)) {
            throw new NotFoundException("Application " + id + " not found");
        }
        applicationRepository.deleteById(id);
    }
}
