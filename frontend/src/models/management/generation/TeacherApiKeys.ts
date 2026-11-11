// A teacher's own API keys for question generation. The key itself is never sent back: only whether
// there is one for each provider and how it ends

export interface TeacherApiKey {
  provider: string;
  configured: boolean;
  lastFour: string | null;
  updatedAt: string | null;
}

export interface TeacherApiKeys {
  // false when the server has no encryption key, so no key can be saved
  available: boolean;
  keys: TeacherApiKey[];
}
