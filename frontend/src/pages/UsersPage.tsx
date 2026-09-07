import { useEffect, useState } from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { UserRepository } from "../api/UserRepository";
import type { UserDto } from "../models/UserDto";
import { DataTable } from "../components/DataTable";
import { ConfirmDialog } from "../components/ConfirmDialog";
import { LoadingSpinner } from "../components/LoadingSpinner";

export function UsersPage() {
  const { user: currentUser } = useAuth();
  const [users, setUsers] = useState<UserDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [deleting, setDeleting] = useState<UserDto | null>(null);

  async function load() {
    setLoading(true);
    try {
      setUsers(await UserRepository.list());
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void load();
  }, []);

  if (!currentUser?.isAdmin) {
    return <Navigate to="/unauthorized" replace />;
  }

  async function handlePromote(id: number) {
    await UserRepository.promote(id);
    await load();
  }

  async function handleDelete() {
    if (!deleting) return;
    await UserRepository.remove(deleting.id);
    setDeleting(null);
    await load();
  }

  if (loading) {
    return <LoadingSpinner />;
  }

  return (
    <div className="users-page">
      <div className="page-header">
        <h1>Users</h1>
      </div>

      <DataTable
        rows={users}
        rowKey={(row) => row.id}
        columns={[
          { header: "Email", render: (row) => row.email },
          { header: "Display Name", render: (row) => row.displayName },
          { header: "Admin", render: (row) => (row.isAdmin ? "Yes" : "No") },
          { header: "Created", render: (row) => new Date(row.createdAt).toLocaleString() },
          {
            header: "Actions",
            render: (row) => (
              <span className="row-actions">
                {!row.isAdmin && (
                  <button type="button" onClick={() => handlePromote(row.id)}>
                    Make Admin
                  </button>
                )}
                <button type="button" className="danger" onClick={() => setDeleting(row)}>
                  Delete
                </button>
              </span>
            ),
          },
        ]}
      />

      {deleting && (
        <ConfirmDialog
          title="Delete User"
          message={`Delete "${deleting.displayName}" (${deleting.email})? This cannot be undone.`}
          onConfirm={handleDelete}
          onCancel={() => setDeleting(null)}
        />
      )}
    </div>
  );
}
