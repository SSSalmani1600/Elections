export interface LoginResponse {
  id: number
  email: string
  username: string
  isAdmin: boolean
}

export interface RegisterResponse {
  email: string
  password: string
  username: string
}
export type ParserResponse = {
  parties: {
    name: string
    candidates: Candidate[]
  }[]
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

export interface Candidate {
  firstName: string
  namePrefix?: string
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

export interface Party2 {
  year: number
  party_id: string
  name: string
}

export interface Constituency {
  name: string
  parties: Party[]
}

export interface User {
  id: number
  email: string
  username: string
  isAdmin?: boolean
}

export interface Statement {
  id: number
  statement: string
  category: string
  explanation: string
  answer: string | null
}
export interface AdminStats {
  totalUsers: number;
  reportedPosts: number;
  pendingReviews: number;
}
export interface LoginResponse {
  id: number
  displayName: string
  token: string
  isAdmin: boolean
}



