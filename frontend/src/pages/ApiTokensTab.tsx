import { useEffect, useState } from "react";
import type { FormEvent } from "react";
import { useOutletContext } from "react-router-dom";
import { ApiTokenRepository } from "../api/ApiTokenRepository";
import type { ApiTokenDto, IssuedApiTokenDto } from "../models/ApiTokenDto";
import { DataTable } from "../components/DataTable";
import { Modal } from "../components/Modal";
import { ConfirmDialog } from "../components/ConfirmDialog";
import { LoadingSpinner } from "../components/LoadingSpinner";
import { RoleGuard } from "../components/RoleGuard";

function extractErrorMessage(error: unknown): string {
  if (typeof error === "object" && error !== null && "response" in error) {
    const response = (error as { response?: { data?: unknown } }).response;
    const data = response?.data;
    if (typeof data === "string" && data.trim()) return data;
    if (data && typeof data === "object") {
      const message = (data as { message?: unknown }).message;
      if (typeof message === "string" && message.trim()) return message;
    }
  }
  return "Something went wrong creating the token. Please try again.";
}

function CreateTokenForm({
  onSubmit,
  onCancel,
}: {
  onSubmit: (name: string) => Promise<void>;
  onCancel: () => void;
}) {
  const [name, setName] = useState("");
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    if (!name.trim()) return;
    setSaving(true);
    setError(null);
    try {
      await onSubmit(name.trim());
    } catch (submitError) {
      setError(extractErrorMessage(submitError));
    } finally {
      setSaving(false);
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      <label>
        Name
        <input
          type="text"
          value={name}
          onChange={(e) => setName(e.target.value)}
          placeholder="e.g. Billing System Integration"
          required
        />
      </label>
      {error && <p className="form-error">{error}</p>}
      <div className="dialog-actions">
        <button type="button" onClick={onCancel}>
          Cancel
        </button>
        <button type="submit" disabled={saving}>
          {saving ? "Creating…" : "Create Token"}
        </button>
      </div>
    </form>
  );
}

function IssuedTokenReveal({ token, onClose }: { token: IssuedApiTokenDto; onClose: () => void }) {
  const [copied, setCopied] = useState(false);

  async function handleCopy() {
    try {
      await navigator.clipboard.writeText(token.rawToken);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch {
      // Clipboard access can be denied by the browser -- the raw value is still selectable/visible
      // in the field below, so there's nothing more to do here.
    }
  }

  return (
    <Modal title="Token Created" onClose={onClose}>
      <p className="form-hint">
        Copy this token now — for security, it will not be shown again. Only its prefix will be visible afterward.
      </p>
      <label>
        Token
        <input type="text" value={token.rawToken} readOnly onFocus={(e) => e.target.select()} />
      </label>
      <div className="dialog-actions">
        <button type="button" onClick={handleCopy}>
          {copied ? "Copied!" : "Copy to Clipboard"}
        </button>
        <button type="button" onClick={onClose}>
          Done
        </button>
      </div>
    </Modal>
  );
}

export function ApiTokensTab() {
  const { applicationId, externalId } = useOutletContext<{ applicationId: number; externalId: string }>();
  const [tokens, setTokens] = useState<ApiTokenDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [showCreate, setShowCreate] = useState(false);
  const [issuedToken, setIssuedToken] = useState<IssuedApiTokenDto | null>(null);
  const [revoking, setRevoking] = useState<ApiTokenDto | null>(null);

  async function load() {
    setLoading(true);
    try {
      setTokens(await ApiTokenRepository.list(applicationId));
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [applicationId]);

  async function handleCreate(name: string) {
    const issued = await ApiTokenRepository.create(applicationId, { name });
    setShowCreate(false);
    setIssuedToken(issued);
    await load();
  }

  async function handleRevoke() {
    if (!revoking) return;
    await ApiTokenRepository.revoke(applicationId, revoking.id);
    setRevoking(null);
    await load();
  }

  if (loading) {
    return <LoadingSpinner />;
  }

  return (
    <div className="api-tokens-tab">
      <div className="page-header">
        <h2>API Tokens</h2>
        <RoleGuard requireAdmin>
          <button type="button" onClick={() => setShowCreate(true)}>
            Create Token
          </button>
        </RoleGuard>
      </div>

      <p className="form-hint">
        Other systems can call this application's read-only API endpoints by passing a token here as{" "}
        <code>Authorization: Bearer &lt;token&gt;</code>, against{" "}
        <code>/api/external/applications/{externalId}/...</code> (e.g.{" "}
        <code>/api/external/applications/{externalId}/subscription-plans</code>).
      </p>

      <DataTable
        rows={tokens}
        rowKey={(row) => row.id}
        emptyMessage="No API tokens yet."
        columns={[
          { header: "Name", render: (row) => row.name },
          { header: "Token", render: (row) => `${row.tokenPrefix}••••••••` },
          { header: "Created", render: (row) => new Date(row.createdAt).toLocaleDateString() },
          {
            header: "Status",
            render: (row) =>
              row.revokedAt ? `Revoked ${new Date(row.revokedAt).toLocaleDateString()}` : "Active",
          },
          {
            header: "Actions",
            render: (row) =>
              row.revokedAt ? null : (
                <RoleGuard requireAdmin>
                  <button type="button" className="danger" onClick={() => setRevoking(row)}>
                    Revoke
                  </button>
                </RoleGuard>
              ),
          },
        ]}
      />

      {showCreate && (
        <Modal title="Create API Token" onClose={() => setShowCreate(false)}>
          <CreateTokenForm onSubmit={handleCreate} onCancel={() => setShowCreate(false)} />
        </Modal>
      )}

      {issuedToken && <IssuedTokenReveal token={issuedToken} onClose={() => setIssuedToken(null)} />}

      {revoking && (
        <ConfirmDialog
          title="Revoke API Token"
          message={`Revoke "${revoking.name}"? Any system using it will immediately lose access. This cannot be undone.`}
          onConfirm={handleRevoke}
          onCancel={() => setRevoking(null)}
        />
      )}
    </div>
  );
}
