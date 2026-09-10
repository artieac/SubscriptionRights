-- =============================================================================
-- Delta: adds Application.ExternalId
--
-- ExternalId is the identifier a machine caller (see ApiTokens in schema.sql)
-- uses in place of the internal numeric Id: only
-- /api/external/applications/{externalId}/... routes accept it, so it must be
-- something an external system would actually know up front, unlike an
-- auto-increment Id. Restricted to URL-safe characters (no percent-encoding
-- ever needed) and kept short since it appears in every external-facing URL
-- path -- format/length are enforced in application code (Application domain
-- model / ApplicationRequest), not by a CHECK constraint here, the same way
-- schema.sql already leaves Name's "non-blank" rule to application code.
--
-- Run this against a database already created from schema.sql. This script
-- only defines the change -- like schema.sql, it is not executed as part of
-- any build/deploy step; run it manually.
-- =============================================================================

USE subscriptionentitlements;

ALTER TABLE Application ADD COLUMN ExternalId VARCHAR(20) NULL AFTER Name;

-- Backfills a best-effort slug from each existing Application's Name. Review
-- the results before the ALTERs below run: two Names collapsing to the same
-- slug fails the UNIQUE constraint, and a Name with no URL-safe characters
-- leaves ExternalId empty (fails the NOT NULL constraint). Fix those rows by
-- hand, then re-run from the ALTERs below.
UPDATE Application
SET ExternalId = LEFT(REGEXP_REPLACE(REGEXP_REPLACE(LOWER(Name), '[^a-z0-9]+', '-'), '(^-+|-+$)', ''), 20)
WHERE ExternalId IS NULL;

ALTER TABLE Application MODIFY COLUMN ExternalId VARCHAR(20) NOT NULL;
ALTER TABLE Application ADD CONSTRAINT UQ_Application_ExternalId UNIQUE (ExternalId);
