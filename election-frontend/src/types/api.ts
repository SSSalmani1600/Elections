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
  namePrefix?: string
  lastName: string
  initials?: string
  gender: string
  localityName: string
  electionName?: string
  partiesId?: string
  candidateId: string
  votes?: string | number
}

export interface Party {
  name: string
  id: string
  votes: number
  candidates: Candidate[]
}

export interface PartyDetail {
  partyId: string
  name: string
  year: number
  candidates: Candidate[]
}

export interface PartyResult {
  partyId: string
  name: string
  votes: number
}

export interface Municipality {
  municipalityId: string
  name: string
  constituencyId: string
  parties: PartyResult[]
}

export interface Constituency {
  name: string
  constituencyId: string
  parties: Party[]
}

export interface User {
  id: number
  email: string
  username: string
  isAdmin?: boolean
  token: string
}

export interface Statement {
  id: number
  statement: string
  category: string
  explanation: string
  answer: string | null
}
export interface AdminStats {
  totalUsers: number
  reportedPosts: number
  pendingReviews: number
}
export interface LoginResponse {
  id: number
  displayName: string
  token: string
  isAdmin: boolean
}

export interface VotingGuideResultRequest {
  votingGuideAnswers: VotingGuideAnswer[]
}

export interface VotingGuideAnswer {
  statementId: number
  answer: string
}

export interface VotingGuideResultResponse {
  votingGuideResults: VotingGuideResult[]
}

export interface VotingGuideResult {
  partyId: number
  partyName: string
  percentage: string
}
