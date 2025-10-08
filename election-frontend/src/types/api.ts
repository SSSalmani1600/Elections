export interface LoginResponse {
  displayName: string
  token: string
}

export interface ParserResponse {
  affiliations: {
    name: string
  }[]
}
