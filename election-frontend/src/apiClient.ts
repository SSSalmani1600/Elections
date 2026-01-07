let refreshPromise: Promise<Response> | null = null

export const backendAPI = "http://oege.ie.hva.nl:9000"

async function callRefresh(): Promise<Response> {
  return fetch(`${backendAPI}/api/auth/refresh`, {
    method: 'POST',
    credentials: 'include',
  })
}

export async function apiFetch(
  input: RequestInfo,
  init: RequestInit = {},
  retry = true,
): Promise<Response> {
  const res = await fetch(input, {
    ...init,
    credentials: 'include',
  })

  if (res.status !== 401 || !retry) {
    return res
  }

  if (!refreshPromise) {
    refreshPromise = callRefresh().finally(() => {
      refreshPromise = null
    })
  }

  const refreshRes = await refreshPromise

  // refresh failed
  if (!refreshRes.ok) {
    throw new Error('unauthorized')
  }

  // retry request
  return apiFetch(input, init, false)
}
