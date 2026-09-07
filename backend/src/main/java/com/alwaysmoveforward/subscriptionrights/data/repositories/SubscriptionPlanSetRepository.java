package com.alwaysmoveforward.subscriptionrights.data.repositories;

import com.alwaysmoveforward.subscriptionrights.data.Entities.SubscriptionPlanSetEntity;
import com.alwaysmoveforward.subscriptionrights.data.Entities.SubscriptionPlanSetItemEntity;
import com.alwaysmoveforward.subscriptionrights.data.dao.SubscriptionPlanSetDAO;
import com.alwaysmoveforward.subscriptionrights.data.dao.SubscriptionPlanSetItemDAO;
import com.alwaysmoveforward.subscriptionrights.data.mapper.SubscriptionPlanSetMapper;
import com.alwaysmoveforward.subscriptionrights.domainmodel.SubscriptionPlanSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * The only layer allowed to touch SubscriptionPlanSetEntity/SubscriptionPlanSetItemEntity.
 * Items are always read/written as part of the whole SubscriptionPlanSet aggregate -- there
 * is no separate items repository.
 */
@Repository
public class SubscriptionPlanSetRepository {

    private final SubscriptionPlanSetDAO subscriptionPlanSetDAO;
    private final SubscriptionPlanSetItemDAO subscriptionPlanSetItemDAO;
    private final SubscriptionPlanSetMapper subscriptionPlanSetMapper;

    public SubscriptionPlanSetRepository(SubscriptionPlanSetDAO subscriptionPlanSetDAO,
                                          SubscriptionPlanSetItemDAO subscriptionPlanSetItemDAO,
                                          SubscriptionPlanSetMapper subscriptionPlanSetMapper) {
        this.subscriptionPlanSetDAO = subscriptionPlanSetDAO;
        this.subscriptionPlanSetItemDAO = subscriptionPlanSetItemDAO;
        this.subscriptionPlanSetMapper = subscriptionPlanSetMapper;
    }

    public Optional<SubscriptionPlanSet> findById(Long id) {
        return subscriptionPlanSetDAO.findById(id).map(this::assemble);
    }

    public List<SubscriptionPlanSet> findAllByApplicationId(Long applicationId) {
        return subscriptionPlanSetDAO.findAllByApplicationId(applicationId).stream()
                .map(this::assemble).toList();
    }

    public Optional<SubscriptionPlanSet> findActiveForApplicationOnDate(Long applicationId, LocalDate date) {
        return subscriptionPlanSetDAO.findActiveOnDate(applicationId, date).stream()
                .findFirst()
                .map(this::assemble);
    }

    /**
     * Upserts {@code set}: inserts a new row (plus its item rows) if it has no id, otherwise
     * updates the existing row and replaces its item rows wholesale (delete-all-then-insert,
     * rather than diffing -- this is a low-frequency admin operation, not a hot path).
     *
     * The delete is explicitly flushed before the inserts are queued: Hibernate's default
     * flush ordering runs entity insertions before deletions, so without this flush, an item
     * unchanged across an update (same plan, same tier) would have its replacement row
     * inserted while the old row still exists, tripping the (set, plan)/(set, tier) unique
     * constraints. (Caught by SubscriptionPlanSetRepositoryTest.)
     */
    @Transactional
    public SubscriptionPlanSet save(SubscriptionPlanSet set) {
        SubscriptionPlanSetEntity entity = subscriptionPlanSetMapper.toEntity(set);
        SubscriptionPlanSetEntity saved = subscriptionPlanSetDAO.save(entity);

        subscriptionPlanSetItemDAO.deleteAllBySubscriptionPlanSetId(saved.getId());
        subscriptionPlanSetItemDAO.flush();
        List<SubscriptionPlanSetItemEntity> itemEntities =
                subscriptionPlanSetMapper.toItemEntities(saved.getId(), set);
        subscriptionPlanSetItemDAO.saveAll(itemEntities);

        return assemble(saved);
    }

    @Transactional
    public void delete(Long id) {
        subscriptionPlanSetItemDAO.deleteAllBySubscriptionPlanSetId(id);
        subscriptionPlanSetDAO.deleteById(id);
    }

    private SubscriptionPlanSet assemble(SubscriptionPlanSetEntity entity) {
        List<SubscriptionPlanSetItemEntity> itemEntities =
                subscriptionPlanSetItemDAO.findAllBySubscriptionPlanSetId(entity.getId());
        return subscriptionPlanSetMapper.toDomainModel(entity, itemEntities);
    }
}
