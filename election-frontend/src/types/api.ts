export interface LoginResponse {
  displayName: string
  token: string
}

export interface RegisterResponse {
  email: string;
  password: string;
  username: string;
}
export type ParserResponse = { parties: { name: string }[] }
export type ParserResponse = {
  parties: {
    name: string;
    candidates: Candidate[];
  }[];
};

export interface Affiliation {
  name: string;
}

export interface AffiliationResponse {
  id: string;
  affiliations: Affiliation[];
}
