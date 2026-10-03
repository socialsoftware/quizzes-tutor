// A heading path of a course material, to ask for questions about part of it
export default class GenerationSection {
  path!: string;
  title!: string;
  chunkCount: number = 0;

  constructor(jsonObj?: { path: string; title: string; chunk_count?: number }) {
    if (jsonObj) {
      this.path = jsonObj.path;
      this.title = jsonObj.title;
      this.chunkCount = jsonObj.chunk_count ?? 0;
    }
  }

  // Where the section sits, without its own title (e.g. "Part I > 1 Numbers")
  get parentPath(): string {
    const separator = this.path.lastIndexOf(' > ');
    return separator === -1 ? '' : this.path.slice(0, separator);
  }
}
