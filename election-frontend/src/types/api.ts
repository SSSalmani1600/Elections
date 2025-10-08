export interface LoginResponse {
    displayName: string
    token: string
}

export interface Affiliation {
  name: string;
  candidates: string;
}

export interface AffiliationResponse {
  id: string;
  affiliations: Affiliation[];
}
