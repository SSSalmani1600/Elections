const API_URL = "http://localhost:8080/api/admin/moderation";



export interface ModerationResult {
  originalText: string;
  moderatedText: string; // De gefilterde tekst (met ***)
  isFlagged: boolean;
  isBlocked: boolean;
  requiresConfirmation: boolean;
  warnings: string[];
}


export class ModerationBlockError extends Error {
  public readonly moderationResult: ModerationResult;

  constructor(message: string, result: ModerationResult) {
    super(message);
    this.name = 'ModerationBlockError';
    // Sla het volledige resultaat op voor gebruik in de UI
    this.moderationResult = result;
  }
}



export async function moderateText(text: string): Promise<ModerationResult> {
  const token = localStorage.getItem("JWT");


  const MODERATE_URL = "http://localhost:8080/api/moderateText";

  const res = await fetch(MODERATE_URL, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`
    },
    body: JSON.stringify({ text }) // Stuur de tekst in de body
  });

  if (res.status === 403) {

    const result: ModerationResult = await res.json();
    throw new ModerationBlockError("Message blocked due to severe content", result);
  }

  if (!res.ok) {

    throw new Error("Failed to check message content. Try again.");
  }

  return await res.json() as ModerationResult;
}


export async function getPendingReactions() {
  const token = localStorage.getItem("JWT");
  const res = await fetch(`${API_URL}/pending`, {
    method: "GET",
    headers: { Authorization: `Bearer ${token}` }
  });
  if (!res.ok) throw new Error("Failed to load pending reactions");
  return await res.json();
}

export async function getFlaggedReactions() {
  const token = localStorage.getItem("JWT");
  const res = await fetch(`${API_URL}/flagged`, {
    method: "GET",
    headers: { Authorization: `Bearer ${token}` }
  });
  if (!res.ok) throw new Error("Failed to load flagged reactions");
  return await res.json();
}

export async function approveReaction(id: number) {
  const token = localStorage.getItem("JWT");
  const res = await fetch(`${API_URL}/${id}/approve`, {
    method: "POST",
    headers: { Authorization: `Bearer ${token}` }
  });
  if (!res.ok) throw new Error("Failed to approve reaction");
}

export async function rejectReaction(id: number) {
  const token = localStorage.getItem("JWT");
  const res = await fetch(`${API_URL}/${id}/reject`, {
    method: "POST",
    headers: { Authorization: `Bearer ${token}` }
  });
  if (!res.ok) throw new Error("Failed to reject reaction");
}

export async function flagReaction(id: number, reason: string) {
  const token = localStorage.getItem("JWT");
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
