package com.alwaysmoveforward.subscriptionrights.data.repositories;

import com.alwaysmoveforward.subscriptionrights.data.Entities.ApplicationEntity;
import com.alwaysmoveforward.subscriptionrights.data.dao.SubscriptionPlanDAO;
import com.alwaysmoveforward.subscriptionrights.data.dao.SubscriptionPlanIdSequenceDAO;
import com.alwaysmoveforward.subscriptionrights.data.mapper.SubscriptionPlanMapper;
import com.alwaysmoveforward.subscriptionrights.domainmodel.SubscriptionPlan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exercises the composite-key (@EmbeddedId) derived query methods, the id-sequence-table
 * id generator, and the "latest version per plan id" grouping logic against a real (H2)
 * JPA metamodel -- these are the things `mvn package` can't catch, since Spring Data only
 * validates that derived query names actually parse against the entity mapping (and any
 * hand-rolled grouping logic only proves itself) once the code actually runs.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class SubscriptionPlanRepositoryTest {

    @Autowired
    private SubscriptionPlanDAO subscriptionPlanDAO;

    @Autowired
    private SubscriptionPlanIdSequenceDAO subscriptionPlanIdSequenceDAO;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    private SubscriptionPlanRepository newRepository() {
        return new SubscriptionPlanRepository(subscriptionPlanDAO, subscriptionPlanIdSequenceDAO, new SubscriptionPlanMapper());
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

    @Test
    void createFindUpdateAndDeleteVersionLifecycle() {
        SubscriptionPlanRepository repository = newRepository();
        Long applicationId = seedApplication();

        SubscriptionPlan created = repository.createNewPlan(applicationId, "Gold", "desc");
        entityManager.flush();
        entityManager.clear();

        assertThat(created.getId()).isNotNull();
        assertThat(created.getVersion()).isEqualTo(1);

        SubscriptionPlan current = repository.findCurrentById(created.getId()).orElseThrow();
        assertThat(current.getVersion()).isEqualTo(1);
        assertThat(current.getName()).isEqualTo("Gold");

        SubscriptionPlan nextVersion = current.nextVersion("Gold Plus", "desc2");
        SubscriptionPlan savedNext = repository.saveNewVersion(nextVersion);
        entityManager.flush();
        entityManager.clear();

        assertThat(savedNext.getVersion()).isEqualTo(2);

        SubscriptionPlan currentAfterUpdate = repository.findCurrentById(created.getId()).orElseThrow();
        assertThat(currentAfterUpdate.getVersion()).isEqualTo(2);
        assertThat(currentAfterUpdate.getName()).isEqualTo("Gold Plus");

        List<SubscriptionPlan> allVersions = repository.findAllVersions(created.getId());
        assertThat(allVersions).hasSize(2);
        assertThat(allVersions.get(0).getVersion()).isEqualTo(2);
        assertThat(allVersions.get(1).getVersion()).isEqualTo(1);

        repository.deleteAllVersions(created.getId());
        entityManager.flush();
        entityManager.clear();

        assertThat(repository.findAllVersions(created.getId())).isEmpty();
        assertThat(repository.findCurrentById(created.getId())).isEmpty();
    }

    @Test
    void findAllCurrentByApplicationIdReturnsOneLatestRowPerPlan() {
        SubscriptionPlanRepository repository = newRepository();
        Long applicationId = seedApplication();

        SubscriptionPlan gold = repository.createNewPlan(applicationId, "Gold", "d");
        SubscriptionPlan silver = repository.createNewPlan(applicationId, "Silver", "d");
        entityManager.flush();
        entityManager.clear();

        // Silver gets a second version; Gold stays at version 1. The result must contain
        // exactly one row per plan id, and Silver's must be the version-2 row, not version 1 --
        // this is the hand-rolled "take the first row seen per id" grouping in
        // SubscriptionPlanRepository.findAllCurrentByApplicationId, which the removal of the
        // stored IsCurrent flag now makes load-bearing.
        SubscriptionPlan silverAgain = repository.findCurrentById(silver.getId()).orElseThrow();
        repository.saveNewVersion(silverAgain.nextVersion("Silver Plus", "d2"));
        entityManager.flush();
        entityManager.clear();

        List<SubscriptionPlan> current = repository.findAllCurrentByApplicationId(applicationId);
        assertThat(current).hasSize(2);
        assertThat(current).filteredOn(p -> p.getId().equals(gold.getId()))
                .singleElement().satisfies(p -> assertThat(p.getVersion()).isEqualTo(1));
        assertThat(current).filteredOn(p -> p.getId().equals(silver.getId()))
                .singleElement().satisfies(p -> {
                    assertThat(p.getVersion()).isEqualTo(2);
                    assertThat(p.getName()).isEqualTo("Silver Plus");
                });
    }
}
