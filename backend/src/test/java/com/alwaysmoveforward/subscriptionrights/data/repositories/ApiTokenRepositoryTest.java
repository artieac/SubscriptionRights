package com.alwaysmoveforward.subscriptionrights.data.repositories;

import com.alwaysmoveforward.subscriptionrights.data.Entities.ApplicationEntity;
import com.alwaysmoveforward.subscriptionrights.data.dao.ApiTokenDAO;
import com.alwaysmoveforward.subscriptionrights.data.mapper.ApiTokenMapper;
import com.alwaysmoveforward.subscriptionrights.domainmodel.ApiToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exercises ApiTokenRepository against a real (H2) JPA metamodel -- in particular, that a
 * revoked token stops being found by findActiveByTokenHash even though its row (and hash) still
 * exist, which is the entire enforcement point of "revoke doesn't delete, it just deactivates".
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ApiTokenRepositoryTest {

    @Autowired
    private ApiTokenDAO apiTokenDAO;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    private ApiTokenRepository newRepository() {
        return new ApiTokenRepository(apiTokenDAO, new ApiTokenMapper());
    }

    private Long seedApplication() {
        ApplicationEntity application = new ApplicationEntity();
        application.setName("Test App " + System.nanoTime());
        application.setDescription("d");
        application.setCreatedAt(Instant.now());
        entityManager.persist(application);
        entityManager.flush();
        return application.getId();
    }

    @Test
    void createFindRevokeLifecycle() {
        ApiTokenRepository repository = newRepository();
        Long applicationId = seedApplication();

        ApiToken created = repository.create(applicationId, "Billing Integration", "hash-abc123", "srt_abc123");
        entityManager.flush();
        entityManager.clear();

        assertThat(created.getId()).isNotNull();
        assertThat(created.isActive()).isTrue();

        Optional<ApiToken> foundActive = repository.findActiveByTokenHash("hash-abc123");
        assertThat(foundActive).isPresent();
        assertThat(foundActive.get().getName()).isEqualTo("Billing Integration");
        assertThat(foundActive.get().getApplicationId()).isEqualTo(applicationId);

        List<ApiToken> forApp = repository.findAllByApplicationId(applicationId);
        assertThat(forApp).hasSize(1);

        repository.revoke(created.getId());
        entityManager.flush();
        entityManager.clear();

        // Row still exists (findAllByApplicationId still sees it) but is no longer active.
        assertThat(repository.findActiveByTokenHash("hash-abc123")).isEmpty();
        List<ApiToken> forAppAfterRevoke = repository.findAllByApplicationId(applicationId);
        assertThat(forAppAfterRevoke).hasSize(1);
        assertThat(forAppAfterRevoke.get(0).isActive()).isFalse();
        assertThat(forAppAfterRevoke.get(0).getRevokedAt()).isNotNull();
    }

    @Test
    void unknownHashIsNotFound() {
        ApiTokenRepository repository = newRepository();
        assertThat(repository.findActiveByTokenHash("no-such-hash")).isEmpty();
    }
}
