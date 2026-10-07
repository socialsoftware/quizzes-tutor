// A heading of a course material with the text under it, to edit the sections of the document by hand
export default class OutlineNode {
  path!: string;
  title!: string;
  depth: number = 1;
  hasText: boolean = false;
  paragraphCount: number = 0;
  preview: string = '';

  constructor(jsonObj?: {
    path: string;
    title: string;
    depth: number;
    has_text?: boolean;
    paragraph_count?: number;
    preview?: string;
  }) {
    if (jsonObj) {
      this.path = jsonObj.path;
      this.title = jsonObj.title;
      this.depth = jsonObj.depth;
      this.hasText = jsonObj.has_text ?? false;
      this.paragraphCount = jsonObj.paragraph_count ?? 0;
      this.preview = jsonObj.preview ?? '';
    }
  }
}

// One change to the sections of a document
export interface OutlineEdit {
  op: 'rename' | 'merge' | 'shift' | 'split';
  path: string;
  // rename: the new name; split: the name of the new section
  title?: string;
  // shift: -1 moves the section a level up, +1 under the section above it
  delta?: number;
  // split: the new section starts at this paragraph (from 0) of the section
  paragraph?: number;
}

export interface Paragraph {
  index: number;
  text: string;
}

export class OutlineEditResult {
  outline: OutlineNode[] = [];
  // old section path -> where its text is now; paths not listed did not change
  pathMap: Record<string, string[]> = {};

  constructor(jsonObj?: { outline: any[]; path_map?: Record<string, string[]> }) {
    if (jsonObj) {
      this.outline = jsonObj.outline.map((node) => new OutlineNode(node));
      this.pathMap = jsonObj.path_map ?? {};
    }
  }
}
