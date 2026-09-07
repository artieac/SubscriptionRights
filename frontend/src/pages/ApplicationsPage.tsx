import { useEffect, useState } from "react";
import type { FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import { ApplicationRepository } from "../api/ApplicationRepository";
import type { ApplicationDto, ApplicationRequest } from "../models/ApplicationDto";
import { DataTable } from "../components/DataTable";
import { Modal } from "../components/Modal";
import { ConfirmDialog } from "../components/ConfirmDialog";
import { LoadingSpinner } from "../components/LoadingSpinner";
import { RoleGuard } from "../components/RoleGuard";

const emptyForm: ApplicationRequest = { name: "", description: "" };

function ApplicationForm({
  initial,
  onSubmit,
  onCancel,
}: {
  initial: ApplicationRequest;
  onSubmit: (request: ApplicationRequest) => Promise<void>;
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
        <input
          value={form.name}
          onChange={(event) => setForm({ ...form, name: event.target.value })}
          required
        />
      </label>
      <label>
        Description
        <textarea
          value={form.description ?? ""}
          onChange={(event) => setForm({ ...form, description: event.target.value })}
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

export function ApplicationsPage() {
  const [applications, setApplications] = useState<ApplicationDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [showCreate, setShowCreate] = useState(false);
  const [editing, setEditing] = useState<ApplicationDto | null>(null);
  const [deleting, setDeleting] = useState<ApplicationDto | null>(null);
  const navigate = useNavigate();

  async function load() {
    setLoading(true);
    try {
      setApplications(await ApplicationRepository.list());
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void load();
  }, []);

  async function handleCreate(request: ApplicationRequest) {
    await ApplicationRepository.create(request);
    setShowCreate(false);
    await load();
  }

  async function handleUpdate(request: ApplicationRequest) {
    if (!editing) return;
    await ApplicationRepository.update(editing.id, request);
    setEditing(null);
    await load();
  }

  async function handleDelete() {
    if (!deleting) return;
    await ApplicationRepository.remove(deleting.id);
    setDeleting(null);
    await load();
  }

  if (loading) {
    return <LoadingSpinner />;
  }

  return (
    <div className="applications-page">
      <div className="page-header">
        <h1>Applications</h1>
        <RoleGuard requireAdmin>
          <button type="button" onClick={() => setShowCreate(true)}>
            Create Application
          </button>
        </RoleGuard>
      </div>

      <DataTable
        rows={applications}
        rowKey={(row) => row.id}
        onRowClick={(row) => navigate(`/applications/${row.id}`)}
        columns={[
          { header: "Name", render: (row) => row.name },
          { header: "Description", render: (row) => row.description ?? "" },
          {
            header: "Actions",
            render: (row) => (
              <RoleGuard requireAdmin>
                <span className="row-actions">
                  <button
                    type="button"
                    onClick={(event) => {
                      event.stopPropagation();
                      setEditing(row);
                    }}
                  >
                    Edit
                  </button>
                  <button
                    type="button"
                    className="danger"
                    onClick={(event) => {
                      event.stopPropagation();
                      setDeleting(row);
                    }}
                  >
                    Delete
                  </button>
                </span>
              </RoleGuard>
            ),
          },
        ]}
      />

      {showCreate && (
        <Modal title="Create Application" onClose={() => setShowCreate(false)}>
          <ApplicationForm initial={emptyForm} onSubmit={handleCreate} onCancel={() => setShowCreate(false)} />
        </Modal>
      )}

      {editing && (
        <Modal title="Edit Application" onClose={() => setEditing(null)}>
          <ApplicationForm
            initial={{ name: editing.name, description: editing.description }}
            onSubmit={handleUpdate}
            onCancel={() => setEditing(null)}
          />
        </Modal>
      )}

      {deleting && (
        <ConfirmDialog
          title="Delete Application"
          message={`Delete "${deleting.name}"? This cannot be undone.`}
          onConfirm={handleDelete}
          onCancel={() => setDeleting(null)}
        />
      )}
    </div>
  );
}
