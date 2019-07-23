export function map_to_object(aMap: Map<any, any>) {
  let obj: object = {};
  aMap.forEach((v, k) => {
    // @ts-ignore
    obj[k] = v;
  });
  return obj;
}
