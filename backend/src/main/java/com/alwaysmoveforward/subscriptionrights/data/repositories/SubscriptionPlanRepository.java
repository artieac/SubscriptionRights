package com.alwaysmoveforward.subscriptionrights.data.repositories;

import com.alwaysmoveforward.subscriptionrights.data.Entities.SubscriptionPlanEntity;
import com.alwaysmoveforward.subscriptionrights.data.Entities.SubscriptionPlanIdSequenceEntity;
import com.alwaysmoveforward.subscriptionrights.data.dao.SubscriptionPlanDAO;
import com.alwaysmoveforward.subscriptionrights.data.dao.SubscriptionPlanIdSequenceDAO;
import com.alwaysmoveforward.subscriptionrights.data.mapper.SubscriptionPlanMapper;
import com.alwaysmoveforward.subscriptionrights.domainmodel.SubscriptionPlan;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class SubscriptionPlanRepository {

    private final SubscriptionPlanDAO subscriptionPlanDAO;
    private final SubscriptionPlanIdSequenceDAO subscriptionPlanIdSequenceDAO;
    private final SubscriptionPlanMapper subscriptionPlanMapper;

    public SubscriptionPlanRepository(SubscriptionPlanDAO subscriptionPlanDAO,
                                       SubscriptionPlanIdSequenceDAO subscriptionPlanIdSequenceDAO,
                                       SubscriptionPlanMapper subscriptionPlanMapper) {
        this.subscriptionPlanDAO = subscriptionPlanDAO;
        this.subscriptionPlanIdSequenceDAO = subscriptionPlanIdSequenceDAO;
        this.subscriptionPlanMapper = subscriptionPlanMapper;
    }

    /**
     * The latest (highest-Version) row per plan id, for every plan belonging to
     * {@code applicationId}. There is no stored "current" flag -- this is computed by
     * pulling every version ordered so the first row seen per id is its latest.
     */
    public List<SubscriptionPlan> findAllCurrentByApplicationId(Long applicationId) {
        List<SubscriptionPlanEntity> all = subscriptionPlanDAO.findAllByApplicationIdOrderByIdAscVersionDesc(applicationId);
        Map<Long, SubscriptionPlanEntity> latestById = new LinkedHashMap<>();
        for (SubscriptionPlanEntity entity : all) {
            latestById.putIfAbsent(entity.getId().getId(), entity);
        }
        return latestById.values().stream().map(subscriptionPlanMapper::toDomainModel).toList();
    }

    /**
     * The latest (highest-Version) row for one plan id.
     */
    public Optional<SubscriptionPlan> findCurrentById(Long id) {
        List<SubscriptionPlanEntity> versions = subscriptionPlanDAO.findAllById_IdOrderById_VersionDesc(id);
        return versions.isEmpty() ? Optional.empty() : Optional.of(subscriptionPlanMapper.toDomainModel(versions.get(0)));
    }

    public List<SubscriptionPlan> findAllVersions(Long id) {
        return subscriptionPlanDAO.findAllById_IdOrderById_VersionDesc(id).stream()
                .map(subscriptionPlanMapper::toDomainModel).toList();
    }

    @Transactional
    public SubscriptionPlan createNewPlan(Long applicationId, String name, String description) {
        Long id = subscriptionPlanIdSequenceDAO.save(new SubscriptionPlanIdSequenceEntity()).getId();
        SubscriptionPlan firstVersion = SubscriptionPlan.firstVersion(id, applicationId, name, description);
        SubscriptionPlanEntity saved = subscriptionPlanDAO.save(subscriptionPlanMapper.toEntity(firstVersion));
        return subscriptionPlanMapper.toDomainModel(saved);
    }

    /**
     * Persists {@code nextVersion} as a new row for its plan id. There is nothing else to
     * do -- with no stored "current" flag, inserting the new highest-Version row is the
     * entire operation.
     */
    public SubscriptionPlan saveNewVersion(SubscriptionPlan nextVersion) {
        SubscriptionPlanEntity saved = subscriptionPlanDAO.save(subscriptionPlanMapper.toEntity(nextVersion));
        return subscriptionPlanMapper.toDomainModel(saved);
    }

    @Transactional
    public void deleteAllVersions(Long id) {
        subscriptionPlanDAO.deleteAllById_Id(id);
    }
}
