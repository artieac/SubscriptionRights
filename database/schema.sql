-- =============================================================================
-- SubscriptionRights schema (MySQL 8.0+ / InnoDB)
--
-- Creates: Application, SubscriptionEntitlements, SubscriptionPlans,
--          SubscriptionPlanGrants, SubscriptionPlanSets, SubscriptionPlanSetItems,
--          Users, ApiTokens
--
-- This script only defines the schema. It is not executed as part of any
-- build/deploy step here -- run it against your target database manually.
--
-- MySQL has no CREATE SEQUENCE object and no partial/filtered unique index
-- (a plain "CREATE UNIQUE INDEX ... WHERE ..." like SQL Server/Postgres
-- support), which the original design relied on. Two adaptations follow:
--   - SubscriptionPlanIdSequence is a plain AUTO_INCREMENT counter table with
--     no other purpose -- the application mints a new SubscriptionPlan id by
--     inserting a row into it and reading back LAST_INSERT_ID(), never
--     deleting rows. It is not referenced by any foreign key.
--   - The two invariants that used to be filtered unique indexes ("at most
--     one current row per SubscriptionPlan id", "unique Name per Application
--     among current SubscriptionPlan rows") are enforced by
--     SubscriptionPlanRepository/SubscriptionPlanService in application code
--     instead, the same way this schema's other cross-row invariants (no
--     overlapping SubscriptionPlanSet date ranges, etc.) are already handled
--     in code rather than in the database. The plain (non-unique) indexes
--     below exist for query performance only.
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Schema + application user
--   The app connects as subscriptionrights_app, which can only read/write rows
--   (SELECT/INSERT/UPDATE/DELETE) -- it has no DDL rights (no CREATE/ALTER/DROP/
--   INDEX), so everything below this block must be run as a more privileged
--   user (e.g. root), not this one.
-- -----------------------------------------------------------------------------
CREATE SCHEMA IF NOT EXISTS subscriptionrights
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE subscriptionrights;

CREATE USER IF NOT EXISTS ''@'%' IDENTIFIED BY '';

GRANT SELECT, INSERT, UPDATE, DELETE ON subscriptionrights.* TO ''@'%';

FLUSH PRIVILEGES;

