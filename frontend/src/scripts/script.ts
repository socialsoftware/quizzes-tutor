import Image from "@/models/Image";
import showdown from "showdown";

export function map_to_object(aMap: Map<any, any>) {
  let obj: object = {};
  aMap.forEach((v, k) => {
    // @ts-ignore
    obj[k] = v;
  });
  return obj;
}

export function convertMarkDown(
  text: string,
  image: Image | null = null
): string {
  const converter = new showdown.Converter();

  if (image) {
    text +=
      "  \n  \n  \n[image]: " +
      process.env.VUE_APP_ROOT_API +
      "/images/questions/" +
      image.url +
      ' "Image"';
  }

  let str = converter.makeHtml(text);
  //remove root paragraphs <p></p>
  str = str.substring(3);
  str = str.substring(0, str.length - 4);

  return str;
}
