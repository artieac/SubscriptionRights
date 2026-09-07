import { useEffect, useState } from "react";
import type { FormEvent } from "react";
import { useOutletContext } from "react-router-dom";
import { SubscriptionEntitlementRepository } from "../api/SubscriptionEntitlementRepository";
import type {
  SubscriptionEntitlementDto,
  SubscriptionEntitlementRequest,
} from "../models/SubscriptionEntitlementDto";
import { DataTable } from "../components/DataTable";
import { Modal } from "../components/Modal";
import { ConfirmDialog } from "../components/ConfirmDialog";
import { LoadingSpinner } from "../components/LoadingSpinner";
import { RoleGuard } from "../components/RoleGuard";

const emptyForm: SubscriptionEntitlementRequest = { name: "", displayName: "" };

function SubscriptionEntitlementForm({
  initial,
  onSubmit,
  onCancel,
}: {
  initial: SubscriptionEntitlementRequest;
  onSubmit: (request: SubscriptionEntitlementRequest) => Promise<void>;
  onCancel: () => void;
}) {
  const [form, setForm] = useState(initial);
  const [saving, setSaving] = useState(false);

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setSaving(true);
    try {
      await onSubmit(form);
    } finally {
      setSaving(false);
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      <label>
        Name
        <input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required />
      </label>
      <label>
        Display Name
        <input
          value={form.displayName}
          onChange={(e) => setForm({ ...form, displayName: e.target.value })}
          required
        />
      </label>
      <div className="dialog-actions">
        <button type="button" onClick={onCancel}>
          Cancel
        </button>
        <button type="submit" disabled={saving}>
          {saving ? "Saving…" : "Save"}
        </button>
      </div>
    </form>
  );
}

export function SubscriptionEntitlementsTab() {
  const { applicationId } = useOutletContext<{ applicationId: number }>();
  const [entitlements, setEntitlements] = useState<SubscriptionEntitlementDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [showCreate, setShowCreate] = useState(false);
  const [editing, setEditing] = useState<SubscriptionEntitlementDto | null>(null);
  const [deleting, setDeleting] = useState<SubscriptionEntitlementDto | null>(null);

  async function load() {
    setLoading(true);
    try {
      setEntitlements(await SubscriptionEntitlementRepository.list(applicationId));
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [applicationId]);

  async function handleCreate(request: SubscriptionEntitlementRequest) {
    await SubscriptionEntitlementRepository.create(applicationId, request);
    setShowCreate(false);
    await load();
  }

  async function handleUpdate(request: SubscriptionEntitlementRequest) {
    if (!editing) return;
    await SubscriptionEntitlementRepository.update(applicationId, editing.id, request);
    setEditing(null);
    await load();
  }

  async function handleDelete() {
    if (!deleting) return;
    await SubscriptionEntitlementRepository.remove(applicationId, deleting.id);
    setDeleting(null);
    await load();
  }

  if (loading) {
    return <LoadingSpinner />;
  }

  return (
    <div className="subscription-entitlements-tab">
      <div className="page-header">
        <h2>Subscription Entitlements</h2>
        <RoleGuard requireAdmin>
          <button type="button" onClick={() => setShowCreate(true)}>
            Create Entitlement
          </button>
        </RoleGuard>
      </div>

      <DataTable
        rows={entitlements}
        rowKey={(row) => row.id}
        columns={[
          { header: "Name", render: (row) => row.name },
          { header: "Display Name", render: (row) => row.displayName },
          {
            header: "Actions",
            render: (row) => (
              <RoleGuard requireAdmin>
                <span className="row-actions">
                  <button type="button" onClick={() => setEditing(row)}>
                    Edit
                  </button>
                  <button type="button" className="danger" onClick={() => setDeleting(row)}>
                    Delete
                  </button>
                </span>
              </RoleGuard>
            ),
          },
        ]}
      />

      {showCreate && (
        <Modal title="Create Subscription Entitlement" onClose={() => setShowCreate(false)}>
          <SubscriptionEntitlementForm
            initial={emptyForm}
            onSubmit={handleCreate}
            onCancel={() => setShowCreate(false)}
          />
        </Modal>
      )}

      {editing && (
        <Modal title="Edit Subscription Entitlement" onClose={() => setEditing(null)}>
          <SubscriptionEntitlementForm
            initial={{ name: editing.name, displayName: editing.displayName }}
            onSubmit={handleUpdate}
            onCancel={() => setEditing(null)}
          />
        </Modal>
      )}

      {deleting && (
        <ConfirmDialog
          title="Delete Subscription Entitlement"
          message={`Delete "${deleting.name}"? This cannot be undone.`}
          onConfirm={handleDelete}
          onCancel={() => setDeleting(null)}
        />
      )}
    </div>
  );
}
