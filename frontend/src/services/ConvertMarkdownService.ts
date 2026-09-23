import Image from '@/models/management/Image';

import sanitizeHtml from 'sanitize-html';
import { Marked, type Tokens } from 'marked';
import { IOptions } from 'sanitize-html';

const sanitizeParams: IOptions = {
  allowedTags: [
    'a',
    'b',
    'blockquote',
    'br',
    'caption',
    'code',
    'div',
    'del',
    'em',
    'h1',
    'h2',
    'h3',
    'h4',
    'h5',
    'h6',
    'hr',
    'i',
    'img',
    'li',
    'nl',
    'ol',
    'p',
    'pre',
    'strike',
    'strong',
    'sub',
    'sup',
    'table',
    'tbody',
    'td',
    'th',
    'thead',
    'tr',
    'ul',
    'u',
  ],
  disallowedTagsMode: 'discard',
  allowedAttributes: {
    a: ['href', 'name', 'target'],
    img: ['src'],
    '*': ['id', 'name', 'class', 'title', 'style'],
  },
  selfClosing: ['img', 'br', 'hr', 'area', 'base', 'basefont'],
  allowedSchemes: ['http', 'https', 'ftp', 'mailto'],
  allowedSchemesByTag: {},
  allowedSchemesAppliedToAttributes: ['href', 'src', 'cite'],
  allowProtocolRelative: true,
};

// GFM already covers what the previous converter was configured for
// (tables, strikethrough, task lists, no intra-word underscore emphasis).
// The two behaviours it does not cover are reproduced below: `__text__` as
// underline rather than bold, and links opening in a new window.
const converter = new Marked({
  gfm: true,
  breaks: false,
  extensions: [
    {
      name: 'underline',
      level: 'inline',
      start(src: string) {
        return src.indexOf('__');
      },
      tokenizer(src: string) {
        const match = /^__(?=[^\s_])([\s\S]*?[^\s_])__(?!_)/.exec(src);
        if (!match) return undefined;
        return {
          type: 'underline',
          raw: match[0],
          tokens: this.lexer.inlineTokens(match[1]),
        };
      },
      renderer(token: Tokens.Generic) {
        return `<u>${this.parser.parseInline(token.tokens ?? [])}</u>`;
      },
    },
  ],
  hooks: {
    // The previous converter accepted ATX headings without a space after the
    // hashes (`###Title`), which CommonMark does not; 132 existing questions
    // rely on it, so normalise them before parsing.
    preprocess(markdown: string) {
      return markdown.replace(/^(#{1,6})(?=[^#\s])/gm, '$1 ');
    },
  },
  renderer: {
    link({ href, title, tokens }: Tokens.Link) {
      const text = this.parser.parseInline(tokens);
      const titleAttr = title ? ` title="${title}"` : '';
      return `<a href="${href}"${titleAttr} target="_blank">${text}</a>`;
    },
  },
});

export function convertMarkDown(
  text: string,
  image: Image | null = null
): string {
  if (image && image.url) {
    text +=
      '  \n  \n  \n[image]: ' +
      import.meta.env.VUE_APP_ROOT_API +
      '/images/questions/' +
      image.url +
      ' "Image"';
  }

  return sanitizeHtml(converter.parse(text, { async: false }), sanitizeParams);
}