-- -----------------------------------------------------------------------------
-- Application
-- -----------------------------------------------------------------------------
CREATE TABLE Application
(
    Id            BIGINT       NOT NULL AUTO_INCREMENT,
    Name          VARCHAR(255) NOT NULL,
    Description   TEXT         NULL,
    CreatedAt     DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (Id),
    CONSTRAINT UQ_Application_Name UNIQUE (Name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- SubscriptionEntitlements
-- -----------------------------------------------------------------------------
CREATE TABLE SubscriptionEntitlements
(
    Id            BIGINT       NOT NULL AUTO_INCREMENT,
    ApplicationId BIGINT       NOT NULL,
    Name          VARCHAR(255) NOT NULL,
    DisplayName   VARCHAR(255) NOT NULL,
    CreatedAt     DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    UpdatedAt     DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (Id),
    CONSTRAINT FK_SubscriptionEntitlements_Application
        FOREIGN KEY (ApplicationId) REFERENCES Application (Id),
    CONSTRAINT UQ_SubscriptionEntitlements_App_Name UNIQUE (ApplicationId, Name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX IX_SubscriptionEntitlements_ApplicationId
    ON SubscriptionEntitlements (ApplicationId);

-- -----------------------------------------------------------------------------
-- SubscriptionPlans
--   Versioned: each edit inserts a new (Id, Version) row rather than mutating
--   one in place, so a plan's history is preserved. Id is NOT an AUTO_INCREMENT
--   column, since it must repeat across a plan's versions -- a new logical
--   plan draws its Id from SubscriptionPlanIdSequence once, at Version 1, and
--   every later version of that plan reuses the same Id.
--   There is no "current version" flag -- a plan's latest version is simply
--   whichever row has MAX(Version) for its Id, which the (Id, Version) primary
--   key already answers with a plain indexed lookup
--   (WHERE Id = ? ORDER BY Version DESC LIMIT 1), so there is nothing to store
--   or keep in sync. What is actually "active" to an end user is never a
--   property of SubscriptionPlans at all -- it's determined entirely by which
--   SubscriptionPlanSet is active for a given date, and that set pins whichever
--   specific plan VERSIONS it chooses (see SubscriptionPlanSetItems below).
--   Application code still must reject a Name that collides with another
--   plan's latest version in the same Application (MySQL can't express that
--   as a plain constraint here) -- see SubscriptionPlanService.
--   Tier no longer lives here -- a plan's tier is a property of its membership
--   in a particular SubscriptionPlanSet, not of the plan itself, since the
--   same plan could in principle be offered at a different tier position in a
--   different set.
-- -----------------------------------------------------------------------------
CREATE TABLE SubscriptionPlanIdSequence
(
    Id BIGINT NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (Id)
) ENGINE=InnoDB;

CREATE TABLE SubscriptionPlans
(
    Id            BIGINT       NOT NULL,
    Version       INT          NOT NULL,
    ApplicationId BIGINT       NOT NULL,
    Name          VARCHAR(255) NOT NULL,
    Description   TEXT         NULL,
    CreatedAt     DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (Id, Version),
    CONSTRAINT FK_SubscriptionPlans_Application
        FOREIGN KEY (ApplicationId) REFERENCES Application (Id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX IX_SubscriptionPlans_ApplicationId
    ON SubscriptionPlans (ApplicationId);

-- Supports the "unique Name per Application among latest-version rows" check
-- in SubscriptionPlanService.
CREATE INDEX IX_SubscriptionPlans_ApplicationId_Name
    ON SubscriptionPlans (ApplicationId, Name);

-- -----------------------------------------------------------------------------
-- SubscriptionPlanGrants
--   Links a specific SubscriptionPlan VERSION to a SubscriptionEntitlement
--   within an Application. A grant pins the exact plan version that was
--   current at the moment it was created -- editing the plan afterward never
--   changes what an existing grant refers to.
-- -----------------------------------------------------------------------------
CREATE TABLE SubscriptionPlanGrants
(
    Id                       BIGINT      NOT NULL AUTO_INCREMENT,
    ApplicationId            BIGINT      NOT NULL,
    SubscriptionPlanId       BIGINT      NOT NULL,
    SubscriptionPlanVersion  INT         NOT NULL,
    SubscriptionEntitlementId BIGINT     NOT NULL,
    -- Backtick-quoted: VALUE is a reserved SQL keyword in MySQL 8 (and always in H2) --
    -- unquoted references to it are rejected or inconsistent depending on context/dialect.
    `Value`                  INT         NOT NULL DEFAULT 0,
    CreatedAt                DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (Id),
    CONSTRAINT FK_SubscriptionPlanGrants_Application
        FOREIGN KEY (ApplicationId) REFERENCES Application (Id),
    CONSTRAINT FK_SubscriptionPlanGrants_SubscriptionPlan
        FOREIGN KEY (SubscriptionPlanId, SubscriptionPlanVersion) REFERENCES SubscriptionPlans (Id, Version),
    CONSTRAINT FK_SubscriptionPlanGrants_SubscriptionEntitlement
        FOREIGN KEY (SubscriptionEntitlementId) REFERENCES SubscriptionEntitlements (Id),
    CONSTRAINT UQ_SubscriptionPlanGrants_PlanVersion_Entitlement
        UNIQUE (SubscriptionPlanId, SubscriptionPlanVersion, SubscriptionEntitlementId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX IX_SubscriptionPlanGrants_ApplicationId
    ON SubscriptionPlanGrants (ApplicationId);

CREATE INDEX IX_SubscriptionPlanGrants_SubscriptionPlan
    ON SubscriptionPlanGrants (SubscriptionPlanId, SubscriptionPlanVersion);

CREATE INDEX IX_SubscriptionPlanGrants_SubscriptionEntitlementId
    ON SubscriptionPlanGrants (SubscriptionEntitlementId);

-- -----------------------------------------------------------------------------
-- SubscriptionPlanSets
--   The set of SubscriptionPlans an Application presents to a user as
--   subscribable options during one date range. An Application can configure
--   more than one set; EffectiveEndDate is nullable (an open-ended set, active
--   until a later one starts). "Only one set may be active for any given date"
--   is NOT enforced here as a table constraint -- MySQL has no clean way to
--   express a date-range-exclusion constraint -- it is enforced by
--   SubscriptionPlanSetService before every create/update, the same way this
--   schema's other cross-row invariants are handled in code rather than in
--   the database.
-- -----------------------------------------------------------------------------
CREATE TABLE SubscriptionPlanSets
(
    Id                 BIGINT       NOT NULL AUTO_INCREMENT,
    ApplicationId      BIGINT       NOT NULL,
    Name               VARCHAR(255) NOT NULL,
    EffectiveStartDate DATE         NOT NULL,
    EffectiveEndDate   DATE         NULL,
    CreatedAt          DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    UpdatedAt          DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (Id),
    CONSTRAINT FK_SubscriptionPlanSets_Application
        FOREIGN KEY (ApplicationId) REFERENCES Application (Id),
    CONSTRAINT CK_SubscriptionPlanSets_DateRange
        CHECK (EffectiveEndDate IS NULL OR EffectiveEndDate >= EffectiveStartDate)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX IX_SubscriptionPlanSets_Application_DateRange
    ON SubscriptionPlanSets (ApplicationId, EffectiveStartDate, EffectiveEndDate);

-- -----------------------------------------------------------------------------
-- SubscriptionPlanSetItems
--   The specific SubscriptionPlan VERSIONS a SubscriptionPlanSet includes, each
--   tiered within that set. SubscriptionPlanSetItems has no repository of its
--   own -- it is owned entirely by the SubscriptionPlanSet aggregate root, the
--   same way SubscriptionPlanSet's own consistency rules (no duplicate plan,
--   no duplicate tier within one set, enforced below) require seeing the whole
--   item collection at once.
-- -----------------------------------------------------------------------------
CREATE TABLE SubscriptionPlanSetItems
(
    Id                      BIGINT      NOT NULL AUTO_INCREMENT,
    SubscriptionPlanSetId   BIGINT      NOT NULL,
    SubscriptionPlanId      BIGINT      NOT NULL,
    SubscriptionPlanVersion INT         NOT NULL,
    Tier                    INT         NOT NULL,
    CreatedAt               DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (Id),
    CONSTRAINT FK_SubscriptionPlanSetItems_SubscriptionPlanSet
        FOREIGN KEY (SubscriptionPlanSetId) REFERENCES SubscriptionPlanSets (Id),
    CONSTRAINT FK_SubscriptionPlanSetItems_SubscriptionPlan
        FOREIGN KEY (SubscriptionPlanId, SubscriptionPlanVersion) REFERENCES SubscriptionPlans (Id, Version),
    CONSTRAINT UQ_SubscriptionPlanSetItems_Set_Plan
        UNIQUE (SubscriptionPlanSetId, SubscriptionPlanId),
    CONSTRAINT UQ_SubscriptionPlanSetItems_Set_Tier
        UNIQUE (SubscriptionPlanSetId, Tier)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX IX_SubscriptionPlanSetItems_SubscriptionPlanSetId
    ON SubscriptionPlanSetItems (SubscriptionPlanSetId);

CREATE INDEX IX_SubscriptionPlanSetItems_SubscriptionPlan
    ON SubscriptionPlanSetItems (SubscriptionPlanId, SubscriptionPlanVersion);

-- -----------------------------------------------------------------------------
-- Users
--   Backs Auth0-based login. A row is created the first time a given
--   IdentityProviderSubject/Email logs in.
-- -----------------------------------------------------------------------------
CREATE TABLE Users
(
    Id                      BIGINT       NOT NULL AUTO_INCREMENT,
    IdentityProviderSubject VARCHAR(255) NOT NULL,
    Email                   VARCHAR(255) NOT NULL,
    DisplayName             VARCHAR(255) NOT NULL,
    IsAdmin                 BOOLEAN      NOT NULL DEFAULT FALSE,
    CreatedAt               DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    UpdatedAt               DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    TimeZone                VARCHAR(100) NULL,
    Locale                  VARCHAR(35)  NULL,

    PRIMARY KEY (Id),
    CONSTRAINT UQ_Users_IdentityProviderSubject UNIQUE (IdentityProviderSubject),
    CONSTRAINT UQ_Users_Email UNIQUE (Email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- ApiTokens
--   Machine credentials for other systems to call this API, scoped to one
--   Application and read-only (see ApiTokenAuthenticationFilter / the
--   ROLE_API_CLIENT authority). Only a SHA-256 hash of the raw token is ever
--   stored -- the raw value is generated and shown to the admin exactly once,
--   at creation, and is not retrievable again afterward, even from this table.
--   TokenPrefix is a short, non-sensitive slice of the raw token kept purely so
--   the UI can tell tokens apart in a list without ever storing/reshowing the
--   full value. RevokedAt is nullable -- NULL means the token is still active;
--   revoking sets it rather than deleting the row, preserving an audit trail.
-- -----------------------------------------------------------------------------
CREATE TABLE ApiTokens
(
    Id            BIGINT       NOT NULL AUTO_INCREMENT,
    ApplicationId BIGINT       NOT NULL,
    Name          VARCHAR(255) NOT NULL,
    TokenHash     VARCHAR(64)  NOT NULL,
    TokenPrefix   VARCHAR(12)  NOT NULL,
    CreatedAt     DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    RevokedAt     DATETIME(6)  NULL,

    PRIMARY KEY (Id),
    CONSTRAINT FK_ApiTokens_Application
        FOREIGN KEY (ApplicationId) REFERENCES Application (Id),
    CONSTRAINT UQ_ApiTokens_TokenHash UNIQUE (TokenHash)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX IX_ApiTokens_ApplicationId
    ON ApiTokens (ApplicationId);
