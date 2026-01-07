import { apiFetch } from "@/apiClient";

export interface PollResult {
  eens: number;
  oneens: number;
  total: number;
}

export interface AdminPoll {
  id: string;
  question: string;
  createdAt: string;
  eensPercentage: number;
  oneensPercentage: number;
  total: number;
}

export interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  number: number; // huidige pagina (0-based)
  size: number;
}

export async function getLatestPoll() {
  const res = await apiFetch("/api/polls/latest");

  if (!res.ok) {
    throw new Error("Kon poll niet ophalen");
  }

  return await res.json();
}

export async function votePoll(
    pollId: string,
    choice: "eens" | "oneens"
): Promise<PollResult> {
  const res = await apiFetch(`/api/polls/${pollId}/vote`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ choice }),
  });

  if (!res.ok) {
    const msg = await res.text();
    throw new Error(msg || "Stemmen mislukt");
  }

  return await res.json();
}

export async function getPollResults(pollId: string): Promise<PollResult> {
  const res = await apiFetch(`/api/polls/${pollId}/results`);

  if (!res.ok) {
    throw new Error("Kon resultaten niet ophalen");
  }

  return await res.json();
}

export async function createPoll(question: string) {
  const res = await apiFetch("/api/admin/polls", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ question }),
  });

  if (!res.ok) {
    const msg = await res.text();
    throw new Error(msg || "Poll aanmaken mislukt");
  }

  return await res.json();
}

export async function getAdminPolls(
    page = 0,
    size = 10
): Promise<Page<AdminPoll>> {
  const res = await apiFetch(
      `/api/admin/polls?page=${page}&size=${size}`
  );

  if (!res.ok) {
    throw new Error("Kon stellingen niet ophalen");
  }

  return await res.json();
}
export async function getMyVote(pollId: string) {
  const res = await apiFetch(`/api/polls/${pollId}/my-vote`);

  if (!res.ok) {
    return null;
  }

  return await res.json();
}
