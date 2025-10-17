export interface LoginResponse {
  displayName: string
  token: string
}

export interface RegisterResponse {
  email: string;
  password: string;
  username: string;
}

export interface ParserResponse {
  parties: {
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
