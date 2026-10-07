// A draft reply to a student's doubt, for the teacher to edit before sending
export default class ReplySuggestion {
  reply!: string;
  // Sections of the course material the draft was written with
  sources: string[] = [];

  constructor(jsonObj?: { reply: string; sources?: string[] }) {
    if (jsonObj) {
      this.reply = jsonObj.reply;
      this.sources = jsonObj.sources ?? [];
    }
  }
}
