export interface LoginResponse {
  displayName: string
  token: string
}

export interface RegisterResponse {
  email: string;
  password: string;
}

export interface ParserResponse {
  affiliations: {
    name: string
  }[]
}

export interface Affiliation {
  name: string;
}

export interface AffiliationResponse {
  id: string;
  affiliations: Affiliation[];
}
