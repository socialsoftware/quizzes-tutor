// The models a teacher can pick for a request of questions

export interface GenerationModelChoice {
  provider: string;
  model: string;
  // The request would be charged to the teacher's own key for this provider
  ownKey: boolean;
}

export interface GenerationModels {
  // What is used when none is picked
  defaultModel: GenerationModelChoice | null;
  // The ones the administrator lists for every teacher
  models: GenerationModelChoice[];
  // Providers the teacher has a key for: any model of them can be typed
  ownKeyProviders: string[];
}
