type Width = string | number | undefined;

interface SizedHeader {
  width?: Width;
  minWidth?: Width;
}

// Pixel width worth enforcing as a minimum, or undefined
function minPixelWidth(width: Width): number | undefined {
  const px =
    typeof width === 'number' ? width : /^(\d+)px$/.exec(width ?? '')?.[1];
  return px !== undefined && Number(px) >= 60 ? Number(px) : undefined;
}

// Vuetify 2 applied a data-table header's `width` as its `min-width` too, so
// fixed-size columns (status selects, dates, image uploads...) kept their size
// when the table ran short of space. Vuetify 3/4 only sets `width`, and the
// browser squeezes those columns until their content no longer fits.
// Only pixel widths are copied: browsers ignore a percentage min-width on table
// cells (so V2's had no effect), and tiny ones ('5px') only meant "as narrow as
// the content", which the 60px floor in _global.scss already covers.
export function withV2ColumnWidths<T extends SizedHeader>(
  headers: readonly T[]
): T[] {
  return headers.map((header) => {
    const minWidth = minPixelWidth(header.width);
    return minWidth === undefined || header.minWidth !== undefined
      ? header
      : { ...header, minWidth: `${minWidth}px` };
  });
}
