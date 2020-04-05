import Image from '@/models/management/Image';
import showdown from 'showdown';
var sanitizeHtml = require('sanitize-html');

export function convertMarkDown(
  text: string,
  image: Image | null = null
): string {
  const converter = new showdown.Converter();

  if (image && image.url) {
    text +=
      '  \n  \n  \n[image]: ' +
      process.env.VUE_APP_ROOT_API +
      '/images/questions/' +
      image.url +
      ' "Image"';
  }

  let str = converter.makeHtml(text);
  //remove root paragraphs <p></p>
  str = str.substring(3);
  str = str.substring(0, str.length - 4);

  return sanitizeHtml(str, {
    allowedTags: ['code', 'img'],
    allowedAttributes: {
      img: ['src']
    }
  });
}

export function convertMarkDownNoFigure(
  text: string,
  image: Image | null = null
): string {
  const converter = new showdown.Converter();

  if (image && image.url) {
    text += ' FIGURE HERE ';
  }

  let str = converter.makeHtml(text);
  //remove root paragraphs <p></p>
  str = str.substring(3);
  str = str.substring(0, str.length - 4);

  return sanitizeHtml(str, {
    allowedTags: ['code', 'img'],
    allowedAttributes: {
      img: ['src']
    }
  });
}
