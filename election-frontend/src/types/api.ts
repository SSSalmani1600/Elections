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

export interface BasicPartyInfo {
  year: number;
  party_id: string;
  name: string;
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

// User interface - wordt gebruikt in AccountView en authStore
export interface User {
  id: number           // Unieke gebruikers-ID uit database
  email: string        // Email adres van gebruiker
  username: string     // Weergavenaam
  isAdmin?: boolean    // Of gebruiker admin rechten heeft
  token: string        // JWT token voor authenticatie
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

export interface VotingGuideParty {
  id: number,
  name: string
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
export interface Reaction {
  id: number;
  message: string;
  flaggedReason?: string;
}
