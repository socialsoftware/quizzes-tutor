// The API keys someone gave (a teacher, or the administrator for the whole site). The key itself is never
// sent back: only which providers have one and how it ends

export interface TeacherApiKey {
  provider: string;
  // How the provider is called
  name: string;
  lastFour: string | null;
  updatedAt: string | null;
}

export interface ApiKeyProvider {
  id: string;
  name: string;
}

export interface TeacherApiKeys {
  // false when the server has no encryption key, so no key can be saved
  available: boolean;
  // The ones given
  keys: TeacherApiKey[];
  // The providers a key can be given for
  providers: ApiKeyProvider[];
}
