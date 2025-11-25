const API_URL = "http://localhost:8080/admin/moderation";

export async function getPendingReactions() {
  const token = localStorage.getItem("token");

  const res = await fetch(`${API_URL}/pending`, {
    method: "GET",
    headers: { Authorization: `Bearer ${token}` }
  });

  if (!res.ok) throw new Error("Failed to load pending reactions");

  return await res.json();
}

export async function getFlaggedReactions() {
  const token = localStorage.getItem("token");

  const res = await fetch(`${API_URL}/flagged`, {
    method: "GET",
    headers: { Authorization: `Bearer ${token}` }
  });

  if (!res.ok) throw new Error("Failed to load flagged reactions");

  return await res.json();
}

export async function approveReaction(id: number) {
  const token = localStorage.getItem("token");

  const res = await fetch(`${API_URL}/${id}/approve`, {
    method: "POST",
    headers: { Authorization: `Bearer ${token}` }
  });

  if (!res.ok) throw new Error("Failed to approve reaction");
}

export async function rejectReaction(id: number) {
  const token = localStorage.getItem("token");

  const res = await fetch(`${API_URL}/${id}/reject`, {
    method: "POST",
    headers: { Authorization: `Bearer ${token}` }
  });

  if (!res.ok) throw new Error("Failed to reject reaction");
}

export async function flagReaction(id: number, reason: string) {
  const token = localStorage.getItem("token");

  const res = await fetch(`${API_URL}/${id}/flag`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json"
    },
    body: JSON.stringify({ reason })
  });

  if (!res.ok) throw new Error("Failed to flag reaction");
}
