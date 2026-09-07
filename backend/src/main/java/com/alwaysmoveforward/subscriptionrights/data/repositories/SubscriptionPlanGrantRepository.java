package com.alwaysmoveforward.subscriptionrights.data.repositories;

import com.alwaysmoveforward.subscriptionrights.data.Entities.SubscriptionPlanGrantEntity;
import com.alwaysmoveforward.subscriptionrights.data.dao.SubscriptionPlanGrantDAO;
import com.alwaysmoveforward.subscriptionrights.data.mapper.SubscriptionPlanGrantMapper;
import com.alwaysmoveforward.subscriptionrights.domainmodel.SubscriptionPlanGrant;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SubscriptionPlanGrantRepository {

    private final SubscriptionPlanGrantDAO subscriptionPlanGrantDAO;
    private final SubscriptionPlanGrantMapper subscriptionPlanGrantMapper;

    public SubscriptionPlanGrantRepository(SubscriptionPlanGrantDAO subscriptionPlanGrantDAO,
                                            SubscriptionPlanGrantMapper subscriptionPlanGrantMapper) {
        this.subscriptionPlanGrantDAO = subscriptionPlanGrantDAO;
        this.subscriptionPlanGrantMapper = subscriptionPlanGrantMapper;
    }

    public List<SubscriptionPlanGrant> findByApplicationId(Long applicationId) {
        return subscriptionPlanGrantDAO.findByApplicationId(applicationId).stream()
                .map(subscriptionPlanGrantMapper::toDomainModel).toList();
    }

    public List<SubscriptionPlanGrant> findByApplicationIdAndSubscriptionPlanId(Long applicationId, Long subscriptionPlanId) {
        return subscriptionPlanGrantDAO.findByApplicationIdAndSubscriptionPlanId(applicationId, subscriptionPlanId).stream()
                .map(subscriptionPlanGrantMapper::toDomainModel).toList();
    }

    public Optional<SubscriptionPlanGrant> findById(Long id) {
        return subscriptionPlanGrantDAO.findById(id).map(subscriptionPlanGrantMapper::toDomainModel);
    }

    public SubscriptionPlanGrant save(SubscriptionPlanGrant subscriptionPlanGrant) {
        SubscriptionPlanGrantEntity saved = subscriptionPlanGrantDAO.save(subscriptionPlanGrantMapper.toEntity(subscriptionPlanGrant));
        return subscriptionPlanGrantMapper.toDomainModel(saved);
    }

    public void deleteById(Long id) {
        subscriptionPlanGrantDAO.deleteById(id);
    }

    /**
     * Forces queued deletes to actually hit the database now. Hibernate's default flush-action
     * ordering runs insertions before deletions regardless of code order, so a caller that
     * deletes some rows then inserts new ones matching the same unique constraint (e.g.
     * SubscriptionPlanGrantService#replaceGrantsForPlan, replacing a version's grants in place)
     * must flush between the two, or the inserts race the not-yet-applied deletes and trip the
     * constraint.
     */
    public void flush() {
        subscriptionPlanGrantDAO.flush();
    }
}
