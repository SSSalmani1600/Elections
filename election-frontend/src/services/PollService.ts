import { apiFetch } from "@/apiClient";

export interface PollResult {
  eens: number;
  oneens: number;
  total: number;
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
