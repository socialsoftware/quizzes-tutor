import Image from '@/models/management/Image';
const sanitizeHtml = require('sanitize-html');
const showdown = require('showdown');

const sanitizeParams = {
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
const converter = new showdown.Converter({
  literalMidWordUnderscores: true,
  strikethrough: true,
  tasklists: true,
  openLinksInNewWindow: true,
  tables: true,
  underline: true,
  backslashEscapesHTMLTags: true,
});

export function convertMarkDown(
  text: string,
  image: Image | null = null
): string {
  if (image && image.url) {
    text +=
      '  \n  \n  \n[image]: ' +
      process.env.VUE_APP_ROOT_API +
      '/images/questions/' +
      image.url +
      ' "Image"';
  }

  return sanitizeHtml(converter.makeHtml(text), sanitizeParams);
}
