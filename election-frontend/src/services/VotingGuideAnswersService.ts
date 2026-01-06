import type { VotingGuideResultRequest } from '@/types/api.ts'
import { backendAPI } from '@/apiClient';

export async function saveAnswers(request: VotingGuideResultRequest) {
  try {
    const res = await fetch(`${backendAPI}/api/voting-guide/answers/save-answers`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify(request),
    });

    if (!res.ok) {
      throw new Error(`Saving answers failed (status ${res.status})`);
    }

  } catch (err: any) {
    throw new Error(`Saving answers error: ${err}`)
  }
}
