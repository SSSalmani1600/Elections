export interface LoginResponse {
  username: string
}

export interface RegisterResponse {
  email: string
  password: string
  username: string
}
export type ParserResponse = { parties: { name: string }[] }

export interface Candidate {
  firstName: string
  lastName: string
  initials: string
  gender: string
  localityName: string
  electionName: string
  partiesId: string
  candidateId: string
  votes: string
}

export interface Candidate {
  firstName: string
  lastName: string
  initials: string
  gender: string
  localityName: string
  electionName: string
  partiesId: string
  candidateId: string
  votes: string
}

export interface Party {
  name: string
  id: string
  votes: number
  candidates: Candidate[]
}

export interface Constituency {
  name: string
  parties: Party[]
}

export interface User {
  id: number
  displayName: string
  email: string
  password: string
}
