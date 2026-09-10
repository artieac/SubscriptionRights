package com.alwaysmoveforward.subscriptionrights.data.repositories;

import com.alwaysmoveforward.subscriptionrights.data.Entities.ApplicationEntity;
import com.alwaysmoveforward.subscriptionrights.data.dao.SubscriptionPlanDAO;
import com.alwaysmoveforward.subscriptionrights.data.dao.SubscriptionPlanSetDAO;
import com.alwaysmoveforward.subscriptionrights.data.dao.SubscriptionPlanSetItemDAO;
import com.alwaysmoveforward.subscriptionrights.data.mapper.SubscriptionPlanMapper;
import com.alwaysmoveforward.subscriptionrights.data.mapper.SubscriptionPlanSetMapper;
import com.alwaysmoveforward.subscriptionrights.domainmodel.SubscriptionPlan;
import com.alwaysmoveforward.subscriptionrights.domainmodel.SubscriptionPlanSet;
import com.alwaysmoveforward.subscriptionrights.domainmodel.SubscriptionPlanSetItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exercises SubscriptionPlanSetRepository's save/findById/findActive against a real (H2)
 * JPA metamodel. In particular this checks that updating a set's items -- which deletes all
 * existing item rows then inserts a fresh set -- does not trip the (set, plan) / (set, tier)
 * unique constraints when an item is unchanged across the edit: Hibernate's flush action
 * ordering runs insertions before deletions by default, so a naive delete-then-insert in Java
 * code order is not guaranteed to hit the database in that order unless the delete is flushed
 * first.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class SubscriptionPlanSetRepositoryTest {

    @Autowired
    private SubscriptionPlanSetDAO subscriptionPlanSetDAO;

    @Autowired
    private SubscriptionPlanSetItemDAO subscriptionPlanSetItemDAO;

    @Autowired
    private SubscriptionPlanDAO subscriptionPlanDAO;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    private SubscriptionPlanSetRepository newRepository() {
        return new SubscriptionPlanSetRepository(subscriptionPlanSetDAO, subscriptionPlanSetItemDAO, new SubscriptionPlanSetMapper());
    }

    private Long seedApplication() {
        ApplicationEntity application = new ApplicationEntity();
        application.setName("Test App " + System.nanoTime());
        application.setExternalId("ext-" + (System.nanoTime() % 1_000_000L));
        application.setDescription("d");
        application.setCreatedAt(Instant.now());
        entityManager.persist(application);
        entityManager.flush();
        return application.getId();
    }

    private void seedPlanVersion(long id, Long applicationId, String name) {
        SubscriptionPlanMapper planMapper = new SubscriptionPlanMapper();
        subscriptionPlanDAO.save(planMapper.toEntity(SubscriptionPlan.firstVersion(id, applicationId, name, "d")));
    }

    @Test
    void updateWithAnUnchangedItemDoesNotViolateUniqueConstraints() {
        SubscriptionPlanSetRepository repository = newRepository();
        Long applicationId = seedApplication();
        seedPlanVersion(1L, applicationId, "Gold");
        seedPlanVersion(2L, applicationId, "Silver");
        entityManager.flush();
        entityManager.clear();

        List<SubscriptionPlanSetItem> initialItems = List.of(
                SubscriptionPlanSetItem.of(1L, 1, 1),
                SubscriptionPlanSetItem.of(2L, 1, 2));
        SubscriptionPlanSet created = SubscriptionPlanSet.create(applicationId, "2024 Pricing",
                LocalDate.of(2024, 1, 1), null, initialItems);
        SubscriptionPlanSet saved = repository.save(created);
        entityManager.flush();
        entityManager.clear();

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getItems()).hasSize(2);

        // Update: item for plan 1 stays at the exact same tier; only plan 2's tier changes.
        // If save() lets Hibernate flush the re-insert of (set, plan 1, tier 1) before the
        // delete of the old (set, plan 1, tier 1) row actually lands, this throws a unique
        // constraint violation.
        SubscriptionPlanSet reloaded = repository.findById(saved.getId()).orElseThrow();
        reloaded.updateItems(List.of(
                SubscriptionPlanSetItem.of(1L, 1, 1),
                SubscriptionPlanSetItem.of(2L, 1, 3)));
        SubscriptionPlanSet updated = repository.save(reloaded);
        entityManager.flush();
        entityManager.clear();

        assertThat(updated.getItems()).hasSize(2);
        SubscriptionPlanSet reloadedAgain = repository.findById(saved.getId()).orElseThrow();
        assertThat(reloadedAgain.getItems()).extracting(SubscriptionPlanSetItem::getTier)
                .containsExactlyInAnyOrder(1, 3);

        List<SubscriptionPlanSet> allForApp = repository.findAllByApplicationId(applicationId);
        assertThat(allForApp).hasSize(1);
    }

    @Test
    void findActiveForApplicationOnDateHandlesOpenEndedRange() {
        SubscriptionPlanSetRepository repository = newRepository();
        Long applicationId = seedApplication();
        seedPlanVersion(1L, applicationId, "Gold");
        entityManager.flush();
        entityManager.clear();

        SubscriptionPlanSet set = SubscriptionPlanSet.create(applicationId, "Open-ended",
                LocalDate.of(2024, 1, 1), null, List.of(SubscriptionPlanSetItem.of(1L, 1, 1)));
        repository.save(set);
        entityManager.flush();
        entityManager.clear();

        assertThat(repository.findActiveForApplicationOnDate(applicationId, LocalDate.of(2024, 6, 1)))
                .isPresent();
        assertThat(repository.findActiveForApplicationOnDate(applicationId, LocalDate.of(2099, 1, 1)))
                .isPresent();
        assertThat(repository.findActiveForApplicationOnDate(applicationId, LocalDate.of(2023, 12, 31)))
                .isEmpty();
    }

    @Test
    void deleteRemovesSetAndItsItems() {
        SubscriptionPlanSetRepository repository = newRepository();
        Long applicationId = seedApplication();
        seedPlanVersion(1L, applicationId, "Gold");
        entityManager.flush();
        entityManager.clear();

        SubscriptionPlanSet set = SubscriptionPlanSet.create(applicationId, "To Delete",
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31), List.of(SubscriptionPlanSetItem.of(1L, 1, 1)));
        SubscriptionPlanSet saved = repository.save(set);
        entityManager.flush();
        entityManager.clear();

        repository.delete(saved.getId());
        entityManager.flush();
        entityManager.clear();

        assertThat(repository.findById(saved.getId())).isEmpty();
        assertThat(subscriptionPlanSetItemDAO.findAllBySubscriptionPlanSetId(saved.getId())).isEmpty();
    }
}
