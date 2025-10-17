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
  affiliations: {
    name: string
  }[]
}

export interface Candidate {
  firstName: string;
  lastName: string;
  initials: string;
  gender: string;
  localityName: string;
  electionName: string;
  partiesId: string;
  candidateId: string;
  votes: string;
}

