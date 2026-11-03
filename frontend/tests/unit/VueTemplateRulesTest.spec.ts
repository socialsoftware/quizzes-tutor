import { describe, expect, test } from 'vitest';

// Every component of the app as text (no Node types needed: Vite reads the files)
const files = import.meta.glob('../../src/**/*.vue', { query: '?raw', import: 'default', eager: true }) as Record<string, string>;
const shortName = (path: string) => path.replace('../../src/', '');

const split = (source: string) => {
  const at = source.indexOf('<script');
  return { template: at < 0 ? source : source.slice(0, at), script: at < 0 ? '' : source.slice(at) };
};

describe('rules the Vue templates of the app have to follow', () => {
  // In <script setup>, <show-question-dialog> is looked up as showQuestionDialog before it is looked up as
  // ShowQuestionDialog: a function with the first name takes the place of the imported component, the Vue
  // calls it as if it rendered, and the dialog is never drawn (the "Show question" of the quiz form was dead)
  test('no function of a <script setup> has the camelCase name of a component tag of its template', () => {
    const clashes: string[] = [];
    for (const [file, source] of Object.entries(files)) {
      const { template, script } = split(source);
      if (!script.startsWith('<script setup')) continue;
      const tags = new Set([...template.matchAll(/<([a-z][a-z0-9]*(?:-[a-z0-9]+)+)\b/g)].map((match) => match[1]));
      for (const tag of tags) {
        const name = tag.replace(/-([a-z0-9])/g, (_, letter) => letter.toUpperCase());
        const asFunction = new RegExp(
          `function\\s+${name}\\s*\\(|const\\s+${name}\\s*(?::[^=]+)?=\\s*(?:async\\s*)?(?:function\\b|\\([^)]*\\)\\s*(?::[^=]+)?=>|[A-Za-z_$][\\w$]*\\s*=>)`
        );
        if (asFunction.test(script)) clashes.push(`${shortName(file)}: ${name}() and <${tag}>`);
      }
    }
    expect(clashes).toEqual([]);
  });

  // The dialog that scrolls is the card inside it: a table straight under v-dialog is cut off by the
  // window and cannot be scrolled (the list of students of a quiz)
  test('every dialog holds a card, so that it scrolls when it is taller than the window', () => {
    const bare: string[] = [];
    for (const [file, source] of Object.entries(files)) {
      const { template } = split(source);
      for (const dialog of template.matchAll(/<v-dialog\b[^>]*>([\s\S]*?)<\/v-dialog>/g)) {
        const first = dialog[1].match(/<([a-zA-Z][\w-]*)/);
        if (first && !['v-card', 'v-sheet', 'v-form', 'v-container', 'template'].includes(first[1]))
          bare.push(`${shortName(file)}: <${first[1]}> directly inside <v-dialog>`);
      }
    }
    expect(bare).toEqual([]);
  });
});
